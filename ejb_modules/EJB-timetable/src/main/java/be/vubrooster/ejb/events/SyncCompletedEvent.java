package be.vubrooster.ejb.events;

import be.vubrooster.ejb.models.Sync;

/**
 * SyncCompletedEvent
 *
 * Created by maxim on 25-Sep-16.
 */
public class SyncCompletedEvent extends Event{
    private Sync sync = null;

    public Sync getSync() {
        return sync;
    }

    public void setSync(Sync sync) {
        this.sync = sync;
    }
}
