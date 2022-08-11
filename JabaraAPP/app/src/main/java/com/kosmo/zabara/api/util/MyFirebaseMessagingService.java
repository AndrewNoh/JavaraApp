package com.kosmo.zabara.api.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kosmo.zabara.MainActivity;
import com.kosmo.zabara.NewActivity;
import com.kosmo.zabara.api.service.FCMService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MyFirebaseMessagingService  extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        //super.onMessageReceived(message);
        Map<String,String> pushNessage = new HashMap<>();
        if(message.getNotification() !=null){
            pushNessage.put("title",message.getNotification().getTitle());
            pushNessage.put("body",message.getNotification().getBody());
        }////////////
        if(message.getData() !=null && message.getData().size() > 0 ){
            //※※※사용자 웹(우리가 만든 웹 앱) UI 폼 하위요소의 파라미터명이 키 이다
            //파이어베이스 웹 UI는 데이타(선택사항)의 키 입력값이 키이다
            Map<String,String> map=message.getData();
            Set<String> keys = map.keySet();
            for(String key:keys){
                String value = map.get(key);
                pushNessage.put(key,value);
            }
        }
        Set<String> keys = pushNessage.keySet();
        for(String key:keys){
            String value = pushNessage.get(key);
            pushNessage.put(key,value);
        }
        //푸쉬메시지를 받으면 앱의 상태바에 노티케이션 띄우기
        showNotification(pushNessage);
    }

    private void showNotification(Map<String, String> pushNessage) {
        Intent intent = new Intent(this, NewActivity.class);
        intent.putExtra("title",pushNessage.get("title"));
        intent.putExtra("body",pushNessage.get("body"));
        //인텐트에 부가정보 저장("title","body")키는 필수고 나머지키는 사용자 정의 키이다)
        Set<String> keys = pushNessage.keySet();
        String message="";
        for(String key:keys){//데이타 메시지는 "message"라는 키로 하나의 문자열로 저장하자
            if(!(key.equals("title") || key.equals("body"))){
                String value = pushNessage.get(key);
                message+=String.format("%s:%s%n",key,value);
                intent.putExtra("message",message);
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //펜딩 인텐트로 설정
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = createNotificationCompatBuilder(pushNessage.get("title"),pushNessage.get("body"));

        //실행할 펜딩 인텐트 설정
        builder.setContentIntent(pendingIntent);
        //Notification객체 생성
        Notification notification=builder.build();
        //통지하기
        //NotificationManager의 notify()메소드로 Notification객체 등록
        //notify(Notification을 구분하기 위한 구분자,Notification객체);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {//버전체크 코드
            //오레오 부터 아래 코드 추가해야 함 시작
            NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ID", "CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);//스마트폰에 노티가 도착했을때 빛을 표시할지 안할지 설정
            notificationChannel.setLightColor(Color.RED);//위 true설정시 빛의 색상
            notificationChannel.enableVibration(true);//노티 도착시 진동 설정
            notificationChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,100});//진동 시간(1000분의 1초)
            //오레오 부터 아래 코드 추가해야 함 끝
            //노피케이션 매니저와 연결
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(1,notification);
    }
    private NotificationCompat.Builder createNotificationCompatBuilder(String title,String content){
        return new NotificationCompat.Builder(this,"CHANNEL_ID")
                .setSmallIcon(android.R.drawable.ic_dialog_email)//노티 도착시 상태바에 표시되는 아이콘
                .setContentTitle(title)//노티 드래그시 보이는 제목
                .setContentText(content)//노티 드래그시 보이는 내용
                .setAutoCancel(true)//노티 드래그후 클릭시 상태바에서 자동으로 사라지도록 설정
                .setWhen(SystemClock.currentThreadTimeMillis())//노티 전달 시간
                .setDefaults(Notification.DEFAULT_VIBRATE);//노티시 알림 방법
    }/////////////////
    //토큰이 변경될때마다 호출되는 콜백 메소드- FCM에서 발행된 토큰을 우리의 서버로 전송
    @Override
    public void onNewToken(@NonNull String token) {
        //생성된 토큰을 내 서버에 보내기
        sendNewTokenToMyServer(token);
    }/////////////
    //내가 만든 웹 서비스(웹 어플리케이션)(UI)와 연동하기 위한 코드들
    private void sendNewTokenToMyServer(String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl("http://192.168.0.38:8080/marketapp/")
                .build();
        FCMService service=retrofit.create(FCMService.class);
        Call<Map<String,String>> call=service.newToken(token);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {t.printStackTrace();}
        });
    }////////////////////
}
