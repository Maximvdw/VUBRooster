package be.vubrooster.api.user.controllers;

import org.springframework.stereotype.Controller;


/**
 * UserController
 *
 * @author Maxim Van de Wynckel
 * @date 16-Apr-16
 */
@Controller
public class UserController {
//    // Logging
//    private final Logger logger = LoggerFactory.getLogger(UserController.class);
//
//    @RequestMapping(value = "/me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    ResponseEntity<String> getProfile(@RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
//                                      @RequestHeader(value = "X-Auth-Token", required = true) String token) {
//        TravelPreferencesServer travelPreferencesServer = ServiceProvider.getTravelPreferencesServer();
//        UserServer userServer = ServiceProvider.getUserServer();
//        try {
//            UserToken userToken = userServer.validateUserSession(token);
//            // Get travel preferences
//            UserTravelPreferences preferences = travelPreferencesServer.findUserTravelPreferenceByUser(userToken.getUser());
//            // Get awards
//            Collection<Award> awards = userServer.findUserAwards(userToken.getUser());
//            JsonArrayBuilder awardsArray = Json.createArrayBuilder();
//            for (Award award : awards){
//                awardsArray.add(award.toJSON());
//            }
//
//            if (preferences != null) {
//                JsonObject result = userToken.getUser().toSafeJSON().add("awards",awardsArray).add("preferences", preferences.toSafeCompactJSON(userToken.getUser().getLocaleInstance())).
//                        build();
//                return new ResponseEntity<>(result.toString(), HttpStatus.OK);
//            }else{
//                JsonObject result = userToken.getUser().toSafeJSON().add("awards",awardsArray).add("preferences","").
//                        build();
//                return new ResponseEntity<>(result.toString(), HttpStatus.OK);
//            }
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
//
//    @RequestMapping(value="/preferences", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    ResponseEntity<String> updateProfile(
//            @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
//            @RequestHeader(value = "X-Auth-Token", required = true) String token,
//            @RequestBody UserUpdateRequest userUpdateRequest
//    ) {
//        TravelPreferencesServer travelPreferencesServer = ServiceProvider.getTravelPreferencesServer();
//        UserServer userServer = ServiceProvider.getUserServer();
//        try {
//            UserToken userToken = userServer.validateUserSession(token);
//            // Get travel preferences
//            UserTravelPreferences preferences = travelPreferencesServer.findUserTravelPreferenceByUser(userToken.getUser());
//            if (preferences == null) {
//                preferences = new UserTravelPreferences();
//                preferences.setUser(userToken.getUser());
//            }
//
//            // Set new settings
//            preferences.setMinAge(userUpdateRequest.min_age);
//            preferences.setMaxAge(userUpdateRequest.max_age);
//            preferences.setBudget(userUpdateRequest.budget);
//            preferences.setDateRangeStart(new Date(userUpdateRequest.date_start));
//            preferences.setDateRangeEnd(new Date(userUpdateRequest.date_end));
//            List<Keyword> keywordList = new ArrayList<Keyword>();
//            for (String k : userUpdateRequest.keywords){
//                Keyword keyword = Keyword.valueOf(k);
//                if (keyword != null){
//                    keywordList.add(keyword);
//                }else{
//                    throw new InvalidKeywordException();
//                }
//            }
//            preferences.setKeywords(keywordList);
//
//            travelPreferencesServer.saveUserTravelPreferences(preferences); // Save
//
//            JsonObject result = userToken.getUser().toSafeJSON().add("preferences", preferences.toSafeCompactJSON(userToken.getUser().getLocaleInstance())).
//                    build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
//        } catch (UnauthorizedUserException | InvalidKeywordException e) {
//            JsonObject result = Json.createObjectBuilder()
//                    .add("error", e.getErrorId())
//                    .add("message", e.getMessage())
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.FORBIDDEN);
//        }
//    }
//
//    @RequestMapping(value ="/like/{friendId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    ResponseEntity<String> likeFriend(
//            @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
//            @RequestHeader(value = "X-Auth-Token", required = true) String token,
//            @PathVariable("friendId") long friendId
//    ) {
//        UserServer userServer = ServiceProvider.getUserServer();
//        UserLikeServer userLikeServer = ServiceProvider.getUserLikeServer();
//        try {
//            UserToken userToken = userServer.validateUserSession(token);
//            User friend = userServer.findUserById(friendId);
//            if (friend == null){
//                throw new NoFriendException();
//            }
//
//            boolean match = userLikeServer.likeUser(userToken.getUser(),friend);
//
//            JsonObject result = Json.createObjectBuilder()
//                    .add("match", match)
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
//        } catch (UnauthorizedUserException | NoFriendException e) {
//            JsonObject result = Json.createObjectBuilder()
//                    .add("error", e.getErrorId())
//                    .add("message", e.getMessage())
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.FORBIDDEN);
//        }catch (Exception e){
//            logger.error("Unknown like friend error",e);
//            JsonObject result = Json.createObjectBuilder()
//                    .add("error", -1)
//                    .add("message", "Unknown error!")
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.FORBIDDEN);
//        }
//    }
//
//    @RequestMapping(value ="/unlike/{friendId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    ResponseEntity<String> unlikeFriend(
//            @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
//            @RequestHeader(value = "X-Auth-Token", required = true) String token,
//            @PathVariable("friendId") long friendId
//    ) {
//        UserServer userServer = ServiceProvider.getUserServer();
//        UserLikeServer userLikeServer = ServiceProvider.getUserLikeServer();
//        try {
//            UserToken userToken = userServer.validateUserSession(token);
//            User friend = userServer.findUserById(friendId);
//            if (friend == null){
//                throw new NoFriendException();
//            }
//
//            userLikeServer.unlikeUser(userToken.getUser(),friend);
//
//            JsonObject result = Json.createObjectBuilder()
//                    .add("match", false)
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
//        } catch (UnauthorizedUserException | NoFriendException e) {
//            JsonObject result = Json.createObjectBuilder()
//                    .add("error", e.getErrorId())
//                    .add("message", e.getMessage())
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.FORBIDDEN);
//        }catch (Exception e){
//            logger.error("Unknown unlike friend error",e);
//            JsonObject result = Json.createObjectBuilder()
//                    .add("error", -1)
//                    .add("message", "Unknown error!")
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.FORBIDDEN);
//        }
//    }
//
//
//    /**
//     * Get all friends using the app
//     *
//     * @param userAgent user agent
//     * @param token session token
//     * @return JSON list of friends using the app
//     */
//    @RequestMapping(value = "/friends", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    ResponseEntity<String> getFriends(
//            @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
//            @RequestHeader(value = "X-Auth-Token", required = true) String token,
//            @RequestParam(value = "lastUpdate") long lastUpdate,
//            @RequestParam(value = "per_page") int perPage,
//            @RequestParam(value = "page") int page) {
//        UserServer userServer = ServiceProvider.getUserServer();
//        try {
//            UserToken userToken = userServer.validateUserSession(token);
//            Collection<User> appFriends = userServer.findFriends(userToken.getUser());
//            JsonArrayBuilder friendList = Json.createArrayBuilder();
//            for (User friend : appFriends){
//                friendList.add(friend.toFriendJSON());
//            }
//            return new ResponseEntity<>(Json.createObjectBuilder().add("friends",friendList).build().toString(), HttpStatus.OK);
//        } catch (UnauthorizedUserException e) {
//            JsonObject result = Json.createObjectBuilder()
//                    .add("error", e.getErrorId())
//                    .add("message", e.getMessage())
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.FORBIDDEN);
//        }
//    }
//
//    /**
//     * Get travel recommendation
//     * @param userAgent
//     * @param token
//     * @param lastUpdate
//     * @param perPage
//     * @param page
//     * @return
//     */
//    @RequestMapping(value = "/recs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    ResponseEntity<String> getRecommendations(
//            @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
//            @RequestHeader(value = "X-Auth-Token", required = true) String token,
//            @RequestParam(value = "lastUpdate", required = false, defaultValue = "-1") long lastUpdate,
//            @RequestParam(value = "per_page", required= false , defaultValue = "-1") int perPage,
//            @RequestParam(value = "page", required = false, defaultValue = "-1") int page) {
//        UserServer userServer = ServiceProvider.getUserServer();
//        TravelPreferencesServer travelPreferencesServer = ServiceProvider.getTravelPreferencesServer();
//        try {
//            UserToken userToken = userServer.validateUserSession(token);
//            Collection<TravelRecommendation> recommendations = travelPreferencesServer.findSortedTravelRecommendations(userToken.getUser());
//            JsonArrayBuilder friendList = Json.createArrayBuilder();
//            for (TravelRecommendation recommendation : recommendations){
//                friendList.add(recommendation.toSafeJSON(userToken.getUser().getLocaleInstance()));
//            }
//            return new ResponseEntity<>(Json.createObjectBuilder().add("recommendations",friendList).build().toString(), HttpStatus.OK);
//        } catch (UnauthorizedUserException e) {
//            JsonObject result = Json.createObjectBuilder()
//                    .add("error", e.getErrorId())
//                    .add("message", e.getMessage())
//                    .build();
//            return new ResponseEntity<>(result.toString(), HttpStatus.FORBIDDEN);
//        }
//    }
//
//    /**
//     * Get the total user count
//     *
//     * @return total user count
//     */
//    @RequestMapping(value = "/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    ResponseEntity<String> getTotalUserCount() {
//        UserServer userServer = ServiceProvider.getUserServer();
//        long count = userServer.getTotalUserCount();
//        JsonObject result = Json.createObjectBuilder()
//                .add("count",count)
//                .add("updated",System.currentTimeMillis())
//                .build();
//        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
//    }
    
}
