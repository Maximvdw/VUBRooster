package be.vubrooster.ejb.models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * @author Maxim Van de Wynckel
 * @date 13-May-16
 */
@MappedSuperclass
public abstract class BaseModel implements Serializable{
    private long syncDate = 0L;
    private long lastUpdate = 0L;

    @Transient
    private boolean dirty = false;

    public long getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(long syncDate) {
        this.syncDate = syncDate;
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