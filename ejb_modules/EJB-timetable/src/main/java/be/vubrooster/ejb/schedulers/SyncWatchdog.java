package be.vubrooster.ejb.schedulers;

import be.vubrooster.ejb.TimeTableServer;
import be.vubrooster.ejb.enums.SyncState;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.service.ServiceProvider;

/**
 * SyncWatchdog
 * Created by maxim on 20-Sep-16.
 */
public class SyncWatchdog implements Runnable {
    private boolean tweetTimeout = false;

    public SyncWatchdog() {

    }

    @Override
    public void run() {
        TimeTableServer timeTableServer = ServiceProvider.getTimeTableServer();
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

                // Send a tweet to the owner
                if (!tweetTimeout) {
                    ServiceProvider.getTwitterServer().postStatus("Sync timeout. State: " + state.name() + ". Started at " + syncStartTime +
                            " (CC @" + ServiceProvider.getConfigurationServer().getString("twitter.owner") + ")");
                    tweetTimeout = true;
                }
            } else if (state == SyncState.SAVING) {
                // The sync has been running for more than X minutes
                // but forcing it to close while saving may be
                // unwise

                // Send a tweet to the owner
                if (!tweetTimeout) {
                    ServiceProvider.getTwitterServer().postStatus("Sync timeout. State: " + state.name() + ". Started at " + syncStartTime +
                            " (CC @" + ServiceProvider.getConfigurationServer().getString("twitter.owner") + ")");
                    tweetTimeout = true;
                }
            } else {
                // The sync is waiting
                tweetTimeout = false;
            }
        }
    }
}
