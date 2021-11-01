package com.company.RasCCTV;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.company.RasCCTV.MainActivity;
import com.company.RasCCTV.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(@NonNull String s){
        super.onNewToken(s);
        Log.d(TAG, "생성된 토큰 : "+s);
    }

    @Override

    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());



        //todo:2021-10-28 18:46:08.911 3880-3999/com.company.RasCCTV D/MyFirebaseMsgService: From: 582045769298
        //2021-10-28 18:46:08.912 3880-3999/com.company.RasCCTV D/MyFirebaseMsgService: Message Notification Body: ㅁㄴㅇㄹ
        //2021-10-28 18:46:55.571 3880-4022/com.company.RasCCTV D/MyFirebaseMsgService: From: 582045769298
        //2021-10-28 18:46:55.572 3880-4022/com.company.RasCCTV D/MyFirebaseMsgService: Message data payload: {body=RasCCTV detect someone, title=RasCCTV detect someone}

        if (remoteMessage.getData().size() > 0) {

            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (true) {

            } else {

                handleNow();

            }

        }

        if (remoteMessage.getNotification() != null) {

            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            sendNotification(remoteMessage.getNotification().getBody());

        }

    }

    private void handleNow() {

        Log.d(TAG, "Short lived task is done.");

    }



    private void sendNotification(String messageBody) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,

                PendingIntent.FLAG_ONE_SHOT);



        String channelId = getString(R.string.default_notification_channel_id);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =

                new NotificationCompat.Builder(this, channelId)

                        .setSmallIcon(R.mipmap.ic_launcher)

                        .setContentTitle("FCM Message")

                        .setContentText(messageBody)

                        .setAutoCancel(true)

                        .setSound(defaultSoundUri)

                        .setContentIntent(pendingIntent);



        NotificationManager notificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelName = getString(R.string.default_notification_channel_name);

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(channel);

        }

        notificationManager.notify(0, notificationBuilder.build());

    }

}