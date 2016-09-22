package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.FacultyServer;
import be.vubrooster.ejb.StudentGroupServer;
import be.vubrooster.ejb.StudyProgramServer;
import be.vubrooster.ejb.models.Course;
import be.vubrooster.ejb.models.Faculty;
import be.vubrooster.ejb.models.StudentGroup;
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
 * EHBStudentGroupManager
 * <p>
 * Created by maxim on 21-Sep-16.
 */
public class EHBStudentGroupManager extends StudentGroupManager {
    public EHBStudentGroupManager(StudentGroupServer server) {
        super(server);
    }

    @Override
    public List<StudentGroup> loadStudentGroups(List<StudentGroup> studentGroupList) {
        super.loadStudentGroups(studentGroupList);

        FacultyServer facultyServer = ServiceProvider.getFacultyServer();
        List<Faculty> faculties = facultyServer.findFaculties(true);
        // Get all groups from remote site
        try {
            Connection.Response res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).method(Connection.Method.GET)
                    .followRedirects(true).execute();
            if (res == null) {
                logger.warn("Unable to get EHB groups from site!");
                return getStudentGroupList();
            }
            Document docCookieFetch = res.parse();
            if (docCookieFetch == null) {
                logger.warn("Unable to get EHB groups from site!");
                return getStudentGroupList();
            }

            // Required for cookie saving
            String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
            String EVENTVALIDATION = docCookieFetch.getElementById("__EVENTVALIDATION").attr("value");
            Map<String, String> cookies = res.cookies();
            res = Jsoup.connect(EHBRooster.getBaseURL()).maxBodySize(1200000).timeout(60000).userAgent(EHBRooster.getUserAgent())
                    .data("__EVENTTARGET", "LinkBtn_StudentSetGroups").data("__EVENTARGUMENT", "")
                    .data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
                    .data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "ProgrammesOfStudy")
                    .data("dlFilter", "").data("tWildcard", "").data("lbWeeks", "t").data("lbDays", "1-7")
                    .data("pDays", "1-7").data("dlPeriod", "1-56")
                    .data("RadioType", "Individual%3Bswsurl%3BSWS_EHB_IND").cookies(cookies).method(Connection.Method.POST)
                    .execute();
            Document doc = res.parse();
            if (doc == null) {
                logger.warn("Unable to get EHB groups from site!");
                return getStudentGroupList();
            }

            Element selectElement = doc.getElementById("dlObject");
            List<Element> optionElements = selectElement.getElementsByTag("option");
            for (Element optionElement : optionElements) {
                StudentGroup group = new StudentGroup(optionElement.text(), optionElement.attr("value"));
                String[] nameSplit = group.getName().split("/");
                String facultyCode = nameSplit[0];
                for (Faculty faculty : faculties) {
                    if (faculty.getCode().equalsIgnoreCase(facultyCode)) {
                        group.setLongName(group.getName());
                        group.setName(filterGroupName(group.getName().substring(facultyCode.length() + 1)));
                    }
                }
                addStudentGroup(group);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return getStudentGroupList();
    }

    public static String filterGroupName(String groupName) {
        groupName = groupName.replace("1BaDig", "1/BaDig");
        groupName = groupName.replace("2BaDig-X S2IT", "2/BaDig-X S2IT");
        groupName = groupName.replace("2BaDig-X-BIT", "2BaDig-X- BIT");
        groupName = groupName.replace("/KO/K", "/K");
        groupName = groupName.replace("/LO/L", "/L");
        groupName = groupName.replace("LSO/BE ", "");
        groupName = groupName.replace("LSO/AA ", "");
        groupName = groupName.replace("/PT", "/Ba PT");
        groupName = groupName.replace("IIM/", "IIM");
        groupName = groupName.replace("1VKP", "1/VKP");
        groupName = groupName.replace("2VKP", "2/VKP");
        groupName = groupName.replace("3VKP", "3/VKP");
        groupName = groupName.replace("3BaDig-X S2IT", "3/BaDig-X S2IT");
        return groupName;
    }

    @Override
    public List<StudentGroup> assignCoursesToGroups(List<StudentGroup> studentGroups) {
        List<Course> courses = ServiceProvider.getCourseServer().findCourses(true);
        for (StudentGroup group : studentGroups){
            String[] groupNameSplit = group.getLongName().split("/");
            for (Course course : courses){
                String[] courseNameSplit = course.getLongName().split("/");
                // Check if they are the same faculty
                if (groupNameSplit[0].equals(courseNameSplit[0])){
                    // Check if the group is inside the course name
                    if (course.getLongName().contains(group.getName() + "@") || course.getLongName().contains(group.getName() + "/")  || course.getLongName().contains(group.getName() + " ")) {
                        group.getCourses().add(course);
                    }
                }
            }
            // Check if there is a group without courses
            if (group.getCourses().size() == 0){
                logger.error("Group without courses: " + group.getName());
            }
        }
        return studentGroups;
    }
}
