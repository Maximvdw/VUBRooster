package be.vubrooster.ejb.models;

import javax.persistence.*;

/**
 * UserGoogleCalendar
 * Created by maxim on 08-Oct-16.
 */
@Entity
@Cacheable()
@Table(name = "gcal_user", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
})
@NamedQueries({
        @NamedQuery(name = "findUserGcals",
                query = "SELECT c FROM UserGoogleCalendar c"),
        @NamedQuery(name = "findUserGcalByUser",
                query = "SELECT c FROM UserGoogleCalendar c WhERE c.user = :user"),
        @NamedQuery(name = "findNewUserGcals",
                query = "SELECT c FROM UserGoogleCalendar c WHERE lastSync = 0"),
})
public class UserGoogleCalendar extends GoogleCalendar{
    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    @JoinColumn(name = "user_id")
    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
