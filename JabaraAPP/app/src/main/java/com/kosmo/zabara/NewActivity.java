package com.kosmo.zabara;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kosmo.zabara.fragment.user.UserFragment;
import com.kosmo.zabara.fragment.chat.ChatFragment;
import com.kosmo.zabara.fragment.main.HomeFragment;
import com.kosmo.zabara.fragment.map.KakaoMapFragment;

public class NewActivity extends AppCompatActivity {
    final String TAG = this.getClass().getSimpleName();

    LinearLayout home_ly;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        init(); //객체 정의
        SettingListener(); //리스너 등록
        //맨 처음 시작할 탭 설정
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void init() {
        home_ly = findViewById(R.id.home_ly);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void SettingListener() {
        //선택 리스너 등록
        bottomNavigationView.setOnItemSelectedListener(new TabSelectedListener());
        bottomNavigationView.setSelectedItemId(R.id.tab_home);

    }

    //프레그먼트 생성
    class TabSelectedListener implements BottomNavigationView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.tab_home: {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.home_ly, HomeFragment.getInstance())
                            .commit();
                    return true;
                }
                case R.id.tab_files: {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.home_ly, KakaoMapFragment.getInstance())
                            .commit();
                    return true;
                }
                case R.id.tab_chat: {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.home_ly,ChatFragment.getInstance())
                            .commit();
                    return true;
                }
                case R.id.tab_user: {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.home_ly, UserFragment.getInstance())
                            .commit();
                    return true;
                }
            }

            return false;
        }
    }
}