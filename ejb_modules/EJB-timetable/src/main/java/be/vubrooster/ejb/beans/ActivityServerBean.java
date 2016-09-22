package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.TimeTableServer;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.models.Activity;
import be.vubrooster.ejb.models.Sync;
import be.vubrooster.ejb.models.TimeTable;
import be.vubrooster.ejb.service.ServiceProvider;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

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
                            savedActivities.add((Activity) getSession().merge(activity));
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
                            getSession().delete(getSession().merge(activity));
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
                ServiceProvider.getTimeTableServer().updateTimeTable((TimeTable) getSession().merge(currentTimeTable));
            }

            // Store sync information
            sync.setAdded(added);
            sync.setRemoved(removed);
            sync.setActivities(getActivitiesCount(false));
            sync.setCourses(ServiceProvider.getCourseServer().getCoursesCount(false));
            sync.setStudentGroups(ServiceProvider.getStudentGroupServer().getStudentGroupsCount(false));
            sync.setStudyProgrammes(ServiceProvider.getStudyProgramServer().getStudyProgrammesCount(false));
            ServiceProvider.getSyncServer().saveSync(sync); // Save sync

            ServiceProvider.getTwitterServer().postStatus("Synchronisation completed in " + sync.getDuration() + "ms. (+" + sync.getAdded() + ") (-" + sync.getRemoved() + ")");
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return savedActivities;
    }

    @Override
    public void saveActivities() {
        activityList = saveActivities(activityList,null);
    }

    @Override
    public void saveActivities(Sync sync) {
        activityList = saveActivities(activityList,sync);
    }

    public void loadActivities() {
        // Reload database
        activityList = findActivities(false);

        activityList = BaseCore.getInstance().getActivityManager().loadActivities(activityList);
    }

    /**
     * Get hibernate session
     *
     * @return hibernate session
     */
    public Session getSession() {
        if (session != null) {
            if (!session.isOpen()) {
                session = (Session) entityManager.getDelegate();
            }
            return session;
        }
        session = (Session) entityManager.getDelegate();
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
