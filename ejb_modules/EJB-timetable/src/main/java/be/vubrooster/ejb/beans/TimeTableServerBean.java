package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.*;
import be.vubrooster.ejb.enums.SyncState;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.managers.EHBRooster;
import be.vubrooster.ejb.managers.VUBRooster;
import be.vubrooster.ejb.models.Sync;
import be.vubrooster.ejb.models.TimeTable;
import be.vubrooster.ejb.schedulers.SchedulerManager;
import be.vubrooster.ejb.schedulers.SyncWatchdog;
import be.vubrooster.ejb.service.ServiceProvider;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TimeTableServerBean
 * <p>
 * Created by maxim on 20-Sep-16.
 */
@Startup
@Remote(TimeTableServer.class)
@Singleton(mappedName = "TimeTableServer")
@DependsOn({"ActivityServerBean","CourseServerBean","FacultyServerBean"
        ,"StudentGroupServerBean","TwitterServerBean","ConfigurationServerBean"
        ,"SyncServerBean","StudyProgramServerBean"})
public class TimeTableServerBean implements TimeTableServer {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(TimeTableServerBean.class);

    @PersistenceContext(name = "vubrooster")
    private EntityManager entityManager;
    private Session session = null;

    private BaseCore baseCore = null;

    // Cache
    private TimeTable currentTimeTable = null;

    private long syncStartTime = 0L;
    private SyncState syncState = SyncState.WAITING;

    @PostConstruct
    public void init() {
        logger.info("=====================================");
        logger.info(" VUBRooster Converter v1.0");
        logger.info(" (c) Maxim Van de Wynckel 2015-2016");
        logger.info("=====================================");

        baseCore = new VUBRooster();

        // Load configuration
        ConfigurationServer configurationServer = ServiceProvider.getConfigurationServer();
        TwitterServer twitterServer = ServiceProvider.getTwitterServer();
        if (configurationServer.getBoolean("twitter.enabled")) {
            twitterServer.signIn(configurationServer.getString("twitter.consumerKey"), configurationServer.getString("twitter.consumerSecret"), configurationServer.getString("twitter.accessToken")
                    , configurationServer.getString("twitter.accessTokenSecret"));
        }

        // Start watchdog
        SchedulerManager.createTask(new SyncWatchdog(), 10, TimeUnit.SECONDS);

        logger.info("Loading latest sync info ...");
        if (getCurrentTimeTable() == null) {
            logger.info("No previous sync found! Starting as new installation ...");
            currentTimeTable = new TimeTable();
            twitterServer.postStatus("[" + (System.currentTimeMillis() / 1000 ) + "] Started application (Fresh database)");
        }else{
            twitterServer.postStatus("[" + (System.currentTimeMillis() / 1000 ) + "] Started application");
        }
        logger.info("Loading faculities ...");
        FacultyServer facultyServer = ServiceProvider.getFacultyServer();
        facultyServer.loadFaculties();
        logger.info("Saving faculties ...");
        facultyServer.saveFaculties();

        SchedulerManager.createTask(new Runnable() {
            @Override
            public void run() {
                if (syncState == SyncState.WAITING) {
                    sync();
                }else{
                    // Sync skipped
                    logger.warn("Synchronisation skipped due to still running!");
                }
            }
        }, 5, TimeUnit.MINUTES);
    }

    /**
     * Perform a sync
     */
    public void sync() {
        logger.info("Performing garbage collect ...");
        System.gc(); // Garbage collect

        // Start the sync
        syncStartTime = System.currentTimeMillis();
        syncState = SyncState.RUNNING;


        getBaseCore().sync();

        // End the sync
        long syncEndTime = System.currentTimeMillis();
        Sync sync = new Sync(System.currentTimeMillis(), 0, 0, (syncEndTime - syncStartTime));
        syncState = SyncState.SAVING;
        logger.info("Synchronisation completed in " + sync.getDuration() + "ms!");

        logger.info("Saving activities to database ...");
        ServiceProvider.getActivitiyServer().saveActivities(sync);
        logger.info("Timetables saved to database! Waiting for next sync ...");

        // Timeout for next sync
        syncState = SyncState.WAITING;
    }

    @Override
    public TimeTable getCurrentTimeTable() {
        if (currentTimeTable == null) {
            List timeTableList = getSession().getNamedQuery("findTimeTables").list();
            if (timeTableList.size() != 0)
                currentTimeTable = (TimeTable) timeTableList.get(timeTableList.size() - 1);
        }
        return currentTimeTable;
    }

    @Override
    public void updateTimeTable(TimeTable timeTable) {
        currentTimeTable = timeTable;
    }

    @Override
    public long getSyncStartTime() {
        return syncStartTime;
    }

    @Override
    public SyncState getSyncState() {
        return syncState;
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

    public BaseCore getBaseCore() {
        return baseCore;
    }

    public void setBaseCore(BaseCore baseCore) {
        this.baseCore = baseCore;
    }
}
