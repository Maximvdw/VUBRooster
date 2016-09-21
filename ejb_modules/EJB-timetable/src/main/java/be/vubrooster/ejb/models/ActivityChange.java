package be.vubrooster.ejb.models;

import javax.persistence.*;
import java.io.Serializable;

/**
 * ActivityChange
 *
 * Created by maxim on 18-Sep-16.
 */
@Entity
@Table(name = "activitychange")
public class ActivityChange extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(cascade = CascadeType.DETACH)
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
