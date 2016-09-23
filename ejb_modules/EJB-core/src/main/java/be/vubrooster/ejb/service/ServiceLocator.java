package be.vubrooster.ejb.service;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Maxim Van de Wynckel
 * @date 12-May-16
 */
public class ServiceLocator {
    public static String COMMONS_SERVER = "java:global/EAR-vubrooster/EJB-core/CommonsServerBean";
    public static String ACTIVITY_SERVER = "java:global/EAR-vubrooster/EJB-timetable/ActivityServerBean";
    public static String COURSE_SERVER = "java:global/EAR-vubrooster/EJB-timetable/CourseServerBean";
    public static String FACULTY_SERVER = "java:global/EAR-vubrooster/EJB-timetable/FacultyServerBean";
    public static String STUDENTGROUP_SERVER = "java:global/EAR-vubrooster/EJB-timetable/StudentGroupServerBean";
    public static String STUDYPROGRAM_SERVER = "java:global/EAR-vubrooster/EJB-timetable/StudyProgramServerBean";
    public static String TIMETABLE_SERVER = "java:global/EAR-vubrooster/EJB-timetable/TimeTableServerBean";
    public static String SYNC_SERVER = "java:global/EAR-vubrooster/EJB-timetable/SyncServerBean";
    public static String CONFIGURATION_SERVER = "java:global/EAR-vubrooster/EJB-config/ConfigurationServerBean";
    public static String TWITTER_SERVER = "java:global/EAR-vubrooster/EJB-twitter/TwitterServerBean";
    public static String USER_SERVER = "java:global/EAR-vubrooster/EJB-user/UserServerBean";
    public static String GCAL_SERVER = "java:global/EAR-vubrooster/EJB-user/GoogleCalendarServerBean";
    public static String CLASSROOM_SERVER = "java:global/EAR-vubrooster/EJB-timetable/ClassRoomServerBean";
    public static String STAFF_SERVER = "java:global/EAR-vubrooster/EJB-timetable/StaffServerBean";

    public static Object doLookup(String name){
        try {
            return InitialContext.doLookup(name);
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
