package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.StudentGroupServer;
import be.vubrooster.ejb.StudyProgramServer;
import be.vubrooster.ejb.enums.Language;
import be.vubrooster.ejb.models.Faculty;
import be.vubrooster.ejb.models.StudyProgram;
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
 * StudyProgramServerBean
 * <p>
 * Created by maxim on 20-Sep-16.
 */
@Startup
@Remote(StudyProgramServer.class)
@Singleton(mappedName = "StudyProgramServer")
public class StudyProgramServerBean implements StudyProgramServer {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(StudyProgramServerBean.class);

    @PersistenceContext(name = "vubrooster")
    private EntityManager entityManager;
    private Session session = null;

    // TODO: 2evenjr is a variable depending on the year - automate fetching
    private String baseURL = "http://splus.cumulus.vub.ac.be:1184/2evenjr/";

    // Cache
    private List<StudyProgram> studyProgramList = new ArrayList<>();

    @Override
    public List<StudyProgram> findStudyProgrammes(boolean useCache) {
        if (studyProgramList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findStudyProgrammes");
            return query.list();
        } else {
            // Use cache
            return studyProgramList;
        }
    }

    @Override
    public int getStudyProgrammesCount(boolean useCache) {
        return studyProgramList.size();
    }

    @Override
    public StudyProgram findStudyProgramById(int id, boolean useCache) {
        if (studyProgramList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findStudyProgramById");
            query.setParameter("id", id);
            return (StudyProgram) query.uniqueResult();
        } else {
            // Use cache
            for (StudyProgram studyProgram : studyProgramList) {
                if (studyProgram.getId() == id) {
                    return studyProgram;
                }
            }
            return null;
        }
    }

    @Override
    public StudyProgram findStudyProgramByName(String name, boolean useCache) {
        if (studyProgramList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findStudyProgramByName");
            query.setParameter("name", name);
            return (StudyProgram) query.uniqueResult();
        } else {
            // Use cache
            for (StudyProgram studyProgram : studyProgramList) {
                if (studyProgram.getName().equalsIgnoreCase(name)) {
                    return studyProgram;
                }
            }
            return null;
        }
    }

    @Override
    public StudyProgram createStudyProgram(StudyProgram studyProgram) {
        return (StudyProgram) getSession().merge(studyProgram);
    }

    @Override
    public List<StudyProgram> saveStudyProgrammes(List<StudyProgram> studyPrograms) {
        List<StudyProgram> savedProgrammes = new ArrayList<>();
        for (StudyProgram program : studyPrograms) {
            savedProgrammes.add((StudyProgram) getSession().merge(program));
        }
        return savedProgrammes;
    }

    /**
     * Add study program to cache
     *
     * @param studyProgram study program
     */
    private void addStudyProgram(StudyProgram studyProgram) {
        if (!studyProgramList.contains(studyProgram)) {
            logger.info("\tStudy program: " + studyProgram.getName());
            studyProgramList.add(studyProgram);
        }
    }

    /**
     * Load study programmes from faculties and put them in the cache
     */
    @Override
    public void loadStudyProgrammes() {
        // Reload database
        studyProgramList = findStudyProgrammes(false);

        // Load the study programmes for each faculty
        List<Faculty> faculties = ServiceProvider.getFacultyServer().findFaculties(true);
        for (Faculty faculty : faculties) {
            // The base URL can contain two types of pages:
            // 1) A page showing links with seperate study programmes for
            // convenience
            // 2) A listbox to select the exact study program
            // It is the gaol to divide study programs exactly
            loadFacultyPage(faculty, Language.DUTCH);
            loadFacultyPage(faculty, Language.ENGLISH);
        }
    }

    @Override
    public void saveStudyProgrammes() {
        studyProgramList = saveStudyProgrammes(studyProgramList);
    }

    private void loadFacultyPage(Faculty faculty, Language language) {
        String url = language == Language.DUTCH ? faculty.getUrlDutch() : faculty.getUrlEnglish();
        if (url.equals("")) {
            return;
        }
        StudyProgram studyProgram = new StudyProgram(url, language == Language.DUTCH ? faculty.getNameDutch() : faculty.getNameEnglish(), language);
        studyProgram.setFaculty(faculty);
        addStudyProgram(studyProgram);
        loadFacultyPage(url, studyProgram, faculty, language);
    }

    private void loadFacultyPage(String url, StudyProgram studyProgram, Faculty faculty, Language language) {
        // StudentGroupManager will be needed to fetch the individual groups
        StudentGroupServer studentGroupServer = ServiceProvider.getStudentGroupServer();
        try {
            Document facultyPage = Jsoup.parse(HtmlUtils.sendGetRequest(url.contains("http://") ? url : getBaseURL() + url, new HashMap<>()).getSource());
            // Check if the page contains <select> , if so it does not contain
            // any links
            if (!facultyPage.getElementsByTag("select").isEmpty()) {
                // Load the detail page
                studentGroupServer.loadStudentGroups(url, studyProgram, faculty, language);
            } else {
                // Loop through the links on the page
                Elements links = facultyPage.getElementsByTag("a");
                for (int i = 0; i < links.size(); i++) {
                    Element link = links.get(i);
                    String linkText = link.text(); // Name of the study program
                    String linkURL = link.attr("href");

                    // Check if it directs to a detail page
                    if (linkURL.startsWith("studset")) {
                        // Check if it says "English programmes"
                        // Some pages also list the programmes, but some don't
                        // so better don't fetch them
                        if (linkText.equalsIgnoreCase("English Programmes")) {
                            // Update URL
                            faculty.setUrlEnglish(linkURL.contains("http://") ? linkURL : getBaseURL() + linkURL);
                            break; // Cancel the fetching
                        }

                        studyProgram = new StudyProgram(linkURL, linkText, language);
                        if (!linkText.equals("")) { // Some ghost links may exist
                            studyProgram.setFaculty(faculty);
                            addStudyProgram(studyProgram);

                            // Load the detail page
                            loadFacultyPage(linkURL.replace(" ", "%20"), studyProgram, faculty, language);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (e.getClass().getName().contains("EJBComponentUnavailableException")){
                return;
            }
            logger.error(
                    "Unable to get faculty page '" + language.name() + "' for faculty: " + faculty.getCode());
            logger.error("Retrying getting faculty page ...");
            loadFacultyPage(url, studyProgram, faculty, language);
        }
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
