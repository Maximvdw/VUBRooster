package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.DayMenuServer;
import be.vubrooster.ejb.FacultyServer;
import be.vubrooster.ejb.TimeTableServer;
import be.vubrooster.ejb.enums.SyncState;
import be.vubrooster.ejb.managers.ActivityManager;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.managers.EHBRooster;
import be.vubrooster.ejb.managers.VUBRooster;
import be.vubrooster.ejb.models.Sync;
import be.vubrooster.ejb.models.TimeTable;
import be.vubrooster.ejb.schedulers.RamWatchdog;
import be.vubrooster.ejb.schedulers.SchedulerManager;
import be.vubrooster.ejb.schedulers.SyncWatchdog;
import be.vubrooster.ejb.service.ServiceProvider;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.*;
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
@DependsOn({"ActivityServerBean", "CourseServerBean", "FacultyServerBean"
        , "StudentGroupServerBean", "TwitterServerBean", "ConfigurationServerBean"
        , "SyncServerBean", "StudyProgramServerBean"})
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

    private boolean firstSync = false;

    @PostConstruct
    public void init() {
        logger.info("=====================================");
        logger.info(" VUBRooster Converter v1.5");
        logger.info(" (c) Maxim Van de Wynckel 2015-2016");
        logger.info("=====================================");

        baseCore = new EHBRooster();

        // Load configuration
        be.vubrooster.ejb.ConfigurationServer configurationServer = ServiceProvider.getConfigurationServer();
        be.vubrooster.ejb.TwitterServer twitterServer = ServiceProvider.getTwitterServer();
        if (configurationServer.getBoolean("twitter.enabled")) {
            twitterServer.signIn(configurationServer.getString("twitter.consumerKey"), configurationServer.getString("twitter.consumerSecret"), configurationServer.getString("twitter.accessToken")
                    , configurationServer.getString("twitter.accessTokenSecret"));
        }else{
            logger.info("Twitter is disabled! Please enable it.");
            return;
        }

        // Start watchdog
        logger.info("Starting watchdogs to monitor the synchronization ...");
        SchedulerManager.createAsyncTask(new SyncWatchdog(), 10, TimeUnit.SECONDS);
        SchedulerManager.createAsyncTask(new RamWatchdog(), 10, TimeUnit.MINUTES);

        logger.info("Loading latest sync info ...");
        if (getCurrentTimeTable() == null) {
            firstSync = true;
            logger.info("No previous sync found! Starting as new installation ...");
            currentTimeTable = new TimeTable();
        }
        logger.info("Loading faculities ...");
        FacultyServer facultyServer = ServiceProvider.getFacultyServer();
        facultyServer.loadFaculties();
        logger.info("Saving faculties ...");
        facultyServer.saveFaculties();

        SchedulerManager.createTask(new Runnable() {
            @Override
            public void run() {
                if (getCurrentTimeTable().getStartTimeStamp() != 0) {
                    try {
                        DayMenuServer dayMenuServer = ServiceProvider.getDayMenuServer();
                        dayMenuServer.loadDayMenus();
                        dayMenuServer.saveDayMenus();
                    } catch (Exception ex) {
                        logger.error("Unable to sync resto menus!");
                    }
                }
            }
        },4,TimeUnit.HOURS);

        SchedulerManager.createTask(new Runnable() {
            @Override
            public void run() {
                try {
                    sync();
                } catch (OutOfMemoryError ex){
                    System.gc();
                    ServiceProvider.getTwitterServer().sendDirectMessage(ServiceProvider.getConfigurationServer().getString("twitter.owner"),"Sync crashed! Out of memory!");
                    ex.printStackTrace();
                    setSyncState(SyncState.WAITING);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.gc();
                    ServiceProvider.getTwitterServer().sendDirectMessage(ServiceProvider.getConfigurationServer().getString("twitter.owner"),"Sync crashed while " + getSyncState().name() + "! Retrying next cycle ...");
                    setSyncState(SyncState.WAITING);
                }
            }
        }, BaseCore.getInstance().getSyncInterval(), TimeUnit.MINUTES);
    }

    /**
     * Perform a sync
     */
    public synchronized void sync() {
        logger.info("Clearing temp cache files ...");
        ActivityManager.clearTempFiles();
        logger.info("Performing garbage collect ...");
        System.gc(); // Garbage collect

        // Start the sync
        syncStartTime = System.currentTimeMillis();
        syncState = SyncState.RUNNING;


        getBaseCore().sync();

        if (getSyncState() != SyncState.CRASHED) {
            // End the sync
            long syncEndTime = System.currentTimeMillis();
            Sync sync = new Sync(System.currentTimeMillis(), 0, 0, (syncEndTime - syncStartTime));
            syncState = SyncState.SAVING;
            logger.info("Synchronisation completed in " + sync.getDuration() + "ms!");

            logger.info("Saving activities to database ...");
            ServiceProvider.getActivitiyServer().saveActivities(sync);
            ServiceProvider.getActivitiyServer().reloadCache();
            logger.info("Timetables saved to database! Waiting for next sync ...");
        }else{
            ServiceProvider.getActivitiyServer().reloadCache();
        }

        // Timeout for next sync
        syncState = SyncState.WAITING;

        logger.info("Freeing up RAM by clearing temp cache ...");
        BaseCore.getInstance().getStudentGroupManager().getStudentGroupList().clear();
        BaseCore.getInstance().getActivityManager().getActivityList().clear();
        BaseCore.getInstance().getStudyProgramManager().getStudyProgramList().clear();
        BaseCore.getInstance().getClassRoomManager().getClassRoomList().clear();
        BaseCore.getInstance().getCourseManager().getCourseList().clear();
        BaseCore.getInstance().getFacultyManager().getFacultyList().clear();
        BaseCore.getInstance().getStaffManager().getStaffList().clear();

        logger.info("Performing garbage collect ...");
        System.gc(); // Garbage collect
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

    @Override
    public void setSyncState(SyncState state) {
        this.syncState = state;
    }

    @Override
    public boolean firstSync() {
        return firstSync;
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
