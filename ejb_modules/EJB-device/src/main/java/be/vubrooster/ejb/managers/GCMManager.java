package be.vubrooster.ejb.managers;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

import java.io.IOException;

/**
 * GCMManager
 *
 * @author Maxim Van de Wynckel
 * @date 15-May-16
 */
public class GCMManager {
    private Sender sender = null;
    private int retries = 3;

    public GCMManager(){
        sender = new Sender("");
    }

    public void send(Message.Builder builder,String deviceID) throws IOException {
        sender.send(builder.build(),deviceID,getRetries());
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }
}
