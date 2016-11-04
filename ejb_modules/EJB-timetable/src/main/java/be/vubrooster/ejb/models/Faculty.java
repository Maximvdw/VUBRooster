package be.vubrooster.ejb.models;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;

/**
 * Faculty
 *
 * @author Maxim Van de Wynckel
 */

@Entity
@Table(name = "faculties", indexes = {
        @Index(name = "i1_faculties", columnList = "id", unique = true),
        @Index(name = "i2_faculties", columnList = "nameDutch", unique = false),
        @Index(name = "i3_faculties", columnList = "nameEnglish", unique = false),
        @Index(name = "i4_faculties", columnList = "code", unique = true),
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
    @Column(name = "id")
    private String id = "";
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

    public Faculty(String id, String code, String nameDutch, String nameEnglish) {
        setId(id);
        setCode(code);
        setNameDutch(nameDutch);
        setNameEnglish(nameEnglish);
    }

    public Faculty(String code, String nameDutch, String nameEnglish) {
        setCode(code);
        setId(code);
        setNameEnglish(nameEnglish);
        setNameDutch(nameDutch);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(Faculty otherFaculty) {
        if (id.equalsIgnoreCase(otherFaculty.id)) {
            return 0;
        }
        return code.compareTo(otherFaculty.getCode());
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof Faculty) {
            Faculty otherFaculty = (Faculty) arg0;
            if (otherFaculty.getCode().equals(getCode())) {
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

    public JsonObjectBuilder toJSON() {
        return Json.createObjectBuilder()
                .add("faculty_id", id)
                .add("name_dutch", getNameDutch())
                .add("name_english", getNameEnglish())
                .add("faculty_code",getCode())
                .add("url_english",getUrlEnglish())
                .add("url_dutch",getUrlDutch());
    }
}
