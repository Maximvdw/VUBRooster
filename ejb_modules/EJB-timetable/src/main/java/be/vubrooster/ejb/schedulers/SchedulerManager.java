package be.vubrooster.ejb.schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SchedulerManager
 * @author Maxim Van de Wynckel
 * @date 08-May-16
 */
public class SchedulerManager {
    private static ScheduledExecutorService scheduler;

    /**
     * Create a new task
     * @param runnable task
     * @param amount amount of time units
     * @param timeUnit time unit
     */
    public static void createTask(Runnable runnable,long amount ,TimeUnit timeUnit){
        if (scheduler == null){
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        scheduler.scheduleAtFixedRate(runnable, 0, amount, timeUnit);
    }
}
