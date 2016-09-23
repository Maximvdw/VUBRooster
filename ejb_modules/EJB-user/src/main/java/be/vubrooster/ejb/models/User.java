package be.vubrooster.ejb.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User
 * Created by maxim on 18-Sep-16.
 */
@Entity()
@Table(name = "users", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
})
@NamedQueries({
        @NamedQuery(name = "findUsers",
                query = "SELECT u FROM User u"),
})
public class User extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "firstName")
    private String firstName = "";
    @Column(name = "surName")
    private String surName = "";
    @Column(name = "email")
    private String email = "";
    @Column(name = "publicKey")
    private String publicKey = "";
    @Column(name = "googleId")
    private String googleId = "";
    @Column(name = "googlePictureLink")
    private String googlePictureLink = "";
    @Column(name = "accessToken")
    private String accessToken = "";
    @Column(name = "refreshToken")
    private String refreshToken = "";
    @Column(name = "defaultTimetable")
    private String defaultTimetable = "";
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "user_studentgroups",
            joinColumns=
            @JoinColumn(name="user_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="studentgroup_id", referencedColumnName="id")
    )
    private List<StudentGroup> groups = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "user_hiddencourses",
            joinColumns=
            @JoinColumn(name="user_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="course_id", referencedColumnName="id")
    )
    private List<Course> hiddenCourses = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<StudentGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<StudentGroup> groups) {
        this.groups = groups;
    }

    public List<Course> getHiddenCourses() {
        return hiddenCourses;
    }

    public void setHiddenCourses(List<Course> hiddenCourses) {
        this.hiddenCourses = hiddenCourses;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getGooglePictureLink() {
        return googlePictureLink;
    }

    public void setGooglePictureLink(String googlePictureLink) {
        this.googlePictureLink = googlePictureLink;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getDefaultTimetable() {
        return defaultTimetable;
    }

    public void setDefaultTimetable(String defaultTimetable) {
        this.defaultTimetable = defaultTimetable;
    }
}
