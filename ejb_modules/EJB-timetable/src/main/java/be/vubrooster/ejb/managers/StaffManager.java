package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.StaffServer;
import be.vubrooster.ejb.models.Staff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * StaffManager
 * Created by maxim on 21-Sep-16.
 */
public class StaffManager {
    // Logging
    public final Logger logger = LoggerFactory.getLogger(StaffManager.class);
    // Servers
    public StaffServer staffServer = null;
    // Cache
    public List<Staff> staffList = new ArrayList<>();

    public StaffManager(StaffServer server){
        staffServer = server;
    }

    /**
     * Load staff
     * @param staffList
     * @return
     */
    public List<Staff> loadStaff(List<Staff> staffList){
        this.staffList = staffList;

        return staffList;
    }

    /**
     * Add staff to cache
     *
     * @param staff staff to add
     */
    public Staff addStaff(Staff staff) {
        if (!staffList.contains(staff)) {
            staff.setDirty(true);
            staff.setLastUpdate(System.currentTimeMillis() / 1000);
            staff.setLastSync(System.currentTimeMillis() / 1000);
            staffList.add(staff);
            return staff;
        }else{
            Staff existingStaff = staffList.get(staffList.indexOf(staff));
            existingStaff.setLastSync(System.currentTimeMillis() / 1000);
            return existingStaff;
        }
    }

    /**
     * Get staff list
     * @return staff
     */
    public List<Staff> getStaffList(){
        return staffList;
    }
}
