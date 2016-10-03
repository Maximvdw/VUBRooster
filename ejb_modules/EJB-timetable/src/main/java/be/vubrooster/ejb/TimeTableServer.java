package be.vubrooster.ejb;

import be.vubrooster.ejb.enums.SyncState;
import be.vubrooster.ejb.models.TimeTable;

/**
 * TimeTableServer
 * Created by maxim on 20-Sep-16.
 */
public interface TimeTableServer {
    /**
     * Get current timetable
     *
     * @return current timetable
     */
    TimeTable getCurrentTimeTable();

    /**
     * Update timetable
     * @param timeTable timetable
     */
    void updateTimeTable(TimeTable timeTable);

    /**
     * Get sync start time
     *
     * @return sync start time
     */
    long getSyncStartTime();

    /**
     * Get the sync state
     *
     * @return sync state
     */
    SyncState getSyncState();

    /**
     * Is this the first sync
     * @return sync
     */
    boolean firstSync();
}
