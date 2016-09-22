package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.FacultyServer;
import be.vubrooster.ejb.StudentGroupServer;
import be.vubrooster.ejb.StudyProgramServer;
import be.vubrooster.ejb.enums.Language;
import be.vubrooster.ejb.models.Faculty;
import be.vubrooster.ejb.models.StudentGroup;
import be.vubrooster.ejb.models.StudyProgram;
import be.vubrooster.ejb.service.ServiceProvider;

import java.util.List;

/**
 * EHBStudyProgramManager
 *
 * Created by maxim on 21-Sep-16.
 */
public class EHBStudyProgramManager extends StudyProgramManager{
    public EHBStudyProgramManager(StudyProgramServer server) {
        super(server);
    }

    @Override
    public List<StudyProgram> loadStudyProgrammes(List<StudyProgram> studyProgramList) {
        super.loadStudyProgrammes(studyProgramList);

        FacultyServer facultyServer = ServiceProvider.getFacultyServer();
        List<Faculty> faculties = facultyServer.findFaculties(true);

        StudentGroupServer studentGroupServer = ServiceProvider.getStudentGroupServer();
        List<StudentGroup> studentGroups = studentGroupServer.findStudentGroups(true);
        for (StudentGroup studentGroup : studentGroups){
            for (Faculty faculty : faculties){
                if (studentGroup.getLongName().contains(faculty.getCode())){
                    StudyProgram studyProgram = new StudyProgram("",faculty.getNameDutch(), Language.DUTCH);
                    studyProgram.setFaculty(faculty);
                    studyProgram = addStudyProgram(studyProgram);
                    studentGroup.addStudyProgram(studyProgram);
                    break;
                }
            }
        }
        return getStudyProgramList();
    }
}
