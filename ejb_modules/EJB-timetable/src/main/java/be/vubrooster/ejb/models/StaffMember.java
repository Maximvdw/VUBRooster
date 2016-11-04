package be.vubrooster.ejb.models;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;

/**
 * StaffMember
 * Created by maxim on 15-Sep-16.
 */
@Entity
@Cacheable()
@Table(name = "staff", indexes = {
        @Index(name = "i1_staff", columnList = "id", unique = true),
})
@NamedQueries({
        @NamedQuery(name = "findStaff",
                query = "SELECT s FROM StaffMember s WHERE s.active = true"),
        @NamedQuery(name = "findStaffMemberById",
                query = "SELECT s FROM StaffMember s WHERE s.id = :id AND s.active = true"),
})
public class StaffMember extends BaseSyncModel implements Comparable<StaffMember>{
    @Id
    @Column(name = "id")
    private String id = "";
    @Column(name = "name")
    private String name = "";
    @Column(name = "listIdx")
    private int listIdx = 0;

    public StaffMember(){

    }

    public StaffMember(String name){
        setName(name);
        setId(name);
    }

    public StaffMember(String name, String splusId){
        setName(name);
        setId(splusId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StaffMember that = (StaffMember) o;

        return id != null ? id.equalsIgnoreCase(that.id) : that.id == null;

    }
    @Override
    public int compareTo(StaffMember o) {
        int idx1 = this.getListIdx();
        int idx2 = o.getListIdx();
        if (idx1 == idx2) return 0;
        else if (idx1 > idx2) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public int getListIdx() {
        return listIdx;
    }

    public void setListIdx(int listIdx) {
        this.listIdx = listIdx;
    }

    public JsonObjectBuilder toJSON() {
        return Json.createObjectBuilder()
                .add("staffmember_id", id)
                .add("name", getName());
    }
}
