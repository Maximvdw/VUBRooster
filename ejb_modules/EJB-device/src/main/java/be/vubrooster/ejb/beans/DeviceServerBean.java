package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.DeviceServer;
import be.vubrooster.ejb.enums.MobileOS;
import be.vubrooster.ejb.models.Device;
import be.vubrooster.ejb.models.User;
import com.google.android.gcm.server.Message;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.Collection;

/**
 * DeviceServerBean
 *
 * @author Maxim Van de Wynckel
 * @date 05-May-16
 */
@Startup
@Remote(DeviceServer.class)
@Singleton(mappedName = "DeviceServer")
public class DeviceServerBean implements DeviceServer {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(DeviceServerBean.class);

    @PersistenceContext(name = "vubrooster")
    private EntityManager entityManager;
    private Session session = null;

    @Override
    public Collection<Device> findDevicesByUser(User user) {
        logger.info("Getting all devices for user: " + user.getId() + "");
        Query query = getSession().getNamedQuery("findDevicesByUser");
        query.setParameter("user", user);
        return query.list();
    }

    @Override
    public Device findDeviceByUID(String deviceUID) {
        Query query = getSession().getNamedQuery("findDeviceByUID");
        query.setParameter("deviceUID", deviceUID);
        return (Device) query.uniqueResult();
    }

    @Override
    public Device saveOrUpdateDevice(Device device) {
        device = (Device) getSession().merge(device);
        return device;
    }

    @Override
    public void deleteDevice(Device device) {
        getSession().delete(device);
    }

    @Override
    public boolean registerDevice(User user, String deviceUID, String deviceOS) {
        Device device = findDeviceByUID(deviceUID);
        if (device != null) {
            device = new Device();
            device.setUser(user);
            device.setOS(MobileOS.fromName(deviceOS));
            device.setDeviceId(deviceUID);
            device.setRegisterDate(new Date(System.currentTimeMillis()));
            getSession().saveOrUpdate(device);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void sendGCMNotification(Message message, Device device) {

    }

    @Override
    public void sendAPNSNotification(String payload, Device device) {

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
