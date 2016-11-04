package be.vubrooster.ejb.models;

import be.vubrooster.ejb.enums.ActivityChangeType;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;

/**
 * ActivityChange
 *
 * Created by maxim on 18-Sep-16.
 */
@Entity
@Table(name = "activity_changes", indexes = {
        @Index(name = "i1_activitychange", columnList = "id", unique = true),
        @Index(name = "i2_activitychange", columnList = "newactivity_id", unique = false),
        @Index(name = "i3_activitychange", columnList = "removedactivity_id", unique = false),
        @Index(name = "i4_activitychange", columnList = "sync_id", unique = false),
})
@NamedQueries({
        @NamedQuery(name = "findActivityChangesForGroup",query = "SELECT DISTINCT ac FROM ActivityChange ac LEFT JOIN ac.newActivity an LEFT JOIN ac.removedActivity ar LEFT JOIN an.groups ang LEFT JOIN ar.groups arg WHERE ((:studentGroup = arg.id) OR (:studentGroup = ang.id)) AND an.active = true ORDER BY ac.sync"),
        @NamedQuery(name = "findActivityChangeByActivity", query = "SELECT DISTINCT ac FROM ActivityChange ac WHERE ac.removedActivity = :activity OR ac.newActivity = :activity")
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
    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    @JoinColumn(name = "sync_id")
    private Sync sync = null;

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

    public Sync getSync() {
        return sync;
    }

    public void setSync(Sync sync) {
        this.sync = sync;
    }

    public JsonObjectBuilder toJSON() {
        return Json.createObjectBuilder()
                .add("change_id", id)
                .add("change_type", getChangeType().name())
                .add("sync", getSync().toJSON())
                .add("new_activity", getNewActivity() != null ? getNewActivity().toCompactJSON() : Json.createObjectBuilder())
                .add("old_activity", getRemovedActivity() != null ? getRemovedActivity().toCompactJSON() : Json.createObjectBuilder());
    }

}
