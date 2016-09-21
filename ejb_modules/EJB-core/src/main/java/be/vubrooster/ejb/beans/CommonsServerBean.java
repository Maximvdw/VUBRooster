package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.CommonsServer;

import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * CommmonServerBean
 *
 * @author Maxim Van de Wynckel
 * @date 17-Apr-16
 */
@Startup
@Remote(CommonsServer.class)
@Singleton(mappedName = "CommonsServer")
public class CommonsServerBean implements CommonsServer {
    private static final long startupTime = System.currentTimeMillis();

    public long getStartupTime() {
        return startupTime;
    }

    @Override
    public boolean isTestServer() {
        return false;
    }
}
