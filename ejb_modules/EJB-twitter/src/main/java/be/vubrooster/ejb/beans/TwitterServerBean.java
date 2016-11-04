package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.TwitterServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import javax.ejb.Asynchronous;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * TwitterServerBean
 * Created by maxim on 21-Sep-16.
 */
@Startup
@Remote(TwitterServer.class)
@Singleton(mappedName = "TwitterServer")
public class TwitterServerBean implements TwitterServer{
    // Logging
    private final Logger logger = LoggerFactory.getLogger(TwitterServerBean.class);

    // Twitter instance
    private Twitter twitter = null;

    public void signIn(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
        // Log in to twitter
        logger.info("Signing in to twitter ...");
        try {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(consumerKey)
                    .setOAuthConsumerSecret(consumerSecret)
                    .setOAuthAccessToken(accessToken)
                    .setOAuthAccessTokenSecret(accessTokenSecret);
            TwitterFactory tf = new TwitterFactory(cb.build());
            twitter = tf.getInstance();
            logger.info("Signed in as: " + twitter.getAccountSettings().getScreenName());
        }catch (Exception ex){
            logger.error("Unable to sign in to twitter!");
        }
    }

    /**
     * Post a tweet on twitter
     * @param status Status tweet
     */
    @Asynchronous
    public void postStatus(String status){
        if (twitter == null)
            return; // Disabled
        logger.info("Sending tweet ...");
        try {
            twitter.updateStatus(status);
            logger.info("Tweet has been send!");
        } catch (TwitterException e) {
            logger.error("Unable to post: " + status);
        }
    }

    @Override
    @Asynchronous
    public void sendDirectMessage(String to, String message) {
        if (twitter == null)
            return; // Disabled
        logger.info("Sending direct message to: " + to + " ...");
        try {
            twitter.sendDirectMessage(to,message);
            logger.info("Direct message has been send!");
        } catch (TwitterException e) {
            logger.error("Unable to send direct message: " + message);
        }
    }
}
