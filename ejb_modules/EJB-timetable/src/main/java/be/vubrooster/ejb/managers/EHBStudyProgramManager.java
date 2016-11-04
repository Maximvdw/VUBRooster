package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.FacultyServer;
import be.vubrooster.ejb.StudyProgramServer;
import be.vubrooster.ejb.enums.Language;
import be.vubrooster.ejb.models.Faculty;
import be.vubrooster.ejb.models.StudyProgram;
import be.vubrooster.ejb.service.ServiceProvider;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * EHBStudyProgramManager
 *
 * Created by maxim on 21-Sep-16.
 */
public class EHBStudyProgramManager extends StudyProgramManager{
    public EHBStudyProgramManager(StudyProgramServer server) {
        super(server);
    }

    public List<StudyProgram> loadStudyProgrammes(List<StudyProgram> studyProgramList) {
        super.loadStudyProgrammes(studyProgramList);

        FacultyServer facultyServer = ServiceProvider.getFacultyServer();
        List<Faculty> faculties = facultyServer.findFaculties(true);
        for (Faculty faculty : faculties) {
            // Get all groups from remote site
            try {
                Connection.Response res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).method(Connection.Method.GET)
                        .followRedirects(true).execute();
                if (res == null) {
                    logger.warn("Unable to get EHB study programmes from site!");
                    return getStudyProgramList();
                }
                Document docCookieFetch = res.parse();
                if (docCookieFetch == null) {
                    logger.warn("Unable to get EHB study programmes from site!");
                    return getStudyProgramList();
                }

                // Required for cookie saving
                String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
                String EVENTVALIDATION = docCookieFetch.getElementById("__EVENTVALIDATION").attr("value");
                Map<String, String> cookies = res.cookies();
                res = Jsoup.connect(EHBRooster.getBaseURL()).maxBodySize(1200000).timeout(60000).userAgent(EHBRooster.getUserAgent())
                        .data("__EVENTTARGET", "").data("__EVENTARGUMENT", "")
                        .data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
                        .data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "ProgrammesOfStudy")
                        .data("dlFilter", faculty.getId()).data("tWildcard", "").data("lbWeeks", "t").data("lbDays", "1-7")
                        .data("pDays", "1-7").data("dlPeriod", "1-56")
                        .data("RadioType", "Individual%3Bswsurl%3BSWS_EHB_IND").cookies(cookies).method(Connection.Method.POST)
                        .execute();
                Document doc = res.parse();
                if (doc == null) {
                    logger.warn("Unable to get EHB study programmes from site!");
                    return getStudyProgramList();
                }

                Element selectElement = doc.getElementById("dlObject");
                List<Element> optionElements = selectElement.getElementsByTag("option");
                int idx = 0;
                for (Element optionElement : optionElements) {
                    StudyProgram program = new StudyProgram(optionElement.text(), optionElement.attr("value"));
                    program.setFaculty(faculty);
                    program.setListIdx(idx);
                    addStudyProgram(program);
                    idx++;
                }
            } catch (IOException e) {
                logger.error("Unable to get student groups from site [#3]!", e);
            }
        }
        try {
            Connection.Response res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).method(Connection.Method.GET)
                    .followRedirects(true).execute();
            if (res == null) {
                logger.warn("Unable to get EHB study programmes from site!");
                return getStudyProgramList();
            }
            Document docCookieFetch = res.parse();
            if (docCookieFetch == null) {
                logger.warn("Unable to get EHB study programmes from site!");
                return getStudyProgramList();
            }

            // Required for cookie saving
            String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
            String EVENTVALIDATION = docCookieFetch.getElementById("__EVENTVALIDATION").attr("value");
            Map<String, String> cookies = res.cookies();
            res = Jsoup.connect(EHBRooster.getBaseURL()).maxBodySize(1200000).timeout(60000).userAgent(EHBRooster.getUserAgent())
                    .data("__EVENTTARGET", "").data("__EVENTARGUMENT", "")
                    .data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
                    .data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "ProgrammesOfStudy")
                    .data("dlFilter", "").data("tWildcard", "").data("lbWeeks", "t").data("lbDays", "1-7")
                    .data("pDays", "1-7").data("dlPeriod", "1-56")
                    .data("RadioType", "Individual%3Bswsurl%3BSWS_EHB_IND").cookies(cookies).method(Connection.Method.POST)
                    .execute();
            Document doc = res.parse();
            if (doc == null) {
                logger.warn("Unable to get EHB study programmes from site!");
                return getStudyProgramList();
            }

            Element selectElement = doc.getElementById("dlObject");
            List<Element> optionElements = selectElement.getElementsByTag("option");
            int idx = 0;
            for (Element optionElement : optionElements) {
                StudyProgram program = new StudyProgram(optionElement.text(), optionElement.attr("value"));
                program.setListIdx(idx);
                addStudyProgram(program);
                idx++;
            }
        } catch (IOException e) {
            logger.error("Unable to get student groups from site [#3]!", e);
        }
        return getStudyProgramList();
    }
}
