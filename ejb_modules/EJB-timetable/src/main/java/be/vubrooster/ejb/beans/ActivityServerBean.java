package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.TimeTableServer;
import be.vubrooster.ejb.enums.ActivityChangeType;
import be.vubrooster.ejb.enums.SyncState;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.models.*;
import be.vubrooster.ejb.service.ServiceProvider;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private List<ActivityChange> activityChanges = new ArrayList<>();

    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    @PostConstruct
    public void init() {

    }

    @Override
    public List<Activity> findActivities(boolean useCache) {
        if (!useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findActivities");
            return query.list();
        } else {
            // Use cache
            return activityList;
        }
    }

    private void duplicateFind(Activity activity, List<Activity> activityList) {
        boolean found = false;
        for (Activity duplicateActivity : activityList) {
            if (duplicateActivity.getBeginTimeUnix() == activity.getBeginTimeUnix()) {
                if (duplicateActivity.getName().equalsIgnoreCase(activity.getName())) {
                    Map<String, Object> extraData = duplicateActivity.getExtraData();
                    extraData.put("staff", extraData.get("staff") + ", " + activity.getStaff());
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            Map<String, Object> extraData = new HashMap<>();
            extraData.put("staff", activity.getStaff());
            activity.setExtraData(extraData);
            activityList.add(activity);
        }
    }

    private void staffMerge(List<Object[]> objects, List<Activity> activityList) {
        for (Object[] obj : objects) {
            Activity act = (Activity) obj[0];
            Map<String, Object> extraData = new HashMap<>();
            extraData.put("staff", obj[1]);
            act.setExtraData(extraData);
            activityList.add(act);
        }
    }


    @Override
    public List<Activity> findAllActivitiesForStaffMember(StaffMember member, boolean useCache) {
        if (useCache) {
            List<Activity> activityList = new ArrayList<>();
            List<Activity> cache = new ArrayList<>(this.activityList);
            for (Activity activity : cache) {
                if (activity.getId() != 0) {
                    if (activity.isActive()) {
                        if (activity.getStaff().contains(member.getName())) {
                            duplicateFind(activity, activityList);
                        }
                    }
                }
            }
            return activityList;
        } else {
            Query query = getSession().getNamedQuery("findAllActivitiesForStaffMember");
            query.setParameter("staff", "%" + member.getId() + "%");
            List<Object[]> objects = query.list();
            List<Activity> activityList = new ArrayList<>();
            staffMerge(objects, activityList);
            return activityList;
        }
    }

    @Override
    public List<Activity> findAllActivitiesForUser(List<StudentGroup> selectedGroups, List<Course> hiddenCourses, boolean useCache) {
        return null;
    }

    @Override
    public List<Activity> findAllActivitiesForClassRoom(ClassRoom classRoom, boolean useCache) {
        if (useCache) {
            List<Activity> activityList = new ArrayList<>();
            List<Activity> cache = new ArrayList<>(this.activityList);
            for (Activity activity : cache) {
                if (activity.getId() != 0) {
                    if (activity.isActive()) {
                        if (activity.getClassRoom().contains(classRoom.getName())) {
                            duplicateFind(activity, activityList);
                        }
                    }
                }
            }

            return activityList;
        } else {
            Query query = getSession().getNamedQuery("findAllActivitiesForClassRoom");
            query.setParameter("classRoom", "%" + classRoom.getId() + "%");
            List<Object[]> objects = query.list();
            List<Activity> activityList = new ArrayList<>();
            staffMerge(objects, activityList);
            return activityList;
        }
    }

    @Override
    public List<Activity> findAllActivitiesForStudentGroup(StudentGroup group, boolean useCache) {
        if (useCache) {
            List<Activity> activityList = new ArrayList<>();
            List<Activity> cache = new ArrayList<>(this.activityList);
            for (Activity activity : cache) {
                if (activity.getId() != 0) {
                    if (activity.isActive()) {
                        if (activity.getGroups().contains(group)) {
                            duplicateFind(activity, activityList);
                        }
                    }
                }
            }

            return activityList;
        } else {
            Query query = getSession().getNamedQuery("findAllActivitiesForStudentGroup");
            query.setParameter("studentGroup", group.getId());
            List<Object[]> objects = query.list();
            List<Activity> activityList = new ArrayList<>();
            staffMerge(objects, activityList);
            return activityList;
        }
    }

    @Override
    public List<Activity> findWeekActivitiesForStaffMember(StaffMember member, int week, boolean useCache) {
        if (useCache) {
            List<Activity> activityList = new ArrayList<>();
            List<Activity> cache = new ArrayList<>(this.activityList);
            for (Activity activity : cache) {
                if (activity.getId() != 0) {
                    if (activity.isActive()) {
                        if (activity.getWeek() == week) {
                            if (activity.getStaff().contains(member.getName())) {
                                duplicateFind(activity, activityList);
                            }
                        }
                    }
                }
            }

            return activityList;
        } else {
            Query query = getSession().getNamedQuery("findWeekActivitiesForStaffMember");
            query.setParameter("staff", "%" + member.getId() + "%");
            List<Object[]> objects = query.list();
            List<Activity> activityList = new ArrayList<>();
            staffMerge(objects, activityList);
            return activityList;
        }
    }

    @Override
    public List<Activity> findWeekActivitiesForUser(List<StudentGroup> selectedGroups, List<Course> hiddenCourses, int week, boolean useCache) {
        return null;
    }

    @Override
    public List<Activity> findWeekActivitiesForClassRoom(ClassRoom classRoom, int week, boolean useCache) {
        if (useCache) {
            List<Activity> activityList = new ArrayList<>();
            List<Activity> cache = new ArrayList<>(this.activityList);
            for (Activity activity : cache) {
                if (activity.isActive()) {
                    if (activity.getWeek() == week) {
                        if (activity.getClassRoom().contains(classRoom.getName())) {
                            duplicateFind(activity, activityList);
                        }
                    }
                }
            }

            return activityList;
        } else {
            Query query = getSession().getNamedQuery("findWeekActivitiesForClassRoom");
            query.setParameter("classRoom", "%" + classRoom.getId() + "%");
            query.setParameter("week", week);
            List<Object[]> objects = query.list();
            List<Activity> activityList = new ArrayList<>();
            staffMerge(objects, activityList);
            return activityList;
        }
    }

    @Override
    public List<Activity> findWeekActivitiesForStudentGroup(StudentGroup group, int week, boolean useCache) {
        if (useCache) {
            List<Activity> activityList = new ArrayList<>();
            List<Activity> cache = new ArrayList<>(this.activityList);
            for (Activity activity : cache) {
                if (activity.getId() != 0) {
                    if (activity.isActive()) {
                        if (activity.getWeek() == week) {
                            if (activity.getGroups().contains(group)) {
                                duplicateFind(activity, activityList);
                            }
                        }
                    }
                }
            }

            return activityList;
        } else {
            Query query = getSession().getNamedQuery("findWeekActivitiesForStudentGroup");
            query.setParameter("studentGroup", group.getId());
            query.setParameter("week", week);
            List<Object[]> objects = query.list();
            List<Activity> activityList = new ArrayList<>();
            staffMerge(objects, activityList);
            return activityList;
        }
    }

    @Override
    public int getActivitiesCount(boolean useCache) {
        return activityList.size();
    }

    @Override
    public Activity findActivityById(int id, boolean useCache) {
        if (!useCache) {
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
        if (timeTableServer.getSyncState() == SyncState.CRASHED) {
            // Crashed - Do not retry
            logger.warn("Sync timeout - cancelling sync");
            return activities;
        }
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
                            logger.debug("Added activity: " + activity.getName() + " [" + activity.getBeginTimeUnix() + "]");
                            savedActivities.add(entityManager.merge(activity));
                        } catch (Exception ex) {
                            logger.error("Unable to sync activity!",ex);
                            error = true;
                        }
                    }
                    if (activity.getLastSync() < currentTimeTable.getLastSync()) {
                        // Removed faculty
                        try {
                            logger.debug("Removed activity: " + activity.getName() + " [" + activity.getBeginTimeUnix() + "]");
                            activity.setActive(false);
                            Activity mergedActivity = entityManager.merge(activity);
                            removedActivities.add(mergedActivity);
                        } catch (Exception ex) {
                            logger.error("Unable to sync activity!",ex);
                            error = true;
                        }
                    }
                }
            }

            logger.info("Added: " + savedActivities.size() + " activities");
            logger.info("Removed: " + removedActivities.size() + " activities");

            // Check for errors
            if (error) {
                ServiceProvider.getTwitterServer().sendDirectMessage(ServiceProvider.getConfigurationServer().getString("twitter.owner"), "!! Sync saving failure " + (System.currentTimeMillis() / 1000));
            }

            // Save models
            if (currentTimeTable != null) {
                currentTimeTable.setLastSync(System.currentTimeMillis() / 1000);
                ServiceProvider.getTimeTableServer().updateTimeTable(entityManager.merge(currentTimeTable));
            }

            // Store sync information
            sync.setAdded(savedActivities.size());
            sync.setRemoved(removedActivities.size());
            if (sync.getRemoved() > 5000) {
                // Possible sync failure
                ServiceProvider.getTwitterServer().sendDirectMessage(ServiceProvider.getConfigurationServer().getString("twitter.owner"), "!! Attention required on last sync: Removed " + sync.getRemoved());
            }
            sync.setActivities(activities.size() - removedActivities.size());
            sync.setCourses(ServiceProvider.getCourseServer().getCoursesCount(false));
            sync.setStudentGroups(ServiceProvider.getStudentGroupServer().getStudentGroupsCount(false));
            sync.setStudyProgrammes(ServiceProvider.getStudyProgramServer().getStudyProgrammesCount(false));
            sync = ServiceProvider.getSyncServer().saveSync(sync); // Save sync

            List<ActivityChange> activityChanges = new ArrayList<>();
            if (!ServiceProvider.getTimeTableServer().firstSync()) {
                List<Activity> newActivities = new ArrayList<>(savedActivities);
                logger.info("Checking for activity changes ...");
                // Check for possible faculty changes
                for (Activity removedActivity : removedActivities) {
                    // For each removed faculty, check if one of the new activities is responsible
                    boolean changeFound = false;
                    for (Activity addedActivity : savedActivities) {
                        if (addedActivity.getName().equalsIgnoreCase(removedActivity.getName())) { // Same name
                            if (addedActivity.getGroupsString().equalsIgnoreCase(removedActivity.getGroupsString())) { // Same groups
                                // At this point you can be certain that its a change of faculty
                                if (addedActivity.getWeek() == removedActivity.getWeek()) { // Same week
                                    if (addedActivity.getDay() == removedActivity.getDay()) { // Same day
                                        if (addedActivity.getBeginTimeUnix() == removedActivity.getBeginTimeUnix()) { // Same time
                                            if (addedActivity.getEndTimeUnix() == removedActivity.getEndTimeUnix()) { // Same end time
                                                if (addedActivity.getClassRoom().equalsIgnoreCase(removedActivity.getClassRoom())) { // Same classroom
                                                    if (addedActivity.getId() == removedActivity.getId()) {
                                                        changeFound = true;
                                                        newActivities.remove(addedActivity);
                                                    } else {
                                                        changeFound = false;
                                                    }
                                                } else { // Different location
                                                    ActivityChange change = new ActivityChange();
                                                    change.setRemovedActivity(removedActivity);
                                                    change.setNewActivity(addedActivity);
                                                    newActivities.remove(addedActivity);
                                                    change.setChangeType(ActivityChangeType.LOCATION);
                                                    change.setSync(sync);
                                                    activityChanges.add(entityManager.merge(change));
                                                    changeFound = true;
                                                }
                                            } else {
                                                ActivityChange change = new ActivityChange();
                                                change.setRemovedActivity(removedActivity);
                                                change.setNewActivity(addedActivity);
                                                newActivities.remove(addedActivity);
                                                change.setChangeType(ActivityChangeType.ENDTIME);
                                                change.setSync(sync);
                                                activityChanges.add(entityManager.merge(change));
                                                changeFound = true;
                                            }
                                        } else { // Different time
                                            ActivityChange change = new ActivityChange();
                                            change.setNewActivity(addedActivity);
                                            change.setRemovedActivity(removedActivity);
                                            newActivities.remove(addedActivity);
                                            change.setChangeType(ActivityChangeType.BEGINTIME);
                                            change.setSync(sync);
                                            activityChanges.add(entityManager.merge(change));
                                            changeFound = true;
                                        }
                                    } else { // Different day
                                        ActivityChange change = new ActivityChange();
                                        change.setNewActivity(addedActivity);
                                        change.setRemovedActivity(removedActivity);
                                        newActivities.remove(addedActivity);
                                        change.setChangeType(ActivityChangeType.DAY);
                                        change.setSync(sync);
                                        activityChanges.add(entityManager.merge(change));
                                        changeFound = true;
                                    }
                                } else if (Math.abs(addedActivity.getWeek() - removedActivity.getWeek()) < 2) { // One week change
                                    // Moved faculty
                                    ActivityChange change = new ActivityChange();
                                    change.setNewActivity(addedActivity);
                                    change.setRemovedActivity(removedActivity);
                                    change.setChangeType(ActivityChangeType.WEEK);
                                    change.setSync(sync);
                                    newActivities.remove(addedActivity);
                                    activityChanges.add(entityManager.merge(change));
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
                        change.setSync(sync);
                        change.setChangeType(ActivityChangeType.REMOVED);
                        activityChanges.add(entityManager.merge(change));
                    }
                }
                for (Activity newActivity : newActivities) {
                    ActivityChange change = new ActivityChange();
                    change.setNewActivity(newActivity);
                    change.setRemovedActivity(null);
                    change.setSync(sync);
                    change.setChangeType(ActivityChangeType.ADDED);
                    activityChanges.add(entityManager.merge(change));
                }
            }

            // Experiment
//            int amount = 0;
//            StudentGroup group = ServiceProvider.getStudentGroupServer().findStudentGroupById("Schakelprogr. M in de Toegepaste Informatica", true);
//            if (group != null) {
//                for (ActivityChange change : activityChanges) {
//                    Activity removedAct = change.getRemovedActivity();
//                    Activity newAct = change.getNewActivity();
//                    if (removedAct != null ? removedAct.getGroups().contains(group) : newAct.getGroups().contains(group)) {
//                        ActivityChangeType changeType = change.getChangeType();
//                        amount++;
//                        if (amount < 11) {
//                            switch (changeType) {
//                                case ADDED:
//                                    ServiceProvider.getTwitterServer().sendDirectMessage("MVdWSoftware",
//                                            newAct.getName() + " [" + newAct.getBeginTimeUnix() + "] has been added");
//                                    break;
//                                case BEGINTIME:
//                                case ENDTIME:
//                                    ServiceProvider.getTwitterServer().sendDirectMessage("MVdWSoftware",
//                                            newAct.getName() + " [" + newAct.getBeginTimeUnix() + "] has changed time (on same day)");
//                                    break;
//                                case DAY:
//                                    ServiceProvider.getTwitterServer().sendDirectMessage("MVdWSoftware",
//                                            newAct.getName() + " [" + newAct.getBeginTimeUnix() + "] has changed day from " + removedAct.getDay() + " to " + newAct.getDay());
//                                    break;
//                                case LOCATION:
//                                    ServiceProvider.getTwitterServer().sendDirectMessage("MVdWSoftware",
//                                            newAct.getName() + " [" + newAct.getBeginTimeUnix() + "] changed classroom from '" + removedAct.getClassRoom() + "' to '" + newAct.getClassRoom() + "'");
//                                    break;
//                                case REMOVED:
//                                    ServiceProvider.getTwitterServer().sendDirectMessage("MVdWSoftware",
//                                            removedAct.getName() + " [" + removedAct.getBeginTimeUnix() + "] has been removed");
//                                    break;
//                                case WEEK:
//                                    ServiceProvider.getTwitterServer().sendDirectMessage("MVdWSoftware",
//                                            removedAct.getName() + " changed week from " +
//                                                    removedAct.getWeek() + " to " + newAct.getWeek());
//                                    break;
//                            }
//                        }
//                    }
//                }
//            }
        } catch (Throwable ex) {
            logger.error("Unable to save activities!",ex);
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
                if (reloadData) {
                    activityList = findActivities(false);
                }

                activityList = BaseCore.getInstance().getActivityManager().loadActivitiesForCourses(activityList);
                return null;
            }
        });
    }

    @Asynchronous
    @AccessTimeout(-1)
    public Future<Void> loadActivitiesForStudyProgrammes(boolean reloadData) {
        return pool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Reload database
                if (reloadData) {
                    activityList = findActivities(false);
                }

                activityList = BaseCore.getInstance().getActivityManager().loadActivitiesForStudyProgram(activityList);
                return null;
            }
        });
    }

    @Override
    public void reloadCache() {
        this.activityList = findActivities(false);
    }

    @Override
    public List<ActivityChange> findActivityChangesForGroup(StudentGroup group, boolean useCache) {
        Query query = getSession().getNamedQuery("findActivityChangesForGroup");
        query.setParameter("studentGroup", group.getId());
        List<ActivityChange> changes = query.list();
        return changes;
    }

    @Override
    public ActivityChange findActivityChangeByActivity(Activity activity, boolean useCache) {
        return null;
    }

    @Asynchronous
    @AccessTimeout(-1)
    public Future<Void> loadActivitiesForGroups(boolean reloadData) {
        return pool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Reload database
                if (reloadData) {
                    reloadCache();
                }

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
                if (reloadData) {
                    reloadCache();
                }

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
                if (reloadData) {
                    reloadCache();
                }

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
