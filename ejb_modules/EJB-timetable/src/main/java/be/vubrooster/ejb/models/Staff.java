package be.vubrooster.ejb.models;

import javax.persistence.*;

/**
 * Staff
 * Created by maxim on 15-Sep-16.
 */
@Entity
@Cacheable(true)
@Table(name = "staff", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
})
@NamedQueries({
        @NamedQuery(name = "findStaff",
                query = "SELECT s FROM Staff s"),
})
public class Staff extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name = "";

    public Staff(){

    }

    public Staff(String name){
        setName(name);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Staff staff = (Staff) o;

        return name != null ? name.equals(staff.name) : staff.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
