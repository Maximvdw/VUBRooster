package be.vubrooster.ejb.models;

import javax.persistence.*;

/**
 * LocationGoogleCalendar
 * Created by maxim on 08-Oct-16.
 */
@Entity
@Cacheable()
@Table(name = "gcal_location", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
})
@NamedQueries({
        @NamedQuery(name = "findLocationGcals",
                query = "SELECT c FROM LocationGoogleCalendar c"),
        @NamedQuery(name = "findLocationGcalByLocation",
                query = "SELECT c FROM LocationGoogleCalendar c WhERE c.classRoom = :classRoom"),
        @NamedQuery(name = "findNewLocationGcals",
                query = "SELECT c FROM LocationGoogleCalendar c WHERE lastSync = 0"),
})
public class LocationGoogleCalendar extends GoogleCalendar{
    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    @JoinColumn(name = "location_id")
    private ClassRoom classRoom = null;

    public ClassRoom getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(ClassRoom classRoom) {
        this.classRoom = classRoom;
    }
}
