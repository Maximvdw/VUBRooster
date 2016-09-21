package be.vubrooster.ejb;

/**
 * ConfigurationServer
 *
 * Created by maxim on 21-Sep-16.
 */
public interface ConfigurationServer {
    /**
     * Get string
     * @param path path of config
     * @return string
     */
    String getString(String path);

    /**
     * Get boolean
     * @param path path of config
     * @return boolean
     */
    boolean getBoolean(String path);
}
