package be.vubrooster.ejb.models;

import javax.persistence.*;
import java.io.Serializable;

/**
 * DayMenu
 * Created by maxim on 19-Sep-16.
 */
@Entity
@Table(name = "daymenu")
public class DayMenu extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int week = 0;
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
