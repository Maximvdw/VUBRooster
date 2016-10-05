package be.vubrooster.api.faculty.controllers;

import be.vubrooster.ejb.FacultyServer;
import be.vubrooster.ejb.models.Faculty;
import be.vubrooster.ejb.service.ServiceProvider;
import be.vubrooster.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.json.*;
import java.util.List;


/**
 * FacultyController
 *
 * @author Maxim Van de Wynckel
 * @date 16-Apr-16
 */
@Controller
public class FacultyController {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(FacultyController.class);

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findAllFaculties(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                            @RequestParam(name = "prettyPrint", defaultValue = "false") boolean prettyPrint)
    {
        FacultyServer facultyServer = ServiceProvider.getFacultyServer();
        List<Faculty> facultyList = facultyServer.findFaculties(false);
        JsonArrayBuilder facultyArray = Json.createArrayBuilder();
        for (Faculty a : facultyList) {
            facultyArray.add(a.toJSON());
        }
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("faculties",facultyArray).build();
        return new ResponseEntity<>(prettyPrint ? JSONUtils.prettyPrint(jsonObject) : jsonObject.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findFacultyById(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                            @PathVariable("id") long facultyId)
    {
        FacultyServer facultyServer = ServiceProvider.getFacultyServer();
        Faculty faculty = facultyServer.findFacultyById((int) facultyId,false);
        if (faculty == null){
            return new ResponseEntity<>("{'error':'No such faculty found!'}", HttpStatus.NOT_FOUND);
        }else{
            JsonObject result = faculty.toJSON().build();
            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/code/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findFacultyByCode(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                           @PathVariable("code") String facultyCode)
    {
        FacultyServer facultyServer = ServiceProvider.getFacultyServer();
        Faculty faculty = facultyServer.findFacultyByCode(facultyCode,false);
        if (faculty == null){
            return new ResponseEntity<>("{'error':'No such faculty found!'}", HttpStatus.NOT_FOUND);
        }else{
            JsonObject result = faculty.toJSON().build();
            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
        }
    }
}
