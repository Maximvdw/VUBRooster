package be.vubrooster.ejb.managers;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.PayloadBuilder;

/**
 * APNSManager
 *
 * @author Maxim Van de Wynckel
 * @date 15-May-16
 */
public class APNSManager {
    private ApnsService service = null;

    public APNSManager() {
        // Initialize APNS service
        service = APNS.newService()
                .withCert("/path/to/certificate.p12", "MyCertPassword")
                .withSandboxDestination()
                .build();
    }

    public void send(PayloadBuilder builder,String deviceID){
        service.push(deviceID,builder.build());
    }
}
