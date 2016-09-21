package be.vubrooster.ejb;

import be.vubrooster.ejb.models.Course;
import be.vubrooster.ejb.models.Faculty;

import java.util.List;

/**
 * CourseServerBean
 * Created by maxim on 20-Sep-16.
 */
public interface CourseServer {
    /**
     * Get all courses
     *
     * @param useCache use cache
     * @return courses collection
     */
    List<Course> findCourses(boolean useCache);

    /**
     * Get courses count
     *
     * @param useCache use cache
     * @return courses count
     */
    int getCoursesCount(boolean useCache);

    /**
     * Find course by name
     *
     * @param name course name
     * @param useCache use cache
     * @return course if found
     */
    Course findCourseByName(String name, boolean useCache);

    /**
     * Find course by id
     * @param id identifier
     * @param useCache use cache
     * @return course if found
     */
    Course findCourseById(int id, boolean useCache);

    /**
     * Create course
     *
     * @param course course to save
     * @return saved course
     */
    Course createCourse(Course course);

    /**
     * Save courses
     *
     * @param courses courses to save
     * @return list of courses
     */
    List<Course> saveCourses(List<Course> courses);

    /**
     * Load courses and put them in the cache
     */
    void loadCourses();

    /**
     * Save all cached courses to database
     */
    void saveCourses();
}
