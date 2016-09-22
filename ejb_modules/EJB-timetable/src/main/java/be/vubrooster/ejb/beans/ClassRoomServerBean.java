package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.ClassRoomServer;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.models.ClassRoom;
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
 * ClassRoomServer
 * Created by maxim on 21-Sep-16.
 */
@Startup
@Remote(ClassRoomServer.class)
@Singleton(mappedName = "ClassRoomServer")
public class ClassRoomServerBean implements ClassRoomServer{
    // Logging
    private final Logger logger = LoggerFactory.getLogger(ActivityServerBean.class);

    @PersistenceContext(name = "vubrooster")
    private EntityManager entityManager;
    private Session session = null;

    // Cache
    private List<ClassRoom> classRoomList = new ArrayList<>();

    @Override
    public List<ClassRoom> findClassRooms(boolean useCache) {
        if (classRoomList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findClassRooms");
            return query.list();
        } else {
            // Use cache
            return classRoomList;
        }
    }

    @Override
    public void loadClassRooms() {
        // Reload database
        classRoomList = findClassRooms(false);

        classRoomList = BaseCore.getInstance().getClassRoomManager().loadClassRooms(classRoomList);
    }

    @Override
    public void saveClassRooms() {
        classRoomList = saveClassRooms(classRoomList);
    }

    @Override
    public List<ClassRoom> saveClassRooms(List<ClassRoom> classRoomList) {
        List<ClassRoom> savedClassRooms = new ArrayList<>();
        for (ClassRoom classRoom : classRoomList){
            savedClassRooms.add((ClassRoom) getSession().merge(classRoom));
        }
        return savedClassRooms;
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
