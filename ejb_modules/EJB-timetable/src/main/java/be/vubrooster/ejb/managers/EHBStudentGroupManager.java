package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.FacultyServer;
import be.vubrooster.ejb.StudentGroupServer;
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
        List<StudyProgram> studyPrograms = ServiceProvider.getStudyProgramServer().findStudyProgrammes(true);
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
            int idx = 0;
            for (Element optionElement : optionElements) {
                StudentGroup group = new StudentGroup(optionElement.text(), optionElement.attr("value"));
                String[] nameSplit = group.getName().split("/");
                String facultyCode = nameSplit[0];
                group.setLongName(group.getName());
                group.setName(filterGroupName(group.getName().substring(facultyCode.length() + 1)));
                for (StudyProgram studyProgram : studyPrograms) {
                    if (studyProgram.getFaculty().getCode().equals(facultyCode)) {
                        group.addStudyProgram(studyProgram);
                        break;
                    }
                }
                group.setListIdx(idx);
                addStudentGroup(group);
                idx++;
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
        groupName = groupName.replace("LSO/", "");
        groupName = groupName.replace("/Radio", "/Ba R");
        if (groupName.length() == 3) {
            groupName = groupName.replace("/B", "/Ba B");
            groupName = groupName.replace("/M", "/Ba M");
            groupName = groupName.replace("/G", "/Ba G");
        }
        groupName = groupName.replace("/PM", "/Ba PM");
        groupName = groupName.replace("/PT", "/Ba PT");
        groupName = groupName.replace("IIM/", "IIM");
        groupName = groupName.replace("1VKP", "1/VKP");
        groupName = groupName.replace("2VKP", "2/VKP");
        groupName = groupName.replace("3VKP", "3/VKP");
        groupName = groupName.replace("3BaDig-X S2IT", "3/BaDig-X S2IT");
        groupName = groupName.replace("1/BE HZ", "1/BE HZ ");
        groupName = groupName.replace("LTAa", "Klasgroep LTAa");
        groupName = groupName.replace("LTAb", "Klasgroep LTAb");
        groupName = groupName.replace("LTA-ERA", "ERA LTA");
        groupName = groupName.replace("LTA-WT", "Klasgroep LTA-WT");
        groupName = groupName.replace("PGD_NCZ", "1/LERPGDNCZ");
        groupName = groupName.replace("Ba AN-2-C", "ANI-2D-CULT");
        groupName = groupName.replace("Ba AN-2-F", "ANI-2D-FIL");
        groupName = groupName.replace("Ba AN-2-P", "ANI-2D-POL");
        groupName = groupName.replace("Ba AN-3-C", "ANI-3D-CULT");
        groupName = groupName.replace("Ba AN-3-F", "ANI-3D-FIL");
        groupName = groupName.replace("Ba AN-3-P", "ANI-3D-POL");
        groupName = groupName.replace("Ba AN-R-C", "ANI-R-CULT");
        groupName = groupName.replace("Ba AN-R-F", "ANI-R-FIL");
        groupName = groupName.replace("Ba AN-R-P", "ANI-R-POL");
        groupName = groupName.replace("4/VP-A", "1/VP4-A");
        groupName = groupName.replace("4/VP-B", "1/VP4-B");
        groupName = groupName.replace("4/VP-C", "1/VP4-C");
        groupName = groupName.replace("4/VP-D", "1/VP4-D");
        groupName = groupName.replace("4/VP-E", "1/VP4-E");
        groupName = groupName.replace("4/VP-F", "1/VP4-F");
        return groupName;
    }

    @Override
    public List<StudentGroup> assignCoursesToGroups(List<StudentGroup> studentGroups) {
        List<Course> courses = ServiceProvider.getCourseServer().findCourses(true);
        for (StudentGroup group : studentGroups) {
            String[] groupNameSplit = group.getLongName().split("/");
            for (Course course : courses) {
                String[] courseNameSplit = course.getLongName().split("/");
                // Check if they are the same faculty
                if (groupNameSplit[0].equals(courseNameSplit[0])) {
                    // Check if the group is inside the course name
                    if (course.getLongName().contains(group.getName() + "@") || course.getLongName().contains(group.getName() + "/") || course.getLongName().contains(group.getName() + " ")) {
                        group.addCourse(course);
                    }
                }
            }
            // Check if there is a group without courses
            if (group.getCourses().size() == 0) {
                logger.error("Group without courses: " + group.getName() + " (" + group.getLongName() + ")");
            }
        }
        return studentGroups;
    }
}
