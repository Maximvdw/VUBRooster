package be.vubrooster.ejb.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * MobileOS
 *
 * Mobile OS detector by user agent
 *
 * Created by Maxim Van de Wynckel on 11/02/2016.
 */
public enum MobileOS {
    ANDROID("Android"), // Android user agents
    IOS("iPad","iPod","iPhone","iOS"), // iOS user agents
    WINDOWS_PHONE("Windows Phone"), // Windows Phone user agents
    UNKNOWN; // Unknown user agent

    // List of possible user agent matches (.contains)
    private List<String> matches = new ArrayList<String>();

    MobileOS(String ... userAgents) {
        for (String userAgent : userAgents) {
            matches.add(userAgent);
        }
    }

    /**
     * Get the OS by its user agent
     * @param userAgent User agent
     * @return Operating System
     */
    public static MobileOS fromUserAgent(String userAgent){
        for (MobileOS os : values()){
            if (os == MobileOS.UNKNOWN){
                continue; // Ignore
            }

            for (String match : os.getMatches()){
                if (userAgent.contains(match)){
                    return os;
                }
            }
        }
        return MobileOS.UNKNOWN; // Unknown OS
    }

    /**
     * Get the OS by its name
     * @param name OS name
     */
    public static MobileOS fromName(String name){
        return MobileOS.ANDROID;
    }

    /**
     * Get possible user agent matches
     * @return User agent matches
     */
    private List<String> getMatches(){
        return this.matches;
    }

}
