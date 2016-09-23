package be.vubrooster.ejb;

import be.vubrooster.ejb.models.StaffMember;

import java.util.List;

/**
 * StaffServer
 *
 * Created by maxim on 21-Sep-16.
 */
public interface StaffServer {
    /**
     * Find staff
     * @param useCache use cache
     * @return list of staff
     */
    List<StaffMember> findStaff(boolean useCache);

    /**
     * Load staff
     */
    void loadStaff();

    /**
     * Save staff
     */
    void saveStaff();

    /**
     * Save staff
     * @param staffList staff list
     * @return list of staff
     */
    List<StaffMember> saveStaff(List<StaffMember> staffList);
}
