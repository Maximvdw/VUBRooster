package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.StudentGroupServer;
import be.vubrooster.ejb.StudyProgramServer;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.models.StudentGroup;
import be.vubrooster.ejb.models.StudyProgram;
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
    public StudentGroup findStudentGroupById(String id, boolean useCache) {
        if (studentGroupList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findStudentGroupById");
            query.setParameter("id", id);
            return (StudentGroup) query.uniqueResult();
        } else {
            // Use cache
            for (StudentGroup studentGroup : studentGroupList) {
                if (studentGroup.getId().equalsIgnoreCase(id)) {
                    return studentGroup;
                }
            }
            return null;
        }
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
        long startTime = ServiceProvider.getTimeTableServer().getSyncStartTime() / 1000;
        for (StudentGroup group : studentGroups) {
            List<StudyProgram> existingProgrammes = new ArrayList<>();
            for (StudyProgram program : group.getStudyProgrammes()) {
                StudyProgram existingProgram = studyProgramServer.findStudyProgramByName(program.getName(), true);
                existingProgrammes.add(existingProgram);
            }
            group.setStudyProgrammes(existingProgrammes);
            if (group.getLastSync() < startTime) {
                logger.info("Removing student group: " + group.getName());
                group.setActive(false);
                entityManager.remove(entityManager.merge(group));
            }else {
                savedStudentGroups.add(entityManager.merge(group));
            }
        }
        return savedStudentGroups;
    }

    @Override
    public void updateStudentGroup(StudentGroup group) {
        getSession().merge(group);
    }

    @Override
    public void loadStudentGroups() {
        studentGroupList = findStudentGroups(false);

        studentGroupList = BaseCore.getInstance().getStudentGroupManager().loadStudentGroups(studentGroupList);
    }

    @Override
    public void saveStudentGroups() {
        studentGroupList = saveStudentGroups(studentGroupList);
    }

    @Override
    public void assignCoursesToGroups() {
        studentGroupList = BaseCore.getInstance().getStudentGroupManager().assignCoursesToGroups(studentGroupList);
        saveStudentGroups();
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
