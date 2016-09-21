package be.vubrooster.ejb;

import be.vubrooster.ejb.models.Faculty;

import java.util.List;

/**
 * FacultyServer
 *
 * Created by maxim on 20-Sep-16.
 */
public interface FacultyServer {
    /**
     * Find all faculties
     *
     * @param useCache use cache
     * @return list of faculties
     */
    List<Faculty> findFaculties(boolean useCache);

    /**
     * Get faculties count
     * @param useCache use cache
     * @return faculties count
     */
    int getFacultiesCount(boolean useCache);

    /**
     * Find faculty by id
     *
     * @param id identifier
     * @param useCache use cache
     * @return faculty if found
     */
    Faculty findFacultyById(int id, boolean useCache);

    /**
     * Find faculty by code
     *
     * @param code faculty code
     * @param useCache use cache
     * @return faculty if found
     */
    Faculty findFacultyByCode(String code, boolean useCache);

    /**
     * Find faculty by dutch name
     *
     * @param name faculty name
     * @param useCache use cache
     * @return faculty if found
     */
    Faculty findFacultyByDutchName(String name, boolean useCache);

    /**
     * Find faculty by english name
     *
     * @param name faculty name
     * @param useCache use cache
     * @return faculty if found
     */
    Faculty findFacultyByEnglishName(String name, boolean useCache);

    /**
     * Find faculty by name
     *
     * @param name faculty name
     * @param useCache use cache
     * @return faculty if found
     */
    Faculty findFacultyByName(String name, boolean useCache);

    /**
     * Create faculty
     *
     * @param faculty faculty to create
     * @return saved faculty
     */
    Faculty createFaculty(Faculty faculty);

    /**
     * Save faculties
     *
     * @param faculties faculties to save
     * @return saved faculties
     */
    List<Faculty> saveFaculties(List<Faculty> faculties);

    /**
     * Load english and dutch faculties and put them in the cache
     */
    void loadFaculties();

    /**
     * Save all cached faculties to database
     */
    void saveFaculties();
}
