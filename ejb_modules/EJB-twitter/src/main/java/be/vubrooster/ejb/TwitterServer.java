package be.vubrooster.ejb;

/**
 * TwitterServer
 *
 * Created by maxim on 21-Sep-16.
 */
public interface TwitterServer {

    /**
     * Post a status message on twitter
     * @param status status to post
     */
    void postStatus(String status);

    /**
     * Send a direct message
     * @param to send it to
     * @param message message to send
     */
    void sendDirectMessage(String to,String message);

    /**
     * Sign in to twitter
     * @param consumerKey OAuth consumer key
     * @param consumerSecret OAuth consumer secret
     * @param accessToken OAuth access token
     * @param accessTokenSecret OAuth access token secret
     */
    void signIn(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret);
}
