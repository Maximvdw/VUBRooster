package be.vubrooster.ejb.models;

import javax.persistence.*;
/**
 * GoogleCalendar
 * Created by maxim on 23-Sep-16.
 */
@MappedSuperclass
public class GoogleCalendar extends BaseSyncModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "googleCalendarId")
    private String googleCalendarId = "";
    @Column(name = "googleCalendarName")
    private String googleCalendarName = "";

    public GoogleCalendar(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoogleCalendarId() {
        return googleCalendarId;
    }

    public void setGoogleCalendarId(String googleCalendarId) {
        this.googleCalendarId = googleCalendarId;
    }

    public String getGoogleCalendarName() {
        return googleCalendarName;
    }

    public void setGoogleCalendarName(String googleCalendarName) {
        this.googleCalendarName = googleCalendarName;
    }
}
