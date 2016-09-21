package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.CourseServer;
import be.vubrooster.ejb.models.Course;
import be.vubrooster.ejb.models.CourseVariant;
import be.vubrooster.utils.HtmlUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
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

    // TODO: 2evenjr is a variable depending on the year - automate fetching
    private String listURL = "http://splus.cumulus.vub.ac.be:1184/2evenjr/opleidingsonderdelen_evenjr.html";
    private String baseURL = "http://splus.cumulus.vub.ac.be:1184/reporting/spreadsheet?submit=toon+de+gegevens+-+show+the+teaching+activities&idtype=name&template=Mod%2BSS&objectclass=module%2Bgroup";

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
    public Course findCourseById(int id, boolean useCache) {
        if (courseList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findCourseById");
            query.setParameter("id", id);
            return (Course) query.uniqueResult();
        } else {
            // Use cache
            for (Course course : courseList) {
                if (course.getId() == id) {
                    return course;
                }
            }
            return null;
        }
    }

    @Override
    public Course createCourse(Course course) {
        return (Course) getSession().merge(course);
    }

    @Override
    public List<Course> saveCourses(List<Course> courses) {
        List<Course> savedCourses = new ArrayList<>();
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
     * Add course to cache
     *
     * @param course course to add
     */
    private void addCourse(Course course) {
        if (!courseList.contains(course)) {
            course.setDirty(true);
            course.setLastUpdate(System.currentTimeMillis() / 1000);
            course.setSyncDate(System.currentTimeMillis() / 1000);
            courseList.add(course);
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
            existingCourse.setSyncDate(System.currentTimeMillis() / 1000);
        }
    }

    /**
     * Load courses and put them in the cache
     */
    @Override
    public void loadCourses() {
        // Reload database
        courseList = findCourses(false);
        List<CourseVariant> courseVariants = getSession().getNamedQuery("findCourseVariants").list();
        for (CourseVariant courseVariant : courseVariants){
            Course c = findCourseByName(courseVariant.getCourse().getName(),true);
            if (!c.addVariant(courseVariant)){
                getSession().delete(courseVariant);
            }
        }

        try {
            Document subjectListPage = Jsoup.parse(HtmlUtils.sendGetRequest(getListURL(), new HashMap<>()).getSource());
            Element selectionBox = subjectListPage.getElementsByTag("select").first();
            Elements options = selectionBox.getElementsByTag("option");
            String url = getBaseURL();
            // Combining the identifier allows loading one single page
            for (int i = 0; i < options.size(); i++) {
                Element option = options.get(i);
                String name = option.text();
                url += "&identifier=" + name.replace(" ", "%20");
            }

            Document subjectsPage = Jsoup.parse(HtmlUtils.sendGetRequest(url, new HashMap<>()).getSource());
            logger.info("Parsing courses and their variation names ...");
            Elements courseInfoTables = subjectsPage.getElementsByClass("label-1-args"); // This class contains the general name of the course
            for (Element courseInfo : courseInfoTables) {
                Element courseName = courseInfo.select(".label-1-0-0").first();

                //String facultyCode = courseInfo.select(".label-1-0-4").first().html().split(" ")[1];
                Course course = new Course((courseName.html()));
                Element root = courseName.parents().get(7); // Go 8 places up
                Element table = root.nextElementSibling(); // The next table should contain the variants of the course
                // Check if the class of that table is correct
                if (table.className().equals("spreadsheet")) {
                    // Get variants
                    Elements rows = table.getElementsByTag("tr");
                    for (int j = 1; j < rows.size(); j++) {
                        Elements columns = rows.get(j).getElementsByTag("td");
                        String variantName = columns.first().html();
                        CourseVariant variant = new CourseVariant(variantName);
                        variant.setDay(columns.get(1).html());
                        variant.setStartTime(columns.get(2).html());
                        variant.setEndTime(columns.get(3).html());
                        variant.setWeeks(columns.get(5).html());
                        variant.setLector(columns.get(6).html());
                        variant.setClassRoom(columns.get(7).html());
                        course.addVariant(variant);
                    }
                }
                addCourse(course);
            }
        } catch (Exception e) {
            logger.error("Unable to load courses!");
            logger.error("Retrying loading courses ...");
            loadCourses();
        }
    }

    @Override
    public void saveCourses() {
        courseList = saveCourses(courseList);
    }

    public String getListURL() {
        return listURL;
    }

    public void setListURL(String listURL) {
        this.listURL = listURL;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
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
