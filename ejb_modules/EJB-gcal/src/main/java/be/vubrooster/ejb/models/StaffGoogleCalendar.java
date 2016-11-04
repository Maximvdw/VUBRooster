package be.vubrooster.ejb.models;

import javax.persistence.*;

/**
 * StaffGoogleCalendar
 * Created by maxim on 08-Oct-16.
 */
@Entity
@Cacheable()
@Table(name = "gcal_staff", indexes = {
        @Index(name = "i1", columnList = "id", unique = true),
})
@NamedQueries({
        @NamedQuery(name = "findStaffGcals",
                query = "SELECT c FROM StaffGoogleCalendar c"),
        @NamedQuery(name = "findStaffGcalByStaff",
                query = "SELECT c FROM StaffGoogleCalendar c WhERE c.staffMember = :staffMember"),
        @NamedQuery(name = "findNewStaffGcals",
                query = "SELECT c FROM StaffGoogleCalendar c WHERE lastSync = 0"),
})
public class StaffGoogleCalendar extends GoogleCalendar{
    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    @JoinColumn(name = "staff_id")
    private StaffMember staffMember = null;

    public StaffMember getStaffMember() {
        return staffMember;
    }

    public void setStaffMember(StaffMember staffMember) {
        this.staffMember = staffMember;
    }
}
