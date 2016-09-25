package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.TimeTableServer;
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
        query.setParameter("staff",member.getId());
        return query.list();
    }

    @Override
    public List<Activity> findAllActivitiesForClassRoom(ClassRoom classRoom) {
        Query query = getSession().getNamedQuery("findAllActivitiesForClassRoom");
        query.setParameter("classRoom",classRoom.getId());
        return query.list();
    }

    @Override
    public List<Activity> findAllActivitiesForStudentGroup(StudentGroup group) {
        Query query = getSession().getNamedQuery("findAllActivitiesForStudentGroup");
        query.setParameter("studentGroup",group.getId());
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

        List<Activity> savedActivities = new ArrayList<>();
        int added = 0;
        int removed = 0;
        try {
            for (Activity activity : activities) {
                if (activity != null) {
                    if (activity.isDirty()) {
                        try {
                            activity.setLastUpdate(System.currentTimeMillis() / 1000);
                            activity.setDirty(false);
                            logger.info("Added activity: " + activity.getName() + " [" + activity.getBeginTimeUnix() + "]");
                            savedActivities.add(entityManager.merge(activity));
                            added++;
                        } catch (Exception ex) {
                            logger.error("Unable to sync activity!");
                            ex.printStackTrace();
                        }
                    }
                    if (activity.getLastSync() < currentTimeTable.getLastSync()) {
                        // Removed activity
                        try {
                            logger.info("Removed activity: " + activity.getName() + " [" + activity.getBeginTimeUnix() + "]");
                            entityManager.remove(entityManager.merge(activity));
                            removed++;
                        } catch (Exception ex) {
                            logger.error("Unable to sync activity!");
                            ex.printStackTrace();
                        }
                    }
                }
            }
            // Save models
            if (currentTimeTable != null) {
                currentTimeTable.setLastSync(System.currentTimeMillis() / 1000);
                ServiceProvider.getTimeTableServer().updateTimeTable(entityManager.merge(currentTimeTable));
            }

            // Store sync information
            sync.setAdded(added);
            sync.setRemoved(removed);
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
