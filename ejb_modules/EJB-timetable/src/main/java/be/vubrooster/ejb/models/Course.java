package be.vubrooster.ejb.models;

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
        @Index(name = "i3", columnList = "splusId", unique = true),
        @Index(name = "i4", columnList = "longName", unique = false),
})
@NamedQueries({
        @NamedQuery(name = "findCourses",
                query = "SELECT c FROM Course c"),
        @NamedQuery(name = "findCourseById", query = "SELECT c FROM Course c WHERE c.id = :id"),
        @NamedQuery(name = "findCourseByName", query = "SELECT c FROM Course c WHERE c.name = :name"),
})
public class Course extends BaseSyncModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name = "";
    @Column(name = "longName")
    private String longName = "";
    @Column(name = "splusId")
    private String splusId = "";
    @Transient
    private List<CourseVariant> variations = new ArrayList<CourseVariant>();

    public Course(){

    }

    public Course(String name, String splusId){
        setName(name);
        setSplusId(splusId);
    }

    public Course(String name){
        setName(name);
        setSplusId(name);
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

        return splusId != null ? splusId.equalsIgnoreCase(course.splusId) : course.splusId == null;

    }

    @Override
    public int hashCode() {
        return splusId != null ? splusId.hashCode() : 0;
    }

    public String getSplusId() {
        return splusId;
    }

    public void setSplusId(String splusId) {
        this.splusId = splusId;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
}
