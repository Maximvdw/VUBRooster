package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.StudentGroupServer;
import be.vubrooster.ejb.models.StudentGroup;
import be.vubrooster.ejb.models.StudyProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * StudentGroupManager
 * Created by maxim on 21-Sep-16.
 */
public abstract class StudentGroupManager {
    // Logging
    public final Logger logger = LoggerFactory.getLogger(StudentGroupManager.class);
    // Servers
    public StudentGroupServer studentGroupServer = null;
    // Cace
    public List<StudentGroup> studentGroupList = new ArrayList<>();

    public StudentGroupManager(StudentGroupServer server){
        studentGroupServer = server;
    }

    /**
     * Load student groups
     * @param studentGroupList
     * @return
     */
    public List<StudentGroup> loadStudentGroups(List<StudentGroup> studentGroupList){
        this.studentGroupList = studentGroupList;

        return studentGroupList;
    }

    public abstract List<StudentGroup> assignCoursesToGroups(List<StudentGroup> studentGroups);

    /**
     * Add a new student group to cache
     *
     * @param studentGroup Student group
     */
    public StudentGroup addStudentGroup(StudentGroup studentGroup) {
        if (!studentGroupList.contains(studentGroup)) {
            studentGroup.setDirty(true);
            studentGroup.setLastUpdate(System.currentTimeMillis() / 1000);
            studentGroup.setLastSync(System.currentTimeMillis() / 1000);
            studentGroupList.add(studentGroup);
            return studentGroup;
        } else {
            StudentGroup existingGroup = studentGroupList.get(studentGroupList.indexOf(studentGroup));
            boolean change = false;
            for (StudyProgram program : studentGroup.getStudyProgrammes()) {
                if (existingGroup.addStudyProgram(program) && !change){
                    change = true;
                }
            }
            if (change) { // You don't know if it was dirty already
                existingGroup.setLastUpdate(System.currentTimeMillis() / 1000);
                existingGroup.setDirty(true);
            }
            existingGroup.setLastSync(System.currentTimeMillis() / 1000);
            return existingGroup;
        }
    }

    /**
     * Get studentGroup list
     * @return studentGroup list
     */
    public List<StudentGroup> getStudentGroupList(){
        return studentGroupList;
    }
}
