package be.vubrooster.ejb.managers;

/**
 * BaseCore
 *
 * Created by maxim on 21-Sep-16.
 */
public abstract class BaseCore {
    // Managers
    private FacultyManager facultyManager = null;
    private StudyProgramManager studyProgramManager = null;
    private StudentGroupManager studentGroupManager = null;
    private CourseManager courseManager = null;
    private ActivityManager activityManager = null;
    private StaffManager staffManager = null;
    private ClassRoomManager classRoomManager = null;
    private DayMenuManager dayMenuManager = null;
    private static BaseCore instance = null;
    private String directory = "";

    public BaseCore(){
        instance = this;
    }

    public abstract void sync();

    public abstract void fastSync();

    public abstract String getDirectory();

    public static BaseCore getInstance() {
        return instance;
    }

    public static void setInstance(BaseCore instance) {
        BaseCore.instance = instance;
    }

    public FacultyManager getFacultyManager() {
        return facultyManager;
    }

    public void setFacultyManager(FacultyManager facultyManager) {
        this.facultyManager = facultyManager;
    }

    public StudyProgramManager getStudyProgramManager() {
        return studyProgramManager;
    }

    public void setStudyProgramManager(StudyProgramManager studyProgramManager) {
        this.studyProgramManager = studyProgramManager;
    }

    public StudentGroupManager getStudentGroupManager() {
        return studentGroupManager;
    }

    public void setStudentGroupManager(StudentGroupManager studentGroupManager) {
        this.studentGroupManager = studentGroupManager;
    }

    public CourseManager getCourseManager() {
        return courseManager;
    }

    public void setCourseManager(CourseManager courseManager) {
        this.courseManager = courseManager;
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    public StaffManager getStaffManager() {
        return staffManager;
    }

    public void setStaffManager(StaffManager staffManager) {
        this.staffManager = staffManager;
    }

    public ClassRoomManager getClassRoomManager() {
        return classRoomManager;
    }

    public void setClassRoomManager(ClassRoomManager classRoomManager) {
        this.classRoomManager = classRoomManager;
    }

    /**
     * Sync timeout in seconds
     * @return sync timeout
     */
    public abstract long getSyncTimeout();

    /**
     * Get sync interval
     * @return sync interval in minutes
     */
    public abstract long getSyncInterval();

    public DayMenuManager getDayMenuManager() {
        return dayMenuManager;
    }

    public void setDayMenuManager(DayMenuManager dayMenuManager) {
        this.dayMenuManager = dayMenuManager;
    }
}
