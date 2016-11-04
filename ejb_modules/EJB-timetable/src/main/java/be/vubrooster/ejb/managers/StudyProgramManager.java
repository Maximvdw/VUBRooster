package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.StudyProgramServer;
import be.vubrooster.ejb.models.StudyProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * StudyProgramManager
 * Created by maxim on 21-Sep-16.
 */
public class StudyProgramManager {
    // Logging
    public final Logger logger = LoggerFactory.getLogger(StudyProgramManager.class);
    // Servers
    public StudyProgramServer studyProgramServer = null;
    // Cace
    public List<StudyProgram> studyProgramList = new ArrayList<>();

    public StudyProgramManager(StudyProgramServer server) {
        studyProgramServer = server;
    }

    /**
     * Load study programmes
     *
     * @param studyProgramList
     * @return
     */
    public List<StudyProgram> loadStudyProgrammes(List<StudyProgram> studyProgramList) {
        this.studyProgramList = studyProgramList;

        return studyProgramList;
    }

    /**
     * Add a new study program to cache
     *
     * @param studyProgram study program to add
     */
    public StudyProgram addStudyProgram(StudyProgram studyProgram) {
        if (!studyProgramList.contains(studyProgram)) {
            logger.info("\tStudy program: " + studyProgram.getName());
            studyProgram.setDirty(true);
            studyProgram.setLastUpdate(System.currentTimeMillis() / 1000);
            studyProgram.setLastSync(System.currentTimeMillis() / 1000);
            studyProgramList.add(studyProgram);
            return studyProgram;
        }else{
            StudyProgram existingProgram = studyProgramList.get(studyProgramList.indexOf(studyProgram));
            logger.info("\tStudy program: " + existingProgram.getName());
            if (existingProgram.getListIdx() != studyProgram.getListIdx()){
                existingProgram.setListIdx(studyProgram.getListIdx());
                existingProgram.setDirty(true);
                existingProgram.setLastUpdate(System.currentTimeMillis() / 1000);
            }
            existingProgram.setLastSync(System.currentTimeMillis() / 1000);
            return existingProgram;
        }
    }

    /**
     * Get study programmes list
     *
     * @return study programmes
     */
    public List<StudyProgram> getStudyProgramList() {
        return studyProgramList;
    }
}
