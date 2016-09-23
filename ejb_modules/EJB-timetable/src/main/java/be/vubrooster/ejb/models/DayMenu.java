package be.vubrooster.ejb.models;

import javax.persistence.*;

/**
 * DayMenu
 * Created by maxim on 19-Sep-16.
 */
@Entity
@Table(name = "daymenu")
public class DayMenu extends BaseSyncModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "week")
    private int week = 0;
    @Column(name = "day")
    private int day = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
