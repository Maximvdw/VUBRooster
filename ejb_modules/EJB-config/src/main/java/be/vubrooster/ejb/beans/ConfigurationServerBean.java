package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.ConfigurationServer;
import be.vubrooster.ejb.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * ConfigurationServerBean
 * Created by maxim on 21-Sep-16.
 */
@Startup
@Remote(ConfigurationServer.class)
@Singleton(mappedName = "ConfigurationServer")
public class ConfigurationServerBean implements ConfigurationServer{
    // Logging
    private final Logger logger = LoggerFactory.getLogger(ConfigurationServer.class);

    private final Configuration configuration = new Configuration("VUBRooster",1);

    @Override
    public String getString(String path) {
        return Configuration.getString(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return Configuration.getBoolean(path);
    }
}
