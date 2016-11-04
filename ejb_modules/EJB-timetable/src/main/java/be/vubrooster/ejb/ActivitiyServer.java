package be.vubrooster.ejb;

import be.vubrooster.ejb.models.*;

import java.util.List;
import java.util.concurrent.Future;

/**
 * ActivityServer
 *
 * Created by maxim on 20-Sep-16.
 */
public interface ActivitiyServer {

    /**
     * Find activities
     *
     * @param useCache use cache
     * @return list of activities
     */
    List<Activity> findActivities(boolean useCache);

    /**
     * Find all activities for staff member
     *
     * @param member member to get activities for
     * @return list of activities
     */
    List<Activity> findAllActivitiesForStaffMember(StaffMember member, boolean useCache);

    /**
     * Find all activities for user
     * @param selectedGroups selected groups
     * @param hiddenCourses hidden courses
     * @param useCache use cache
     * @return list of activities
     */
    List<Activity> findAllActivitiesForUser(List<StudentGroup> selectedGroups, List<Course> hiddenCourses, boolean useCache);

    /**
     * Find all activities for class room
     * @param classRoom class rom to get activities for
     * @return list of activities
     */
    List<Activity> findAllActivitiesForClassRoom(ClassRoom classRoom, boolean useCache);

    /**
     * Find all activities for student group
     * @param group student group to get activities for
     * @return list of activities
     */
    List<Activity> findAllActivitiesForStudentGroup(StudentGroup group, boolean useCache);


    /**
     * Find week activities for staff member
     *
     * @param member member to get activities for
     * @param week week to get
     * @return list of activities
     */
    List<Activity> findWeekActivitiesForStaffMember(StaffMember member,int week, boolean useCache);

    /**
     * Find week activities for user
     * @param selectedGroups selected groups
     * @param hiddenCourses hidden courses
     * @param useCache use cache
     * @return list of activities
     */
    List<Activity> findWeekActivitiesForUser(List<StudentGroup> selectedGroups, List<Course> hiddenCourses, int week, boolean useCache);

    /**
     * Find week activities for class room
     * @param classRoom class rom to get activities for
     * @param week week to get
     * @return list of activities
     */
    List<Activity> findWeekActivitiesForClassRoom(ClassRoom classRoom,int week, boolean useCache);

    /**
     * Find week activities for student group
     * @param group student group to get activities for
     * @param week week to get
     * @return list of activities
     */
    List<Activity> findWeekActivitiesForStudentGroup(StudentGroup group,int week, boolean useCache);

    /**
     * Get activities count
     *
     * @param useCache use cache
     * @return activities count
     */
    int getActivitiesCount(boolean useCache);

    /**
     * Find faculty by id
     *
     * @param id identifier
     * @param useCache use cache
     * @return faculty if found
     */
    Activity findActivityById(int id, boolean useCache);

    /**
     * Find activities by name
     *
     * @param name name of faculty
     * @param useCache use cache
     * @return list of activities
     */
    List<Activity> findActivitiesByName(String name, boolean useCache);

    /**
     * Create faculty
     *
     * @param activity faculty to save
     * @return saved faculty
     */
    Activity createActivity(Activity activity);

    /**
     * Save activities to database
     *
     * @param activities activities to save
     * @param sync sync instance
     * @return list of activities
     */
    List<Activity> saveActivities(List<Activity> activities, Sync sync);

    /**
     * Save all cached activities to database
     */
    void saveActivities();

    /**
     * Save all cached activities to database
     * @param sync sync to store data
     */
    void saveActivities(Sync sync);

    /**
     * Load activities for groups
     * @return future
     */
    Future loadActivitiesForGroups(boolean reloadData);

    /**
     * Load acitivites for staff
     * @return future
     */
    Future loadActivitiesForStaff(boolean reloadData);

    /**
     * Load activities for classrooms
     * @return future
     */
    Future loadActivitiesForClassRooms(boolean reloadData);

    /**
     * Load activities for faculty
     * @return future
     */
    Future loadActivitiesForCourses(boolean reloadData);

    /**
     * Load activities for study programmes
     * @return future
     */
    Future loadActivitiesForStudyProgrammes(boolean reloadData);

    void reloadCache();

    /**
     * Find activitiy changes for group
     * @param group group to get changes for
     * @param useCache use cache
     * @return list of activity changes
     */
    List<ActivityChange> findActivityChangesForGroup(StudentGroup group, boolean useCache);

    /**
     * Find activity history
     * @param activity activity to get history for
     * @param useCache use cache
     * @return list of activity changes
     */
    ActivityChange findActivityChangeByActivity(Activity activity,boolean useCache);
}
