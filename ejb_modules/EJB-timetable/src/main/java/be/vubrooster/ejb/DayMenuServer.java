package be.vubrooster.ejb;

import be.vubrooster.ejb.models.DayMenu;

import java.util.List;

/**
 * DayMenuServer
 * <p>
 * Created by maxim on 29-Oct-16.
 */
public interface DayMenuServer {

    /**
     * Find all day menus
     * @param useCache use cache
     * @return list of day menus
     */
    List<DayMenu> findDayMenus(boolean useCache);

    /**
     * Find day menu by id
     *
     * @param id       id
     * @param useCache use cache
     * @return day menu
     */
    DayMenu findDayMenuById(int id, boolean useCache);

    /**
     * Find all day menus for campus
     *
     * @param campus   campus
     * @param useCache use cache
     * @return list of day menus
     */
    List<DayMenu> findAllDayMenusForCampus(String campus, boolean useCache);

    /**
     * Find day menus for campus on week
     *
     * @param campus   campus
     * @param week     week
     * @param useCache use cache
     * @return list of day menus
     */
    List<DayMenu> findDayMenusForCampusOnWeek(String campus, int week, boolean useCache);

    /**
     * Load day menus
     */
    void loadDayMenus();

    /**
     * Save day menus
     */
    void saveDayMenus();
}
