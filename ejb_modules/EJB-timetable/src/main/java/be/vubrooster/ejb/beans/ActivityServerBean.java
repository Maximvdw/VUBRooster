package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.TimeTableServer;
import be.vubrooster.ejb.enums.ActivityChangeType;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.models.*;
import be.vubrooster.ejb.service.ServiceProvider;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ActivityServerBean
 * <p>
 * Created by maxim on 20-Sep-16.
 */
@Startup
@Remote(ActivitiyServer.class)
@Singleton(mappedName = "ActivityServer")
public class ActivityServerBean implements ActivitiyServer {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(ActivityServerBean.class);

    @PersistenceContext(name = "vubrooster")
    private EntityManager entityManager;
    private Session session = null;

    // Cache
    private List<Activity> activityList = new ArrayList<>();

    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    @PostConstruct
    public void init() {

    }

    @Override
    public List<Activity> findActivities(boolean useCache) {
        if (activityList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findActivities");
            return query.list();
        } else {
            // Use cache
            return activityList;
        }
    }

    @Override
    public List<Activity> findAllActivitiesForStaffMember(StaffMember member) {
        Query query = getSession().getNamedQuery("findAllActivitiesForStaffMember");
        query.setParameter("staff", member.getId());
        return query.list();
    }

    @Override
    public List<Activity> findAllActivitiesForClassRoom(ClassRoom classRoom) {
        Query query = getSession().getNamedQuery("findAllActivitiesForClassRoom");
        query.setParameter("classRoom", classRoom.getId());
        return query.list();
    }

    @Override
    public List<Activity> findAllActivitiesForStudentGroup(StudentGroup group) {
        Query query = getSession().getNamedQuery("findAllActivitiesForStudentGroup");
        query.setParameter("studentGroup", group.getId());
        return query.list();
    }

    @Override
    public int getActivitiesCount(boolean useCache) {
        return activityList.size();
    }

    @Override
    public Activity findActivityById(int id, boolean useCache) {
        if (activityList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findActivityById");
            query.setParameter("id", id);
            return (Activity) query.uniqueResult();
        } else {
            // Use cache
            for (Activity activity : activityList) {
                if (activity.getId() == id) {
                    return activity;
                }
            }
            return null;
        }
    }

    @Override
    public List<Activity> findActivitiesByName(String name, boolean useCache) {
        return null;
    }

    @Override
    public Activity createActivity(Activity activity) {
        return (Activity) getSession().merge(activity);
    }

    @Override
    public List<Activity> saveActivities(List<Activity> activities, Sync sync) {
        TimeTableServer timeTableServer = ServiceProvider.getTimeTableServer();
        TimeTable currentTimeTable = timeTableServer.getCurrentTimeTable();

        List<Activity> removedActivities = new ArrayList<>();
        List<Activity> savedActivities = new ArrayList<>();

        boolean error = false;
        try {
            // Save and remove the activities
            for (Activity activity : activities) {
                if (activity != null) {
                    if (activity.isDirty()) {
                        try {
                            activity.setLastUpdate(System.currentTimeMillis() / 1000);
                            activity.setDirty(false);
                            logger.info("Added activity: " + activity.getName() + " [" + activity.getBeginTimeUnix() + "]");
                            savedActivities.add(entityManager.merge(activity));
                        } catch (Exception ex) {
                            logger.error("Unable to sync activity!");
                            ex.printStackTrace();
                            error = true;
                        }
                    }
                    if (activity.getLastSync() < currentTimeTable.getLastSync()) {
                        // Removed activity
                        try {
                            logger.info("Removed activity: " + activity.getName() + " [" + activity.getBeginTimeUnix() + "]");
                            activity.setActive(false);
                            Activity mergedActivity = entityManager.merge(activity);
                            removedActivities.add(mergedActivity);
                        } catch (Exception ex) {
                            logger.error("Unable to sync activity!");
                            ex.printStackTrace();
                            error = true;
                        }
                    }
                }
            }
            // Check for errors
            if (error) {
                ServiceProvider.getTwitterServer().postStatus("!! Sync saving failure " + (System.currentTimeMillis() / 1000) + " (CC @" + ServiceProvider.getConfigurationServer().getString("twitter.owner") + ")");
            }

            // Save models
            if (currentTimeTable != null) {
                currentTimeTable.setLastSync(System.currentTimeMillis() / 1000);
                ServiceProvider.getTimeTableServer().updateTimeTable(entityManager.merge(currentTimeTable));
            }

            if (!ServiceProvider.getTimeTableServer().firstSync()) {
                List<Activity> newActivities = new ArrayList<>(savedActivities);
                logger.info("Checking for activity changes ...");
                // Check for possible activity changes
                for (Activity removedActivity : removedActivities) {
                    // For each removed activity, check if one of the new activities is responsible
                    boolean changeFound = false;
                    for (Activity addedActivity : savedActivities) {
                        if (addedActivity.getName().equalsIgnoreCase(removedActivity.getName())) { // Same name
                            if (addedActivity.getGroupsString().equals(removedActivity.getGroupsString())) { // Same groups
                                // At this point you can be certain that its a change of activity
                                if (addedActivity.getWeek() == removedActivity.getWeek()) { // Same week
                                    if (addedActivity.getDay() == removedActivity.getDay()) { // Same day
                                        if (addedActivity.getBeginTimeUnix() == removedActivity.getBeginTimeUnix()) { // Same time
                                            if (addedActivity.getEndTimeUnix() == removedActivity.getEndTimeUnix()) { // Same end time
                                                if (addedActivity.getClassRoom().equalsIgnoreCase(removedActivity.getClassRoom())) { // Same classroom
                                                    changeFound = false;
                                                } else { // Different location
                                                    ActivityChange change = new ActivityChange();
                                                    change.setRemovedActivity(removedActivity);
                                                    change.setNewActivity(addedActivity);
                                                    newActivities.remove(addedActivity);
                                                    change.setChangeType(ActivityChangeType.LOCATION);
                                                    entityManager.merge(change);
                                                    changeFound = true;
                                                }
                                            } else {
                                                ActivityChange change = new ActivityChange();
                                                change.setRemovedActivity(removedActivity);
                                                change.setNewActivity(addedActivity);
                                                newActivities.remove(addedActivity);
                                                change.setChangeType(ActivityChangeType.ENDTIME);
                                                entityManager.merge(change);
                                                changeFound = true;
                                            }
                                        } else { // Different time
                                            ActivityChange change = new ActivityChange();
                                            change.setNewActivity(addedActivity);
                                            change.setRemovedActivity(removedActivity);
                                            newActivities.remove(addedActivity);
                                            change.setChangeType(ActivityChangeType.BEGINTIME);
                                            entityManager.merge(change);
                                            changeFound = true;
                                        }
                                    } else { // Different day
                                        ActivityChange change = new ActivityChange();
                                        change.setNewActivity(addedActivity);
                                        change.setRemovedActivity(removedActivity);
                                        newActivities.remove(addedActivity);
                                        change.setChangeType(ActivityChangeType.DAY);
                                        entityManager.merge(change);
                                        changeFound = true;
                                    }
                                } else if (Math.abs(addedActivity.getWeek() - removedActivity.getWeek()) < 2) { // One week change
                                    // Moved activity
                                    ActivityChange change = new ActivityChange();
                                    change.setNewActivity(addedActivity);
                                    change.setRemovedActivity(removedActivity);
                                    change.setChangeType(ActivityChangeType.WEEK);
                                    newActivities.remove(addedActivity);
                                    entityManager.merge(change);
                                    changeFound = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!changeFound) {
                        // Removed - no replacement
                        ActivityChange change = new ActivityChange();
                        change.setNewActivity(null);
                        change.setRemovedActivity(removedActivity);
                        change.setChangeType(ActivityChangeType.REMOVED);
                        entityManager.merge(change);
                    }
                }
                for (Activity newActivity : newActivities) {
                    ActivityChange change = new ActivityChange();
                    change.setNewActivity(newActivity);
                    change.setRemovedActivity(null);
                    change.setChangeType(ActivityChangeType.ADDED);
                    entityManager.merge(change);
                }
            }

            // Store sync information
            sync.setAdded(savedActivities.size());
            sync.setRemoved(removedActivities.size());
            if (sync.getRemoved() > 5000) {
                // Possible sync failure
                ServiceProvider.getTwitterServer().postStatus("!! Attention required on last sync: Removed " + sync.getRemoved() + " (CC @" + ServiceProvider.getConfigurationServer().getString("twitter.owner") + ")");
            } else if (sync.getRemoved() > 500) {
                // Possible sync failure
                ServiceProvider.getTwitterServer().postStatus("Possible attention required on last sync: Removed " + sync.getRemoved() + " (CC @" + ServiceProvider.getConfigurationServer().getString("twitter.owner") + ")");
            }
            sync.setActivities(getActivitiesCount(false));
            sync.setCourses(ServiceProvider.getCourseServer().getCoursesCount(false));
            sync.setStudentGroups(ServiceProvider.getStudentGroupServer().getStudentGroupsCount(false));
            sync.setStudyProgrammes(ServiceProvider.getStudyProgramServer().getStudyProgrammesCount(false));
            ServiceProvider.getSyncServer().saveSync(sync); // Save sync
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return savedActivities;
    }

    @Override
    public void saveActivities() {
        activityList = saveActivities(activityList, null);
    }

    @Override
    public void saveActivities(Sync sync) {
        activityList = saveActivities(activityList, sync);
    }

    @Asynchronous
    @AccessTimeout(-1)
    public Future<Void> loadActivitiesForCourses(boolean reloadData) {
        return pool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Reload database
                if (reloadData)
                    activityList = findActivities(false);

                activityList = BaseCore.getInstance().getActivityManager().loadActivitiesForCourses(activityList);
                return null;
            }
        });
    }

    @Asynchronous
    @AccessTimeout(-1)
    public Future<Void> loadActivitiesForGroups(boolean reloadData) {
        return pool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Reload database
                if (reloadData)
                    activityList = findActivities(false);

                activityList = BaseCore.getInstance().getActivityManager().loadActivitiesForGroups(activityList);
                return null;
            }
        });
    }

    @Asynchronous
    @AccessTimeout(-1)
    public Future<Void> loadActivitiesForStaff(boolean reloadData) {
        return pool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Reload database
                if (reloadData)
                    activityList = findActivities(false);

                activityList = BaseCore.getInstance().getActivityManager().loadActivitiesForStaff(activityList);
                return null;
            }
        });
    }

    @Asynchronous
    @AccessTimeout(-1)
    public Future<Void> loadActivitiesForClassRooms(boolean reloadData) {
        return pool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Reload database
                if (reloadData)
                    activityList = findActivities(false);

                activityList = BaseCore.getInstance().getActivityManager().loadActivitiesForClassRooms(activityList);
                return null;
            }
        });
    }

    /**
     * Get hibernate session
     *
     * @return hibernate session
     */
    public Session getSession() {
        if (session != null) {
            if (!session.isOpen()) {
                session = entityManager.unwrap(Session.class);
            }
            return session;
        }
        session = entityManager.unwrap(Session.class);
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * Set entity manager
     *
     * @param em entity manager
     */
    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }
}
