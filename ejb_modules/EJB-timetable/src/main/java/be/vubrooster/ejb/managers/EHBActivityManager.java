package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.CourseServer;
import be.vubrooster.ejb.models.*;
import be.vubrooster.ejb.service.ServiceProvider;
import be.vubrooster.utils.HtmlResponse;
import be.vubrooster.utils.HtmlUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * EHBActivityManager
 * <p>
 * Created by maxim on 21-Sep-16.
 */
public class EHBActivityManager extends ActivityManager {
    private static String baseURL = "https://rooster.ehb.be/Scientia/SWS/SYL_PRD_1617/default.aspx";
    private static String baseTimeTableURL = "https://rooster.ehb.be/Scientia/SWS/SYL_PRD_1617/showtimetable.aspx";
    private static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0";

    public EHBActivityManager(ActivitiyServer server) {
        super(server);
    }

    public static String getBaseURL() {
        return baseURL;
    }

    public static void setBaseURL(String baseURL) {
        EHBActivityManager.baseURL = baseURL;
    }

    public static String getBaseTimeTableURL() {
        return baseTimeTableURL;
    }

    public static void setBaseTimeTableURL(String baseTimeTableURL) {
        EHBActivityManager.baseTimeTableURL = baseTimeTableURL;
    }

    @Override
    public List<Activity> loadActivitiesForGroups(List<Activity> activityList) {
        super.loadActivitiesForGroups(activityList);
        TimeTable timeTable = ServiceProvider.getTimeTableServer().getCurrentTimeTable();

        List<StudentGroup> allGroups = ServiceProvider.getStudentGroupServer().findStudentGroups(true);

        List<List<StudentGroup>> chunkedCourses = new ArrayList<>();
        while (allGroups.size() != 0) {
            List<StudentGroup> chunk = new ArrayList<>();
            List<StudentGroup> remaining = new ArrayList<>(allGroups);
            for (StudentGroup group : remaining) {
                if (chunk.size() == 1000) {
                    break;
                }
                chunk.add(group);
                allGroups.remove(group);
            }
            chunkedCourses.add(chunk);
        }
        int idx = 1;
        for (List<StudentGroup> chunk : chunkedCourses) {
            for (int i = 0 ; i < 2; i ++) {
                logger.info("Fetching group timetables for chunk " + idx + " / " + (chunkedCourses.size() * 2));
                if (!fetchGroupTimeTable(chunk, timeTable, i+1)){
                    logger.error("Error while getting group timetables for chunk " + idx + " / " + (chunkedCourses.size() * 2));
                    i--;
                }
                idx++;
            }
        }
        return getActivityList();
    }

    @Override
    public List<Activity> loadActivitiesForStaff(List<Activity> activityList) {
        super.loadActivitiesForStaff(activityList);
        TimeTable timeTable = ServiceProvider.getTimeTableServer().getCurrentTimeTable();

        List<StaffMember> allStaff = ServiceProvider.getStaffServer().findStaff(true);

        List<List<StaffMember>> chunkedCourses = new ArrayList<>();
        while (allStaff.size() != 0) {
            List<StaffMember> chunk = new ArrayList<>();
            List<StaffMember> remaining = new ArrayList<>(allStaff);
            for (StaffMember member : remaining) {
                if (chunk.size() == 1000) {
                    break;
                }
                chunk.add(member);
                allStaff.remove(member);
            }
            chunkedCourses.add(chunk);
        }
        int idx = 1;
        for (List<StaffMember> chunk : chunkedCourses) {
            for (int i = 0 ; i < 2; i ++) {
                logger.info("Fetching staff timetables for chunk " + idx + " / " + (chunkedCourses.size() * 2));
                if (!fetchStaffTimeTable(chunk, timeTable, i+1)){
                    logger.error("Error while getting staff timetables for chunk " + idx + " / " + (chunkedCourses.size() * 2));
                    i--;
                }
                idx++;
            }
        }

        return getActivityList();
    }

    @Override
    public List<Activity> loadActivitiesForClassRooms(List<Activity> activityList) {
        super.loadActivitiesForClassRooms(activityList);
        TimeTable timeTable = ServiceProvider.getTimeTableServer().getCurrentTimeTable();

        List<ClassRoom> allClasses = ServiceProvider.getClassRoomServer().findClassRooms(true);

        List<List<ClassRoom>> chunkedCourses = new ArrayList<>();
        while (allClasses.size() != 0) {
            List<ClassRoom> chunk = new ArrayList<>();
            List<ClassRoom> remaining = new ArrayList<>(allClasses);
            for (ClassRoom classRoom : remaining) {
                if (chunk.size() == 1000) {
                    break;
                }
                chunk.add(classRoom);
                allClasses.remove(classRoom);
            }
            chunkedCourses.add(chunk);
        }
        int idx = 1;
        for (List<ClassRoom> chunk : chunkedCourses) {
            for (int i = 0 ; i < 2; i ++) {
                logger.info("Fetching location timetables for chunk " + idx + " / " + (chunkedCourses.size() * 2));
                if (!fetchClassRoomTimeTable(chunk, timeTable, i+1)){
                    logger.error("Error while getting timetables for chunk " + idx + " / " + (chunkedCourses.size() * 2));
                    i--;
                }
                idx++;
            }
        }
        return getActivityList();
    }

    @Override
    public List<Activity> loadActivitiesForCourses(List<Activity> activityList) {
        super.loadActivitiesForCourses(activityList);
        TimeTable timeTable = ServiceProvider.getTimeTableServer().getCurrentTimeTable();

        List<Course> allCourses = ServiceProvider.getCourseServer().findCourses(true);

        List<List<Course>> chunkedCourses = new ArrayList<>();
        while (allCourses.size() != 0) {
            List<Course> chunk = new ArrayList<>();
            List<Course> remaining = new ArrayList<>(allCourses);
            for (Course course : remaining) {
                if (chunk.size() == 1000) {
                    break;
                }
                chunk.add(course);
                allCourses.remove(course);
            }
            chunkedCourses.add(chunk);
        }
        int idx = 1;
        for (List<Course> chunk : chunkedCourses) {
            for (int i = 0 ; i < 2; i ++) {
                logger.info("Fetching courses timetables for chunk " + idx + " / " + (chunkedCourses.size() * 2));
                if (!fetchCourseTimeTable(chunk, timeTable, i+1)){
                    logger.error("Error while getting courses timetables for chunk " + idx + " / " + (chunkedCourses.size() * 2));
                    i--;
                }
                idx++;
            }
        }
        List<StudentGroup> groups = ServiceProvider.getStudentGroupServer().findStudentGroups(true);
        for (Activity activity : getActivityList()) {
            for (StudentGroup group : groups) {
                boolean courseFound = false;
                for (Course groupCourse : group.getCourses()){
                    for (Course activityCourse : activity.getCourses()){
                        if (activityCourse.getId().equals(groupCourse.getId())){
                            activity.addGroup(group);
                            courseFound = true;
                            break;
                        }
                    }
                    if (courseFound)
                        break;
                }
            }
        }
        ServiceProvider.getTimeTableServer().updateTimeTable(timeTable);

        return getActivityList();
    }

    public boolean fetchCourseTimeTable(List<Course> courses, TimeTable timeTable,int semester) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("NL"));
        // Set GMT timezone to avoid problems with daylight savings
        // Clients/frontends should deal with this depending on their location
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            Collections.sort(courses);
            Connection.Response res = null;

            // Get cookies again if lost
            res = Jsoup.connect(getBaseURL()).userAgent(userAgent).timeout(60000).method(Connection.Method.GET).execute();
            if (res == null) {
                logger.warn("Unable to get EHB groups from site!");
            }

            Document docCookieFetch = res.parse();
            if (docCookieFetch == null) {
                logger.warn("Unable to get EHB groups from site!");
            }

            // Required for cookie saving
            String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
            String EVENTVALIDATION = (docCookieFetch.getElementById("__EVENTVALIDATION").attr("value"));
            Map<String, String> cookies = res.cookies();

            res = Jsoup.connect(getBaseURL()).maxBodySize(12000000).userAgent(userAgent).timeout(60000).data("__EVENTTARGET", "LinkBtn_StudentSets")
                    .data("__EVENTARGUMENT", "").data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
                    .data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "ProgrammesOfStudy")
                    .data("dlFilter", "").data("tWildcard", "").data("lbWeeks", "t").data("lbDays", "1-7")
                    .data("dlPeriod", "1-56").data("RadioType", "Individual%3Bswsurl%3BSWS_EHB_IND").cookies(cookies)
                    .method(Connection.Method.POST).followRedirects(true).execute();
            Document doc = res.parse();
            if (doc == null) {
                logger.warn("Unable to get subjects from site [#1]!");
                return false;
            }


            VIEWSTATE = doc.getElementById("__VIEWSTATE").attr("value");
            EVENTVALIDATION = doc.getElementById("__EVENTVALIDATION").attr("value");
            Connection conn = Jsoup.connect(getBaseURL()).maxBodySize(12000000).userAgent(userAgent).timeout(60000).data("__EVENTTARGET", "")
                    .data("__EVENTARGUMENT", "").data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
                    .data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "StudentSets").data("dlFilter", "").data("tWildcard", "").data("lbWeeks", getWeeks(semester)).data("lbDays", "1-7")
                    .data("dlPeriod", "1-56").data("RadioType", "TextSpreadsheet;swsurl;SWS_EHB_TS")
                    .data("bGetTimetable", "Toon+rooster").cookies(cookies).method(Connection.Method.POST);
            for (Course course : courses) {
                conn.data("dlObject", course.getId());
            }

            res = conn.execute();
            doc = res.parse();
            if (doc == null) {
                logger.warn("Unable to get timetable from site [#2]!");
                return false;
            }

            logger.info("Downloading timetables ...");
            HtmlResponse getResponse = HtmlUtils.sendGetRequest(getBaseTimeTableURL(), cookies, 120000);
            logger.info("Parsing timetables ...");
            Document timeTableDoc = Jsoup.parse(getResponse.getSource());
            List<Element> dayTables = timeTableDoc.getElementsByClass("spreadsheet");
            List<Element> subjectTitleElements = timeTableDoc.getElementsByClass("header-2-0-1");
            String startWeek = timeTableDoc.getElementsByClass("header-2-0-5").first().html();
            if (timeTable.getStartTimeStamp() == 0) {
                timeTable.setStartTimeStamp(formatter.parse(startWeek).getTime() / 1000);
            }
            for (int subjectNr = 0; subjectNr < subjectTitleElements.size(); subjectNr++) {
                Course course = courses.get(subjectNr);
                if (course != null) {
                    List<Activity> parsedActivities = parseTimeTable(dayTables,subjectNr,timeTable);
                    for (Activity activity : parsedActivities){
                        activity.addCourse(course);
                        addActivity(activity);
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            logger.warn("Unable to get timetables!");
            ex.printStackTrace();
            return false;
        }
    }

    public boolean fetchStaffTimeTable(List<StaffMember> staffMembers, TimeTable timeTable, int semester) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("NL"));
        // Set GMT timezone to avoid problems with daylight savings
        // Clients/frontends should deal with this depending on their location
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            Collections.sort(staffMembers);
            Connection.Response res = null;

            // Get cookies again if lost
            res = Jsoup.connect(getBaseURL()).userAgent(userAgent).timeout(60000).method(Connection.Method.GET).execute();
            if (res == null) {
                logger.warn("Unable to get EHB staff from site!");
            }

            Document docCookieFetch = res.parse();
            if (docCookieFetch == null) {
                logger.warn("Unable to get EHB staff from site!");
            }

            // Required for cookie saving
            String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
            String EVENTVALIDATION = (docCookieFetch.getElementById("__EVENTVALIDATION").attr("value"));
            Map<String, String> cookies = res.cookies();

            res = Jsoup.connect(getBaseURL()).maxBodySize(12000000).userAgent(userAgent).timeout(60000).data("__EVENTTARGET", "LinkBtn_Staff")
                    .data("__EVENTARGUMENT", "").data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
                    .data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "ProgrammesOfStudy")
                    .data("dlFilter", "").data("tWildcard", "").data("lbWeeks", "t").data("lbDays", "1-7")
                    .data("dlPeriod", "1-56").data("RadioType", "Individual%3Bswsurl%3BSWS_EHB_IND").cookies(cookies)
                    .method(Connection.Method.POST).followRedirects(true).execute();
            Document doc = res.parse();
            if (doc == null) {
                logger.warn("Unable to get subjects from site [#1]!");
                return false;
            }


            VIEWSTATE = doc.getElementById("__VIEWSTATE").attr("value");
            EVENTVALIDATION = doc.getElementById("__EVENTVALIDATION").attr("value");
            Connection conn = Jsoup.connect(getBaseURL()).maxBodySize(12000000).userAgent(userAgent).timeout(60000).data("__EVENTTARGET", "")
                    .data("__EVENTARGUMENT", "").data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
                    .data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "Staff").data("dlFilter", "").data("tWildcard", "").data("lbWeeks", getWeeks(semester)).data("lbDays", "1-7")
                    .data("dlPeriod", "1-56").data("RadioType", "TextSpreadsheet;swsurl;SWS_EHB_TS")
                    .data("bGetTimetable", "Toon+rooster").cookies(cookies).method(Connection.Method.POST);
            for (StaffMember staffMember : staffMembers) {
                conn.data("dlObject", staffMember.getId());
            }

            res = conn.execute();
            doc = res.parse();
            if (doc == null) {
                logger.warn("Unable to get timetable from site [#2]!");
                return false;
            }

            logger.info("Downloading timetables ...");
            HtmlResponse getResponse = HtmlUtils.sendGetRequest(getBaseTimeTableURL(), cookies, 120000);
            logger.info("Parsing timetables ...");
            Document timeTableDoc = Jsoup.parse(getResponse.getSource());
            List<Element> dayTables = timeTableDoc.getElementsByClass("spreadsheet");
            List<Element> subjectTitleElements = timeTableDoc.getElementsByClass("header-2-0-1");
            for (int staffNr = 0; staffNr < subjectTitleElements.size(); staffNr++) {
                StaffMember staffMember = staffMembers.get(staffNr);
                if (staffMember != null) {
                    List<Activity> parsedActivities = parseTimeTable(dayTables,staffNr,timeTable);
                    for (Activity activity : parsedActivities){
                        addActivity(activity);
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            logger.warn("Unable to get timetables!");
            ex.printStackTrace();
            return false;
        }
    }

    public boolean fetchClassRoomTimeTable(List<ClassRoom> classRoomList, TimeTable timeTable, int semester) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("NL"));
        // Set GMT timezone to avoid problems with daylight savings
        // Clients/frontends should deal with this depending on their location
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            Collections.sort(classRoomList);
            Connection.Response res = null;

            // Get cookies again if lost
            res = Jsoup.connect(getBaseURL()).userAgent(userAgent).timeout(60000).method(Connection.Method.GET).execute();
            if (res == null) {
                logger.warn("Unable to get EHB classrooms from site!");
            }

            Document docCookieFetch = res.parse();
            if (docCookieFetch == null) {
                logger.warn("Unable to get EHB classrooms from site!");
            }

            // Required for cookie saving
            String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
            String EVENTVALIDATION = (docCookieFetch.getElementById("__EVENTVALIDATION").attr("value"));
            Map<String, String> cookies = res.cookies();

            res = Jsoup.connect(getBaseURL()).maxBodySize(12000000).userAgent(userAgent).timeout(60000).data("__EVENTTARGET", "LinkBtn_Locations")
                    .data("__EVENTARGUMENT", "").data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
                    .data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "ProgrammesOfStudy")
                    .data("dlFilter", "").data("tWildcard", "").data("lbWeeks", "t").data("lbDays", "1-7")
                    .data("dlPeriod", "1-56").data("RadioType", "Individual%3Bswsurl%3BSWS_EHB_IND").cookies(cookies)
                    .method(Connection.Method.POST).followRedirects(true).execute();
            Document doc = res.parse();
            if (doc == null) {
                logger.warn("Unable to get subjects from site [#1]!");
                return false;
            }


            VIEWSTATE = doc.getElementById("__VIEWSTATE").attr("value");
            EVENTVALIDATION = doc.getElementById("__EVENTVALIDATION").attr("value");
            Connection conn = Jsoup.connect(getBaseURL()).maxBodySize(12000000).userAgent(userAgent).timeout(60000).data("__EVENTTARGET", "")
                    .data("__EVENTARGUMENT", "").data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
                    .data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "Locations").data("dlFilter", "").data("tWildcard", "").data("lbWeeks", getWeeks(semester)).data("lbDays", "1-7")
                    .data("dlPeriod", "1-56").data("RadioType", "TextSpreadsheet;swsurl;SWS_EHB_TS")
                    .data("bGetTimetable", "Toon+rooster").cookies(cookies).method(Connection.Method.POST);
            for (ClassRoom classRoom: classRoomList) {
                conn.data("dlObject", classRoom.getId());
            }

            res = conn.execute();
            doc = res.parse();
            if (doc == null) {
                logger.warn("Unable to get timetable from site [#2]!");
                return false;
            }

            logger.info("Downloading timetables ...");
            HtmlResponse getResponse = HtmlUtils.sendGetRequest(getBaseTimeTableURL(), cookies, 120000);
            logger.info("Parsing timetables ...");
            Document timeTableDoc = Jsoup.parse(getResponse.getSource());
            List<Element> dayTables = timeTableDoc.getElementsByClass("spreadsheet");
            List<Element> subjectTitleElements = timeTableDoc.getElementsByClass("header-2-0-1");
            for (int classNr = 0; classNr < subjectTitleElements.size(); classNr++) {
                ClassRoom classRoom = classRoomList.get(classNr);
                if (classRoom != null) {
                    List<Activity> parsedActivities = parseTimeTable(dayTables,classNr,timeTable);
                    for (Activity activity : parsedActivities){
                        addActivity(activity);
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            logger.warn("Unable to get timetables!");
            ex.printStackTrace();
            return false;
        }
    }

    public List<Activity> parseTimeTable(List<Element> dayTables,int nr, TimeTable timeTable){
        List<Activity> activityList = new ArrayList<>();
        // Voor elke dag
        for (int i = 1; i <= 7; i++) {
            Element dayTable = dayTables.get(i - 1 + (nr * 7));
            if (dayTable.getElementsByTag("tbody").size() != 0) {
                Element tbodyElement = dayTable.getElementsByTag("tbody").first();
                List<Element> rows = tbodyElement.getElementsByTag("tr");
                // Een dag kan meerdere rijen hebben naargelang overlappingen
                for (int row = 1; row < rows.size(); row++) {
                    Element rowElement = rows.get(row);
                    List<Element> columnElements = rowElement.getElementsByTag("td");
                    if (columnElements.size() == 9) {
                        String activity = columnElements.get(0).text();
                        String lessonForm = columnElements.get(1).text();
                        String begin = columnElements.get(2).text();
                        String end = columnElements.get(3).text();
                        if (!begin.contains(":") || !end.contains(":")) {
                            logger.error("ERROR: " + activity + " NPE BEGIN/END DATE");
                        }
                        String weeks = columnElements.get(5).text();
                        String staff = columnElements.get(6).text();
                        String classRoom = columnElements.get(7).text();
                        String groupsString = columnElements.get(8).text();
                        List<Integer> weeksList = parseWeeks(weeks, new ArrayList<>());
                        for (int week : weeksList) {
                            Activity activityObj = new Activity(activity, classRoom);
                            activityObj.setLessonForm(lessonForm);
                            activityObj.setWeeksLabel(weeks);
                            activityObj.setBeginTime(begin);
                            activityObj.setEndTime(end);
                            activityObj.setDay(i);
                            activityObj.setStaff(staff);
                            activityObj.setGroupsString(groupsString);
                            activityObj.setWeek(week);
                            // Get the start unix time of the week
                            // It is the start week (fex 19 sep 2016) + week
                            long weekStart = timeTable.getStartTimeStamp() + ((60 * 60 * 24 * 7) * (week - 1));
                            // Get the day start time: week + day
                            long dayStart = weekStart + ((60 * 60 * 24) * (i - 1));
                            // Get the start time of the activity: day + 8:00 + every half hour
                            String[] beginSplit = begin.split(":");
                            String[] endSplit = end.split(":");
                            long startTime = dayStart + (Integer.parseInt(beginSplit[0]) * 60 * 60) + (Integer.parseInt(beginSplit[1]) * 60);
                            long endTime = dayStart + (Integer.parseInt(endSplit[0]) * 60 * 60) + (Integer.parseInt(endSplit[1]) * 60);

                            activityObj.setBeginTimeUnix(startTime);
                            activityObj.setEndTimeUnix(endTime);

                            activityList.add(activityObj);
                        }
                    }
                }
            }
        }
        return activityList;
    }

    public boolean fetchGroupTimeTable(List<StudentGroup> studentGroups, TimeTable timeTable,int semester) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("NL"));
        // Set GMT timezone to avoid problems with daylight savings
        // Clients/frontends should deal with this depending on their location
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            Collections.sort(studentGroups);
            Connection.Response res = null;

            // Get cookies again if lost
            res = Jsoup.connect(getBaseURL()).userAgent(userAgent).timeout(60000).method(Connection.Method.GET).execute();
            if (res == null) {
                logger.warn("Unable to get EHB group from site!");
            }

            Document docCookieFetch = res.parse();
            if (docCookieFetch == null) {
                logger.warn("Unable to get EHB group from site!");
            }

            // Required for cookie saving
            String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
            String EVENTVALIDATION = (docCookieFetch.getElementById("__EVENTVALIDATION").attr("value"));
            Map<String, String> cookies = res.cookies();

            res = Jsoup.connect(getBaseURL()).maxBodySize(12000000).userAgent(userAgent).timeout(60000).data("__EVENTTARGET", "LinkBtn_StudentSetGroups")
                    .data("__EVENTARGUMENT", "").data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
                    .data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "ProgrammesOfStudy")
                    .data("dlFilter", "").data("tWildcard", "").data("lbWeeks", "t").data("lbDays", "1-7")
                    .data("dlPeriod", "1-56").data("RadioType", "Individual%3Bswsurl%3BSWS_EHB_IND").cookies(cookies)
                    .method(Connection.Method.POST).followRedirects(true).execute();
            Document doc = res.parse();
            if (doc == null) {
                logger.warn("Unable to get groups from site [#1]!");
                return false;
            }


            VIEWSTATE = doc.getElementById("__VIEWSTATE").attr("value");
            EVENTVALIDATION = doc.getElementById("__EVENTVALIDATION").attr("value");
            Connection conn = Jsoup.connect(getBaseURL()).maxBodySize(12000000).userAgent(userAgent).timeout(60000).data("__EVENTTARGET", "")
                    .data("__EVENTARGUMENT", "").data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
                    .data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "StudentSetGroups").data("dlFilter", "").data("tWildcard", "").data("lbWeeks", getWeeks(semester)).data("lbDays", "1-7")
                    .data("dlPeriod", "1-56").data("RadioType", "TextSpreadsheet;swsurl;SWS_EHB_TS")
                    .data("bGetTimetable", "Toon+rooster").cookies(cookies).method(Connection.Method.POST);
            for (StudentGroup studentGroup : studentGroups) {
                conn.data("dlObject", studentGroup.getId());
            }

            res = conn.execute();
            doc = res.parse();
            if (doc == null) {
                logger.warn("Unable to get timetable from site [#2]!");
                return false;
            }

            logger.info("Downloading timetables ...");
            HtmlResponse getResponse = HtmlUtils.sendGetRequest(getBaseTimeTableURL(), cookies, 120000);
            logger.info("Parsing timetables ...");
            Document timeTableDoc = Jsoup.parse(getResponse.getSource());
            List<Element> dayTables = timeTableDoc.getElementsByClass("spreadsheet");
            List<Element> subjectTitleElements = timeTableDoc.getElementsByClass("header-2-0-1");
            for (int groupNr = 0; groupNr < subjectTitleElements.size(); groupNr++) {
                StudentGroup studentGroup = studentGroups.get(groupNr);
                if (studentGroup != null) {
                    List<Activity> parsedActivities = parseTimeTable(dayTables,groupNr,timeTable);
                    CourseServer courseServer = ServiceProvider.getCourseServer();
                    List<Activity> courseLessActivities = new ArrayList<>();
                    for (Activity activity : parsedActivities) {
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
                        if (names.getValue() > 1) {
                            Course course = new Course(names.getKey());
                            course.setDirty(true);
                            course.setLastUpdate(System.currentTimeMillis() / 1000);
                            course.setLastSync(System.currentTimeMillis() / 1000);
                            course = ServiceProvider.getCourseServer().createCourse(course);
                            if (!studentGroup.getCourses().contains(course)){
                                studentGroup.getCourses().add(course);
                            }
                        }
                    }
                    for (Activity activity : courseLessActivities) {
                        // Try to match it to an existing course
                        Course course = courseServer.findCourseByName(activity.getName(), true);
                        if (course != null) {
                            activity.getCourses().add(course);
                        }
                    }
                    for (Activity activity : parsedActivities){
                        activity.addGroup(studentGroup);
                        addActivity(activity);
                    }
                    int idx = studentGroups.indexOf(studentGroup);
                    studentGroups.remove(studentGroup);
                    studentGroups.add(idx,ServiceProvider.getStudentGroupServer().createStudentGroup(studentGroup));
                }
            }
            return true;
        } catch (Exception ex) {
            logger.warn("Unable to get timetables!");
            ex.printStackTrace();
            return false;
        }
    }

    public String getWeeks(int semester){
        if (semester == 1){
            return "1;2;3;4;5;6;7;8;9;10;11;12;13;14;15;16;17;18;19";
        }else{
            return "20;21;22;23;24;25;26;27;28;29;30;31;32;33;34;35;36;37;38;39;40";
        }
    }
}
