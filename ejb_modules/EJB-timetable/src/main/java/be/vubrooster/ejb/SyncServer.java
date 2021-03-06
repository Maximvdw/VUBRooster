package be.vubrooster.ejb;

import be.vubrooster.ejb.models.Sync;

import java.util.List;

/**
 * SyncServer
 *
 * Created by maxim on 20-Sep-16.
 */
public interface SyncServer {
    /**
     * Find synchronisations
     *
     * @return list of syncs
     */
    List<Sync> findSyncs();

    /**
     * Find a sync by id
     * @param id id to get sync
     * @return sync
     */
    Sync findSyncById(int id);

    /**
     * Get the amount of syncs made
     *
     * @return syncs count
     */
    long getSyncsCount();

    /**
     * Save synchronisation stats
     *
     * @param sync synchronisation
     * @return saved sync
     */
    Sync saveSync(Sync sync);
}
