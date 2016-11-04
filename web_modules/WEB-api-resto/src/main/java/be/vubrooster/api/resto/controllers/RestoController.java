package be.vubrooster.api.resto.controllers;

import be.vubrooster.ejb.DayMenuServer;
import be.vubrooster.ejb.models.DayMenu;
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
public class RestoController {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(RestoController.class);

    @RequestMapping(value = "/{campus}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findAllDayMenusForCampus(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                                    @PathVariable("campus") String campus,
                                                    @RequestParam(name = "prettyPrint", defaultValue = "false") boolean prettyPrint) {
        DayMenuServer dayMenuServer = ServiceProvider.getDayMenuServer();
        List<DayMenu> dayMenuList = dayMenuServer.findAllDayMenusForCampus(campus, false);
        JsonArrayBuilder dayMenuArray = Json.createArrayBuilder();
        for (DayMenu a : dayMenuList) {
            dayMenuArray.add(a.toJSON());
        }
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("daymenus", dayMenuArray).build();
        return new ResponseEntity<>(prettyPrint ? JSONUtils.prettyPrint(jsonObject) : jsonObject.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{campus}/{week}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findAllDayMenusForCampusOnWeek(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                                          @PathVariable("campus") String campus,
                                                          @PathVariable("week") int week,
                                                          @RequestParam(name = "prettyPrint", defaultValue = "false") boolean prettyPrint) {
        DayMenuServer dayMenuServer = ServiceProvider.getDayMenuServer();
        List<DayMenu> dayMenuList = dayMenuServer.findDayMenusForCampusOnWeek(campus,week, false);
        JsonArrayBuilder dayMenuArray = Json.createArrayBuilder();
        for (DayMenu a : dayMenuList) {
            dayMenuArray.add(a.toJSON());
        }
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("daymenus", dayMenuArray).build();
        return new ResponseEntity<>(prettyPrint ? JSONUtils.prettyPrint(jsonObject) : jsonObject.toString(), HttpStatus.OK);
    }
}
