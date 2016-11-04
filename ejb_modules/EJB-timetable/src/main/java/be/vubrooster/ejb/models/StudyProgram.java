package be.vubrooster.ejb.models;

import be.vubrooster.ejb.enums.Language;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;

/**
 * Study program
 * 
 * @author Maxim Van de Wynckel
 */
@Entity
@Table(name = "studyprogrammes", indexes = {
        @Index(name = "i1_studyprogrammes", columnList = "id", unique = true),
        @Index(name = "i2_studyprogrammes", columnList = "name", unique = false),
})
@NamedQueries({
		@NamedQuery(name = "findStudyProgrammes",
				query = "SELECT sp FROM StudyProgram sp"),
		@NamedQuery(name = "findStudyProgramById", query = "SELECT sp FROM StudyProgram sp WHERE sp.id = :id"),
		@NamedQuery(name = "findStudyProgramByName", query = "SELECT sp FROM StudyProgram sp WHERE sp.name = :name"),
})
public class StudyProgram extends BaseSyncModel implements Comparable<StudyProgram>{
	@Id
    @Column(name = "id")
	private String id = "";
    @Column(name = "name")
	private String name = "";
    @Column(name = "language")
	private Language language = Language.DUTCH;
    @Column(name = "url")
	private String url = "";
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "faculty_id")
	private Faculty faculty = null;
    @Column(name = "listIdx")
    private int listIdx = 0;

	public StudyProgram(){

	}

	public StudyProgram(String url, String name, Language language) {
		setUrl(url);
		setName(name);
		setId(getName());
		setLanguage(language);
	}


    public StudyProgram(String name, String id) {
        setName(name);
        setId(id);
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof StudyProgram) {
			StudyProgram otherStudy = (StudyProgram) o;
			if (otherStudy.getId().equalsIgnoreCase(getId())){
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(StudyProgram o) {
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

	public Faculty getFaculty() {
		return faculty;
	}

	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

	public JsonObjectBuilder toJSON() {
		return Json.createObjectBuilder()
				.add("studyprogram_id", id)
				.add("name", getName())
				.add("language", getLanguage().name())
				.add("url",getUrl())
				.add("faculty",faculty.toJSON());
	}

    public int getListIdx() {
        return listIdx;
    }

    public void setListIdx(int listIdx) {
        this.listIdx = listIdx;
    }
}
