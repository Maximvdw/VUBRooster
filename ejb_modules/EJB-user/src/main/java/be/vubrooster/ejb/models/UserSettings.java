package be.vubrooster.ejb.models;

import javax.persistence.*;

/**
 * UserSettings
 *
 * Created by maxim on 03-Oct-16.
 */
@Entity()
@Table(name = "user_settings", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
})
@NamedQueries({

})
public class UserSettings extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    @JoinColumn(name = "user_id")
    private User user = null;

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
