package com.appisoft.iperkz.firebase;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.callback.UpdateTokenRequestCallback;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.uploader.ByteBufferUploadProvider;
import com.appisoft.perkz.DisplayMessageActivity;
import com.appisoft.perkz.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.chromium.net.CronetEngine;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UrlRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FirebaseService extends FirebaseMessagingService {
    private String TAG = "FirebaseService";
    public final static String TOPIC_ALL = "ALL_";
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
       sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage receivedMessage) {

        Intent intent = new Intent(this, DisplayMessageActivity.class);
        String messgTitle = receivedMessage.getNotification().getTitle();
        String messgBody = receivedMessage.getNotification().getBody();
        /*
        Map<String, String> data = receivedMessage.getData();
        Set<String > ketset = data.keySet();
        String messageJson = data.get("MESSG_INFO");

        NotificationMessage message = new NotificationMessage();
        try {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(messageJson, NotificationMessage.class);
        }catch (Exception e) {
        }
        */
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "TEST")
                .setSmallIcon(R.drawable.ic_app_notify_icon)
                .setContentTitle(messgTitle)
                .setContentText(messgBody)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messgBody))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(-2, builder.build());

    }

    private void sendRegistrationToServer(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        CronetEngine cronetEngine = Cronet.getCronetEngine(this);
        UpdateTokenRequestCallback callback = new UpdateTokenRequestCallback();


        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                Data.SERVER_URL + "/api/updateToken", callback, executor);
        requestBuilder.addHeader("Content-Type","application/json");
        requestBuilder.setHttpMethod("POST");

        ObjectMapper mapper = new ObjectMapper();
        try {
            CustomerEntity customer = new CustomerEntity();
            LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
            customer.setCustomerId(loginRepository.getCustomerEntity().getCustomerId());
            customer.setToken(token);
            String jsonValue = mapper.writeValueAsString(customer);
            Log.println(Log.INFO, TAG, jsonValue);
            byte[] bytes = mapper.writeValueAsBytes(customer);
            UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
            requestBuilder.setUploadDataProvider(provider, executor);
            UrlRequest request = requestBuilder.build();
            request.start();
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
