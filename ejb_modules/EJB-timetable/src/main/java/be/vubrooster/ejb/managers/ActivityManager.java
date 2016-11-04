package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.models.Activity;
import be.vubrooster.ejb.models.Course;
import be.vubrooster.ejb.models.StudentGroup;
import be.vubrooster.ejb.models.StudyProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
    public List<Activity> activityList = new ArrayList<>();

    public ActivityManager(ActivitiyServer server) {
        activitiyServer = server;
    }

    /**
     * Load activities for groups
     *
     * @return future
     */
    public List<Activity> loadActivitiesForGroups(List<Activity> activityList) {
        this.activityList = activityList;

        return activityList;
    }

    /**
     * Load acitivites for staff
     *
     * @return future
     */
    public List<Activity> loadActivitiesForStaff(List<Activity> activityList) {
        this.activityList = activityList;

        return activityList;
    }

    /**
     * Load acitivites for study program
     *
     * @return future
     */
    public List<Activity> loadActivitiesForStudyProgram(List<Activity> activityList) {
        this.activityList = activityList;

        return activityList;
    }

    /**
     * Load activities for classrooms
     *
     * @return future
     */
    public List<Activity> loadActivitiesForClassRooms(List<Activity> activityList) {
        this.activityList = activityList;

        return activityList;
    }

    /**
     * Load activities for faculty
     *
     * @return future
     */
    public List<Activity> loadActivitiesForCourses(List<Activity> activityList) {
        this.activityList = activityList;

        return activityList;
    }

    /**
     * Add faculty to cache
     *
     * @param activity faculty to add
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
                if (existingActivity.addGroup(group) && !change) {
                    if (existingActivity.getId() != 0) {
                        logger.debug("\tAdded group: " + group.getName() + " to exisiting activity " + existingActivity.getId());
                    }
                    change = true;
                }
            }
            for (Course course : activity.getCourses()) {
                // Check if the course is added
                if (existingActivity.addCourse(course) && !change) {
                    if (existingActivity.getId() != 0) {
                        logger.debug("\tAdded course: " + course.getName() + " to exisiting activity " + existingActivity.getId());
                    }
                    change = true;
                }
            }
            for (StudyProgram program : activity.getStudyProgrammes()) {
                // Check if the program is added
                if (existingActivity.addStudyProgram(program) && !change) {
                    if (existingActivity.getId() != 0) {
                        logger.debug("\tAdded study program: " + program.getName() + " to exisiting activity " + existingActivity.getId());
                    }
                    change = true;
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
     * Get faculty list
     *
     * @return faculty list
     */
    public List<Activity> getActivityList() {
        return activityList;
    }

    /**
     * Parse staff
     *
     * @param eventStaff staff label
     * @param staffMembers      staffMembers array
     * @return list of weeks
     */
    public List<String> parseStaffMembers(String eventStaff, List<String> staffMembers) {
        if (eventStaff.contains(", ")) {
            // Multiple values
            String[] values = eventStaff.split(", ");
            for (String value : values) {
                parseStaffMembers(value, staffMembers);
            }
        } else {
            staffMembers.add(eventStaff);
        }
        return staffMembers;
    }

    public File saveTimetable(String directory, String name, String source){
        String fullDir  = BaseCore.getInstance().getDirectory() + "/Timetable_CACHE/" + directory + "/";
        File file = new File(fullDir);
        file.mkdirs();
        file = new File(fullDir + name + ".html");
        PrintWriter out = null;
        try{
            if (!file.exists()){
                file.createNewFile();
            }
            out = new PrintWriter(file);
            out.println(source);
        }catch (Exception ex){
            logger.error("Unable to save timetable to file: " + name,ex);
        }finally{
            if (out != null){
                out.close();
            }
        }
        return file;
    }

    public static boolean clearTempFiles(){
        try {
            String fullDir = BaseCore.getInstance().getDirectory() + "/Timetable_CACHE/";
            Path directory = Paths.get(fullDir);
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }catch (Exception ex){
            return false;
        }
        return true;
    }

    /**
     * Parse weeks
     *
     * @param eventWeeks weeks label
     * @param weeks      weeks array
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
