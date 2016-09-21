package be.vubrooster.ejb.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activity
 * Created by maxim on 15-Sep-16.
 */
@Entity
@Cacheable(true)
@Table(name = "activities", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
        @Index(name = "i2", columnList = "name", unique = false),
        @Index(name = "i3", columnList = "lector", unique = false),
        @Index(name = "i4", columnList = "week", unique = false),
        @Index(name = "i5", columnList = "day", unique = false),
        @Index(name = "i6", columnList = "beginTimeUnix", unique = false),
        @Index(name = "i7", columnList = "endTimeUnix", unique = false),
})
@NamedQueries({
        @NamedQuery(name = "findActivities",
                query = "SELECT a FROM Activity a"),
        @NamedQuery(name = "findActivityById", query = "SELECT a FROM Activity a WHERE a.id = :id"),
})
public class Activity extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name = "";
    private String classRoom = "";
    @Column(name = "lector",length = 750)
    private String lector = "";
    @Column(name = "groupsString",length = 750)
    private String groupsString = "";
    @ManyToMany(cascade = CascadeType.DETACH,fetch = FetchType.EAGER)
    private List<Course> courses = Collections.synchronizedList(new ArrayList<>());
    private int week = 0;
    private String weeksLabel = "";
    private String beginTime = "";
    private long beginTimeUnix = 0;
    private String endTime = "";
    private long endTimeUnix = 0;
    private int day = 0;
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private List<StudentGroup> groups = Collections.synchronizedList(new ArrayList<>());
    @Transient
    private boolean dirty = false;

    public Activity(){

    }

    public Activity(String name, String classRoom){
        setName(name);
        setClassRoom(classRoom);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public String getLector() {
        return lector;
    }

    public void setLector(String lector) {
        this.lector = lector;
    }

    public String getGroupsString() {
        return groupsString;
    }

    public void setGroupsString(String groupsString) {
        this.groupsString = groupsString;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int dayOfWeek) {
        this.day = dayOfWeek;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getWeeksLabel() {
        return weeksLabel;
    }

    public void setWeeksLabel(String weeksLabel) {
        this.weeksLabel = weeksLabel;
    }

    public long getBeginTimeUnix() {
        return beginTimeUnix;
    }

    public void setBeginTimeUnix(long startTime) {
        this.beginTimeUnix = startTime;
    }

    public long getEndTimeUnix() {
        return endTimeUnix;
    }

    public void setEndTimeUnix(long endTime) {
        this.endTimeUnix = endTime;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<StudentGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<StudentGroup> groups) {
        this.groups = groups;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean addGroup(StudentGroup group){
        if (group == null){
            return false;
        }
        if (!groups.contains(group)){
            groups.add(group);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Activity activity = (Activity) o;

        if (week != activity.week) return false;
        if (beginTimeUnix != activity.beginTimeUnix) return false;
        if (endTimeUnix != activity.endTimeUnix) return false;
        if (day != activity.day) return false;
        if (name != null ? !name.equals(activity.name) : activity.name != null) return false;
        if (classRoom != null ? !classRoom.equals(activity.classRoom) : activity.classRoom != null) return false;
        if (lector != null ? !lector.equals(activity.lector) : activity.lector != null) return false;
        return groupsString != null ? groupsString.equals(activity.groupsString) : activity.groupsString == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (classRoom != null ? classRoom.hashCode() : 0);
        result = 31 * result + (lector != null ? lector.hashCode() : 0);
        result = 31 * result + (groupsString != null ? groupsString.hashCode() : 0);
        result = 31 * result + week;
        result = 31 * result + (int) (beginTimeUnix ^ (beginTimeUnix >>> 32));
        result = 31 * result + (int) (endTimeUnix ^ (endTimeUnix >>> 32));
        result = 31 * result + day;
        return result;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
