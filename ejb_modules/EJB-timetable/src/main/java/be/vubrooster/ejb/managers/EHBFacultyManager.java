package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.FacultyServer;
import be.vubrooster.ejb.models.Faculty;

import java.util.List;

/**
 * EHBFacultyManager
 * Created by maxim on 21-Sep-16.
 */
public class EHBFacultyManager extends FacultyManager{

    public EHBFacultyManager(FacultyServer server) {
        super(server);
    }

    @Override
    public List<Faculty> loadFaculties(List<Faculty> facultyList) {
        super.loadFaculties(facultyList);

        addFaculty(new Faculty("DT","Design & Technologie",""));
        addFaculty(new Faculty("EDU","Onderwijs & Pedagogie",""));
        addFaculty(new Faculty("GL","Gezondheidszorg & Landschapsarchitectuur",""));
        addFaculty(new Faculty("MMM","Management, Media & Maatschappij",""));
        addFaculty(new Faculty("RITCS","Beeld, Geluid, Montage, Production Management & Podiumtechnieken",""));

        return getFacultyList();
    }
}
