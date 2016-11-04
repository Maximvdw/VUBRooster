package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.DayMenuServer;
import be.vubrooster.ejb.managers.BaseCore;
import be.vubrooster.ejb.models.DayMenu;
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
 * DayMenuServerBean
 * <p>
 * Created by maxim on 29-Oct-16.
 */
@Startup
@Remote(DayMenuServer.class)
@Singleton(mappedName = "DayMenuServer")
public class DayMenuServerBean implements DayMenuServer {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(DayMenuServerBean.class);

    @PersistenceContext(name = "vubrooster")
    private EntityManager entityManager;
    private Session session = null;

    // Cache
    private List<DayMenu> dayMenuList = new ArrayList<>();

    @Override
    public List<DayMenu> findDayMenus(boolean useCache) {
        if (dayMenuList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findDayMenus");
            return query.list();
        } else {
            // Use cache
            return dayMenuList;
        }
    }

    @Override
    public DayMenu findDayMenuById(int id, boolean useCache) {
        if (dayMenuList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findDayMenuById");
            query.setParameter("id", id);
            return (DayMenu) query.uniqueResult();
        } else {
            // Use cache
            for (DayMenu o : dayMenuList) {
                if (o.getId() == id) {
                    return o;
                }
            }
            return null;
        }
    }

    @Override
    public List<DayMenu> findAllDayMenusForCampus(String campus, boolean useCache) {
        if (dayMenuList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findAllDayMenusForCampus");
            query.setParameter("campus",campus);
            return query.list();
        } else {
            // Use cache
            List<DayMenu> matched = new ArrayList<>();
            List<DayMenu> cache = new ArrayList<>(dayMenuList);
            for (DayMenu menu : cache){
                if (menu.getCampus().equalsIgnoreCase(campus)){
                    matched.add(menu);
                }
            }
            return matched;
        }
    }

    @Override
    public List<DayMenu> findDayMenusForCampusOnWeek(String campus, int week, boolean useCache) {
        if (dayMenuList.isEmpty() || !useCache) {
            // Perform query
            Query query = getSession().getNamedQuery("findDayMenusForCampusOnWeek");
            query.setParameter("campus",campus);
            query.setParameter("week",week);
            return query.list();
        } else {
            // Use cache
            List<DayMenu> matched = new ArrayList<>();
            List<DayMenu> cache = new ArrayList<>(dayMenuList);
            for (DayMenu menu : cache){
                if (menu.getCampus().equalsIgnoreCase(campus) && week == menu.getWeek()){
                    matched.add(menu);
                }
            }
            return matched;
        }
    }

    @Override
    public void loadDayMenus() {
        // Reload database
        dayMenuList = findDayMenus(false);

        dayMenuList = BaseCore.getInstance().getDayMenuManager().loadMenus(dayMenuList);
    }

    @Override
    public void saveDayMenus() {
        List<DayMenu> savedDayMenus = new ArrayList<>();
        for (DayMenu menu : dayMenuList) {
            savedDayMenus.add(entityManager.merge(menu));
        }
        dayMenuList = savedDayMenus;
    }

    /**
     * Get hibernate session
     *
     * @return hibernate session
     */
    public Session getSession() {
        if (session != null) {
            if (!session.isOpen()) {
                session = entityManager.unwrap(Session.class);
            }
            return session;
        }
        session = entityManager.unwrap(Session.class);
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
