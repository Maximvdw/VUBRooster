package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.CourseServer;
import be.vubrooster.ejb.models.Course;
import be.vubrooster.ejb.models.CourseVariant;
import be.vubrooster.utils.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;

/**
 * VUBCourseManager
 *
 * Created by maxim on 21-Sep-16.
 */
public class VUBCourseManager extends CourseManager{
    // TODO: 2evenjr is a variable depending on the year - automate fetching
    private String listURL = "http://splus.cumulus.vub.ac.be:1184/2evenjr/opleidingsonderdelen_evenjr.html";
    private String baseURL = "http://splus.cumulus.vub.ac.be:1184/reporting/spreadsheet?submit=toon+de+gegevens+-+show+the+teaching+activities&idtype=name&template=Mod%2BSS&objectclass=module%2Bgroup";

    public VUBCourseManager(CourseServer server) {
        super(server);
    }

    @Override
    public List<Course> loadCourses(List<Course> courseList) {
        super.loadCourses(courseList);

        List<CourseVariant> courseVariants = courseServer.findCourseVariants();
        for (CourseVariant courseVariant : courseVariants){
            Course course = null;
            for (Course c : courseList){
                if (c.getName().equalsIgnoreCase(courseVariant.getCourse().getName())){
                    course = c;
                    break;
                }
            }
            if (course != null) {
                if (!course.addVariant(courseVariant)) {
                    // Delete it would be a great option... but yeah
                    // its more work for you Maxim... your decision
                }
            }
        }

        try {
            Document subjectListPage = Jsoup.parse(HtmlUtils.sendGetRequest(getListURL(), new HashMap<>()).getSource());
            Element selectionBox = subjectListPage.getElementsByTag("select").first();
            Elements options = selectionBox.getElementsByTag("option");
            String url = getBaseURL();
            // Combining the identifier allows loading one single page
            for (int i = 0; i < options.size(); i++) {
                Element option = options.get(i);
                String name = option.text();
                url += "&identifier=" + name.replace(" ", "%20");
            }

            Document subjectsPage = Jsoup.parse(HtmlUtils.sendGetRequest(url, new HashMap<>()).getSource());
            logger.info("Parsing courses and their variation names ...");
            Elements courseInfoTables = subjectsPage.getElementsByClass("label-1-args"); // This class contains the general name of the course
            for (Element courseInfo : courseInfoTables) {
                Element courseName = courseInfo.select(".label-1-0-0").first();

                //String facultyCode = courseInfo.select(".label-1-0-4").first().html().split(" ")[1];
                Course course = new Course((courseName.html()));
                course.setId(course.getName() + " " + courseInfo.select(".label-1-0-4").first().html());
                Element root = courseName.parents().get(7); // Go 8 places up
                Element table = root.nextElementSibling(); // The next table should contain the variants of the course
                // Check if the class of that table is correct
                if (table.className().equals("spreadsheet")) {
                    // Get variants
                    Elements rows = table.getElementsByTag("tr");
                    for (int j = 1; j < rows.size(); j++) {
                        Elements columns = rows.get(j).getElementsByTag("td");
                        String variantName = columns.first().html().replace("\u00A0", "");
                        CourseVariant variant = new CourseVariant(variantName);
                        variant.setDay(columns.get(1).html().replace("\u00A0", ""));
                        variant.setStartTime(columns.get(2).html().replace("\u00A0", ""));
                        variant.setEndTime(columns.get(3).html().replace("\u00A0", ""));
                        variant.setWeeks(columns.get(5).html().replace("\u00A0", ""));
                        variant.setStaff(columns.get(6).html().replace("\u00A0", ""));
                        variant.setClassRoom(columns.get(7).html().replace("\u00A0", ""));
                        course.addVariant(variant);
                    }
                }
                addCourse(course);
            }
        } catch (Exception e) {
            logger.error("Unable to load courses!");
            logger.error("Retrying loading courses ...");
            loadCourses(courseList);
        }

        return getCourseList();
    }

    public String getListURL() {
        return listURL;
    }

    public void setListURL(String listURL) {
        this.listURL = listURL;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
}
