package com.kosmo.zabara;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
        //스레드 정의
        Runnable runnable = () -> {
            Intent intent = new Intent(SplashActivity.this, IntroLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        };
        //3초후에 스레드 실행
        worker.schedule(runnable, 5, TimeUnit.SECONDS);
    }

}