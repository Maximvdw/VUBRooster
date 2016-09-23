package be.vubrooster.api.user.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * AuthenticationController
 *
 * @author Maxim Van de Wynckel
 * @date 16-Apr-16
 */
@Controller
@RequestMapping("/auth")
public class AuthenticationController {
//    // Logging
//    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
//
//    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    ResponseEntity<String> authenticate(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
//                                        @RequestBody AuthenticationRequest authenticationRequest) {
//        UserServer userServer = ServiceProvider.getUserServer();
//        try {
//            UserToken userToken = userServer.authenticateFacebookUser(authenticationRequest.facebook_token, authenticationRequest.facebook_id);
//            JsonObject result = userToken.toSafeJSON().build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
//        } catch (BaseException e) {
//            logger.warn("Login fail","Error authenticating " + authenticationRequest.facebook_token + " #" + authenticationRequest.facebook_id,e);
//            JsonObject result = Json.createObjectBuilder()
//                    .add("error", e.getErrorId())
//                    .add("message", e.getMessage())
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.FORBIDDEN);
//        } catch (Exception e){
//            logger.error("Unknown authentication error",e);
//            JsonObject result = Json.createObjectBuilder()
//                    .add("error", -1)
//                    .add("message", "Unknown error!")
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.FORBIDDEN);
//        }
//    }
//
//    @RequestMapping(value = "/deauthorize", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    ResponseEntity<String> deauthorize(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
//                                       @RequestHeader(value = "X-Auth-Token", required = true) String token) {
//        UserServer userServer = ServiceProvider.getUserServer();
//        try {
//            UserToken userToken = userServer.validateUserSession(token);
//            userServer.deactivateUser(userToken.getUser());
//            JsonObject result = userToken.getUser().toSafeJSON()
//                    .add("status", "ok")
//                    .add("message","Goodbye :'(")
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
//        } catch (UnauthorizedUserException e) {
//            JsonObject result = Json.createObjectBuilder()
//                    .add("error", e.getErrorId())
//                    .add("message", e.getMessage())
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.FORBIDDEN);
//        }catch (Exception e){
//            logger.error("Unknown getProfile error",e);
//            JsonObject result = Json.createObjectBuilder()
//                    .add("error", -1)
//                    .add("message", "Unknown error!")
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.FORBIDDEN);
//        }
//    }

}
