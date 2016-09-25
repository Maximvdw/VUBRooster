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

        for (Faculty faculty : faculties){
            StudyProgram studyProgram = new StudyProgram("",faculty.getNameDutch(), Language.DUTCH);
            studyProgram.setFaculty(faculty);
            addStudyProgram(studyProgram);
        }
        return getStudyProgramList();
    }
}
