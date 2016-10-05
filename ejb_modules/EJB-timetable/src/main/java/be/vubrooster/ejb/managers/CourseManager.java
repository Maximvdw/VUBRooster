package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.CourseServer;
import be.vubrooster.ejb.models.Course;
import be.vubrooster.ejb.models.CourseVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * CourseManager
 * Created by maxim on 21-Sep-16.
 */
public class CourseManager {
    // Logging
    public final Logger logger = LoggerFactory.getLogger(CourseManager.class);
    // Servers
    public CourseServer courseServer = null;
    // Cace
    public List<Course> courseList = new ArrayList<>();

    public CourseManager(CourseServer server){
        courseServer = server;
    }

    /**
     * Load faculty
     * @param courseList
     * @return
     */
    public List<Course> loadCourses(List<Course> courseList){
        this.courseList = courseList;

        return courseList;
    }

    /**
     * Add course to cache
     *
     * @param course course to add
     */
    public Course addCourse(Course course) {
        if (!courseList.contains(course)) {
            course.setDirty(true);
            course.setLastUpdate(System.currentTimeMillis() / 1000);
            course.setLastSync(System.currentTimeMillis() / 1000);
            courseList.add(course);
            return course;
        }else{
            Course existingCourse = courseList.get(courseList.indexOf(course));
            boolean change = false;
            for (CourseVariant variant : course.getVariations()){
                if (!existingCourse.getVariations().contains(variant)){
                    if (existingCourse.addVariant(variant) && !change){
                        change = true;
                    }
                }
            }
            if (change) { // You don't know if it was dirty already
                existingCourse.setLastUpdate(System.currentTimeMillis() / 1000);
                existingCourse.setDirty(true);
            }
            existingCourse.setLastSync(System.currentTimeMillis() / 1000);
            return existingCourse;
        }
    }

    /**
     * Get course list
     * @return faculty
     */
    public List<Course> getCourseList(){
        return courseList;
    }
}
