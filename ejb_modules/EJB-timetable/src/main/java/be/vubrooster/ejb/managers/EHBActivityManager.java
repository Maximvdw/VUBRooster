package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.models.Activity;
import be.vubrooster.ejb.models.Course;
import be.vubrooster.ejb.models.TimeTable;
import be.vubrooster.ejb.service.ServiceProvider;
import be.vubrooster.utils.HtmlResponse;
import be.vubrooster.utils.HtmlUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EHBActivityManager
 *
 * Created by maxim on 21-Sep-16.
 */
public class EHBActivityManager extends ActivityManager{
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
    public List<Activity> loadActivities(List<Activity> activityList) {
        super.loadActivities(activityList);
        List<Course> allCourses = ServiceProvider.getCourseServer().findCourses(true);


        List<List<Course>> chunkedCourses = new ArrayList<>();
        while (allCourses.size() != 0){
            List<Course> chunk = new ArrayList<>();
            List<Course> remaining = new ArrayList<>(allCourses);
            for (Course course : remaining){
                if (chunk.size() == 1000){
                    break;
                }
                chunk.add(course);
                allCourses.remove(course);
            }
            chunkedCourses.add(chunk);
        }
        for (List<Course> chunk : chunkedCourses){
            fetchCourseTimeTable(chunk);
        }

        return getActivityList();
    }

    public boolean fetchCourseTimeTable(List<Course> courses) {
        try {
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
                    .data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "StudentSets").data("dlFilter", "").data("tWildcard", "").data("lbWeeks", "1;2;3;4;5;6;7;8;9;10;11;12;13;14;15;16;17;18;19").data("lbDays", "1-7")
                    .data("dlPeriod", "1-56").data("RadioType", "TextSpreadsheet;swsurl;SWS_EHB_TS")
                    .data("bGetTimetable", "Toon+rooster").cookies(cookies).method(Connection.Method.POST);
            for (Course course : courses) {
                conn.data("dlObject", course.getSplusId());
            }

            res = conn.execute();
            doc = res.parse();
            if (doc == null) {
                logger.warn("Unable to get timetable from site [#2]!");
                return false;
            }

            HtmlResponse getResponse = HtmlUtils.sendGetRequest(getBaseTimeTableURL(), cookies, 120000);
            Document timeTableDoc = Jsoup.parse(getResponse.getSource());
            List<Element> dayTables = timeTableDoc.getElementsByClass("spreadsheet");
            List<Element> subjectTitleElements = timeTableDoc.getElementsByClass("header-2-0-1");
            TimeTable timeTable = ServiceProvider.getTimeTableServer().getCurrentTimeTable();
            for (int subjectNr = 0; subjectNr < cookies.size(); subjectNr++) {
                Course course = courses.get(subjectNr);
                subjectTitleElements.get(subjectNr).text();
                for (int i = 1; i <= 7; i++) {
                    Element dayTable = dayTables.get(i - 1 + (subjectNr * 7));
                    if (dayTable.getElementsByTag("tbody").size() != 0) {
                        Element tbodyElement = dayTable.getElementsByTag("tbody").first();
                        List<Element> rows = tbodyElement.getElementsByTag("tr");
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
                                String duration = columnElements.get(4).text();
                                String weeks = columnElements.get(5).text();
                                String lector = columnElements.get(6).text();
                                String classRoom = columnElements.get(7).text();
                                String groupsString = columnElements.get(8).text();
                                Activity activityObj = new Activity(activity,classRoom);
                                addActivity(activityObj);
                            }
                        }
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
}
