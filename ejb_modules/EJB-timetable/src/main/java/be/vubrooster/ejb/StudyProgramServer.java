package be.vubrooster.ejb;

import be.vubrooster.ejb.models.StudyProgram;

import java.util.List;

/**
 * StudyProgramServer
 *
 * Created by maxim on 20-Sep-16.
 */
public interface StudyProgramServer {
    /**
     * Find study programmes
     *
     * @param useCache use cache
     * @return list of study programmes
     */
    List<StudyProgram> findStudyProgrammes(boolean useCache);

    /**
     * Get study programmes count
     *
     * @param useCache use cache
     * @return study programmes count
     */
    int getStudyProgrammesCount(boolean useCache);

    /**
     * Find study program by id
     *
     * @param id identifier
     * @param useCache use cache
     * @return study program if found
     */
    StudyProgram findStudyProgramById(int id, boolean useCache);

    /**
     * Find study program by name
     *
     * @param name name
     * @param useCache use cache
     * @return study program if found
     */
    StudyProgram findStudyProgramByName(String name, boolean useCache);

    /**
     * Create study program
     *
     * @param studyProgram study program to save
     * @return saved study program
     */
    StudyProgram createStudyProgram(StudyProgram studyProgram);

    /**
     * Save study programmes
     *
     * @param studyPrograms study programmes to save
     * @return study programmes to save
     */
    List<StudyProgram> saveStudyProgrammes(List<StudyProgram> studyPrograms);

    /**
     * Load study programmes from faculties and put them in the cache
     */
    void loadStudyProgrammes();

    /**
     * Save all cached study programmes to database
     */
    void saveStudyProgrammes();
}
