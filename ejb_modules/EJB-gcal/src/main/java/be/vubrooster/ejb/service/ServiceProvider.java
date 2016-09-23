package be.vubrooster.ejb.service;


import be.vubrooster.ejb.*;

/**
 * ServiceProvider
 *
 * @author Maxim Van de Wynckel
 * @date 16-Apr-16
 */
public class ServiceProvider {
    private static CommonsServer commonsServer;
    private static ActivitiyServer activitiyServer;
    private static CourseServer courseServer;
    private static FacultyServer facultyServer;
    private static StudyProgramServer studyProgramServer;
    private static StudentGroupServer studentGroupServer;
    private static SyncServer syncServer;
    private static TimeTableServer timeTableServer;
    private static ConfigurationServer configurationServer;
    private static StaffServer staffServer;
    private static ClassRoomServer classRoomServer;

    /**
     * Get the commons server
     *
     * @return Commons server
     */
    public static CommonsServer getCommonsServer() {
        if (commonsServer == null) {
            commonsServer = (CommonsServer) ServiceLocator.doLookup(ServiceLocator.COMMONS_SERVER);
        }
        return commonsServer;
    }

    /**
     * Get the activity server
     *
     * @return Activity server
     */
    public static ActivitiyServer getActivitiyServer() {
        if (activitiyServer == null) {
            activitiyServer = (ActivitiyServer) ServiceLocator.doLookup(ServiceLocator.ACTIVITY_SERVER);
        }
        return activitiyServer;
    }

    /**
     * Get the student group server
     *
     * @return Student Group server
     */
    public static StudentGroupServer getStudentGroupServer() {
        if (studentGroupServer == null) {
            studentGroupServer = (StudentGroupServer) ServiceLocator.doLookup(ServiceLocator.STUDENTGROUP_SERVER);
        }
        return studentGroupServer;
    }

    /**
     * Get the study program server
     *
     * @return Study program server
     */
    public static StudyProgramServer getStudyProgramServer() {
        if (studyProgramServer == null) {
            studyProgramServer = (StudyProgramServer) ServiceLocator.doLookup(ServiceLocator.STUDYPROGRAM_SERVER);
        }
        return studyProgramServer;
    }

    /**
     * Get the faculty server
     *
     * @return faculty server
     */
    public static FacultyServer getFacultyServer() {
        if (facultyServer == null) {
            facultyServer = (FacultyServer) ServiceLocator.doLookup(ServiceLocator.FACULTY_SERVER);
        }
        return facultyServer;
    }

    /**
     * Get the course server
     *
     * @return course server
     */
    public static CourseServer getCourseServer() {
        if (courseServer == null) {
            courseServer = (CourseServer) ServiceLocator.doLookup(ServiceLocator.COURSE_SERVER);
        }
        return courseServer;
    }

    /**
     * Get the timetable server
     *
     * @return timetable server
     */
    public static TimeTableServer getTimeTableServer() {
        if (timeTableServer == null) {
            timeTableServer = (TimeTableServer) ServiceLocator.doLookup(ServiceLocator.TIMETABLE_SERVER);
        }
        return timeTableServer;
    }

    /**
     * Get the sync server
     *
     * @return sync server
     */
    public static SyncServer getSyncServer() {
        if (syncServer == null) {
            syncServer = (SyncServer) ServiceLocator.doLookup(ServiceLocator.SYNC_SERVER);
        }
        return syncServer;
    }

    /**
     * Get the configuration server
     *
     * @return configuration server
     */
    public static ConfigurationServer getConfigurationServer() {
        if (configurationServer == null) {
            configurationServer = (ConfigurationServer) ServiceLocator.doLookup(ServiceLocator.CONFIGURATION_SERVER);
        }
        return configurationServer;
    }

    /**
     * Get the staff server
     * @return staff server
     */
    public static StaffServer getStaffServer() {
        if (staffServer == null) {
            staffServer = (StaffServer) ServiceLocator.doLookup(ServiceLocator.STAFF_SERVER);
        }
        return staffServer;
    }

    /**
     * Get the class room server
     * @return class room server
     */
    public static ClassRoomServer getClassRoomServer() {
        if (classRoomServer == null) {
            classRoomServer = (ClassRoomServer) ServiceLocator.doLookup(ServiceLocator.CLASSROOM_SERVER);
        }
        return classRoomServer;
    }

}
