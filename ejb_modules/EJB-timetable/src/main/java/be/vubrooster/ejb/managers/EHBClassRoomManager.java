package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.ClassRoomServer;
import be.vubrooster.ejb.models.ClassRoom;
import be.vubrooster.utils.HtmlResponse;
import be.vubrooster.utils.HtmlUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Map;

/**
 * EHBClassRoomManager
 * Created by maxim on 23-Sep-16.
 */
public class EHBClassRoomManager extends ClassRoomManager{
    public EHBClassRoomManager(ClassRoomServer server) {
        super(server);
    }

    @Override
    public List<ClassRoom> loadClassRooms(List<ClassRoom> classRoomList) {
        super.loadClassRooms(classRoomList);
        try {
            Connection.Response res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).method(Connection.Method.GET)
                    .execute();
            if (res == null) {
                logger.warn("Unable to get EHB classrooms from site!");
                return getClassRoomList();
            }
            Document docCookieFetch = res.parse();
            if (docCookieFetch == null) {
                logger.warn("Unable to get EHB classrooms from site!");
                return getClassRoomList();
            }

            // Required for cookie saving
            String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
            String EVENTVALIDATION = docCookieFetch.getElementById("__EVENTVALIDATION").attr("value");
            Map<String, String> cookies = res.cookies();
            Document doc = Jsoup.connect(EHBRooster.getBaseURL()).maxBodySize(1200000).userAgent(EHBRooster.getUserAgent()).timeout(60000)
                    .data("__EVENTTARGET", "LinkBtn_Locations").data("__EVENTARGUMENT", "").data("__LASTFOCUS", "")
                    .data("__VIEWSTATE", VIEWSTATE).data("__EVENTVALIDATION", EVENTVALIDATION)
                    .data("tLinkType", "ProgrammesOfStudy").data("dlFilter", "").data("tWildcard", "")
                    .data("lbWeeks", "t").data("lbDays", "1-7").data("pDays", "1-7").data("dlPeriod", "1-56")
                    .data("RadioType", "Individual;swsurl;SWS_EHB_IND").cookies(cookies).post();
            if (doc == null) {
                logger.warn("Unable to get EHB classrooms from site!");
                return getClassRoomList();
            }
            // Jsoup doet lastig met grote lijsten....

            HtmlResponse getResponse = HtmlUtils.sendGetRequest(EHBRooster.getBaseURL(), cookies, 60000);
            doc = Jsoup.parse(getResponse.getSource());

            Element selectElement = doc.getElementById("dlObject");
            List<Element> optionElements = selectElement.children();
            int idx = 0;
            for (Element optionElement : optionElements) {
                ClassRoom classRoom = new ClassRoom(optionElement.text(), optionElement.attr("value"));
                classRoom.setListIdx(idx);
                addClassRoom(classRoom);
                idx++;
            }
        } catch (Exception ex) {
            logger.error("Unable to get classrooms from site [#3]!",ex);
        }

        return getClassRoomList();
    }
}
