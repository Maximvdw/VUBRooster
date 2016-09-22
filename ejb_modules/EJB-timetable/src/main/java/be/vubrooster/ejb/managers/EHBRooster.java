package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.CourseServer;
import be.vubrooster.ejb.StudentGroupServer;
import be.vubrooster.ejb.StudyProgramServer;
import be.vubrooster.ejb.beans.TimeTableServerBean;
import be.vubrooster.ejb.service.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EHBRooster
 * Created by maxim on 21-Sep-16.
 */
public class EHBRooster extends BaseCore {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(TimeTableServerBean.class);

    private static String baseURL = "https://rooster.ehb.be/Scientia/SWS/SYL_PRD_1617/default.aspx";
    private static String baseTimeTableURL = "https://rooster.ehb.be/Scientia/SWS/SYL_PRD_1617/showtimetable.aspx";
    private static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0";

    public EHBRooster() {
        super();
        setFacultyManager(new EHBFacultyManager(ServiceProvider.getFacultyServer()));
        setStudentGroupManager(new EHBStudentGroupManager(ServiceProvider.getStudentGroupServer()));
        setActivityManager(new EHBActivityManager(ServiceProvider.getActivitiyServer()));
        setCourseManager(new EHBCourseManager(ServiceProvider.getCourseServer()));
        setStudyProgramManager(new EHBStudyProgramManager(ServiceProvider.getStudyProgramServer()));
    }

    @Override
    public void sync() {
        StudyProgramServer studyProgramServer = ServiceProvider.getStudyProgramServer();
        StudentGroupServer studentGroupServer = ServiceProvider.getStudentGroupServer();
        logger.info("Loading student groups");
        studentGroupServer.loadStudentGroups();
        logger.info("Loading study programmes ...");
        studyProgramServer.loadStudyProgrammes();
        logger.info("Saving study programmes to database ...");
        studyProgramServer.saveStudyProgrammes();
        logger.info("Saving student groups to database ...");
        studentGroupServer.saveStudentGroups();

        logger.info("Loading courses ...");
        CourseServer courseServer = ServiceProvider.getCourseServer();
        courseServer.loadCourses();
        logger.info("Saving courses to database ...");
        courseServer.saveCourses();
        logger.info("Loading timetables for all student groups ...");
        ActivitiyServer activitiyServer = ServiceProvider.getActivitiyServer();
        activitiyServer.loadActivities();
        logger.info("Assigning courses to groups ...");
        studentGroupServer.assignCoursesToGroups();
    }

    public static String getBaseURL() {
        return baseURL;
    }

    public static void setBaseURL(String baseURL) {
        EHBRooster.baseURL = baseURL;
    }

    public static String getBaseTimeTableURL() {
        return baseTimeTableURL;
    }

    public static void setBaseTimeTableURL(String baseTimeTableURL) {
        EHBRooster.baseTimeTableURL = baseTimeTableURL;
    }

    public static String getUserAgent() {
        return userAgent;
    }

    public static void setUserAgent(String userAgent) {
        EHBRooster.userAgent = userAgent;
    }
}
