package be.vubrooster.ejb;

import be.vubrooster.ejb.models.ClassRoom;

import java.util.List;

/**
 * ClassRoomServerBean
 *
 * Created by maxim on 21-Sep-16.
 */
public interface ClassRoomServer {
    /**
     * Find class rooms
     *
     * @param useCache use cache
     * @return list of classes
     */
    List<ClassRoom> findClassRooms(boolean useCache);

    /**
     * Find classroom by id
     *
     * @param id identifier
     * @param useCache use cache
     * @return faculty if found
     */
    ClassRoom findClassRoomById(String id, boolean useCache);

    /**
     * Load class rooms
     */
    void loadClassRooms();

    /**
     * Save class rooms
     */
    void saveClassRooms();

    /**
     * Save class rooms
     *
     * @param classRoomList class rooms to save
     * @return saved list
     */
    List<ClassRoom> saveClassRooms(List<ClassRoom> classRoomList);
}
