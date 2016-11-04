package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.FacultyServer;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.models.Faculty;
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
    public Faculty findFacultyById(String id, boolean useCache) {
        if (facultyList.isEmpty() || !useCache){
            // Perform query
            Query query = getSession().getNamedQuery("findFacultyById");
            query.setParameter("id",id);
            return (Faculty) query.uniqueResult();
        }else{
            // Use cache
            for (Faculty faculty : facultyList){
                if (faculty.getId().equalsIgnoreCase("")){
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
            savedFaculties.add(entityManager.merge(faculty));
        }
        return savedFaculties;
    }

    /**
     * Load english and dutch faculties and put them in the cache
     */
    @Override
    public void loadFaculties(){
        // Reload database
        facultyList = findFaculties(false);

        facultyList = BaseCore.getInstance().getFacultyManager().loadFaculties(facultyList);
    }

    @Override
    public void saveFaculties() {
        facultyList = saveFaculties(facultyList);
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
