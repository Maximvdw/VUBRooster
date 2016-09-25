package be.vubrooster.ejb;

import be.vubrooster.ejb.models.GoogleCalendar;

import java.util.List;

/**
 * GoogleCalendarServer
 *
 * Created by maxim on 21-Sep-16.
 */
public interface GoogleCalendarServer {
    /**
     * Find google calendars
     *
     * @return list of google calendars
     */
    List<GoogleCalendar> findGoogleCalendars();

    /**
     * Find new unsynced google calendars
     *
     * @return list of unsynced calendars
     */
    List<GoogleCalendar> findNewCalendars();

    /**
     * Synchronize google calendar
     *
     * @param calendar google calendar
     */
    void synchronizeGoogleCalendar(GoogleCalendar calendar);

    /**
     * Synchronize all google calendars
     */
    void synchronizeGoogleCalendars();

    /**
     * Check and synchronize new calendars
     */
    void synchronizeNewCalendars();

    /**
     * Create default calendars
     */
    void createDefaultCalendars();

    void clearCalendars();
}
