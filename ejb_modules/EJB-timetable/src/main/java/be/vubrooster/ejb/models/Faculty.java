package be.vubrooster.ejb.models;

import javax.persistence.*;

/**
 * Faculty
 * 
 * @author Maxim Van de Wynckel
 */

@Entity
@Table(name = "faculties", indexes = {
		@Index(name = "i1", columnList = "id", unique = true),
		@Index(name = "i2", columnList = "nameDutch", unique = false),
		@Index(name = "i3", columnList = "nameEnglish", unique = false),
		@Index(name = "i4", columnList = "code", unique = true),
})
@NamedQueries({
		@NamedQuery(name = "findFaculties",
				query = "SELECT f FROM Faculty f"),
		@NamedQuery(name = "findFacultyById", query = "SELECT f FROM Faculty f WHERE f.id = :id"),
		@NamedQuery(name = "findFacultyByCode", query = "SELECT f FROM Faculty f WHERE f.code = :code AND f.code= :code"),
		@NamedQuery(name = "findFacultyByDutchName", query = "SELECT f FROM Faculty f WHERE f.nameDutch = :name"),
		@NamedQuery(name = "findFacultyByEnglishName", query = "SELECT f FROM Faculty f WHERE f.nameEnglish = :name"),
})
public class Faculty extends BaseSyncModel implements Comparable<Faculty> {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
    @Column(name = "nameDutch")
	private String nameDutch = "";
    @Column(name = "nameEnglish")
    private String nameEnglish = "";
    @Column(name = "code")
	private String code = "";
    @Column(name = "urlDutch")
	private String urlDutch = "";
    @Column(name = "urlEnglish")
	private String urlEnglish = "";

	public Faculty() {

	}

	public Faculty(String code, String nameDutch, String nameEnglish) {
		setCode(code);
		setNameEnglish(nameEnglish);
		setNameDutch(nameDutch);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int compareTo(Faculty otherFaculty) {
		if (id == otherFaculty.id) {
			return 0;
		}
		return code.compareTo(otherFaculty.getCode());
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Faculty) {
			Faculty otherFaculty = (Faculty) arg0;
			if (otherFaculty.getCode().equals(getCode())){
				return true;
			}
		}
		return false;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String shortName) {
		this.code = shortName;
	}

	public String getNameDutch() {
		return nameDutch;
	}

	public void setNameDutch(String nameDutch) {
		this.nameDutch = nameDutch;
	}

	public String getNameEnglish() {
		return nameEnglish;
	}

	public void setNameEnglish(String nameEnglish) {
		this.nameEnglish = nameEnglish;
	}

	public String getUrlDutch() {
		return urlDutch;
	}

	public void setUrlDutch(String urlDutch) {
		this.urlDutch = urlDutch;
	}

	public String getUrlEnglish() {
		return urlEnglish;
	}

	public void setUrlEnglish(String urlEnglish) {
		this.urlEnglish = urlEnglish;
	}
}
