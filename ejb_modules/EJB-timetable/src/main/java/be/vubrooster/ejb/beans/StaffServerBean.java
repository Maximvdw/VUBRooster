package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.StaffServer;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.models.StaffMember;
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
 * StaffServerBean
 *
 * Created by maxim on 21-Sep-16.
 */
@Startup
@Remote(StaffServer.class)
@Singleton(mappedName = "StaffServer")
public class StaffServerBean implements StaffServer{
    // Logging
    private final Logger logger = LoggerFactory.getLogger(ActivityServerBean.class);

    @PersistenceContext(name = "vubrooster")
    private EntityManager entityManager;
    private Session session = null;

    // Cache
    private List<StaffMember> staffList = new ArrayList<>();

    @Override
    public List<StaffMember> findStaff(boolean useCache) {
        if (staffList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findStaff");
            return query.list();
        } else {
            // Use cache
            return staffList;
        }
    }

    @Override
    public void loadStaff() {
        // Reload database
        staffList = findStaff(false);

        staffList = BaseCore.getInstance().getStaffManager().loadStaff(staffList);
    }

    @Override
    public void saveStaff() {
        staffList = saveStaff(staffList);
    }

    @Override
    public List<StaffMember> saveStaff(List<StaffMember> staffList) {
        List<StaffMember> savedStaff = new ArrayList<>();
        for (StaffMember staff : staffList){
            savedStaff.add((StaffMember) getSession().merge(staff));
        }
        return savedStaff;
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
