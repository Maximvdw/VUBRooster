package be.vubrooster.ejb.schedulers;

import be.vubrooster.ejb.service.ServiceProvider;

/**
 * RAM watchdog
 * Created by maxim on 26-Sep-16.
 */
public class RamWatchdog implements Runnable{
    private boolean tweetTimeout = false;
    @Override
    public void run() {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        double percentage = (100. / totalMemory) * (totalMemory - freeMemory);
        if (percentage > 80 && !tweetTimeout){
            ServiceProvider.getTwitterServer().postStatus("RAM Usage high (" + ((int)percentage) + "%)" +  " (CC @" + ServiceProvider.getConfigurationServer().getString("twitter.owner") + ")");
            tweetTimeout = true;
        }else{
            tweetTimeout = false;
        }
    }
}
