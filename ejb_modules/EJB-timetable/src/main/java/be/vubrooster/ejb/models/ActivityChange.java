package be.vubrooster.ejb.models;

import be.vubrooster.ejb.enums.ActivityChangeType;

import javax.persistence.*;

/**
 * ActivityChange
 *
 * Created by maxim on 18-Sep-16.
 */
@Entity
@Table(name = "activitychange")
public class ActivityChange extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "newactivity_id")
    private Activity newActivity = null;
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
}
