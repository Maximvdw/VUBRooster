package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.UserServer;
import be.vubrooster.ejb.models.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * UserServerBean
 * Created by maxim on 21-Sep-16.
 */
@Startup
@Remote(UserServer.class)
@Singleton(mappedName = "UserServer")
public class UserServerBean implements UserServer{
    // Logging
    private final Logger logger = LoggerFactory.getLogger(UserServerBean.class);

    @PersistenceContext(name = "vubrooster")
    private EntityManager entityManager;
    private Session session = null;

    @Override
    public List<User> findUsers() {
        Query query = getSession().getNamedQuery("findUsers");
        return query.list();
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
