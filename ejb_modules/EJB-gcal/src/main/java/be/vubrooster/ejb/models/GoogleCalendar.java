package be.vubrooster.ejb.models;

import be.vubrooster.ejb.enums.CalendarType;

import javax.persistence.*;
/**
 * GoogleCalendar
 * Created by maxim on 23-Sep-16.
 */
@Entity
@Cacheable()
@Table(name = "googlecalendars", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
})
@NamedQueries({
        @NamedQuery(name = "findGoogleCalendars",
                query = "SELECT c FROM GoogleCalendar c"),
        @NamedQuery(name = "findNewGoogleCalendars",
                query = "SELECT c FROM GoogleCalendar c WHERE lastSync = 0"),
})
public class GoogleCalendar extends BaseSyncModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    private User user = null;
    @Column(name = "googleCalendarId")
    private String googleCalendarId = "";
    @Column(name = "googleCalendarName")
    private String googleCalendarName = "";
    @Column(name = "type")
    private CalendarType type = CalendarType.GROUP;
    @Column(name = "typeId")
    private String typeId = "";

    public GoogleCalendar(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public CalendarType getType() {
        return type;
    }

    public void setType(CalendarType type) {
        this.type = type;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}
