package be.vubrooster.ejb.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Maxim Van de Wynckel
 * @date 03-May-16
 */
public class Settings {
    final static Properties properties = new Properties();

    static{
        try (final InputStream stream = Settings.class.getResourceAsStream("../resources/settings.properties")) {
                properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a setting
     * @param key key setting
     * @return value of setting
     */
    public static String get(String key){
        return (String) properties.get(key);
    }
}