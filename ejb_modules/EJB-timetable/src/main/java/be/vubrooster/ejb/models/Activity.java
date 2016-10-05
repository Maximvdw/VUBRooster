package be.vubrooster.ejb.models;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;
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
        @Index(name = "i3", columnList = "staff", unique = false),
        @Index(name = "i4", columnList = "week", unique = false),
        @Index(name = "i5", columnList = "day", unique = false),
        @Index(name = "i6", columnList = "beginTimeUnix", unique = false),
        @Index(name = "i7", columnList = "endTimeUnix", unique = false),
        @Index(name = "i8", columnList = "active", unique = false),
})
@NamedQueries({
        @NamedQuery(name = "findActivities",
                query = "SELECT a FROM Activity a WHERE a.active = true"),
        @NamedQuery(name = "findActivityById", query = "SELECT a FROM Activity a WHERE a.id = :id AND a.active = true"),
        @NamedQuery(name = "findAllActivitiesForStaffMember",query = "SELECT a,group_concat(a.staff) FROM Activity a WHERE (a.staff LIKE :staff) AND a.active = true GROUP BY a.staff,a.beginTimeUnix ORDER BY a.beginTimeUnix ASC"),
        @NamedQuery(name = "findAllActivitiesForClassRoom",query = "SELECT a,group_concat(a.staff) FROM Activity a WHERE (a.classRoom LIKE :classRoom) AND a.active = true GROUP BY a.name,a.beginTimeUnix ORDER BY a.beginTimeUnix ASC"),
        @NamedQuery(name = "findAllActivitiesForStudentGroup",query = "SELECT a,group_concat(a.staff) FROM Activity a JOIN a.groups b WHERE b.id = :studentGroup AND a.active = true GROUP BY a.name,a.beginTimeUnix ORDER BY a.beginTimeUnix ASC"),
        @NamedQuery(name = "findWeekActivitiesForStaffMember",query = "SELECT a,group_concat(a.staff) FROM Activity a WHERE (a.staff LIKE :staff) AND a.week = :week AND a.active = true GROUP BY a.staff,a.beginTimeUnix ORDER BY a.beginTimeUnix ASC"),
        @NamedQuery(name = "findWeekActivitiesForClassRoom",query = "SELECT a,group_concat(a.staff) FROM Activity a WHERE (a.classRoom LIKE :classRoom) AND a.week = :week AND a.active = true GROUP BY a.name,a.beginTimeUnix ORDER BY a.beginTimeUnix ASC"),
        @NamedQuery(name = "findWeekActivitiesForStudentGroup",query = "SELECT a,group_concat(a.staff) FROM Activity a JOIN a.groups b WHERE b.id = :studentGroup AND a.week = :week AND a.active = true GROUP BY a.name,a.beginTimeUnix ORDER BY a.beginTimeUnix ASC"),
})
public class Activity extends BaseSyncModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name = "";
    @Column(name = "classRoom")
    private String classRoom = "";
    @Column(name = "staff",length = 750)
    private String staff = "";
    @Column(name = "groupsString",length = 750)
    private String groupsString = "";
    @ManyToMany(cascade = CascadeType.DETACH,fetch = FetchType.EAGER)
    @JoinTable(name = "activity_courses",
            joinColumns=
            @JoinColumn(name="activity_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="course_id", referencedColumnName="id"),
            uniqueConstraints = { @UniqueConstraint(columnNames = {
                    "activity_id", "course_id" })}
    )
    private List<Course> courses = Collections.synchronizedList(new ArrayList<>());
    @Column(name = "week")
    private int week = 0;
    @Column(name = "weeksLabel")
    private String weeksLabel = "";
    @Column(name = "lessonForm")
    private String lessonForm = "";
    @Column(name = "beginTime")
    private String beginTime = "";
    @Column(name = "beginTimeUnix")
    private long beginTimeUnix = 0;
    @Column(name = "endTime")
    private String endTime = "";
    @Column(name = "endTimeUnix")
    private long endTimeUnix = 0;
    @Column(name = "day")
    private int day = 0;
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinTable(name = "activity_studentgroups",
            joinColumns=
            @JoinColumn(name="activity_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="studentgroup_id", referencedColumnName="id"),
            uniqueConstraints = { @UniqueConstraint(columnNames = {
                    "activity_id", "studentgroup_id" })}
    )
    private List<StudentGroup> groups = Collections.synchronizedList(new ArrayList<>());

    public Activity(){

    }

    public Activity(String name, String classRoom){
        setName(name);
        setClassRoom(classRoom);
    }

    @PreUpdate
    @PrePersist
    @PreRemove
    protected void prePersist() {
        if (groupsString.equals("")){
            if (groups.size() > 0) {
                groupsString = groups.get(0).getName();
                for (int i = 1; i < groups.size(); i++) {
                    groupsString += ", " + groups.get(i).getName();
                }
            }
        }
        if (groupsString.length() >= 750){
            groupsString = groupsString.substring(0,740) + " ...";
        }
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

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
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

    public boolean addCourse(Course course){
        if (course == null){
            return false;
        }
        if (!courses.contains(course)){
            courses.add(course);
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
        if (staff != null ? !staff.equals(activity.staff) : activity.staff != null) return false;
        return groupsString != null ? groupsString.equals(activity.groupsString) : activity.groupsString == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (classRoom != null ? classRoom.hashCode() : 0);
        result = 31 * result + (staff != null ? staff.hashCode() : 0);
        result = 31 * result + (groupsString != null ? groupsString.hashCode() : 0);
        result = 31 * result + week;
        result = 31 * result + (int) (beginTimeUnix ^ (beginTimeUnix >>> 32));
        result = 31 * result + (int) (endTimeUnix ^ (endTimeUnix >>> 32));
        result = 31 * result + day;
        return result;
    }

    public JsonObjectBuilder toCompactJSON(){
        return Json.createObjectBuilder()
                .add("activity_id",id)
                .add("summary",getName())
                .add("location",getClassRoom())
                .add("start_unix",getBeginTimeUnix())
                .add("end_unix",getEndTimeUnix())
                .add("start_time",getBeginTime())
                .add("end_time",getEndTime())
                .add("weeks_label",weeksLabel)
                .add("groups_label",groupsString)
                .add("lesson_type",getLessonForm())
                .add("week",getWeek())
                .add("day",getDay())
                .add("staff",getStaff())
                .add("last_sync",getLastSync())
                .add("last_update",getLastUpdate())
                .add("active",isActive());
    }

    public JsonObjectBuilder toFullJSON(){
        JsonArrayBuilder coursesArray = Json.createArrayBuilder();
        for (Course c : getCourses()){
            coursesArray.add(c.toJSON());
        }
        JsonArrayBuilder groupsArray = Json.createArrayBuilder();
        for (StudentGroup g : getGroups()){
            groupsArray.add(g.toCompactJSON());
        }
        return Json.createObjectBuilder()
                .add("activity_id",id)
                .add("summary",getName())
                .add("location",getClassRoom())
                .add("start_unix",getBeginTimeUnix())
                .add("end_unix",getEndTimeUnix())
                .add("start_time",getBeginTime())
                .add("end_time",getEndTime())
                .add("weeks_label",weeksLabel)
                .add("groups_label",groupsString)
                .add("lesson_type",getLessonForm())
                .add("week",getWeek())
                .add("day",getDay())
                .add("staff",getStaff())
                .add("last_sync",getLastSync())
                .add("last_update",getLastUpdate())
                .add("courses",coursesArray)
                .add("studentgroups",groupsArray);
    }

    public String getLessonForm() {
        return lessonForm;
    }

    public void setLessonForm(String lessonForm) {
        this.lessonForm = lessonForm;
    }
}
