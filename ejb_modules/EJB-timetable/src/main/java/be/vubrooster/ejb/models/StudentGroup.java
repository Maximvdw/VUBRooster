package be.vubrooster.ejb.models;

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
        @Index(name = "i1", columnList = "id", unique = true),
        @Index(name = "i2", columnList = "name", unique = true),
        @Index(name = "i3", columnList = "splusId", unique = true),
        @Index(name = "i4", columnList = "active", unique = false),
})
@NamedQueries({
        @NamedQuery(name = "findStudentGroups",
                query = "SELECT sg FROM StudentGroup sg"),
        @NamedQuery(name = "findStudentGroupById", query = "SELECT sg FROM StudentGroup sg WHERE sg.id = :id"),
        @NamedQuery(name = "findStudentGroupBySplusId", query = "SELECT sg FROM StudentGroup sg WHERE sg.splusId = :splusId"),
        @NamedQuery(name = "findStudentGroupByName", query = "SELECT sg FROM StudentGroup sg WHERE sg.name = :name"),
})
public class StudentGroup extends BaseSyncModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private int id;
    @Column(name = "name")
	private String name = "";
    @Column(name = "longName")
    private String longName = "";
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinTable(name = "studentgroup_studyprogrammes",
            joinColumns=
            @JoinColumn(name="studentgroup_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="studyprogram_id", referencedColumnName="id")
    )
	private List<StudyProgram> studyProgrammes = new ArrayList<>();
    @Column(name = "active")
	private boolean active = true;
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinTable(name = "studentgroup_courses",
            joinColumns=
            @JoinColumn(name="studentgroup_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="course_id", referencedColumnName="id")
    )
	private List<Course> courses = new ArrayList<>();
    @Column(name = "splusId")
    private String splusId = "";
    @Column(name = "individual")
    private boolean individual = false;

    public StudentGroup(){

    }

    public StudentGroup(String name, String splusId){
        setName(name);
        setSplusId(splusId);
    }

	public StudentGroup(String name){
		setName(name);
        setSplusId(name);
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

	public boolean addStudyProgram(StudyProgram studyProgram){
	    if (!studyProgrammes.contains(studyProgram)) {
	        studyProgrammes.add(studyProgram);
            return true;
        }
        return false;
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

		return name != null ? name.equals(that.name) : that.name == null;

	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

    public boolean isIndividual() {
        return individual;
    }

    public void setIndividual(boolean individual) {
        this.individual = individual;
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
