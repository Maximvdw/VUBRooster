package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.CourseServer;
import be.vubrooster.ejb.models.Course;
import be.vubrooster.ejb.models.StudentGroup;
import be.vubrooster.ejb.service.ServiceProvider;
import be.vubrooster.utils.HtmlResponse;
import be.vubrooster.utils.HtmlUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EHBCourseManager
 * Created by maxim on 21-Sep-16.
 */
public class EHBCourseManager extends CourseManager{
    private static String baseURL = "https://rooster.ehb.be/Scientia/SWS/SYL_PRD_1617/default.aspx";
    private static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0";

    public EHBCourseManager(CourseServer server) {
        super(server);
    }

    public static String getBaseURL() {
        return baseURL;
    }

    public static void setBaseURL(String baseURL) {
        EHBCourseManager.baseURL = baseURL;
    }

    @Override
    public List<Course> loadCourses(List<Course> courseList) {
        super.loadCourses(courseList);

        try {
            Connection.Response res = Jsoup.connect(getBaseURL()).timeout(60000).method(Connection.Method.GET)
                    .execute();
            if (res == null) {
                logger.warn("Unable to get EHB subjects from site!");
                return getCourseList();
            }
            Document docCookieFetch = res.parse();
            if (docCookieFetch == null) {
                logger.warn("Unable to get EHB subjects from site!");
                return getCourseList();
            }

            // Required for cookie saving
            String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
            String EVENTVALIDATION = docCookieFetch.getElementById("__EVENTVALIDATION").attr("value");
            Map<String, String> cookies = res.cookies();
            Document doc = Jsoup.connect(getBaseURL()).maxBodySize(1200000).userAgent(userAgent).timeout(60000)
                    .data("__EVENTTARGET", "LinkBtn_StudentSets").data("__EVENTARGUMENT", "")
                    .data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
                    .data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "ProgrammesOfStudy")
                    .data("dlFilter", "").data("tWildcard", "").data("lbWeeks", "t").data("lbDays", "1-7")
                    .data("pDays", "1-7").data("dlPeriod", "1-56")
                    .data("RadioType", "Individual;swsurl;SWS_EHB_IND").cookies(cookies).post();
            if (doc == null) {
                logger.warn("Unable to get EHB subjects from site!");
                return getCourseList();
            }
            // Jsoup doet lastig met grote lijsten....

            HtmlResponse getResponse = HtmlUtils.sendGetRequest(getBaseURL(), cookies, 60000);
            doc = Jsoup.parse(getResponse.getSource());

            Element selectElement = doc.getElementById("dlObject");
            List<Element> optionElements = selectElement.children();
            for (Element optionElement : optionElements) {
                Course course = new Course(optionElement.text(), optionElement.attr("value"));
                course.setLongName(course.getName());
                String[] nameSplit = course.getName().split("/");
                course.setName(nameSplit[nameSplit.length-1]);
                addCourse(course);
            }
        } catch (Exception ex) {
            logger.warn("Unable to get subjects from site [#3]!");
            ex.printStackTrace();
        }

        return getCourseList();
    }
}
