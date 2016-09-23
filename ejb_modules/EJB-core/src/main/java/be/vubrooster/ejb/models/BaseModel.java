package be.vubrooster.ejb.models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * @author Maxim Van de Wynckel
 * @project Friendtury
 * @date 13-May-16
 */
@MappedSuperclass
public abstract class BaseModel implements Serializable{
    @Column(name = "DateCreated", nullable = true)
    private Date dateCreated;

    @Column(name = "DateModified", nullable = true)
    private Date dateModified;

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date date) {
        dateCreated = date;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date date) {
        dateModified = date;
    }

    @PreUpdate
    @PrePersist
    @PreRemove
    protected void prePersist() {
        Date now = new Date(System.currentTimeMillis());
        if (dateCreated == null) {
            dateCreated = now;
        }
        dateModified = now;
    }

    @PostUpdate
    @PostPersist
    @PostRemove
    protected void postPersist() {

    }

}