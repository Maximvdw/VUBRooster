package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.StudentGroupServer;
import be.vubrooster.ejb.StudyProgramServer;
import be.vubrooster.ejb.enums.Language;
import be.vubrooster.ejb.models.*;
import be.vubrooster.ejb.service.ServiceProvider;
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
 * StudentGroupServerBean
 * <p>
 * Created by maxim on 20-Sep-16.
 */
@Startup
@Remote(StudentGroupServer.class)
@Singleton(mappedName = "StudentGroupServer")
public class StudentGroupServerBean implements StudentGroupServer {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(StudentGroupServerBean.class);

    @PersistenceContext(name = "vubrooster")
    private EntityManager entityManager;
    private Session session = null;

    // TODO: 2evenjr is a variable depending on the year - automate fetching
    private String baseURL = "http://splus.cumulus.vub.ac.be:1184/2evenjr/";

    // Cache
    private List<StudentGroup> studentGroupList = new ArrayList<>();

    @Override
    public List<StudentGroup> findStudentGroups(boolean useCache) {
        if (studentGroupList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findStudentGroups");
            return query.list();
        } else {
            // Use cache
            return studentGroupList;
        }
    }

    @Override
    public int getStudentGroupsCount(boolean useCache) {
        return studentGroupList.size();
    }

    @Override
    public StudentGroup findStudentGroupById(int id, boolean useCache) {
        return null;
    }

    @Override
    public StudentGroup findStudentGroupByName(String name, boolean useCache) {
        if (studentGroupList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findStudentGroupByName");
            query.setParameter("name", name);
            return (StudentGroup) query.uniqueResult();
        } else {
            // Use cache
            for (StudentGroup studentGroup : studentGroupList) {
                if (studentGroup.getName().equalsIgnoreCase(name)) {
                    return studentGroup;
                }
            }
            return null;
        }
    }

    @Override
    public List<StudentGroup> findStudentGroupsByStudyProgram(StudyProgram studyProgram, boolean useCache) {
        List<StudentGroup> groups = new ArrayList<>();
        if (studentGroupList.isEmpty() || !useCache) {
            // Perform query

        } else {
            // Use cache
            for (StudentGroup studentGroup : studentGroupList) {
                if (studentGroup.getStudyProgrammes().contains(studyProgram)) {
                    groups.add(studentGroup);
                }
            }
        }
        return groups;
    }

    @Override
    public StudentGroup createStudentGroup(StudentGroup studentGroup) {
        return (StudentGroup) getSession().merge(studentGroup);
    }

    @Override
    public List<StudentGroup> saveStudentGroups(List<StudentGroup> studentGroups) {
        List<StudentGroup> savedStudentGroups = new ArrayList<>();
        StudyProgramServer studyProgramServer = ServiceProvider.getStudyProgramServer();
        for (StudentGroup group : studentGroups) {
            List<StudyProgram> existingProgrammes = new ArrayList<>();
            for (StudyProgram program : group.getStudyProgrammes()) {
                StudyProgram existingProgram = studyProgramServer.findStudyProgramByName(program.getName(), true);
                existingProgrammes.add(existingProgram);
            }
            group.setStudyProgrammes(existingProgrammes);
            savedStudentGroups.add((StudentGroup) getSession().merge(group));
        }
        return savedStudentGroups;
    }

    /**
     * Add student group to cache
     *
     * @param group group to add
     */
    private void addStudentGroup(StudentGroup group) {
        if (!studentGroupList.contains(group)) {
            studentGroupList.add(group);
        } else {
            StudentGroup existingGroup = studentGroupList.get(studentGroupList.indexOf(group));
            for (StudyProgram program : group.getStudyProgrammes()) {
                existingGroup.addStudyProgram(program);
            }
        }
    }

    @Override
    public void assignCoursesToGroups() {
        logger.info("Assiging courses to groups ...");
        // Get the courses of the student groups using the activites
        List<Activity> activityList = ServiceProvider.getActivitiyServer().findActivities(true);
        for (Activity activity : activityList) {
            if (activity != null) {
                for (StudentGroup group : activity.getGroups()) {
                    StudentGroup existingGroup = findStudentGroupByName(group.getName(), true);
                    for (Course course : activity.getCourses()) {
                        if (!existingGroup.getCourses().contains(course)) {
                            existingGroup.getCourses().add(course);
                        }
                    }
                }
            }
        }
        saveStudentGroups();
    }

    @Override
    public void loadStudentGroups() {
        studentGroupList = findStudentGroups(false);
    }

    /**
     * Load student groups from url
     *
     * @param url          url to get them from
     * @param studyProgram studyprogram the groups belong to
     * @param faculty      faculty the groups belong to
     * @param language     language
     */
    @Override
    public void loadStudentGroups(String url, StudyProgram studyProgram, Faculty faculty, Language language) {
        try {
            Document facultyPage = Jsoup.parse(HtmlUtils.sendGetRequest(url.contains("http://") ? url : getBaseURL() + url, new HashMap<String, String>()).getSource());
            Element selectionBox = facultyPage.getElementsByTag("select").first();
            Elements options = selectionBox.getElementsByTag("option");
            // Add a warning in case there are no results
            if (options.size() == 0) {
                logger.warn("No student groups '" + language.name() + "' for study program: "
                        + studyProgram.getName() + " [" + faculty.getCode() + "]");
            }

            // Load SPLUS template data
            Element splusObjectClass = facultyPage.select("[name=objectclass]").first();
            boolean individual = true;
            if (splusObjectClass.val().replace(" ", "%20").contains("group")) {
                individual = false;
            }

            for (int i = 0; i < options.size(); i++) {
                Element option = options.get(i);
                String name = option.text(); // Student group
                StudentGroup group = new StudentGroup(name);
                group.addStudyProgram(studyProgram);
                group.setIndividual(individual);
                addStudentGroup(group);
                logger.info("\t\t" + group.getName());
            }
        } catch (Exception e) {
            if (e.getClass().getName().contains("EJBComponentUnavailableException")){
                return;
            }
            logger.error("Unable to get student groups '" + language.name() + "' for study program: "
                    + studyProgram.getName() + " [" + faculty.getCode() + "]");
            logger.error("Retrying getting student groups ...");
            loadStudentGroups(url, studyProgram, faculty, language);
        }
    }

    @Override
    public void saveStudentGroups() {
        studentGroupList = saveStudentGroups(studentGroupList);
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
