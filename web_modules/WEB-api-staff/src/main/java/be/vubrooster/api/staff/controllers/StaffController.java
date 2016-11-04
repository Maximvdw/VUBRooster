package be.vubrooster.api.staff.controllers;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.StaffServer;
import be.vubrooster.ejb.models.Activity;
import be.vubrooster.ejb.models.StaffMember;
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
import java.util.List;


/**
 * StaffController
 *
 * @author Maxim Van de Wynckel
 * @date 16-Apr-16
 */
@Controller
public class StaffController {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(StaffController.class);

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findAllStaffMembers(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                               @RequestParam(name = "prettyPrint", defaultValue = "false") boolean prettyPrint) {
        StaffServer staffServer = ServiceProvider.getStaffServer();
        List<StaffMember> staffMemberList = staffServer.findStaff(false);
        JsonArrayBuilder staffArray;
        staffArray = Json.createArrayBuilder();
        for (StaffMember a : staffMemberList) {
            staffArray.add(a.toJSON());
        }
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("staff", staffArray).build();
        return new ResponseEntity<>(prettyPrint ? JSONUtils.prettyPrint(jsonObject) : jsonObject.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findActivityById(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                            @PathVariable("id") long activityId,
                                            @RequestParam(name = "prettyPrint", defaultValue = "false") boolean prettyPrint) {
        ActivitiyServer activityServer = ServiceProvider.getActivitiyServer();
        Activity activity = activityServer.findActivityById((int) activityId, false);
        if (activity == null) {
            return new ResponseEntity<>("{'error':'No such activity found!'}", HttpStatus.NOT_FOUND);
        } else {
            JsonObject result = activity.toFullJSON().build();
            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
        }
    }
}
