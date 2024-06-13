
package services;


import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Scalendar {

}
/*import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import entities.Evenement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* class to demonstrate use of Calendar events list API */
/*public class Scalendar {
    /**
     * Application name.
     *
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    /**
     * Global instance of the JSON factory.

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

     * Diectory to store authorization tokens for this application.
     *
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     *
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = Scalendar.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static List<Event> getGoogleCalendarEvents(List<Evenement> evenements) {
        List<Event> googleEvents = new ArrayList<>();
        for (Evenement evenement : evenements) {
            EventDateTime startDateTime = new EventDateTime()
                    .setDateTime(new DateTime(evenement.getDatedebut()));
            EventDateTime endDateTime = new EventDateTime()
                    .setDateTime(new DateTime(evenement.getDatefin()));

            Event googleEvent = new Event()
                    .setSummary(evenement.getNameevent())

                    .setStart(startDateTime)
                    .setEnd(endDateTime);

            googleEvents.add(googleEvent);
        }
        return googleEvents;
    }


    public static List<Event> fetchEventsFromEvenementEntity() {
        List<Event> googleEvents = new ArrayList<>();
        try {
            ServiceEvenement serviceEvenement = new ServiceEvenement();
            List<Evenement> evenements = serviceEvenement.fetchSelectedFields();
            for (Evenement evenement : evenements) {
                Event googleEvent = new Event()
                        .setSummary(evenement.getNameevent())

                        .setStart(new EventDateTime().setDateTime(new DateTime(evenement.getDatedebut())))
                        .setEnd(new EventDateTime().setDateTime(new DateTime(evenement.getDatefin())));
                googleEvents.add(googleEvent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return googleEvents;
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> googleEvents = events.getItems();

        // Fetch events from the Evenement entity and add them to the list of Google Calendar events
        googleEvents.addAll(fetchEventsFromEvenementEntity());

        if (googleEvents.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : googleEvents) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
    }
}*/