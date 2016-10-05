package be.vubrooster.ejb;

import be.vubrooster.ejb.models.Course;
import be.vubrooster.ejb.models.CourseVariant;

import java.util.List;

/**
 * CourseServerBean
 * Created by maxim on 20-Sep-16.
 */
public interface CourseServer {
    /**
     * Get all faculty
     *
     * @param useCache use cache
     * @return faculty collection
     */
    List<Course> findCourses(boolean useCache);

    /**
     * Get all course variants
     * @return course variants
     */
    List<CourseVariant> findCourseVariants();

    /**
     * Get faculty count
     *
     * @param useCache use cache
     * @return faculty count
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
    Course findCourseById(String id, boolean useCache);

    /**
     * Create course
     *
     * @param course course to save
     * @return saved course
     */
    Course createCourse(Course course);

    /**
     * Save faculty
     *
     * @param courses faculty to save
     * @return list of faculty
     */
    List<Course> saveCourses(List<Course> courses);

    /**
     * Load faculty and put them in the cache
     */
    void loadCourses();

    /**
     * Save all cached faculty to database
     */
    void saveCourses();
}
