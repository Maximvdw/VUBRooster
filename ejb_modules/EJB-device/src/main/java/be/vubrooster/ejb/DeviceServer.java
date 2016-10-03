package be.vubrooster.ejb;

import be.vubrooster.ejb.models.Device;
import be.vubrooster.ejb.models.User;
import com.google.android.gcm.server.Message;

import java.util.Collection;

/**
 * DeviceServer
 *
 * @author Maxim Van de Wynckel
 * @date 05-May-16
 */
public interface DeviceServer {
    /**
     * Find registered devices by user
     *
     * @param user user to get devices for
     * @return list of devices
     */
    Collection<Device> findDevicesByUser(User user);

    /**
     * Find registered device by ites UID
     * @param deviceUID device UID
     * @return Device instance
     */
    Device findDeviceByUID(String deviceUID);

    /**
     * Save or update device
     *
     * @param device device to save
     * @return saved device
     */
    Device saveOrUpdateDevice(Device device);

    /**
     * Delete a device
     *
     * @param device device to delete
     */
    void deleteDevice(Device device);

    /**
     * Register a device
     *
     * @param user user to register the device to
     * @param deviceUID device unique identifier
     * @param deviceOS device operating system
     * @return adding the device success
     */
    boolean registerDevice(User user, String deviceUID, String deviceOS);

    /**
     * Send a GCM notification
     *
     * @param message Message to send
     * @param device device to send it to
     */
    void sendGCMNotification(Message message, Device device);

    /**
     * Send an APNS message
     *
     * @param payload payload t osend
     * @param device device to send it to
     */
    void sendAPNSNotification(String payload, Device device);
}
