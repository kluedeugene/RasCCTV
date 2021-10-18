package com.company.RasCCTV;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {


    private final String TAG= "MyFirebase";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);

        Log.w(TAG, " onMessageReceived is called ");
        String msg, title;

        msg= remoteMessage.getNotification().getBody();
        title= remoteMessage.getNotification().getTitle();

        Notification.Builder noti= new Notification.Builder(this)
                .setContentTitle("New push from : "+title)
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,noti.build());


        //FCM 메시지를 받고 노티피케이션 데이터가 있다면
//        if (remoteMessage.getNotification()!=null){
//            Log.d(TAG, "title : "+ remoteMessage.getNotification().getTitle());
//            Log.d(TAG, "body : "+ remoteMessage.getNotification().getBody());
//
//            //제목과 내용외에 추가로 "키"와 "값"을 전송했따면 다음과 같은 방법으로 값을 가져올수있음.
////            Log.d(TAG, "전송된값 : "+ remoteMessage.getData().get("여기에 전송한 key를 입력"));
//        }
    }

    @Override
    public void onNewToken(@NonNull String s){
        super.onNewToken(s);
        Log.d(TAG, "생성된 토큰 : "+s);
    }


}