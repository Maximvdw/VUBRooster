package be.vubrooster.api.faculty.controllers;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.ClassRoomServer;
import be.vubrooster.ejb.StaffServer;
import be.vubrooster.ejb.StudentGroupServer;
import be.vubrooster.ejb.models.Activity;
import be.vubrooster.ejb.models.ClassRoom;
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
import java.net.URLDecoder;
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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findActivityById(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                            @PathVariable("id") long activityId) {
        ActivitiyServer activityServer = ServiceProvider.getActivitiyServer();
        Activity activity = activityServer.findActivityById((int) activityId, false);
        if (activity == null) {
            return new ResponseEntity<>("{'error':'No such activity found!'}", HttpStatus.NOT_FOUND);
        } else {
            JsonObject result = activity.toFullJSON().build();
            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/all/group/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findAllActivitiesByGroup(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                                    @PathVariable("id") String groupId) {
        try {
            ActivitiyServer activityServer = ServiceProvider.getActivitiyServer();
            StudentGroupServer studentGroupServer = ServiceProvider.getStudentGroupServer();
            StudentGroup studentGroup = studentGroupServer.findStudentGroupById(URLDecoder.decode(groupId, "UTF-8"), false);
            List<Activity> activityList = activityServer.findAllActivitiesForStudentGroup(studentGroup);
            JsonArrayBuilder activityArray = Json.createArrayBuilder();
            for (Activity a : activityList) {
                activityArray.add(a.toCompactJSON());
            }
            JsonObject jsonObject = Json.createObjectBuilder()
                    .add("activities",activityArray).build();
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/all/staff/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findAllActivitiesByStaff(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                                    @PathVariable("id") String staffId) {
        try {
            ActivitiyServer activityServer = ServiceProvider.getActivitiyServer();
            StaffServer staffServer = ServiceProvider.getStaffServer();
            StaffMember staffMember = staffServer.findStaffMemberById(URLDecoder.decode(staffId, "UTF-8"), false);
            List<Activity> activityList = activityServer.findAllActivitiesForStaffMember(staffMember);
            JsonArrayBuilder activityArray;
            activityArray = Json.createArrayBuilder();
            for (Activity a : activityList) {
                activityArray.add(a.toCompactJSON());
            }
            JsonObject jsonObject = Json.createObjectBuilder()
                    .add("activities",activityArray).build();
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/all/location/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findAllActivitiesByLocation(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                                       @PathVariable("id") String locationId) {
        try {
            ActivitiyServer activityServer = ServiceProvider.getActivitiyServer();
            ClassRoomServer classRoomServer = ServiceProvider.getClassRoomServer();
            ClassRoom classRoom = classRoomServer.findClassRoomById(URLDecoder.decode(locationId, "UTF-8"), false);
            List<Activity> activityList = activityServer.findAllActivitiesForClassRoom(classRoom);
            JsonArrayBuilder activityArray;
            activityArray = Json.createArrayBuilder();
            for (Activity a : activityList) {
                activityArray.add(a.toCompactJSON());
            }
            JsonObject jsonObject = Json.createObjectBuilder()
                    .add("activities",activityArray).build();
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }
}
