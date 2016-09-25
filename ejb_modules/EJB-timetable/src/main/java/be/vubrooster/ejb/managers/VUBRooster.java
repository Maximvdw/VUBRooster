package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.*;
import be.vubrooster.ejb.beans.TimeTableServerBean;
import be.vubrooster.ejb.service.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * VUBRooster
 *
 * Created by maxim on 21-Sep-16.
 */
public class VUBRooster extends BaseCore{
    // Logging
    private final Logger logger = LoggerFactory.getLogger(TimeTableServerBean.class);

    public VUBRooster(){
        super();
        setFacultyManager(new VUBFacultyManager(ServiceProvider.getFacultyServer()));
        setStudentGroupManager(new VUBStudentGroupManager(ServiceProvider.getStudentGroupServer()));
        setActivityManager(new VUBActivityManager(ServiceProvider.getActivitiyServer()));
        setCourseManager(new VUBCourseManager(ServiceProvider.getCourseServer()));
        setStudyProgramManager(new VUBStudyProgramManager(ServiceProvider.getStudyProgramServer()));
        setStaffManager(new VUBStaffManager(ServiceProvider.getStaffServer()));
        setClassRoomManager(new VUBClassRoomManager(ServiceProvider.getClassRoomServer()));
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
        Future<Void> activityLoadFuture = activitiyServer.loadActivitiesForGroups(true);
        while (!activityLoadFuture.isDone()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("Assigning courses to groups ...");
        studentGroupServer.assignCoursesToGroups();
        logger.info("Extracting staff members from activities ...");
        StaffServer staffServer = ServiceProvider.getStaffServer();
        staffServer.loadStaff();;
        logger.info("Saving staff to database ...");
        staffServer.saveStaff();
        logger.info("Extracting classrooms from activities ...");
        ClassRoomServer classRoomServer = ServiceProvider.getClassRoomServer();
        classRoomServer.loadClassRooms();;
        logger.info("Saving classrooms to database ...");
        classRoomServer.saveClassRooms();
        logger.info("Loading timetables for all extracted teachers ...");
        activityLoadFuture = activitiyServer.loadActivitiesForStaff(false);
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
        return 7;
    }

    @Override
    public long getSyncInterval() {
        return 30;
    }
}
