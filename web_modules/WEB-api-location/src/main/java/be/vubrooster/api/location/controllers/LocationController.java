package be.vubrooster.api.location.controllers;

import be.vubrooster.ejb.ActivitiyServer;
import be.vubrooster.ejb.ClassRoomServer;
import be.vubrooster.ejb.models.Activity;
import be.vubrooster.ejb.models.ClassRoom;
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
 * LocationController
 *
 * @author Maxim Van de Wynckel
 * @date 16-Apr-16
 */
@Controller
public class LocationController {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(LocationController.class);

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findAllLocations(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent)
    {
        ClassRoomServer classRoomServer = ServiceProvider.getClassRoomServer();
        List<ClassRoom> classRooomList = classRoomServer.findClassRooms(false);
        JsonArrayBuilder locationsArray = Json.createArrayBuilder();
        for (ClassRoom a : classRooomList) {
            locationsArray.add(a.toJSON());
        }
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("locations",locationsArray).build();
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
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

}
