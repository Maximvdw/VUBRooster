package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.StaffServer;
import be.vubrooster.ejb.models.Activity;
import be.vubrooster.ejb.models.Staff;
import be.vubrooster.ejb.service.ServiceProvider;

import java.util.List;

/**
 * VUBStaffManager
 * Created by maxim on 22-Sep-16.
 */
public class VUBStaffManager extends StaffManager{
    public VUBStaffManager(StaffServer server) {
        super(server);
    }

    @Override
    public List<Staff> loadStaff(List<Staff> staffList) {
        super.loadStaff(staffList);
        List<Activity> activityList = ServiceProvider.getActivitiyServer().findActivities(true);
        for (Activity activity : activityList){
            if (activity != null) {
                if (activity.getStaff().contains(",")) {
                    // Multiple teachers
                    String[] teachers = activity.getStaff().split(",");
                    for (String teacher : teachers) {
                        Staff staff = new Staff(teacher);
                        addStaff(staff);
                    }
                } else {
                    // Only one teacher
                    Staff staff = new Staff(activity.getStaff());
                    addStaff(staff);
                }
            }
        }
        return getStaffList();
    }
}
