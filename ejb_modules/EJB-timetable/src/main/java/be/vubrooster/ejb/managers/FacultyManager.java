package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.FacultyServer;
import be.vubrooster.ejb.models.Faculty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * FacultyManager
 * Created by maxim on 21-Sep-16.
 */
public class FacultyManager {
    // Logging
    public final Logger logger = LoggerFactory.getLogger(FacultyManager.class);
    // Servers
    public FacultyServer facultyServer = null;
    // Cache
    public List<Faculty> facultyList = new ArrayList<>();

    public FacultyManager(FacultyServer server){
        facultyServer = server;
    }

    /**
     * Load faculties
     * @param facultyList
     * @return
     */
    public List<Faculty> loadFaculties(List<Faculty> facultyList){
        this.facultyList = facultyList;

        return facultyList;
    }

    /**
     * Add a new faculty to cache
     *
     * @param faculty faculty
     */
    public Faculty addFaculty(Faculty faculty) {
        if (!facultyList.contains(faculty)) {
            facultyList.add(faculty);
            return faculty;
        }else {
            Faculty existingFaculty = facultyList.get(facultyList.indexOf(faculty));
            return existingFaculty;
        }
    }

    /**
     * Get faculty list
     * @return faculty list
     */
    public List<Faculty> getFacultyList(){
        return facultyList;
    }
}
