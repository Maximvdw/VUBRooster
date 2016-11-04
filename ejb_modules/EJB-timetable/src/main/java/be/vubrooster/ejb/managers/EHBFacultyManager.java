package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.FacultyServer;
import be.vubrooster.ejb.models.Faculty;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        addFaculty(new Faculty("DT","Design & Technologie","Design & Technology"));
        addFaculty(new Faculty("EDU","Onderwijs & Pedagogie",""));
        addFaculty(new Faculty("GL","Gezondheidszorg & Landschapsarchitectuur",""));
        addFaculty(new Faculty("MMM","Management, Media & Maatschappij",""));
        addFaculty(new Faculty("RITCS","Beeld, Geluid, Montage, Production Management & Podiumtechnieken",""));
        addFaculty(new Faculty("KCB","Koninklijk Conservatorium Brussel",""));

        // Fetch the ids of the faculties
        Map<String,String> idMap = fetchFacultyIDs();

        // Fetch the correct ID
        for (Faculty faculty : getFacultyList()){
            String id = idMap.get(faculty.getCode());
            faculty.setId(id);
        }

        return getFacultyList();
    }

    public Map<String,String> fetchFacultyIDs(){
        Map<String,String> facultyIDs = new HashMap<>();
        try {
            Connection.Response res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).method(Connection.Method.GET)
                    .followRedirects(true).execute();
            if (res == null) {
                logger.warn("Unable to get EHB faculties from site!");
                return null;
            }
            Document doc = res.parse();
            if (doc == null) {
                logger.warn("Unable to get EHB faculties from site!");
                return null;
            }

            Element selectElement = doc.getElementById("dlFilter");
            List<Element> optionElements = selectElement.getElementsByTag("option");
            for (Element optionElement : optionElements) {
                facultyIDs.put(optionElement.html(),optionElement.val());
            }
            return facultyIDs;
        } catch (IOException e) {
            logger.error("Unable to get faculties from site [#3]!", e);
        }
        return null;
    }
}
