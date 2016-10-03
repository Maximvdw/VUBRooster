package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.FacultyServer;
import be.vubrooster.ejb.enums.Language;
import be.vubrooster.ejb.models.Faculty;
import be.vubrooster.utils.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * VUBFacultyManager
 * <p>
 * Created by maxim on 21-Sep-16.
 */
public class VUBFacultyManager extends FacultyManager {
    // Base URL for faculties
    private String baseURLDutch = "https://my.vub.ac.be/les-en-examenroosters";
    private String baseURLEnglish = "https://my.vub.ac.be/en/timetables-and-exam-schedules";

    public VUBFacultyManager(FacultyServer server) {
        super(server);
    }

    @Override
    public List<Faculty> loadFaculties(List<Faculty> facultyList) {
        super.loadFaculties(facultyList);

        boolean success = false;
        while (!success){
            success = loadFaculties(Language.DUTCH, baseURLDutch);
        }
        success = false;
        while (!success){
            success = loadFaculties(Language.ENGLISH, baseURLEnglish);
        }

        logger.info("Loaded " + facultyList.size() + " faculties!");
        for (Faculty faculty : facultyList) {
            logger.info("\t[" + faculty.getCode() + "] " + faculty.getNameDutch());
            logger.info("\t English: " + faculty.getNameEnglish());
            logger.info("\t URL Dutch: " + faculty.getUrlDutch());
            logger.info("\t URL English: " + faculty.getUrlEnglish());
        }

        return getFacultyList();
    }

    /**
     * Load facultities for specific language and url
     *
     * @param language language
     * @param url      url to load them from
     */
    private boolean loadFaculties(Language language, String url) {
        try {
            Document facultiesPage = Jsoup
                    .parse(HtmlUtils.sendGetRequest(url, new HashMap<>()).getSource());
            Elements rows = facultiesPage.getElementsByTag("tr");
            for (Element row : rows) {
                // Each row contains 3 columns
                // 1) The name of the faculty
                // 2) The time table link
                // 3) Exams.. just forget about this link

                Elements columns = row.getElementsByTag("td");
                if (columns.first().getElementsByTag("a").isEmpty()) {
                    continue;
                }

                // Faculty name
                String facultyName = columns.get(0).getElementsByTag("a").last().text();
                // Do some additional filtering
                facultyName = facultyName.replace("\u00A0", " ");
                facultyName = facultyName.replace("Â", " ");
                while (facultyName.contains("  ")) {
                    facultyName = facultyName.replace("  ", " ");
                }
                facultyName = facultyName.trim();

                // Faculty url
                String facultyURL = columns.get(1).getElementsByTag("a").first().attr("href");

                // Get the short name using the url
                // studset(.*?)_
                Pattern p = Pattern.compile("studset(.*?)_");
                Matcher m = p.matcher(facultyURL);
                String facultyShortName = "";
                while (m.find()) {
                    facultyShortName = m.group(1);
                }

                Faculty tempFaculty = null;
                for (Faculty cachedFaculty : getFacultyList()) {
                    if (cachedFaculty.getCode().equalsIgnoreCase(facultyShortName)) {
                        tempFaculty = cachedFaculty;
                        break;
                    }
                }
                if (tempFaculty == null) {
                    tempFaculty = new Faculty();
                    tempFaculty.setCode(facultyShortName);
                } else {
                    // Already exists (other dutch/english)
                    if (tempFaculty.getId() != 0) {
                        continue;
                    }
                }

                if (language == Language.DUTCH) {
                    tempFaculty.setNameDutch(facultyName);
                    tempFaculty.setUrlDutch(facultyURL);
                } else {
                    tempFaculty.setNameEnglish(facultyName);
                    tempFaculty.setUrlEnglish(facultyURL);
                }

                addFaculty(tempFaculty);
            }
            return true;
        } catch (Exception ex) {
            logger.info("Unable to load faculties. Retrying ...");
            ex.printStackTrace();
            return false;
        }
    }

    public String getBaseURLDutch() {
        return baseURLDutch;
    }

    public void setBaseURLDutch(String baseURLDutch) {
        this.baseURLDutch = baseURLDutch;
    }

    public String getBaseURLEnglish() {
        return baseURLEnglish;
    }

    public void setBaseURLEnglish(String baseURLEnglish) {
        this.baseURLEnglish = baseURLEnglish;
    }
}
