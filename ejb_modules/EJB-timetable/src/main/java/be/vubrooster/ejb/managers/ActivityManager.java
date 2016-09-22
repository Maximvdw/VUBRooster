package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.FacultyServer;
import be.vubrooster.ejb.models.Activity;
import be.vubrooster.ejb.models.Faculty;
import be.vubrooster.ejb.models.StudentGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * ActivityManager
 * Created by maxim on 21-Sep-16.
 */
public class ActivityManager {
    // Logging
    public final Logger logger = LoggerFactory.getLogger(ActivityManager.class);
    // Servers
    public ActivitiyServer activitiyServer = null;
    // Cace
    public List<Activity> activityList= new ArrayList<>();

    public ActivityManager(ActivitiyServer server){
        activitiyServer = server;
    }

    /**
     * Load activities
     * @param activityList
     * @return
     */
    public List<Activity> loadActivities(List<Activity> activityList){
        this.activityList = activityList;

        return activityList;
    }

    /**
     * Add activity to cache
     *
     * @param activity activity to add
     */
    public Activity addActivity(Activity activity) {
        if (activity == null) {
            return null;
        }
        if (!activityList.contains(activity)) {
            activity.setDirty(true);
            activity.setLastUpdate(System.currentTimeMillis() / 1000);
            activity.setLastSync(System.currentTimeMillis() / 1000);
            activityList.add(activity);
            return activity;
        } else {
            Activity existingActivity = activityList.get(activityList.indexOf(activity));
            boolean change = false;
            for (StudentGroup group : activity.getGroups()) {
                // Check if the groups are added
                try {
                    if (existingActivity.addGroup(group) && !change) {
                        change = true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (change) { // You don't know if it was dirty already
                existingActivity.setLastUpdate(System.currentTimeMillis() / 1000);
                existingActivity.setDirty(true);
            }
            existingActivity.setLastSync(System.currentTimeMillis() / 1000);
            return existingActivity;
        }
    }

    /**
     * Get activity list
     * @return activity list
     */
    public List<Activity> getActivityList(){
        return activityList;
    }

    /**
     * Parse weeks
     * @param eventWeeks weeks label
     * @param weeks weeks array
     * @return list of weeks
     */
    public List<Integer> parseWeeks(String eventWeeks, List<Integer> weeks) {
        if (eventWeeks.contains(", ")) {
            // Multiple values
            String[] values = eventWeeks.split(", ");
            for (String value : values) {
                parseWeeks(value, weeks);
            }
        } else {
            // May contain range (-)
            if (eventWeeks.contains("-")) {
                // Range
                String[] range = eventWeeks.split("-");
                int range1 = Integer.parseInt(range[0]);
                int range2 = Integer.parseInt(range[1]);
                for (int l = range1; l <= range2; l++) {
                    weeks.add(l);
                }
            } else {
                weeks.add(Integer.parseInt(eventWeeks));
            }
        }
        return weeks;
    }

}
