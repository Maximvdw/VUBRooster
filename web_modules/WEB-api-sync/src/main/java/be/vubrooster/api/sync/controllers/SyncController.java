package be.vubrooster.api.sync.controllers;

import be.vubrooster.api.sync.service.ServiceProvider;
import be.vubrooster.ejb.StudyProgramServer;
import be.vubrooster.ejb.models.StudyProgram;
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
 * SyncController
 *
 * @author Maxim Van de Wynckel
 * @date 16-Apr-16
 */
@Controller
public class SyncController {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(SyncController.class);

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<String> findAllStudyProgrammes(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
                                                 @RequestParam(name = "prettyPrint", defaultValue = "false") boolean prettyPrint)
    {
        StudyProgramServer studyProgramServer = ServiceProvider.getStudyProgramServer();
        List<StudyProgram> studyProgramList = studyProgramServer.findStudyProgrammes(false);
        JsonArrayBuilder studyProgramArray;
        studyProgramArray = Json.createArrayBuilder();
        for (StudyProgram a : studyProgramList) {
            studyProgramArray.add(a.toJSON());
        }
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("studyprogrammes",studyProgramArray).build();
        return new ResponseEntity<>(prettyPrint ? JSONUtils.prettyPrint(jsonObject) : jsonObject.toString(), HttpStatus.OK);
    }


}
