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
            faculty.setDirty(true);
            faculty.setLastUpdate(System.currentTimeMillis() / 1000);
            faculty.setLastSync(System.currentTimeMillis() / 1000);
            facultyList.add(faculty);
            return faculty;
        }else {
            Faculty existingFaculty = facultyList.get(facultyList.indexOf(faculty));
            if (!existingFaculty.getNameDutch().equalsIgnoreCase(faculty.getNameDutch())){
                existingFaculty.setDirty(true);
                existingFaculty.setLastUpdate(System.currentTimeMillis() / 1000);
                existingFaculty.setNameDutch(faculty.getNameDutch());
            }
            if (!existingFaculty.getNameEnglish().equalsIgnoreCase(faculty.getNameEnglish())){
                existingFaculty.setDirty(true);
                existingFaculty.setLastUpdate(System.currentTimeMillis() / 1000);
                existingFaculty.setNameEnglish(faculty.getNameEnglish());
            }
            if (!existingFaculty.getUrlDutch().equalsIgnoreCase(faculty.getUrlDutch())){
                existingFaculty.setDirty(true);
                existingFaculty.setLastUpdate(System.currentTimeMillis() / 1000);
                existingFaculty.setUrlDutch(faculty.getUrlDutch());
            }
            if (!existingFaculty.getUrlEnglish().equalsIgnoreCase(faculty.getUrlEnglish())){
                existingFaculty.setDirty(true);
                existingFaculty.setLastUpdate(System.currentTimeMillis() / 1000);
                existingFaculty.setUrlEnglish(faculty.getUrlEnglish());
            }
            if (!existingFaculty.getId().equalsIgnoreCase(faculty.getId())){
                existingFaculty.setDirty(true);
                existingFaculty.setLastUpdate(System.currentTimeMillis() / 1000);
                existingFaculty.setId(faculty.getId());
            }
            existingFaculty.setLastSync(System.currentTimeMillis() / 1000);
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
