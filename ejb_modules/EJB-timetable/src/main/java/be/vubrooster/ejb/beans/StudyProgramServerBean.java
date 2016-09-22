package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.StudyProgramServer;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.models.StudyProgram;
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
     * Load study programmes from faculties and put them in the cache
     */
    @Override
    public void loadStudyProgrammes() {
        // Reload database
        studyProgramList = findStudyProgrammes(false);

        studyProgramList = BaseCore.getInstance().getStudyProgramManager().loadStudyProgrammes(studyProgramList);
    }

    @Override
    public void saveStudyProgrammes() {
        studyProgramList = saveStudyProgrammes(studyProgramList);
        ServiceProvider.getStudentGroupServer().saveStudentGroups();
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
