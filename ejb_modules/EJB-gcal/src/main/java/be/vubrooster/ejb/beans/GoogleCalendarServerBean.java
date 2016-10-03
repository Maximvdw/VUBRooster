package be.vubrooster.ejb.beans;

import be.vubrooster.ejb.ConfigurationServer;
import be.vubrooster.ejb.GoogleCalendarServer;
import be.vubrooster.ejb.models.*;
import be.vubrooster.ejb.schedulers.SchedulerManager;
import be.vubrooster.ejb.service.ServiceProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private String clientId = "";
    private String applicationName = "";
    private String serviceFile = "";

    private GoogleCredential appCredential = null;

    @PostConstruct
    public void init() {
        SchedulerManager.createTask(new Runnable() {

            @Override
            public void run() {
//                clearCalendars();
//                createDefaultCalendars();
            }
        }, 5, TimeUnit.MINUTES);
    }

    /**
     * Get client secret
     *
     * @return secret
     */
    public String getClientSecret() {
        if (clientSecret.equals("")) {
            loadConfiguration();
        }
        return clientSecret;
    }

    public String getServiceFile() {
        if (serviceFile.equals("")) {
            loadConfiguration();
        }
        return serviceFile;
    }

    /**
     * Get application name
     *
     * @return application name
     */
    public String getApplicationName() {
        if (applicationName.equals("")) {
            loadConfiguration();
        }
        return applicationName;
    }

    /**
     * Get client id
     *
     * @return client id
     */
    public String getClientId() {
        if (clientId.equals("")) {
            loadConfiguration();
        }
        return clientId;
    }

    public void loadConfiguration() {
        // Load configuration
        ConfigurationServer configurationServer = ServiceProvider.getConfigurationServer();
        clientSecret = configurationServer.getString("google.client_secret");
        applicationName = configurationServer.getString("google.application_name");
        clientId = configurationServer.getString("google.client_id");
        serviceFile = configurationServer.getString("google.service_file");
    }

    @Override
    public List<GoogleCalendar> findGoogleCalendars() {
        Query query = getSession().getNamedQuery("findGoogleCalendars");
        return query.list();
    }

    @Override
    public List<GoogleCalendar> findNewCalendars() {
        Query query = getSession().getNamedQuery("findNewGoogleCalendars");
        return query.list();
    }

    @Override
    public void synchronizeGoogleCalendar(GoogleCalendar calendar) {
        // Get the credentials
        GoogleCredential appCredential = getAppCredentials();
        GoogleCredential credential = getCredentials(calendar.getUser());
        try {
            Calendar appClient = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), new GsonFactory(), appCredential).setApplicationName(getApplicationName()).build();
            Calendar userClient = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), new GsonFactory(), credential).setApplicationName(getApplicationName()).build();
            com.google.api.services.calendar.model.Calendar googleCalendar = null;
            if (calendar.getGoogleCalendarId().equals("")) {
                // Create a new calendar
                googleCalendar = createNewCalendar(appClient, calendar.getGoogleCalendarName());
            } else {
                // Try looking for an existing calendar
                CalendarListEntry existingCalendar = appClient.calendarList().get(calendar.getGoogleCalendarId()).execute();
                if (existingCalendar == null) {
                    // Does no longer exist!
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private com.google.api.services.calendar.model.Calendar createNewCalendar(Calendar client, String name) {
        try {
            com.google.api.services.calendar.model.Calendar googleCalendar =
                    new com.google.api.services.calendar.model.Calendar();
            googleCalendar.setLocation("https://www.vubrooster.be/");
            googleCalendar.setTimeZone("Europe/Brussels");
            googleCalendar.setSummary(name);
            com.google.api.services.calendar.model.Calendar createdCalendar = client.calendars().insert(googleCalendar).execute();
            return createdCalendar;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    private Event createEvent(Activity activity) {
        Event event = new Event()
                .setSummary(activity.getName())
                .setLocation(activity.getClassRoom())
                .setDescription("A chance to hear more about Google's developer products.");

        DateTime startDateTime = new DateTime(activity.getBeginTimeUnix() * 1000);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Europe/Brussels");
        event.setStart(start);

        DateTime endDateTime = new DateTime(activity.getEndTimeUnix() * 1000);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Europe/Brussels");
        event.setEnd(end);

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        return event;
    }

    @Override
    public void synchronizeGoogleCalendars() {
        // Fetch all calendars
        List<GoogleCalendar> googleCalendars = findGoogleCalendars();
        for (GoogleCalendar calendar : googleCalendars) {
            synchronizeGoogleCalendar(calendar);
        }
    }

    @Override
    public void synchronizeNewCalendars() {
        // Fetch all new calendars
        List<GoogleCalendar> googleCalendars = findNewCalendars();
        for (GoogleCalendar calendar : googleCalendars) {
            synchronizeGoogleCalendar(calendar);
        }
    }

    @Override
    public void createDefaultCalendars() {
        GoogleCredential appCredential = getAppCredentials();
        try {
            Calendar appClient = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), new GsonFactory(), appCredential).setApplicationName(getApplicationName()).build();
            List<CalendarListEntry> calendarList = appClient.calendarList().list().execute().getItems();



            // Get staff members
            List<StaffMember> staffMembers = ServiceProvider.getStaffServer().findStaff(false);
            for (StaffMember member : staffMembers) {
                CalendarListEntry existingCalendar = null;
                for (CalendarListEntry entry : calendarList) {
                    if (entry.getSummary().equals("STAFF: " + member.getId())) {
                        existingCalendar = entry;
                        break;
                    }
                }
                String googleCalendarId;
                if (existingCalendar == null) {
                    // Create a calendar
                    com.google.api.services.calendar.model.Calendar googleCalendar = createNewCalendar(appClient, "STAFF: " + member.getId());
                    AclRule rule = new AclRule();
                    AclRule.Scope scope = new AclRule.Scope();
                    scope.setType("default");
                    scope.setValue("");
                    rule.setScope(scope);
                    rule.setRole("reader");
                    appClient.acl().insert(googleCalendar.getId(),rule).execute();
                    googleCalendarId = googleCalendar.getId();
                } else {
                    googleCalendarId = existingCalendar.getId();
                }
                if (!googleCalendarId.equals("")) {
                    // Get all activities for member
                    List<Activity> activityList = ServiceProvider.getActivitiyServer().findAllActivitiesForStaffMember(member);
                    for (Activity activity : activityList) {
                        try {
                            Event event = createEvent(activity);
                            appClient.events().insert(googleCalendarId, event).execute();
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }
            }

            // Get groups
            List<StudentGroup> groups = ServiceProvider.getStudentGroupServer().findStudentGroups(false);
            for (StudentGroup group : groups) {
                CalendarListEntry existingCalendar = null;
                for (CalendarListEntry entry : calendarList) {
                    if (entry.getSummary().equals("GROUP: " + group.getId())) {
                        existingCalendar = entry;
                        break;
                    }
                }
                String googleCalendarId;
                if (existingCalendar == null) {
                    // Create a calendar
                    com.google.api.services.calendar.model.Calendar googleCalendar = createNewCalendar(appClient, "GROUP: " + group.getId());
                    googleCalendarId = googleCalendar.getId();
                } else {
                    googleCalendarId = existingCalendar.getId();
                }
                if (!googleCalendarId.equals("")) {
                    // Get all activities for member
                    List<Activity> activityList = ServiceProvider.getActivitiyServer().findAllActivitiesForStudentGroup(group);
                    for (Activity activity : activityList) {
                        try {
                            Event event = createEvent(activity);
                            appClient.events().insert(googleCalendarId, event).execute();
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearCalendars() {
        GoogleCredential appCredential = getAppCredentials();
        try {
            Calendar appClient = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), new GsonFactory(), appCredential).setApplicationName(getApplicationName()).build();
            List<CalendarListEntry> calendarList = appClient.calendarList().list().execute().getItems();
            for (CalendarListEntry entry : calendarList) {
                appClient.calendars().delete(entry.getId()).execute();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get credentials for a specific user
     *
     * @param user user to get credentials from
     * @return google credentials
     */
    public GoogleCredential getCredentials(User user) {
        GoogleCredential credential =
                new GoogleCredential.Builder()
                        .setTransport(HTTP_TRANSPORT)
                        .setJsonFactory(JSON_FACTORY)
                        .setClientSecrets(getClientId(), getClientSecret()).build();
        credential.setAccessToken(user.getAccessToken());
        credential.setRefreshToken(user.getRefreshToken());
        return credential;
    }

    /**
     * Get credentials for  the app
     *
     * @return google credentials
     */
    public GoogleCredential getAppCredentials() {
        if (appCredential != null) {
            return appCredential;
        }
        try {
            GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(new File("VUBRooster", getServiceFile())))
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
            appCredential = credential;
        } catch (Exception ex) {

        }
        return appCredential;
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
