package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.DayMenuServer;
import be.vubrooster.ejb.enums.Language;
import be.vubrooster.ejb.models.DayMenu;
import be.vubrooster.ejb.models.TimeTable;
import be.vubrooster.ejb.service.ServiceProvider;
import be.vubrooster.utils.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * VUBDayMenuManager
 * <p>
 * Created by maxim on 29-Oct-16.
 */
public class VUBDayMenuManager extends DayMenuManager {
    private static String etterbeekURL = "https://my.vub.ac.be/staff/etterbeek";
    private static String jetteURL = "https://my.vub.ac.be/staff/jette";

    private SimpleDateFormat formatter = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.forLanguageTag("NL"));

    public VUBDayMenuManager(DayMenuServer server) {
        super(server);
    }

    @Override
    public List<DayMenu> loadMenus(List<DayMenu> dayMenuList) {
        super.loadMenus(dayMenuList);
        // Set GMT
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        loadDayMenus("Etterbeek", etterbeekURL);
        loadDayMenus("Jette", jetteURL);
        return getDayMenuList();
    }

    public boolean loadDayMenus(String campus, String url) {
        try {
            Document restoPage = Jsoup
                    .parse(HtmlUtils.sendGetRequest(url, new HashMap<>()).getSource());
            Elements rows = restoPage.getElementsByClass("views-row");
            TimeTable timeTable = ServiceProvider.getTimeTableServer().getCurrentTimeTable();
            for (Element row : rows) {
                Element dateStr = row.getElementsByTag("span").first();
                Date date = new Date(formatter.parse(dateStr.html()).getTime() / 1000);
                Elements tableRows = row.getElementsByClass("tr");
                DayMenu dayMenu = new DayMenu();
                dayMenu.setCampus(campus);

                for (Element tableRow : tableRows) {
                    Elements tds = tableRow.getElementsByTag("td");
                    dayMenu.addMenu(tds.get(0).html(),tds.get(1).html());
                }

                addMenu(dayMenu);
            }
            return true;
        } catch (Exception ex) {
            logger.error("Unable to load day menus. Retrying ...", ex);
            return false;
        }
    }
}
