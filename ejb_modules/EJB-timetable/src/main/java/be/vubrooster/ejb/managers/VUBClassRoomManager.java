package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.ClassRoomServer;
import be.vubrooster.ejb.models.Activity;
import be.vubrooster.ejb.models.ClassRoom;
import be.vubrooster.ejb.service.ServiceProvider;

import java.util.List;

/**
 * VUBClassRoomManager
 * Created by maxim on 22-Sep-16.
 */
public class VUBClassRoomManager extends ClassRoomManager{
    public VUBClassRoomManager(ClassRoomServer server) {
        super(server);
    }

    @Override
    public List<ClassRoom> loadClassRooms(List<ClassRoom> classRoomList) {
        super.loadClassRooms(classRoomList);
        List<Activity> activityList = ServiceProvider.getActivitiyServer().findActivities(true);
        for (Activity activity : activityList){
            if (activity != null) {
                if (activity.getClassRoom().contains(",")) {
                    // Multiple teachers
                    String[] classRooms = activity.getClassRoom().split(",");
                    for (String classRoomString : classRooms) {
                        ClassRoom classRoom = new ClassRoom(classRoomString.trim());
                        addClassRoom(classRoom);
                    }
                } else {
                    // Only one teacher
                    ClassRoom classRoom = new ClassRoom(activity.getClassRoom());
                    addClassRoom(classRoom);
                }
            }
        }
        return getClassRoomList();
    }
}
