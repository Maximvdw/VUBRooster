package be.vubrooster.ejb.schedulers;

import be.vubrooster.ejb.TimeTableServer;
import be.vubrooster.ejb.enums.SyncState;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.service.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SyncWatchdog
 * Created by maxim on 20-Sep-16.
 */
public class SyncWatchdog implements Runnable {
    // Logging
    public final Logger logger = LoggerFactory.getLogger(SyncWatchdog.class);

    private boolean tweetTimeout = false;
    private TimeTableServer timeTableServer;

    public SyncWatchdog() {
        logger.info("[WATCHDOG] Synchronization watchdog started!");
        timeTableServer = ServiceProvider.getTimeTableServer();
    }

    @Override
    public void run() {
        long syncStartTime = timeTableServer.getSyncStartTime();
        if (syncStartTime == 0){
            return;
        }

        long threshold = 1000 * 60 * BaseCore.getInstance().getSyncTimeout();

        // Check if the sync has been down for some time
        if (System.currentTimeMillis() > syncStartTime + threshold) {
            // Check what the current status is
            SyncState state = timeTableServer.getSyncState();
            if (state == SyncState.RUNNING) {
                // The sync has been running for more than X minutes
                // forcefully close it
                // This usually means the server is down and it is retrying ...
                ServiceProvider.getTimeTableServer().setSyncState(SyncState.CRASHED);

                // Send a tweet to the owner
                if (!tweetTimeout) {
                    logger.warn("[WATCHDOG] Synchronization timeout! [Running]");
                    ServiceProvider.getTwitterServer().sendDirectMessage(ServiceProvider.getConfigurationServer().getString("twitter.owner"),"Sync timeout. State: " + state.name() + ". Started at " + syncStartTime);
                    tweetTimeout = true;
                }
            } else if (state == SyncState.SAVING) {
                // The sync has been running for more than X minutes
                // but forcing it to close while saving may be
                // unwise
                ServiceProvider.getTimeTableServer().setSyncState(SyncState.CRASHED);

                // Send a tweet to the owner
                if (!tweetTimeout) {
                    logger.warn("[WATCHDOG] Synchronization timeout! [Saving]");
                    ServiceProvider.getTwitterServer().sendDirectMessage(ServiceProvider.getConfigurationServer().getString("twitter.owner"), "Sync timeout. State: " + state.name() + ". Started at " + syncStartTime);
                    tweetTimeout = true;
                }
            }else if (state == SyncState.CRASHED){
                // Already know
            } else {
                // The sync is waiting
                tweetTimeout = false;
            }
        }else{
            tweetTimeout = false;
        }
    }
}
