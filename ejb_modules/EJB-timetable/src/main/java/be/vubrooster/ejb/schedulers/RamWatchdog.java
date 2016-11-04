package be.vubrooster.ejb.schedulers;

import be.vubrooster.ejb.service.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RAM watchdog
 * Created by maxim on 26-Sep-16.
 */
public class RamWatchdog implements Runnable{
    // Logging
    public final Logger logger = LoggerFactory.getLogger(RamWatchdog.class);

    private boolean tweetTimeout = false;

    public RamWatchdog(){
        logger.info("[WATCHDOG] RAM watchdog started!");
    }

    @Override
    public void run() {
        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long totalFreeMemory = freeMemory + (maxMemory - allocatedMemory);

        double percentage = (100. / maxMemory) * (maxMemory - totalFreeMemory);
        if (percentage > 95 && !tweetTimeout){
            logger.warn("[WATCHDOG] Ram usage high ! " + percentage + "%");
            ServiceProvider.getTwitterServer().sendDirectMessage(ServiceProvider.getConfigurationServer().getString("twitter.owner"),"RAM Usage high (" + ((int)percentage) + "%)");
            tweetTimeout = true;
        }else{
            tweetTimeout = false;
        }
        logger.info("RAM Usage: " + (Math.round(percentage * 100) / 100.) + "% [Used " +  humanReadableByteCount(maxMemory-totalFreeMemory,false)  + "]");
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
