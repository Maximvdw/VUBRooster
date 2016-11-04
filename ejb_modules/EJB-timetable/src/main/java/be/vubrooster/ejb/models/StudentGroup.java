package be.vubrooster.ejb.models;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentGroup
 *
 * @author Maxim Van de Wynckel
 */
@Entity
@Table(name = "studentgroups", indexes = {
        @Index(name = "i1_studentgroups", columnList = "id", unique = true),
        @Index(name = "i2_studentgroups", columnList = "name", unique = true),
        @Index(name = "i3_studentgroups", columnList = "active", unique = false),
})
@NamedQueries({
        @NamedQuery(name = "findStudentGroups",
                query = "SELECT sg FROM StudentGroup sg"),
        @NamedQuery(name = "findStudentGroupById", query = "SELECT sg FROM StudentGroup sg WHERE sg.id = :id"),
        @NamedQuery(name = "findStudentGroupByName", query = "SELECT sg FROM StudentGroup sg WHERE sg.name = :name"),
})
public class StudentGroup extends BaseSyncModel implements Comparable<StudentGroup> {
    @Id
    @Column(name = "id")
    private String id = "";
    @Column(name = "name")
    private String name = "";
    @Column(name = "longName")
    private String longName = "";
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinTable(name = "studentgroup_studyprogrammes",
            joinColumns =
            @JoinColumn(name = "studentgroup_id", referencedColumnName = "id"),
            inverseJoinColumns =
            @JoinColumn(name = "studyprogram_id", referencedColumnName = "id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {
                    "studentgroup_id", "studyprogram_id"})}
    )
    private List<StudyProgram> studyProgrammes = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinTable(name = "studentgroup_courses",
            joinColumns =
            @JoinColumn(name = "studentgroup_id", referencedColumnName = "id"),
            inverseJoinColumns =
            @JoinColumn(name = "course_id", referencedColumnName = "id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {
                    "studentgroup_id", "course_id"})}
    )
    private List<Course> courses = new ArrayList<>();
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty = null;
    @Column(name = "individual")
    private boolean individual = false;
    @Column(name = "listIdx")
    private int listIdx = 0;

    public StudentGroup() {

    }

    public StudentGroup(String name, String splusId) {
        setName(name);
        setId(splusId);
    }

    public StudentGroup(String name) {
        setName(name);
        setId(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public boolean addCourse(Course course) {
        if (!courses.contains(course)) {
            courses.add(course);
            return true;
        }
        return false;
    }

    public boolean addStudyProgram(StudyProgram studyProgram) {
        if (!studyProgrammes.contains(studyProgram)) {
            studyProgrammes.add(studyProgram);
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(StudentGroup o) {
        int idx1 = this.getListIdx();
        int idx2 = o.getListIdx();
        if (idx2 == idx1) {
            return 0;
        } else if (idx1 > idx2) {
            return 1;
        } else {
            return -1;
        }
    }

    public List<StudyProgram> getStudyProgrammes() {
        return studyProgrammes;
    }

    public void setStudyProgrammes(List<StudyProgram> studyProgrammes) {
        this.studyProgrammes = studyProgrammes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StudentGroup that = (StudentGroup) o;

        return id != null ? id.equalsIgnoreCase(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public boolean isIndividual() {
        return individual;
    }

    public void setIndividual(boolean individual) {
        this.individual = individual;
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

    public JsonObjectBuilder toCompactJSON() {
        return Json.createObjectBuilder()
                .add("studentgroup_id", getId())
                .add("name", getName())
                .add("long_name", getLongName())
                .add("faculty", faculty.toJSON());
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }
}
