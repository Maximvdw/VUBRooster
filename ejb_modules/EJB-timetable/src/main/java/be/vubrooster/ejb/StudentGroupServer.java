package be.vubrooster.ejb;

import be.vubrooster.ejb.enums.Language;
import be.vubrooster.ejb.models.Faculty;
import be.vubrooster.ejb.models.StudentGroup;
import be.vubrooster.ejb.models.StudyProgram;

import java.util.List;

/**
 * StudentGroupServer
 *
 * Created by maxim on 20-Sep-16.
 */
public interface StudentGroupServer {
    /**
     * Find student groups
     *
     * @param useCache use cache
     * @return list of student groups
     */
    List<StudentGroup> findStudentGroups(boolean useCache);

    /**
     * Get student groups count
     *
     * @param useCache use cache
     * @return student groups count
     */
    int getStudentGroupsCount(boolean useCache);

    /**
     * Find student group by id
     *
     * @param id identifier
     * @param useCache use cache
     * @return studentgroup if found
     */
    StudentGroup findStudentGroupById(int id, boolean useCache);

    /**
     * Find student group by name
     *
     * @param name name
     * @param useCache use cache
     * @return studentgroup if found
     */
    StudentGroup findStudentGroupByName(String name, boolean useCache);

    /**
     * Find student groups by study program
     *
     * @param studyProgram study program
     * @param useCache use cache
     * @return list of student groups
     */
    List<StudentGroup> findStudentGroupsByStudyProgram(StudyProgram studyProgram, boolean useCache);

    /**
     * Create student group
     *
     * @param studentGroup student group to save
     * @return saved student group
     */
    StudentGroup createStudentGroup(StudentGroup studentGroup);

    /**
     * Save student groups
     *
     * @param studentGroups groups to save
     * @return saved student groups
     */
    List<StudentGroup> saveStudentGroups(List<StudentGroup> studentGroups);

    /**
     * Assign courses to groups
     */
    void assignCoursesToGroups();

    /**
     * Load student groups to cache
     */
    void loadStudentGroups();

    /**
     * Load student groups from url
     *
     * @param url url to get them from
     * @param studyProgram studyprogram the groups belong to
     * @param faculty faculty the groups belong to
     * @param language language
     */
    void loadStudentGroups(String url, StudyProgram studyProgram, Faculty faculty, Language language);

    /**
     * Save all cached student groups to database
     */
    void saveStudentGroups();
}
