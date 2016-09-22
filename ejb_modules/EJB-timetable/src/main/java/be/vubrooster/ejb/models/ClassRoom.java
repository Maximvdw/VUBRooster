package be.vubrooster.ejb.models;

import javax.persistence.*;

/**
 * ClassRoom
 * <p>
 * Created by maxim on 21-Sep-16.
 */
@Entity
@Cacheable(true)
@Table(name = "classrooms", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
})
@NamedQueries({
        @NamedQuery(name = "findClassRooms",
                query = "SELECT c FROM ClassRoom c"),
})
public class ClassRoom extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name = "";

    public ClassRoom(String name) {
        setName(name);
    }

    public ClassRoom() {

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

        ClassRoom classRoom = (ClassRoom) o;

        return name != null ? name.equals(classRoom.name) : classRoom.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
