package be.vubrooster.ejb.models;

import javax.persistence.*;

/**
 * CourseVariant
 *
 * Created by maxim on 16-Sep-16.
 */
@Entity
@Table(name = "coursevariants", indexes = {
        @Index(name = "i1_coursevariants", columnList = "id", unique = true),
        @Index(name = "i2_coursevariants", columnList = "name", unique = false),
        @Index(name = "i3_coursevariants", columnList = "course_id", unique = false),
})
@NamedQueries({
        @NamedQuery(name = "findCourseVariants",
                query = "SELECT c FROM CourseVariant c"),
})
public class CourseVariant extends BaseSyncModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name = "";
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "course_id")
    private Course course = null;
    @Column(name = "day")
    private String day = "";
    @Column(name = "weeks")
    private String weeks = "";
    @Column(name = "staff")
    private String staff = "";
    @Column(name = "classRoom")
    private String classRoom = "";
    @Column(name = "startTime")
    private String startTime = "";
    @Column(name = "endTime")
    private String endTime = "";

    public CourseVariant() {

    }

    public CourseVariant(String name){
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseVariant variant = (CourseVariant) o;

        if (name != null ? !name.equals(variant.name) : variant.name != null) return false;
        if (course != null ? !course.equals(variant.course) : variant.course != null) return false;
        if (day != null ? !day.equals(variant.day) : variant.day != null) return false;
        if (weeks != null ? !weeks.equals(variant.weeks) : variant.weeks != null) return false;
        if (staff != null ? !staff.equals(variant.staff) : variant.staff != null) return false;
        if (classRoom != null ? !classRoom.equals(variant.classRoom) : variant.classRoom != null) return false;
        if (startTime != null ? !startTime.equals(variant.startTime) : variant.startTime != null) return false;
        return endTime != null ? endTime.equals(variant.endTime) : variant.endTime == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (course != null ? course.hashCode() : 0);
        result = 31 * result + (day != null ? day.hashCode() : 0);
        result = 31 * result + (weeks != null ? weeks.hashCode() : 0);
        result = 31 * result + (staff != null ? staff.hashCode() : 0);
        result = 31 * result + (classRoom != null ? classRoom.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        return result;
    }
}
