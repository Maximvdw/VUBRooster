package be.vubrooster.ejb.models;

import be.vubrooster.ejb.enums.Language;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Study program
 * 
 * @author Maxim Van de Wynckel
 */
@Entity
@Table(name = "studyprogrammes", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
        @Index(name = "i2", columnList = "name", unique = true),
})
@NamedQueries({
		@NamedQuery(name = "findStudyProgrammes",
				query = "SELECT sp FROM StudyProgram sp"),
		@NamedQuery(name = "findStudyProgramById", query = "SELECT sp FROM StudyProgram sp WHERE sp.id = :id"),
		@NamedQuery(name = "findStudyProgramByName", query = "SELECT sp FROM StudyProgram sp WHERE sp.name = :name"),
})
public class StudyProgram extends BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name = "";
	private Language language = Language.DUTCH;
	private String url = "";
    @ManyToOne(cascade = CascadeType.DETACH)
	private Faculty faculty = null;

	public StudyProgram(){

	}

	public StudyProgram(String url, String name, Language language) {
		setUrl(url);
		setName(name);
		setLanguage(language);
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
			if (otherStudy.getName().equals(getName())){
				return true;
			}
		}
		return false;
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
}
