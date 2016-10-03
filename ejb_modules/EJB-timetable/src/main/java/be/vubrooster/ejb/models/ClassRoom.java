package be.vubrooster.ejb.models;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
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
})
@NamedQueries({
        @NamedQuery(name = "findClassRooms",
                query = "SELECT c FROM ClassRoom c"),
})
public class ClassRoom extends BaseSyncModel implements Comparable<ClassRoom>{
    @Id
    @Column(name = "id")
    private String id = "";
    @Column(name = "name")
    private String name = "";
    @Column(name = "listIdx")
    private int listIdx = 0;

    public ClassRoom(String name) {
        setName(name);
        setId(name);
    }

    public ClassRoom(String name, String splusId){
        setName(name);
        setId(splusId);
    }

    public ClassRoom() {

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

        ClassRoom classRoom = (ClassRoom) o;

        return id != null ? id.equalsIgnoreCase(classRoom.id) : classRoom.id == null;

    }


    @Override
    public int compareTo(ClassRoom o) {
        int idx1 = this.getListIdx();
        int idx2 = o.getListIdx();
        if (idx1 == idx2) {
            return 0;
        } else if (idx1 > idx2) {
            return 1;
        } else {
            return -1;
        }
    }

    public JsonObjectBuilder toJSON(){
        return Json.createObjectBuilder()
                .add("classroom_id",id)
                .add("name",getName());
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
}
