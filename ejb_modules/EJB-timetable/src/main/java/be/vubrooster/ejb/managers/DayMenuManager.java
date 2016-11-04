package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.DayMenuServer;
import be.vubrooster.ejb.models.DayMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * DayMenuManager
 * Created by maxim on 21-Sep-16.
 */
public class DayMenuManager {
    // Logging
    public final Logger logger = LoggerFactory.getLogger(DayMenuManager.class);
    // Servers
    public DayMenuServer dayMenuServer = null;
    // Cache
    public List<DayMenu> dayMenuList = new ArrayList<>();

    public DayMenuManager(DayMenuServer server){
        dayMenuServer = server;
    }

    /**
     * Load daymenus
     * @param dayMenuList
     * @return
     */
    public List<DayMenu> loadMenus(List<DayMenu> dayMenuList){
        this.dayMenuList = dayMenuList;

        return dayMenuList;
    }

    /**
     * Add daymenu to cache
     *
     * @param menu daymenu to add
     */
    public DayMenu addMenu(DayMenu menu) {
        if (!dayMenuList.contains(menu)) {
            menu.setDirty(true);
            menu.setLastUpdate(System.currentTimeMillis() / 1000);
            menu.setLastSync(System.currentTimeMillis() / 1000);
            dayMenuList.add(menu);
            return menu;
        }else{
            DayMenu existingMenu = dayMenuList.get(dayMenuList.indexOf(menu));
            existingMenu.setLastSync(System.currentTimeMillis() / 1000);
            return existingMenu;
        }
    }

    /**
     * Get staff list
     * @return staff
     */
    public List<DayMenu> getDayMenuList(){
        return dayMenuList;
    }
}
