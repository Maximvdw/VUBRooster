package be.vubrooster.ejb.models;

import javax.persistence.*;

/**
 * ActivityChange
 *
 * Created by maxim on 18-Sep-16.
 */
@Entity
@Table(name = "activitychange")
public class ActivityChange extends BaseSyncModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "activity_id")
    private Activity activity = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
