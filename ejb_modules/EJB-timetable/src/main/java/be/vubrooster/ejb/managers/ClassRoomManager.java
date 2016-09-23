package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.ClassRoomServer;
import be.vubrooster.ejb.models.ClassRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassRoomManager
 * Created by maxim on 21-Sep-16.
 */
public class ClassRoomManager {
    // Logging
    public final Logger logger = LoggerFactory.getLogger(ClassRoomManager.class);
    // Servers
    public ClassRoomServer classRoomServer = null;
    // Cache
    public List<ClassRoom> classRoomList = new ArrayList<>();

    public ClassRoomManager(ClassRoomServer server){
        classRoomServer = server;
    }

    /**
     * Load class rooms
     * @param classRoomList
     * @return
     */
    public List<ClassRoom> loadClassRooms(List<ClassRoom> classRoomList){
        this.classRoomList = classRoomList;

        return classRoomList;
    }

    /**
     * Add classroom to cache
     *
     * @param classRoom staff to add
     */
    public ClassRoom addClassRoom(ClassRoom classRoom) {
        if (!classRoomList.contains(classRoom)) {
            classRoom.setDirty(true);
            classRoom.setLastUpdate(System.currentTimeMillis() / 1000);
            classRoom.setLastSync(System.currentTimeMillis() / 1000);
            classRoomList.add(classRoom);
            return classRoom;
        }else{
            ClassRoom existingClassRoom = classRoomList.get(classRoomList.indexOf(classRoom));
            existingClassRoom.setLastSync(System.currentTimeMillis() / 1000);
            return existingClassRoom;
        }
    }

    /**
     * Get classroom list
     * @return classrooms
     */
    public List<ClassRoom> getClassRoomList(){
        return classRoomList;
    }
}
