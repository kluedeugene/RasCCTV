package com.company.RasCCTV;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    //새 토큰이 생성될때마다 onNewToken 함수가 호출 됨. 토큰은 다음의 이유로 변경될수잇음
    //1.앱에서 인스턴스 id 삭제
    //2.새 기기에서 앱 복원
    //3.사용자가 앱삭제 / 재설치
    //4.사용자가 앱 데이터 소거

    private final String TAG= "MyFirebase";

//    public MyFirebaseMessaging() {
//        super();
//    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //FCM 메시지를 받고 노티피케이션 데이터가 있다면
        if (remoteMessage.getNotification()!=null){
            Log.d(TAG, "title : "+ remoteMessage.getNotification().getTitle());
            Log.d(TAG, "body : "+ remoteMessage.getNotification().getBody());

            //제목과 내용외에 추가로 "키"와 "값"을 전송했따면 다음과 같은 방법으로 값을 가져올수있음.
//            Log.d(TAG, "전송된값 : "+ remoteMessage.getData().get("여기에 전송한 key를 입력"));
        }
    }

    @Override
    public void onNewToken(String s){
        Log.d(TAG, "생성된 토큰 : "+s);
    }

//    private RemoteViews getCustomDesign(String title, String message) {
//        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
//        remoteViews.setTextViewText(R.id.noti_title, title);
//        remoteViews.setTextViewText(R.id.noti_message, message);
//        remoteViews.setImageViewResource(R.id.logo, R.drawable.popup_logo);
//        return remoteViews;
//    }

}
