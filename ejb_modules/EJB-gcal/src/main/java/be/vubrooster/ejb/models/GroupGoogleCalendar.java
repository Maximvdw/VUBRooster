package be.vubrooster.ejb.models;

import javax.persistence.*;

/**
 * GroupGoogleCalendar
 * Created by maxim on 08-Oct-16.
 */
@Entity
@Cacheable()
@Table(name = "gcal_group", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
})
@NamedQueries({
        @NamedQuery(name = "findGroupGcals",
                query = "SELECT c FROM GroupGoogleCalendar c"),
        @NamedQuery(name = "findGroupGcalByLocation",
                query = "SELECT c FROM GroupGoogleCalendar c WhERE c.studentGroup = :studentGroup"),
        @NamedQuery(name = "findNewGroupGcals",
                query = "SELECT c FROM GroupGoogleCalendar c WHERE lastSync = 0"),
})
public class GroupGoogleCalendar extends GoogleCalendar{
    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    @JoinColumn(name = "group_id")
    private StudentGroup studentGroup = null;

    public StudentGroup getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(StudentGroup studentGroup) {
        this.studentGroup = studentGroup;
    }
}
