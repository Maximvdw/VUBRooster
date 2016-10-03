package be.vubrooster.api.user.service;


/**
 * ServiceProvider
 *
 * @author Maxim Van de Wynckel
 * @date 16-Apr-16
 */
public class ServiceProvider {
    private static be.vubrooster.ejb.CommonsServer commonsServer;

    /**
     * Get the commons server
     *
     * @return Commons server
     */
    public static be.vubrooster.ejb.CommonsServer getCommonsServer() {
        if (commonsServer == null) {
            commonsServer = (be.vubrooster.ejb.CommonsServer) be.vubrooster.ejb.service.ServiceLocator.doLookup(be.vubrooster.ejb.service.ServiceLocator.COMMONS_SERVER);
        }
        return commonsServer;
    }

}
