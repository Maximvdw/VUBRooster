package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.SyncServer;
import be.vubrooster.ejb.models.Sync;
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
 * SyncServerBean
 *
 * Created by maxim on 20-Sep-16.
 */
@Startup
@Remote(SyncServer.class)
@Singleton(mappedName = "SyncServer")
public class SyncServerBean implements SyncServer{
    // Logging
    private final Logger logger = LoggerFactory.getLogger(SyncServerBean.class);

    @PersistenceContext(name = "vubrooster")
    private EntityManager entityManager;
    private Session session = null;

    @Override
    public List<Sync> findSyncs() {
        return null;
    }

    @Override
    public long getSyncsCount() {
        return findSyncs().size();
    }

    @Override
    public Sync saveSync(Sync sync) {
        return (Sync) getSession().merge(sync);
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
