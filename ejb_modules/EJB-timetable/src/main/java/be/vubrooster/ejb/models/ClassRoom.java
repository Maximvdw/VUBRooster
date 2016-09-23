package be.vubrooster.ejb.models;

import javax.persistence.*;

/**
 * ClassRoom
 * <p>
 * Created by maxim on 21-Sep-16.
 */
@Entity
@Cacheable()
@Table(name = "classrooms", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
        @Index(name = "i2", columnList = "splusId", unique = true),
})
@NamedQueries({
        @NamedQuery(name = "findClassRooms",
                query = "SELECT c FROM ClassRoom c"),
})
public class ClassRoom extends BaseSyncModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name = "";
    @Column(name = "splusId")
    private String splusId = "";

    public ClassRoom(String name) {
        setName(name);
        setSplusId(name);
    }

    public ClassRoom(String name, String splusId){
        setName(name);
        setSplusId(splusId);
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

        ClassRoom classRoom = (ClassRoom) o;

        return splusId != null ? splusId.equals(classRoom.splusId) : classRoom.splusId == null;

    }

    @Override
    public int hashCode() {
        return splusId != null ? splusId.hashCode() : 0;
    }
}
