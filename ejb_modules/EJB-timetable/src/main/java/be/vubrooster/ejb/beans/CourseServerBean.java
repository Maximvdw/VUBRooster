package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.CourseServer;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.models.Course;
import be.vubrooster.ejb.models.CourseVariant;
import be.vubrooster.ejb.models.TimeTable;
import be.vubrooster.ejb.service.ServiceProvider;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * CourseServerBean
 * Created by maxim on 20-Sep-16.
 */
@Startup
@Remote(CourseServer.class)
@Singleton(mappedName = "CourseServer")
public class CourseServerBean implements CourseServer {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(CourseServerBean.class);

    @PersistenceContext(name = "vubrooster")
    private EntityManager entityManager;
    private Session session = null;

    // Cache
    private List<Course> courseList = new ArrayList<Course>();

    @Override
    public List<Course> findCourses(boolean useCache) {
        if (courseList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findCourses");
            return query.list();
        } else {
            // Use cache
            return courseList;
        }
    }

    @Override
    public List<CourseVariant> findCourseVariants() {
        Query query = getSession().getNamedQuery("findCourseVariants");
        return query.list();
    }

    @Override
    public int getCoursesCount(boolean useCache) {
        return courseList.size();
    }

    @Override
    public Course findCourseByName(String name, boolean useCache) {
        if (courseList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findCourseByName");
            query.setParameter("name", name);
            return (Course) query.uniqueResult();
        } else {
            // Use cache
            for (Course course : courseList) {
                if (course.getName().equalsIgnoreCase(name)) {
                    return course;
                }
                for (CourseVariant variation : course.getVariations()) {
                    if (variation.getName().equalsIgnoreCase(name)) {
                        return course;
                    }
                }
            }
            return null;
        }
    }

    @Override
    public Course findCourseById(String id, boolean useCache) {
        if (courseList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findCourseById");
            query.setParameter("id", id);
            return (Course) query.uniqueResult();
        } else {
            // Use cache
            for (Course course : courseList) {
                if (course.getId().equals(id)) {
                    return course;
                }
            }
            return null;
        }
    }

    @Override
    public Course createCourse(Course course) {
        Course savedCourse = entityManager.merge(course);
        if (!courseList.contains(savedCourse)){
            courseList.add(savedCourse);
        }
        return savedCourse;
    }

    @Override
    public List<Course> saveCourses(List<Course> courses) {
        List<Course> savedCourses = new ArrayList<>();
        long startTime = ServiceProvider.getTimeTableServer().getSyncStartTime() / 1000;
        for (Course course : courses) {
            if (course.isDirty()) {
                Course savedCourse = (Course) getSession().merge(course);
                List<CourseVariant> savedVariants = new ArrayList<>();
                for (CourseVariant variant : course.getVariations()) {
                    variant.setCourse(savedCourse);
                    if (variant.isDirty()) {
                        savedVariants.add((CourseVariant) getSession().merge(variant));
                    }else{
                        savedVariants.add(variant);
                    }
                }
                savedCourse.setVariations(savedVariants);
                savedCourses.add(savedCourse);
            }else{
                // Check if removed
                savedCourses.add(course);
            }
        }
        return savedCourses;
    }

    /**
     * Load courses and put them in the cache
     */
    @Override
    public void loadCourses() {
        // Reload database
        courseList = findCourses(false);

        courseList = BaseCore.getInstance().getCourseManager().loadCourses(courseList);
    }

    @Override
    public void saveCourses() {
        courseList = saveCourses(courseList);
    }

    /**
     * Get hibernate session
     *
     * @return hibernate session
     */
    public Session getSession() {
        if (session != null) {
            if (!session.isOpen()) {
                session = (Session) entityManager.getDelegate();
            }
            return session;
        }
        session = (Session) entityManager.getDelegate();
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * Set entity manager
     *
     * @param em entity manager
     */
    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }
}
