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
    List<Activity> findAllActivitiesForStaffMember(StaffMember member);

    /**
     * Find all activities for class room
     * @param classRoom class rom to get activities for
     * @return list of activities
     */
    List<Activity> findAllActivitiesForClassRoom(ClassRoom classRoom);

    /**
     * Find all activities for student group
     * @param group student group to get activities for
     * @return list of activities
     */
    List<Activity> findAllActivitiesForStudentGroup(StudentGroup group);

    /**
     * Get activities count
     *
     * @param useCache use cache
     * @return activities count
     */
    int getActivitiesCount(boolean useCache);

    /**
     * Find activity by id
     *
     * @param id identifier
     * @param useCache use cache
     * @return activity if found
     */
    Activity findActivityById(int id, boolean useCache);

    /**
     * Find activities by name
     *
     * @param name name of activity
     * @param useCache use cache
     * @return list of activities
     */
    List<Activity> findActivitiesByName(String name, boolean useCache);

    /**
     * Create activity
     *
     * @param activity activity to save
     * @return saved activity
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
     * Load activities for courses
     * @return future
     */
    Future loadActivitiesForCourses(boolean reloadData);
}
