package be.vubrooster.ejb;

/**
 * CommonsServer
 *
 * @author Maxim Van de Wynckel
 * @date 17-Apr-16
 */
public interface CommonsServer {
    /**
     * Get the startup date time of the
     * backend.
     *
     * @return startup date time
     */
    long getStartupTime();

    /**
     * Check if it is a test server
     *
     * @return check if test server
     */
    boolean isTestServer();
}
