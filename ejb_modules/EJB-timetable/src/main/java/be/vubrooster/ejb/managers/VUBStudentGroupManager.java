package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.StudentGroupServer;
import be.vubrooster.ejb.enums.Language;
import be.vubrooster.ejb.models.*;
import be.vubrooster.ejb.service.ServiceProvider;
import be.vubrooster.utils.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;

/**
 * VUBStudentGroupManager
 *
 * Created by maxim on 21-Sep-16.
 */
public class VUBStudentGroupManager extends StudentGroupManager{
    // TODO: 2evenjr is a variable depending on the year - automate fetching
    private String baseURL = "http://splus.cumulus.vub.ac.be:1184/2evenjr/";

    public VUBStudentGroupManager(StudentGroupServer server) {
        super(server);
    }

    @Override
    public List<StudentGroup> assignCoursesToGroups(List<StudentGroup> studentGroups) {
        // Get the faculty of the student groups using the activites
        List<Activity> activityList = ServiceProvider.getActivitiyServer().findActivities(true);
        for (Activity activity : activityList) {
            if (activity != null) {
                for (StudentGroup group : activity.getGroups()) {
                    StudentGroup existingGroup = null;
                    for (StudentGroup searchGroup : studentGroups){
                        if (searchGroup.getName().equalsIgnoreCase(group.getName())){
                            existingGroup = searchGroup;
                            break;
                        }
                    }
                    if (existingGroup != null) {
                        for (Course course : activity.getCourses()) {
                            if (!existingGroup.addCourse(course)) {
                                existingGroup.setDirty(true);
                            }
                        }
                    }
                }
            }
        }
        return studentGroups;
    }

    /**
     * Load student groups from url
     *
     * @param url          url to get them from
     * @param studyProgram studyprogram the groups belong to
     * @param faculty      faculty the groups belong to
     * @param language     language
     */
    public void loadStudentGroups(String url, StudyProgram studyProgram, Faculty faculty, Language language) {
        try {
            Document facultyPage = Jsoup.parse(HtmlUtils.sendGetRequest(url.contains("http://") ? url : getBaseURL() + url, new HashMap<String, String>()).getSource());
            Element selectionBox = facultyPage.getElementsByTag("select").first();
            Elements options = selectionBox.getElementsByTag("option");
            // Add a warning in case there are no results
            if (options.size() == 0) {
                logger.warn("No student groups '" + language.name() + "' for study program: "
                        + studyProgram.getName() + " [" + faculty.getCode() + "]");
            }

            // Load SPLUS template data
            Element splusObjectClass = facultyPage.select("[name=objectclass]").first();
            boolean individual = true;
            if (splusObjectClass.val().replace(" ", "%20").contains("group")) {
                individual = false;
            }

            for (int i = 0; i < options.size(); i++) {
                Element option = options.get(i);
                String name = option.text(); // Student group
                StudentGroup group = new StudentGroup(name);
                group.addStudyProgram(studyProgram);
                group.setIndividual(individual);
                addStudentGroup(group);
                logger.info("\t\t" + group.getName());
            }
        } catch (Exception e) {
            if (e.getClass().getName().contains("EJBComponentUnavailableException")){
                return;
            }
            logger.error("Unable to get student groups '" + language.name() + "' for study program: "
                    + studyProgram.getName() + " [" + faculty.getCode() + "]");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            logger.error("Retrying getting student groups ...");
            loadStudentGroups(url, studyProgram, faculty, language);
        }
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
}
