package be.vubrooster.ejb.models;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Maxim Van de Wynckel
 * @date 13-May-16
 */
@MappedSuperclass
public abstract class BaseModel implements Serializable{
    private long lastSync = 0L;
    private long lastUpdate = 0L;

    @Transient
    private boolean dirty = false;

    public long getLastSync() {
        return lastSync;
    }

    public void setLastSync(long lastSync) {
        this.lastSync = lastSync;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}