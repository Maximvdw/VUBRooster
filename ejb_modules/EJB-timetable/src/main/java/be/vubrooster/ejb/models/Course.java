package be.vubrooster.ejb.models;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Course
 *
 * Created by Maxim Van de Wynckel on 15-Sep-16.
 */
@Entity
@Table(name = "courses", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
        @Index(name = "i2", columnList = "name", unique = false),
        @Index(name = "i4", columnList = "longName", unique = false),
})
@NamedQueries({
        @NamedQuery(name = "findCourses",
                query = "SELECT c FROM Course c"),
        @NamedQuery(name = "findCourseById", query = "SELECT c FROM Course c WHERE c.id = :id"),
        @NamedQuery(name = "findCourseByName", query = "SELECT c FROM Course c WHERE c.name = :name"),
})
public class Course extends BaseSyncModel implements Comparable<Course>{
    @Id
    @Column(name = "id")
    private String id = "";
    @Column(name = "name")
    private String name = "";
    @Column(name = "longName")
    private String longName = "";
    @Column(name = "listIdx")
    private int listIdx = 0;
    @Transient
    private List<CourseVariant> variations = new ArrayList<CourseVariant>();

    public Course(){

    }

    public Course(String name, String splusId){
        setName(name);
        setId(splusId);
    }

    public Course(String name){
        setName(name);
        setId(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean addVariant(CourseVariant variant){
        if (!variations.contains(variant)) {
            variant.setCourse(this);
            variant.setDirty(true);
            variant.setLastUpdate(System.currentTimeMillis() / 1000);
            variant.setLastSync(System.currentTimeMillis() / 1000);
            variations.add(variant);
            return true;
        }
        return false;
    }

    public List<CourseVariant> getVariations() {
        return variations;
    }

    public void setVariations(List<CourseVariant> variations) {
        this.variations = variations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        return id != null ? id.equalsIgnoreCase(course.id) : course.id == null;

    }

    @Override
    public int compareTo(Course o) {
        int idx1 = this.getListIdx();
        int idx2 = o.getListIdx();
        if (idx1 == idx2) {
            return 0;
        } else if (idx1 > idx2) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public int getListIdx() {
        return listIdx;
    }

    public void setListIdx(int listIdx) {
        this.listIdx = listIdx;
    }

    public JsonObjectBuilder toJSON(){
        return Json.createObjectBuilder()
                .add("course_id",getId())
                .add("name",getName())
                .add("long_name",getLongName());
    }
}
