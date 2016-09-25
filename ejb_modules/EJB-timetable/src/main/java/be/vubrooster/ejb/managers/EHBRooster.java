package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.*;
import be.vubrooster.ejb.beans.TimeTableServerBean;
import be.vubrooster.ejb.service.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * EHBooster
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
        setStaffManager(new EHBStaffManager(ServiceProvider.getStaffServer()));
        setClassRoomManager(new EHBClassRoomManager(ServiceProvider.getClassRoomServer()));
    }

    @Override
    public void sync() {
        StudyProgramServer studyProgramServer = ServiceProvider.getStudyProgramServer();
        StudentGroupServer studentGroupServer = ServiceProvider.getStudentGroupServer();
        logger.info("Loading courses ...");
        CourseServer courseServer = ServiceProvider.getCourseServer();
        courseServer.loadCourses();
        logger.info("Saving courses to database ...");
        courseServer.saveCourses();
        logger.info("Loading study programmes ...");
        studyProgramServer.loadStudyProgrammes();
        logger.info("Loading student groups");
        studentGroupServer.loadStudentGroups();
        logger.info("Saving study programmes to database ...");
        studyProgramServer.saveStudyProgrammes();
        logger.info("Saving student groups to database ...");
        studentGroupServer.saveStudentGroups();
        logger.info("Assigning courses to groups ...");
        studentGroupServer.assignCoursesToGroups();
        logger.info("Extracting staff members from activities ...");
        StaffServer staffServer = ServiceProvider.getStaffServer();
        staffServer.loadStaff();
        logger.info("Saving staff to database ...");
        staffServer.saveStaff();
        logger.info("Extracting classrooms from activities ...");
        ClassRoomServer classRoomServer = ServiceProvider.getClassRoomServer();
        classRoomServer.loadClassRooms();;
        logger.info("Saving classrooms to database ...");
        classRoomServer.saveClassRooms();
        logger.info("Loading timetables for all courses ...");
        ActivitiyServer activitiyServer = ServiceProvider.getActivitiyServer();
        Future<Void> activityLoadFuture = activitiyServer.loadActivitiesForCourses(true);
        while (!activityLoadFuture.isDone()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("Loading timetables for all extracted teachers ...");
        activityLoadFuture = activitiyServer.loadActivitiesForStaff(false);
        while (!activityLoadFuture.isDone()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("Loading timetables for all extracted classrooms ...");
        activityLoadFuture = activitiyServer.loadActivitiesForClassRooms(false);
        while (!activityLoadFuture.isDone()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("Loading timetables for all groups...");
        activityLoadFuture = activitiyServer.loadActivitiesForGroups(false);
        while (!activityLoadFuture.isDone()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public long getSyncTimeout() {
        return 70;
    }

    @Override
    public long getSyncInterval() {
        return 25;
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
