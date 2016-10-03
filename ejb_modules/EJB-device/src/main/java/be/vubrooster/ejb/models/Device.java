package be.vubrooster.ejb.models;

import be.vubrooster.ejb.enums.MobileOS;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * Device
 *
 * Holds a user device
 *
 * @author Maxim Van de Wynckel
 * @date 05-May-16
 */
@Entity
@Cacheable(true)
@Table(name = "device")
@NamedQueries({
        @NamedQuery(name = "findDevicesByUser", query = "SELECT d FROM Device d WHERE d.user = :user"),
        @NamedQuery(name = "findDeviceByUID" , query = "SELECT d FROM Device d WHERE d.deviceId = :deviceUID")
})
public class Device extends BaseModel implements Serializable{

    private long id;
    private String deviceId = "";
    private MobileOS os = MobileOS.ANDROID;
    private String alias = "";
    private User user = null;
    private Date registerDate = null;

    /**
     * Get device id
     *
     * @return device id
     */
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "DeviceId",unique = true)
    public long getId() {
        return id;
    }

    /**
     * Set device id
     *
     * @param id device id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get device OS
     * @return device os
     */
    public MobileOS getOS() {
        return os;
    }

    public void setOS(MobileOS os) {
        this.os = os;
    }

    /**
     * Get device ID
     * @return device id
     */
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Get device alias
     * @return device alias
     */
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Get the user this device belongs to
     * @return user of this device
     */
    @JoinColumn(name = "UserId", insertable = true, updatable = true)
    @ManyToOne(targetEntity = User.class, fetch= FetchType.LAZY)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get the registration date of the device
     *
     * @return registration date
     */
    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    /**
     * Convert the object to safe json
     *
     * @return JSON object
     */
    public JsonObjectBuilder toSafeJSON(){
        return Json.createObjectBuilder()
                .add("id",getId())
                .add("alias",getAlias())
                .add("device_uid",getDeviceId())
                .add("device_os",getOS().name())
                .add("registration_date",getRegisterDate().getTime());
    }
}
