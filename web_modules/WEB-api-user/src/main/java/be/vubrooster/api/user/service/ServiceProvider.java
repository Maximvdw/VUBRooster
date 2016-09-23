package be.vubrooster.api.user.service;


import be.vubrooster.ejb.CommonsServer;
import be.vubrooster.ejb.service.ServiceLocator;

/**
 * ServiceProvider
 *
 * @author Maxim Van de Wynckel
 * @date 16-Apr-16
 */
public class ServiceProvider {
    private static CommonsServer commonsServer;

    /**
     * Get the commons server
     *
     * @return Commons server
     */
    public static CommonsServer getCommonsServer() {
        if (commonsServer == null) {
            commonsServer = (CommonsServer) ServiceLocator.doLookup(ServiceLocator.COMMONS_SERVER);
        }
        return commonsServer;
    }

}
