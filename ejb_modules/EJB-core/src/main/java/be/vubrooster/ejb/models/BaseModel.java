package be.vubrooster.ejb.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxim Van de Wynckel
 * @project Friendtury
 * @date 13-May-16
 */
@MappedSuperclass
public abstract class BaseModel implements Serializable{
    @Column(name = "dateCreated")
    private long dateCreated;

    @Column(name = "dateModified")
    private long dateModified;

    @Transient
    private Map<String,Object> extraData = new HashMap<>();

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long date) {
        dateCreated = date;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long date) {
        dateModified = date;
    }

    @PreUpdate
    @PrePersist
    @PreRemove
    protected void prePersist() {
        if (dateCreated == 0) {
            dateCreated = System.currentTimeMillis();
        }
        dateModified = System.currentTimeMillis();
    }

    @PostUpdate
    @PostPersist
    @PostRemove
    protected void postPersist() {

    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }
}