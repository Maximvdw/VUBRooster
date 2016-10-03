package be.vubrooster.api.activity.controllers;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.StaffServer;
import be.vubrooster.ejb.StudentGroupServer;
import be.vubrooster.ejb.models.Activity;
import be.vubrooster.ejb.models.StaffMember;
import be.vubrooster.ejb.models.StudentGroup;
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
 * ActivityController
 *
 * @author Maxim Van de Wynckel
 * @date 16-Apr-16
 */
@Controller
public class ActivityController {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findAllActivities(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent)
    {
        ActivitiyServer activityServer = ServiceProvider.getActivitiyServer();
        List<Activity> activityList = activityServer.findActivities(false);
        JsonArrayBuilder activityArray;
        activityArray = Json.createArrayBuilder();
        for (Activity a : activityList) {
            activityArray.add(a.toCompactJSON());
        }
        return new ResponseEntity<>(activityArray.build().toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findActivityById(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                            @PathVariable("id") long activityId)
    {
        ActivitiyServer activityServer = ServiceProvider.getActivitiyServer();
        Activity activity = activityServer.findActivityById((int) activityId,false);
        if (activity == null){
            return new ResponseEntity<>("{'error':'No such activity found!'}", HttpStatus.NOT_FOUND);
        }else{
            JsonObject result = activity.toFullJSON().build();
            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/group/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findAllActivitiesByGroup(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                            @PathVariable("id") String groupId)
    {
        ActivitiyServer activityServer = ServiceProvider.getActivitiyServer();
        StudentGroupServer studentGroupServer = ServiceProvider.getStudentGroupServer();
        StudentGroup studentGroup = studentGroupServer.findStudentGroupById(groupId,false);
        List<Activity> activityList = activityServer.findAllActivitiesForStudentGroup(studentGroup);
        JsonArrayBuilder activityArray;
        activityArray = Json.createArrayBuilder();
        for (Activity a : activityList) {
            activityArray.add(a.toCompactJSON());
        }
        return new ResponseEntity<>(activityArray.build().toString(), HttpStatus.OK);
    }

//    @RequestMapping(value = "/staff/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    ResponseEntity<String> findAllActivitiesByStaff(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
//                                                    @PathVariable("id") String staffId)
//    {
//        ActivitiyServer activityServer = ServiceProvider.getActivitiyServer();
//        StaffServer staffServer = ServiceProvider.getStaffServer();
//        StaffMember staffMember = staffServer.
//        List<Activity> activityList = activityServer.findAllActivitiesForStaffMember(studentGroup);
//        JsonArrayBuilder activityArray;
//        activityArray = Json.createArrayBuilder();
//        for (Activity a : activityList) {
//            activityArray.add(a.toCompactJSON());
//        }
//        return new ResponseEntity<>(activityArray.build().toString(), HttpStatus.OK);
//    }
}
