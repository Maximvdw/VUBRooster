package be.vubrooster.ejb.models;

import javax.persistence.*;
import java.io.Serializable;
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
        @Index(name = "i2", columnList = "name", unique = true),
})
@NamedQueries({
        @NamedQuery(name = "findCourses",
                query = "SELECT c FROM Course c"),
        @NamedQuery(name = "findCourseById", query = "SELECT c FROM Course c WHERE c.id = :id"),
        @NamedQuery(name = "findCourseByName", query = "SELECT c FROM Course c WHERE c.name = :name"),
})
public class Course extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name = "";
    @Transient
    private List<CourseVariant> variations = new ArrayList<CourseVariant>();

    public Course(){

    }

    public Course(String name){
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

    public boolean addVariant(CourseVariant variant){
        if (!variations.contains(variant)) {
            variant.setCourse(this);
            variant.setDirty(true);
            variant.setLastUpdate(System.currentTimeMillis() / 1000);
            variant.setSyncDate(System.currentTimeMillis() / 1000);
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

        return name != null ? name.equalsIgnoreCase(course.name) : course.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
