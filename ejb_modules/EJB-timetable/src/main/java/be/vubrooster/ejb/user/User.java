package be.vubrooster.ejb.user;

import be.vubrooster.ejb.models.StudentGroup;
import be.vubrooster.ejb.models.Course;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User
 * Created by maxim on 18-Sep-16.
 */
@Entity(name = "users")
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String privateKey = "";
    private String publicKey = "";
    @OneToMany(cascade = CascadeType.DETACH)
    private List<StudentGroup> groups = new ArrayList<>();
    @OneToMany(cascade = CascadeType.DETACH)
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
