package be.vubrooster.api.course.controllers;

import be.vubrooster.ejb.CourseServer;
import be.vubrooster.ejb.models.Course;
import be.vubrooster.ejb.service.ServiceProvider;
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
import java.util.List;


/**
 * CourseManager
 *
 * @author Maxim Van de Wynckel
 * @date 16-Apr-16
 */
@Controller
public class CourseManager {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(CourseManager.class);

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findAllCourses(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent)
    {
        CourseServer courseServer = ServiceProvider.getCourseServer();
        List<Course> courseList = courseServer.findCourses(false);
        JsonArrayBuilder courseArray = Json.createArrayBuilder();
        for (Course a : courseList) {
            courseArray.add(a.toJSON());
        }
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("courses",courseArray).build();
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findCourseById(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                            @PathVariable("id") String courseId)
    {
        CourseServer courseServer = ServiceProvider.getCourseServer();
        Course course = courseServer.findCourseById(courseId,false);
        if (course == null){
            return new ResponseEntity<>("{'error':'No such course found!'}", HttpStatus.NOT_FOUND);
        }else{
            JsonObject result = course.toJSON().build();
            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
        }
    }

}
