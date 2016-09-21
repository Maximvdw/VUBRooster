package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.FacultyServer;
import be.vubrooster.ejb.enums.Language;
import be.vubrooster.ejb.models.Faculty;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FacultyServerBean
 *
 * Created by maxim on 20-Sep-16.
 */
@Startup
@Remote(FacultyServer.class)
@Singleton(mappedName = "FacultyServer")
public class FacultyServerBean implements FacultyServer{
    // Logging
    private final Logger logger = LoggerFactory.getLogger(FacultyServerBean.class);

    @PersistenceContext(name = "vubrooster")
    private EntityManager entityManager;
    private Session session = null;

    // Base URL for faculties
    private String baseURLDutch = "https://my.vub.ac.be/les-en-examenroosters";
    private String baseURLEnglish = "https://my.vub.ac.be/en/timetables-and-exam-schedules";

    // Cache
    private List<Faculty> facultyList = new ArrayList<>();

    @Override
    public List<Faculty> findFaculties(boolean useCache) {
        if (facultyList.isEmpty() || !useCache){
            // Perform query
            Query query = getSession().getNamedQuery("findFaculties");
            return query.list();
        }else{
            // Use cache
            return facultyList;
        }
    }

    @Override
    public int getFacultiesCount(boolean useCache) {
        return facultyList.size();
    }

    @Override
    public Faculty findFacultyById(int id, boolean useCache) {
        if (facultyList.isEmpty() || !useCache){
            // Perform query
            Query query = getSession().getNamedQuery("findFacultyById");
            query.setParameter("id",id);
            return (Faculty) query.uniqueResult();
        }else{
            // Use cache
            for (Faculty faculty : facultyList){
                if (faculty.getId() == id){
                    return faculty;
                }
            }
            return null;
        }
    }

    @Override
    public Faculty findFacultyByCode(String code, boolean useCache) {
        if (facultyList.isEmpty() || !useCache){
            // Perform query
            Query query = getSession().getNamedQuery("findFacultyByCode");
            query.setParameter("code",code);
            return (Faculty) query.uniqueResult();
        }else{
            // Use cache
            for (Faculty faculty : facultyList){
                if (faculty.getCode().equals(code)){
                    return faculty;
                }
            }
            return null;
        }
    }

    @Override
    public Faculty findFacultyByDutchName(String name, boolean useCache) {
        if (facultyList.isEmpty() || !useCache){
            // Perform query
            Query query = getSession().getNamedQuery("findFacultyByDutchName");
            query.setParameter("name",name);
            return (Faculty) query.uniqueResult();
        }else{
            // Use cache
            for (Faculty faculty : facultyList){
                if (faculty.getNameDutch().equalsIgnoreCase(name)){
                    return faculty;
                }
            }
            return null;
        }
    }

    @Override
    public Faculty findFacultyByEnglishName(String name, boolean useCache) {
        if (facultyList.isEmpty() || !useCache){
            // Perform query
            Query query = getSession().getNamedQuery("findFacultyByEnglishName");
            query.setParameter("name",name);
            return (Faculty) query.uniqueResult();
        }else{
            // Use cache
            for (Faculty faculty : facultyList){
                if (faculty.getNameEnglish().equalsIgnoreCase(name)){
                    return faculty;
                }
            }
            return null;
        }
    }

    @Override
    public Faculty findFacultyByName(String name, boolean useCache) {
        Faculty faculty = findFacultyByDutchName(name,useCache);
        if (faculty == null){
            faculty = findFacultyByEnglishName(name,useCache);
        }
        return faculty;
    }

    @Override
    public Faculty createFaculty(Faculty faculty) {
        return (Faculty) getSession().merge(faculty);
    }

    @Override
    public List<Faculty> saveFaculties(List<Faculty> faculties) {
        List<Faculty> savedFaculties = new ArrayList<>();
        for (Faculty faculty : faculties){
            savedFaculties.add((Faculty) getSession().merge(faculty));
        }
        return savedFaculties;
    }

    /**
     * Add a new faculty to cache
     *
     * @param faculty faculty
     */
    private void addFaculty(Faculty faculty) {
        if (!facultyList.contains(faculty)) {
            facultyList.add(faculty);
        }
    }

    /**
     * Load english and dutch faculties and put them in the cache
     */
    @Override
    public void loadFaculties(){
        // Reload database
        facultyList = findFaculties(false);

        loadFaculties(Language.DUTCH, baseURLDutch);
        loadFaculties(Language.ENGLISH, baseURLEnglish);

        logger.info("Loaded " + facultyList.size() + " faculties!");
        for (Faculty faculty : facultyList) {
            logger.info("\t[" + faculty.getCode() + "] " + faculty.getNameDutch());
            logger.info("\t English: " + faculty.getNameEnglish());
            logger.info("\t URL Dutch: " + faculty.getUrlDutch());
            logger.info("\t URL English: " + faculty.getUrlEnglish());
        }
    }

    @Override
    public void saveFaculties() {
        facultyList = saveFaculties(facultyList);
    }

    /**
     * Load facultities for specific language and url
     * @param language language
     * @param url url to load them from
     */
    private void loadFaculties(Language language, String url) {
        try {
            Document facultiesPage = Jsoup
                    .parse(HtmlUtils.sendGetRequest(url, new HashMap<String, String>()).getSource());
            Elements rows = facultiesPage.getElementsByTag("tr");
            for (Element row : rows) {
                // Each row contains 3 columns
                // 1) The name of the faculty
                // 2) The time table link
                // 3) Exams.. just forget about this link

                Elements columns = row.getElementsByTag("td");
                if (columns.first().getElementsByTag("a").isEmpty()) {
                    continue;
                }

                // Faculty name
                String facultyName = columns.get(0).getElementsByTag("a").last().text();
                // Do some additional filtering
                facultyName = facultyName.replace("\u00A0"," ");
                facultyName = facultyName.replace("Â"," ");
                while (facultyName.contains("  ")){
                    facultyName = facultyName.replace("  "," ");
                }
                facultyName = facultyName.trim();

                // Faculty url
                String facultyURL = columns.get(1).getElementsByTag("a").first().attr("href");

                // Get the short name using the url
                // studset(.*?)_
                Pattern p = Pattern.compile("studset(.*?)_");
                Matcher m = p.matcher(facultyURL);
                String facultyShortName = "";
                while (m.find()) {
                    facultyShortName = m.group(1);
                }

                Faculty tempFaculty = findFacultyByCode(facultyShortName,true);
                if (tempFaculty == null) {
                    tempFaculty = new Faculty();
                    tempFaculty.setCode(facultyShortName);
                } else {
                    // Already exists (other dutch/english)
                    if (tempFaculty.getId() != 0) {
                        continue;
                    }
                }

                if (language == Language.DUTCH) {
                    tempFaculty.setNameDutch(facultyName);
                    tempFaculty.setUrlDutch(facultyURL);
                } else {
                    tempFaculty.setNameEnglish(facultyName);
                    tempFaculty.setUrlEnglish(facultyURL);
                }

                addFaculty(tempFaculty);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getBaseURLDutch() {
        return baseURLDutch;
    }

    public void setBaseURLDutch(String baseURLDutch) {
        this.baseURLDutch = baseURLDutch;
    }

    public String getBaseURLEnglish() {
        return baseURLEnglish;
    }

    public void setBaseURLEnglish(String baseURLEnglish) {
        this.baseURLEnglish = baseURLEnglish;
    }

    /**
     * Get hibernate session
     *
     * @return hibernate session
     */
    public Session getSession() {
        if (session != null) {
            if (!session.isOpen()){
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
