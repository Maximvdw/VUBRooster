package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.CourseServer;
import be.vubrooster.ejb.StudentGroupServer;
import be.vubrooster.ejb.models.*;
import be.vubrooster.ejb.service.ServiceProvider;
import be.vubrooster.utils.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * VUBActivityManager
 * Created by maxim on 21-Sep-16.
 */
public class VUBActivityManager extends ActivityManager{
    private String baseURL = "http://splus.cumulus.vub.ac.be:1184/reporting/individual?periods2=3-27&idtype=name&weeks=1-52&days=1-7&periods=3-31";

    private SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("NL"));

    public VUBActivityManager(ActivitiyServer server) {
        super(server);
    }

    @Override
    public List<Activity> loadActivities(List<Activity> activityList) {
        super.loadActivities(activityList);

        TimeTable currentTimeTable = ServiceProvider.getTimeTableServer().getCurrentTimeTable();
        // Set GMT timezone to avoid problems with daylight savings
        // Clients/frontends should deal with this depending on their location
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        // Load student groups
        List<StudentGroup> allStudentGroups = ServiceProvider.getStudentGroupServer().findStudentGroups(true);

        // Split the study programmes into individual and grouped
        List<StudentGroup> individualGroups = new ArrayList<>();
        List<StudentGroup> groupedGroups = new ArrayList<>();
        for (StudentGroup group : allStudentGroups) {
            if (group.isIndividual()) {
                individualGroups.add(group);
            } else {
                groupedGroups.add(group);
            }
        }
        // DISCLAIMER: DO NOT MAKE TOO LARGE QUERY GROUPS
        // THIS WILL CRASH THE VUB SPLUS SERVER
        // MAXIMUM SIZE 300

        int maxQuerySize = 50;
        final Map<Integer, List<StudentGroup>> queryGroups = new TreeMap<>();
        int queryPart = 0;
        int groupSize = 0;
        List<StudentGroup> groups = new ArrayList<>();
        StudentGroupServer studentGroupServer = ServiceProvider.getStudentGroupServer();
        splitStudentGroups(individualGroups, studentGroupServer, groups, groupSize, maxQuerySize, queryGroups, queryPart);
        if (groups.size() != 0) {
            queryGroups.put(queryPart, new ArrayList<>(groups));
            queryPart++;
        }
        groupSize = 0;
        groups.clear();
        splitStudentGroups(groupedGroups, studentGroupServer, groups, groupSize, maxQuerySize, queryGroups, queryPart);
        if (groups.size() != 0) {
            queryGroups.put(queryPart, new ArrayList<>(groups));
            groups.clear();
        }

        // Create a thread pool
        List<Thread> threadPool = new ArrayList<>();
        for (final Map.Entry<Integer, List<StudentGroup>> queryGroup : queryGroups.entrySet()) {
            Thread th = new Thread(() -> {
                logger.info("Fetching timetables: " + (queryGroup.getKey() + 1) + "/" + (queryGroups.size()));
                fetchTimeTable(queryGroup, currentTimeTable);
            });
            th.start();
            threadPool.add(th);
            try {
                Thread.sleep(100); // Prevent quick thread starting/connections to server
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (threadPool.size() >= 2) {
                while (threadPool.size() > 1) {
                    List<Thread> shadedThreadPool = new ArrayList<Thread>(threadPool);
                    for (Thread thread : shadedThreadPool){
                        if (!thread.isAlive())
                        {
                            threadPool.remove(thread);
                        }
                    }
                }
            }
        }

        while (threadPool.size() != 0) {
            // Wait
            List<Thread> shadedThreadPool = new ArrayList<Thread>(threadPool);
            for (Thread thread : shadedThreadPool){
                if (!thread.isAlive())
                {
                    threadPool.remove(thread);
                }
            }
        }
        ServiceProvider.getTimeTableServer().updateTimeTable(currentTimeTable);

        return getActivityList();
    }

    /**
     * Split student groups in equal groups
     *
     * @param filteredGroups
     * @param studentGroupServer
     * @param groups
     * @param groupSize
     * @param maxQuerySize
     * @param queryGroups
     * @param queryPart
     */
    private void splitStudentGroups(List<StudentGroup> filteredGroups, StudentGroupServer studentGroupServer, List<StudentGroup> groups, int groupSize, int maxQuerySize, Map<Integer, List<StudentGroup>> queryGroups, int queryPart) {
        for (StudentGroup group : filteredGroups) {
            groups.add(group);
            groupSize++;
            if (groupSize > maxQuerySize) {
                queryGroups.put(queryPart, new ArrayList<>(groups));
                queryPart++;
                groupSize = 0;
                groups.clear();
            }
        }
    }

    private void fetchTimeTable(Map.Entry<Integer, List<StudentGroup>> queryGroup, TimeTable currentTimeTable) {
        try {
            String timetableURL = baseURL;
            boolean grouped = false;
            for (StudentGroup group : queryGroup.getValue()) {
                grouped = !group.isIndividual();
                timetableURL += "&identifier=" + group.getName().replace(" ", "%20");
            }
            timetableURL += "&template=" + (grouped ? "Group%2BIndividual" : "Student+Set+Individual") + "&objectclass=" + (grouped ? "Student+Set%2Bgroup" : "Student+Set");
            Document timetablePage = Jsoup.parse(HtmlUtils.sendGetRequest(timetableURL, new HashMap<>(), 25000).getSource());

            // Get year start date
            String startDate = timetablePage.select(grouped ? ".header-4-0-3" : ".header-6-0-3").first().html();
            currentTimeTable.setStartTimeStamp(formatter.parse(startDate).getTime() / 1000);

            int i = 0;
            Elements groupNames = timetablePage.select(grouped ? ".header-0-0-1" : ".header-1-0-1");
            for (Element groupName : groupNames) {
                // Find the group by name
                // Its possible that they are:
                // 1) Not in order
                // 2) Not exist
                StudentGroup group = null;
                for (StudentGroup g : queryGroup.getValue()) {
                    if (g.getName().equalsIgnoreCase(groupName.html())) {
                        group = g;
                        break;
                    }
                }
                if (group != null) {
                    Element table = timetablePage.select(".grid-border-args").get(i); // Get the table element
                    i++;
                    parseTimeTable(group, table, currentTimeTable);
                }
            }
        } catch (ConnectException ex) {
            logger.info("Retrying ...");
            fetchTimeTable(queryGroup, currentTimeTable);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void parseTimeTable(StudentGroup group, Element table, TimeTable currentTimeTable) {
        // Create formatter for the hours:minutes format of the activities
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        timeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        CourseServer courseServer = ServiceProvider.getCourseServer();

        logger.info("Extracting models for group: " + group.getName());
        try {
            Elements rows = table.select("tbody").first().children();
            Element headerRow = rows.first();
            Elements headerColumns = headerRow.getElementsByTag("td");
            // Loop through the days
            int totalColSpan = 0;
            List<Integer> dayMapping = new ArrayList<Integer>();
            for (int i = 1; i < 8; i++) {
                if (headerColumns.get(i).hasAttr("colspan")) {
                    int colspan = Integer.parseInt(headerColumns.get(i).attr("colspan")); // Columns per day
                    totalColSpan += colspan;
                    for (int j = 0; j < colspan; j++) {
                        dayMapping.add(i);
                    }
                }
            }

            // Table row column structure cache
            int[][] structure = new int[29][totalColSpan];
            for (int j = 1; j <= 29; j++) {
                Element row = rows.get(j);
                Elements columns = row.children();
                for (int k = 1; k < columns.size(); k++) {
                    int idxColumn = k;
                    if (columns.size() - 1 != totalColSpan) { // Empty columns
                        List<Integer> columnMap = new ArrayList<Integer>();
                        for (int l = 0; l < totalColSpan; l++) {
                            if (structure[j - 1][l] == 0) {
                                columnMap.add(l);
                            }
                        }
                        idxColumn = columnMap.get(idxColumn - 1) + 1;
                    }
                    Element column = columns.get(k);
                    if (column.hasAttr("rowspan")) { // Possible an event
                        int rowspan = Integer.parseInt(column.attr("rowspan"));
                        if (rowspan > 1) {
                            // Map the structure of empty TD
                            for (int l = 0; l < rowspan - 1; l++) {
                                structure[j + l][idxColumn - 1] = 1;
                            }
                            // Activity
                            Elements eventData = column.getElementsByTag("td");
                            // Extract activity data
                            String eventName = eventData.get(1).html();
                            String eventClass = eventData.get(2).html();
                            String eventWeeks = eventData.get(3).html();
                            String eventLector = eventData.get(4).html();

                            // Parse the weeks
                            List<Integer> weeks = new ArrayList<Integer>();
                            if (!eventWeeks.equals("")) {
                                parseWeeks(eventWeeks, weeks);
                            }

                            // Try to match it to an existing course
                            Course course = courseServer.findCourseByName(eventName, true);
                            // Add the event for all weeks
                            for (int week : weeks) {
                                Activity activity = new Activity(eventName, eventClass);
                                activity.setStaff(eventLector);
                                activity.setWeeksLabel(eventWeeks);
                                // Save the day of the week for convenience
                                activity.setDay(dayMapping.get(idxColumn - 1));
                                activity.setWeek(week);
                                activity.getGroups().add(group);
                                // If the course does not exist, put it in a misc group

                                activity.getCourses().add(course);

                                // Get the start unix time of the week
                                // It is the start week (fex 19 sep 2016) + week
                                long weekStart = currentTimeTable.getStartTimeStamp() + ((60 * 60 * 24 * 7) * (week - 1));
                                // Get the day start time: week + day
                                long dayStart = weekStart + ((60 * 60 * 24) * (activity.getDay() - 1));
                                // Get the start time of the activity: day + 8:00 + every half hour
                                long startTime = dayStart + (8 * 60 * 60) + ((j - 1) * 30 * 60);
                                long endTime = startTime + (rowspan * 30 * 60);

                                activity.setBeginTimeUnix(startTime);
                                activity.setBeginTime(timeFormatter.format(new Date(startTime * 1000)));
                                activity.setEndTime(timeFormatter.format(new Date(endTime * 1000)));
                                activity.setEndTimeUnix(endTime);

                                addActivity(activity);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
}
