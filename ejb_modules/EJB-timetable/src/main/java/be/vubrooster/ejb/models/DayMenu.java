package be.vubrooster.ejb.models;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * DayMenu
 * Created by maxim on 19-Sep-16.
 */
@Entity
@Table(name = "daymenus", indexes = {
        @Index(name = "i1_daymenu", columnList = "id", unique = true),
        @Index(name = "i2_daymenu", columnList = "week", unique = false),
        @Index(name = "i3_daymenu", columnList = "day", unique = false),
})
@NamedQueries({
        @NamedQuery(name = "findDayMenus" , query = "SELECT m FROM DayMenu m"),
        @NamedQuery(name = "findDayMenuById" , query = "SELECT m FROM DayMenu m WHERE id = :id"),
        @NamedQuery(name = "findAllDayMenusForCampus",query = "SELECT m FROM DayMenu m WHERE campus = :campus"),
        @NamedQuery(name = "findDayMenusForCampusOnWeek",query = "SELECT m FROM DayMenu m WHERE campus = :campus AND week = :week"),
})
public class DayMenu extends BaseSyncModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "week")
    private int week = 0;
    @Column(name = "day")
    private int day = 0;
    private String startTime = "";
    private String endTime = "";
    private String campus = "";
    @Column(name = "beginTimeUnix")
    private long beginTimeUnix = 0;
    @Column(name = "endTimeUnix")
    private long endTimeUnix = 0;
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name="daymenus_menu", joinColumns=@JoinColumn(name="id"))
    @MapKeyColumn (name="type")
    @Column(name="value")
    private Map<String,String> menu = new TreeMap<>();

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

    public Map<String, String> getMenu() {
        return menu;
    }

    public void setMenu(Map<String, String> menu) {
        this.menu = menu;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public void addMenu(String type, String value){
        menu.put(type,value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayMenu dayMenu = (DayMenu) o;

        if (week != dayMenu.week) return false;
        if (day != dayMenu.day) return false;
        return campus != null ? campus.equalsIgnoreCase(dayMenu.campus) : dayMenu.campus == null;

    }

    @Override
    public int hashCode() {
        int result = week;
        result = 31 * result + day;
        result = 31 * result + (campus != null ? campus.hashCode() : 0);
        return result;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getBeginTimeUnix() {
        return beginTimeUnix;
    }

    public void setBeginTimeUnix(long beginTimeUnix) {
        this.beginTimeUnix = beginTimeUnix;
    }

    public long getEndTimeUnix() {
        return endTimeUnix;
    }

    public void setEndTimeUnix(long endTimeUnix) {
        this.endTimeUnix = endTimeUnix;
    }

    public JsonObjectBuilder toJSON() {
        JsonArrayBuilder menus = Json.createArrayBuilder();
        for (Map.Entry<String,String> entry : getMenu().entrySet()){
            menus.add(Json.createObjectBuilder().add(entry.getKey(),entry.getValue()).build());
        }
        return Json.createObjectBuilder()
                .add("daymenu_id", id)
                .add("start_unix", getBeginTimeUnix())
                .add("end_unix", getEndTimeUnix())
                .add("week",getWeek())
                .add("day",getDay())
                .add("campus",getCampus())
                .add("open_at",getStartTime())
                .add("closes_at",getEndTime())
                .add("menus",menus.build());
    }
}
