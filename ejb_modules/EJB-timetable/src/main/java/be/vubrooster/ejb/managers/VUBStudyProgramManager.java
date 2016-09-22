package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.StudyProgramServer;
import be.vubrooster.ejb.enums.Language;
import be.vubrooster.ejb.models.Faculty;
import be.vubrooster.ejb.models.StudyProgram;
import be.vubrooster.ejb.service.ServiceProvider;
import be.vubrooster.utils.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;

/**
 * VUBStudyProgramManager
 *
 * Created by maxim on 21-Sep-16.
 */
public class VUBStudyProgramManager extends StudyProgramManager{
    // TODO: 2evenjr is a variable depending on the year - automate fetching
    private String baseURL = "http://splus.cumulus.vub.ac.be:1184/2evenjr/";

    public VUBStudyProgramManager(StudyProgramServer server) {
        super(server);
    }

    @Override
    public List<StudyProgram> loadStudyProgrammes(List<StudyProgram> studyProgramList) {
        super.loadStudyProgrammes(studyProgramList);

        // Load the study programmes for each faculty
        List<Faculty> faculties = ServiceProvider.getFacultyServer().findFaculties(true);
        for (Faculty faculty : faculties) {
            // The base URL can contain two types of pages:
            // 1) A page showing links with seperate study programmes for
            // convenience
            // 2) A listbox to select the exact study program
            // It is the gaol to divide study programs exactly
            loadFacultyPage(faculty, Language.DUTCH);
            loadFacultyPage(faculty, Language.ENGLISH);
        }

        return getStudyProgramList();
    }

    private void loadFacultyPage(Faculty faculty, Language language) {
        String url = language == Language.DUTCH ? faculty.getUrlDutch() : faculty.getUrlEnglish();
        if (url.equals("")) {
            return;
        }
        StudyProgram studyProgram = new StudyProgram(url, language == Language.DUTCH ? faculty.getNameDutch() : faculty.getNameEnglish(), language);
        studyProgram.setFaculty(faculty);
        addStudyProgram(studyProgram);
        loadFacultyPage(url, studyProgram, faculty, language);
    }

    private void loadFacultyPage(String url, StudyProgram studyProgram, Faculty faculty, Language language) {
        // StudentGroupManager will be needed to fetch the individual groups
        try {
            Document facultyPage = Jsoup.parse(HtmlUtils.sendGetRequest(url.contains("http://") ? url : getBaseURL() + url, new HashMap<>()).getSource());
            // Check if the page contains <select> , if so it does not contain
            // any links
            if (!facultyPage.getElementsByTag("select").isEmpty()) {
                // Load the detail page
                ((VUBStudentGroupManager)BaseCore.getInstance().getStudentGroupManager()).loadStudentGroups(url, studyProgram, faculty, language);
            } else {
                // Loop through the links on the page
                Elements links = facultyPage.getElementsByTag("a");
                for (int i = 0; i < links.size(); i++) {
                    Element link = links.get(i);
                    String linkText = link.text(); // Name of the study program
                    String linkURL = link.attr("href");

                    // Check if it directs to a detail page
                    if (linkURL.startsWith("studset")) {
                        // Check if it says "English programmes"
                        // Some pages also list the programmes, but some don't
                        // so better don't fetch them
                        if (linkText.equalsIgnoreCase("English Programmes")) {
                            // Update URL
                            faculty.setUrlEnglish(linkURL.contains("http://") ? linkURL : getBaseURL() + linkURL);
                            break; // Cancel the fetching
                        }

                        studyProgram = new StudyProgram(linkURL, linkText, language);
                        if (!linkText.equals("")) { // Some ghost links may exist
                            studyProgram.setFaculty(faculty);
                            addStudyProgram(studyProgram);

                            // Load the detail page
                            loadFacultyPage(linkURL.replace(" ", "%20"), studyProgram, faculty, language);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (e.getClass().getName().contains("EJBComponentUnavailableException")){
                return;
            }
            logger.error(
                    "Unable to get faculty page '" + language.name() + "' for faculty: " + faculty.getCode());
            logger.error("Retrying getting faculty page ...");
            loadFacultyPage(url, studyProgram, faculty, language);
        }
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
}
