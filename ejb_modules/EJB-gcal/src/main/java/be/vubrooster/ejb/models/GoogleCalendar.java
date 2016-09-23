package be.vubrooster.ejb.models;

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
})
public class GoogleCalendar extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    private User user = null;

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
}
