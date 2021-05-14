package Notifications;

import Cache.RedisConnection;
import Config.Config;
import Models.Notification;
import Models.UserData;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Firebase {
    private static Properties config = Config.getInstance().getFirebaseConfig();
    private static String key = config.getProperty("auth");
    private static String sendNotifications = config.getProperty("sendNotifications");
    private static final Logger LOGGER = Logger.getLogger(RedisConnection.class.getName());


    public static void sendNotification(JSONObject userJSON, Notification notification) {
        try {
            if(sendNotifications.equals("false")) return;
            UserData userData = new Gson().fromJson(userJSON.toString(), UserData.class);
            String[] firebaseTokens = userData.getFirebaseTokens();
            if (firebaseTokens == null || firebaseTokens.length == 0) return;
            HttpClient client = HttpClient.newHttpClient();
            JSONObject body = new JSONObject();
            JSONObject notificationJSON = new JSONObject();
            notificationJSON.put("title", notification.getTitle());
            notificationJSON.put("body", notification.getBody());
            body.put("registration_ids", firebaseTokens);
            body.put("notification", notificationJSON);
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://fcm.googleapis.com/fcm/send"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", key).POST(
                            (HttpRequest.BodyPublishers.ofString(
                                    body.toString()
                            )
                            ))
                    .build();
            client
                    .sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(System.out::println);

            LOGGER.log(Level.INFO, "Notification Sent");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}


