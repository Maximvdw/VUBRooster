package be.vubrooster.ejb.models;

import javax.persistence.*;
import java.io.Serializable;

/**
 * TimeTable
 * Created by maxim on 15-Sep-16.
 */
@Entity
@Table(name = "timetable")
@NamedQueries({
        @NamedQuery(name = "findTimeTables",
                query = "SELECT t FROM TimeTable t"),
})
public class TimeTable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private long startTimeStamp = 0;
    private long lastSync = 0;

    public TimeTable(){
        setStartTimeStamp(System.currentTimeMillis());
    }

    public TimeTable(long startDate){
        setStartTimeStamp(startDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(long startDate) {
        this.startTimeStamp = startDate;
    }

    public long getLastSync() {
        return lastSync;
    }

    public void setLastSync(long lastSync) {
        this.lastSync = lastSync;
    }
}
