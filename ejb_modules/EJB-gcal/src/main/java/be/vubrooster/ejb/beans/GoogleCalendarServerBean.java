package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.ConfigurationServer;
import be.vubrooster.ejb.GoogleCalendarServer;
import be.vubrooster.ejb.models.GoogleCalendar;
import be.vubrooster.ejb.models.User;
import be.vubrooster.ejb.service.ServiceProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * GoogleCalendarServerBean
 * <p>
 * Created by maxim on 23-Sep-16.
 */
@Startup
@Remote(GoogleCalendarServer.class)
@Singleton(mappedName = "GoogleCalendarServer")
public class GoogleCalendarServerBean implements GoogleCalendarServer {
    // Logging
    private final Logger logger = LoggerFactory.getLogger(GoogleCalendarServerBean.class);

    @PersistenceContext(name = "vubrooster")
    private EntityManager entityManager;
    private Session session = null;

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private String clientSecret = "";
    private String applicationName = "";

    /**
     * Get client secret
     *
     * @return secret
     */
    public String getClientSecret(){
        if (clientSecret.equals("")){
            loadConfiguration();
        }
        return clientSecret;
    }

    /**
     * Get application name
     *
     * @return application name
     */
    public String getApplicationName(){
        if (applicationName.equals("")){
            loadConfiguration();
        }
        return applicationName;
    }

    public void loadConfiguration(){
        // Load configuration
        ConfigurationServer configurationServer = ServiceProvider.getConfigurationServer();
        clientSecret = configurationServer.getString("google.client_secret");
        applicationName = configurationServer.getString("google.application_name");
    }

    @Override
    public List<GoogleCalendar> findGoogleCalendars() {
        Query query = getSession().getNamedQuery("findGoogleCalendars");
        return query.list();
    }

    @Override
    public void synchronizeGoogleCalendar(GoogleCalendar calendar) {
        // Get the credentials
        GoogleCredential credential = getCredentials(calendar.getUser());
        try {
            Calendar client = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), new GsonFactory(), credential).setApplicationName(getApplicationName()).build();
            CalendarList calendarList = client.calendarList().list().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void synchronizeGoogleCalendars() {
        // Fetch all calendars
        List<GoogleCalendar> googleCalendars = findGoogleCalendars();
        for (GoogleCalendar calendar : googleCalendars){
            synchronizeGoogleCalendar(calendar);
        }
    }

    /**
     * Get credentials for a specific user
     * @param user user to get credentials from
     * @return google credentials
     */
    public GoogleCredential getCredentials(User user){
        GoogleCredential credential =
                new GoogleCredential.Builder()
                        .setTransport(HTTP_TRANSPORT)
                        .setJsonFactory(JSON_FACTORY)
                        .setClientSecrets("",getClientSecret()).build();
        credential.setAccessToken(user.getAccessToken());
        credential.setRefreshToken(user.getRefreshToken());
        return credential;
    }

    /**
     * Get hibernate session
     *
     * @return hibernate session
     */
    public Session getSession() {
        if (session != null) {
            if (!session.isOpen()) {
                session = (Session) entityManager.getDelegate();
            }
            return session;
        }
        session = (Session) entityManager.getDelegate();
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * Set entity manager
     *
     * @param em entity manager
     */
    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }
}
