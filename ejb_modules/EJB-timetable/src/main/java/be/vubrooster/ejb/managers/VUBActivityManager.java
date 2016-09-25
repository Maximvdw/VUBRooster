package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.CourseServer;
import be.vubrooster.ejb.models.*;
import be.vubrooster.ejb.service.ServiceProvider;
import be.vubrooster.utils.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * VUBActivityManager
 * Created by maxim on 21-Sep-16.
 */
public class VUBActivityManager extends ActivityManager {
    private String baseURL = "http://splus.cumulus.vub.ac.be:1184/reporting/individual?periods2=3-27&idtype=name&weeks=1-52&days=1-7&periods=3-31";

    private SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("NL"));

    private enum TimeTableType {
        STUDENT_INDIVIDUAL,
        STUDENT_GROUPED,
        STAFF
    }

    public VUBActivityManager(ActivitiyServer server) {
        super(server);
    }

    @Override
    public List<Activity> loadActivitiesForGroups(List<Activity> activityList) {
        super.loadActivitiesForGroups(activityList);

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

        //int maxQuerySize = 50;
        int maxQuerySize = 25;
        final Map<Integer, List<StudentGroup>> queryGroups = new TreeMap<>();
        int queryPart = 0;
        int groupSize = 0;
        List<StudentGroup> groups = new ArrayList<>();
        splitStudentGroups(individualGroups, groups, groupSize, maxQuerySize, queryGroups, queryPart);
        if (groups.size() != 0) {
            queryGroups.put(queryPart, new ArrayList<>(groups));
            queryPart++;
        }
        groupSize = 0;
        groups.clear();
        splitStudentGroups(groupedGroups, groups, groupSize, maxQuerySize, queryGroups, queryPart);
        if (groups.size() != 0) {
            queryGroups.put(queryPart, new ArrayList<>(groups));
            groups.clear();
        }

        // Create a thread pool
        List<Thread> threadPool = new ArrayList<>();
        for (final Map.Entry<Integer, List<StudentGroup>> queryGroup : queryGroups.entrySet()) {
            Thread th = new Thread(() -> {
                logger.info("Fetching timetables: " + (queryGroup.getKey() + 1) + "/" + (queryGroups.size()));
                fetchGroupTimeTable(queryGroup, currentTimeTable);
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
                    for (Thread thread : shadedThreadPool) {
                        if (!thread.isAlive()) {
                            threadPool.remove(thread);
                        }
                    }
                }
            }
        }

        while (threadPool.size() != 0) {
            // Wait
            List<Thread> shadedThreadPool = new ArrayList<Thread>(threadPool);
            for (Thread thread : shadedThreadPool) {
                if (!thread.isAlive()) {
                    threadPool.remove(thread);
                }
            }
        }
        ServiceProvider.getTimeTableServer().updateTimeTable(currentTimeTable);
        return getActivityList();
    }

    @Override
    public List<Activity> loadActivitiesForStaff(List<Activity> activityList) {
        super.loadActivitiesForStaff(activityList);
        TimeTable currentTimeTable = ServiceProvider.getTimeTableServer().getCurrentTimeTable();
        // Set GMT timezone to avoid problems with daylight savings
        // Clients/frontends should deal with this depending on their location
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        List<StaffMember> staffMembers = ServiceProvider.getStaffServer().findStaff(true);
        //int maxQuerySize = 50;
        int maxQuerySize = 25;
        final Map<Integer, List<StaffMember>> queryGroups = new TreeMap<>();
        splitStaffMembers(staffMembers, maxQuerySize, queryGroups);

        // Create a thread pool
        List<Thread> threadPool = new ArrayList<>();
        for (final Map.Entry<Integer, List<StaffMember>> queryGroup : queryGroups.entrySet()) {
            Thread th = new Thread(() -> {
                logger.info("Fetching staff timetables: " + (queryGroup.getKey() + 1) + "/" + (queryGroups.size()));
                fetchStaffTimeTable(queryGroup, currentTimeTable);
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
                    for (Thread thread : shadedThreadPool) {
                        if (!thread.isAlive()) {
                            threadPool.remove(thread);
                        }
                    }
                }
            }
        }

        while (threadPool.size() != 0) {
            // Wait
            List<Thread> shadedThreadPool = new ArrayList<Thread>(threadPool);
            for (Thread thread : shadedThreadPool) {
                if (!thread.isAlive()) {
                    threadPool.remove(thread);
                }
            }
        }

        return getActivityList();
    }

    @Override
    public List<Activity> loadActivitiesForClassRooms(List<Activity> activityList) {
        super.loadActivitiesForClassRooms(activityList);

        return getActivityList();
    }

    @Override
    public List<Activity> loadActivitiesForCourses(List<Activity> activityList) {
        super.loadActivitiesForCourses(activityList);

        return getActivityList();
    }

    /**
     * Split student groups in equal groups
     *
     * @param filteredGroups
     * @param groups
     * @param groupSize
     * @param maxQuerySize
     * @param queryGroups
     * @param queryPart
     */
    private void splitStudentGroups(List<StudentGroup> filteredGroups, List<StudentGroup> groups, int groupSize, int maxQuerySize, Map<Integer, List<StudentGroup>> queryGroups, int queryPart) {
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

    private void splitStaffMembers(List<StaffMember> filteredStaffMembers, int maxQuerySize, Map<Integer, List<StaffMember>> queryGroups) {
        List<StaffMember> staffMembers = new ArrayList<>();
        int queryPart = 0;
        for (StaffMember staffMember : filteredStaffMembers) {
            staffMembers.add(staffMember);
            if (staffMembers.size() > maxQuerySize) {
                queryGroups.put(queryPart, new ArrayList<>(staffMembers));
                queryPart++;
                staffMembers.clear();
            }
        }
    }

    private void fetchStaffTimeTable(Map.Entry<Integer, List<StaffMember>> queryGroup, TimeTable currentTimeTable) {
        try {
            String timetableURL = baseURL;
            for (StaffMember staffMember : queryGroup.getValue()) {
                timetableURL += "&identifier=" + staffMember.getId().replace(" ", "%20");
            }
            timetableURL += "&template=Staff%2BIndividual&objectclass=staff";
            Document timetablePage = Jsoup.parse(HtmlUtils.sendGetRequest(timetableURL, new HashMap<>(), 25000).getSource());

            int i = 0;
            Elements staffNames = timetablePage.select(".header-1-0-2");
            for (Element staffName : staffNames) {
                // Find the group by name
                // Its possible that they are:
                // 1) Not in order
                // 2) Not exist
                StaffMember member = null;
                for (StaffMember m : queryGroup.getValue()) {
                    if (m.getName().equalsIgnoreCase(staffName.html())) {
                        member = m;
                        break;
                    }
                }
                if (member != null) {
                    Element table = timetablePage.select(".grid-border-args").get(i); // Get the table element
                    i++;
                    parseTimeTable(member, table, currentTimeTable);
                }
            }
        } catch (ConnectException | SocketTimeoutException ex) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("Retrying ...");
            fetchStaffTimeTable(queryGroup, currentTimeTable);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void fetchGroupTimeTable(Map.Entry<Integer, List<StudentGroup>> queryGroup, TimeTable currentTimeTable) {
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
        } catch (ConnectException | SocketTimeoutException ex) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("Retrying ...");
            fetchGroupTimeTable(queryGroup, currentTimeTable);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private List<Activity> parseTimeTable(Element table, TimeTable currentTimeTable, SimpleDateFormat timeFormatter, CourseServer courseServer, TimeTableType timeTableType) {
        List<Activity> activityList = new ArrayList<>();
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
                            String eventWeeks = eventData.get(timeTableType != TimeTableType.STAFF ? 3 : 4).html();
                            String eventLector = timeTableType != TimeTableType.STAFF ? eventData.get(4).html() : "";

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
                                // If the course does not exist, put it in a misc group

                                if (course == null) {
                                    if (eventName.contains(" (WPO") || eventName.contains(" (HOC")) {
                                        course = new Course(eventName.substring(0, eventName.indexOf("(") - 1));
                                        course = ServiceProvider.getCourseServer().createCourse(course);
                                        activity.getCourses().add(course);
                                    } else {
                                        // Unknown
                                    }
                                } else {
                                    activity.getCourses().add(course);
                                }

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

                                activityList.add(activity);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        List<Activity> courseLessActivities = new ArrayList<>();
        for (Activity activity : activityList) {
            if (activity.getCourses().size() == 0) {
                courseLessActivities.add(activity);
            }
        }
        Map<String, Integer> nameCounter = new HashMap<>();
        for (Activity activity : courseLessActivities) {
            if (nameCounter.containsKey(activity.getName())) {
                nameCounter.put(activity.getName(), nameCounter.get(activity.getName()) + 1);
            } else {
                nameCounter.put(activity.getName(), 1);
            }
        }
        for (Map.Entry<String, Integer> names : nameCounter.entrySet()) {
            if (names.getValue() > 2) {
                Course course = new Course(names.getKey());
                ServiceProvider.getCourseServer().createCourse(course);
            }
        }
        for (Activity activity : courseLessActivities) {
            // Try to match it to an existing course
            Course course = courseServer.findCourseByName(activity.getName(), true);
            if (course != null) {
                activity.getCourses().add(course);
            }
        }
        return activityList;
    }

    private void parseTimeTable(StudentGroup group, Element table, TimeTable currentTimeTable) {
        // Create formatter for the hours:minutes format of the activities
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        timeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        CourseServer courseServer = ServiceProvider.getCourseServer();

        logger.info("Extracting models for group: " + group.getName());
        List<Activity> activityList = parseTimeTable(table, currentTimeTable, timeFormatter, courseServer, TimeTableType.STUDENT_GROUPED);
        for (Activity activity : activityList) {
            activity.addGroup(group);
            addActivity(activity);
        }
    }

    private void parseTimeTable(StaffMember staffMember, Element table, TimeTable currentTimeTable) {
        // Create formatter for the hours:minutes format of the activities
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        timeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        CourseServer courseServer = ServiceProvider.getCourseServer();

        logger.info("Extracting models for staff member: " + staffMember.getName());
        List<Activity> activityList = parseTimeTable(table, currentTimeTable, timeFormatter, courseServer, TimeTableType.STAFF);
        for (Activity activity : activityList) {
            activity.setStaff(staffMember.getName());
            addActivity(activity);
        }
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
}
