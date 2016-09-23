package be.vubrooster.ejb.models;

import javax.persistence.*;

/**
 * StaffMember
 * Created by maxim on 15-Sep-16.
 */
@Entity
@Cacheable()
@Table(name = "staff", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
        @Index(name = "i2", columnList = "splusId", unique = true),
})
@NamedQueries({
        @NamedQuery(name = "findStaff",
                query = "SELECT s FROM StaffMember s"),
})
public class StaffMember extends BaseSyncModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name = "";
    @Column(name = "splusId")
    private String splusId = "";

    public StaffMember(){

    }

    public StaffMember(String name){
        setName(name);
        setSplusId(name);
    }

    public StaffMember(String name, String splusId){
        setName(name);
        setSplusId(splusId);
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

    public String getSplusId() {
        return splusId;
    }

    public void setSplusId(String splusId) {
        this.splusId = splusId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StaffMember that = (StaffMember) o;

        return splusId != null ? splusId.equals(that.splusId) : that.splusId == null;

    }

    @Override
    public int hashCode() {
        return splusId != null ? splusId.hashCode() : 0;
    }
}
