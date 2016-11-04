package be.vubrooster.api.studentgroup.controllers;

import be.vubrooster.ejb.StudentGroupServer;
import be.vubrooster.ejb.models.StudentGroup;
import be.vubrooster.ejb.service.ServiceProvider;
import be.vubrooster.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.net.URLDecoder;
import java.util.List;


/**
 * StudentGroupController
 *
 * @author Maxim Van de Wynckel
 * @date 16-Apr-16
 */
@Controller
public class StudentGroupController {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(StudentGroupController.class);

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findAllStudentGroups(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                                @RequestParam(name = "prettyPrint", defaultValue = "false") boolean prettyPrint) {
        StudentGroupServer studentGroupServer = ServiceProvider.getStudentGroupServer();
        List<StudentGroup> activityList = studentGroupServer.findStudentGroups(false);
        JsonArrayBuilder studentGroupArray = Json.createArrayBuilder();
        for (StudentGroup a : activityList) {
            studentGroupArray.add(a.toCompactJSON());
        }
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("studentgroups", studentGroupArray).build();
        return new ResponseEntity<>(prettyPrint ? JSONUtils.prettyPrint(jsonObject) : jsonObject.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findStudentGroupById(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                                @PathVariable("id") String studentGroupId,
                                                @RequestParam(name = "prettyPrint", defaultValue = "false") boolean prettyPrint) {
        try {
            StudentGroupServer studentGroupServer = ServiceProvider.getStudentGroupServer();
            StudentGroup studentGroup = studentGroupServer.findStudentGroupById(URLDecoder.decode(studentGroupId, "UTF-8"), false);
            if (studentGroup == null) {
                return new ResponseEntity<>("{'error':'No such student group found!'}", HttpStatus.NOT_FOUND);
            } else {
                JsonObject result = studentGroup.toCompactJSON().build();
                return new ResponseEntity<>(result.toString(), HttpStatus.OK);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }
}
