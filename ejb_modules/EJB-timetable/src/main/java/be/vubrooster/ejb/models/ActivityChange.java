package be.vubrooster.ejb.models;

import be.vubrooster.ejb.enums.ActivityChangeType;

import javax.persistence.*;

/**
 * ActivityChange
 *
 * Created by maxim on 18-Sep-16.
 */
@Entity
@Table(name = "activity_changes")
@NamedQueries({
        @NamedQuery(name = "findActivityChangesForGroup",query = "SELECT ac FROM ActivityChange ac"),
        @NamedQuery(name = "findActivityChangeByActivity", query = "SELECT ac FROM ActivityChange ac WHERE ac.removedActivity = :activity OR ac.newActivity = :activity")
})
public class ActivityChange extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    @JoinColumn(name = "newactivity_id")
    private Activity newActivity = null;
    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    @JoinColumn(name = "removedactivity_id")
    private Activity removedActivity = null;
    private ActivityChangeType changeType = ActivityChangeType.LOCATION;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ActivityChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ActivityChangeType changeType) {
        this.changeType = changeType;
    }

    public Activity getNewActivity() {
        return newActivity;
    }

    public void setNewActivity(Activity newActivity) {
        this.newActivity = newActivity;
    }

    public Activity getRemovedActivity() {
        return removedActivity;
    }

    public void setRemovedActivity(Activity removedActivity) {
        this.removedActivity = removedActivity;
    }
}
