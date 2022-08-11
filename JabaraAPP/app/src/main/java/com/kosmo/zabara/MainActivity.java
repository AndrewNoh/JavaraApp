package com.kosmo.zabara;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.kosmo.zabara.databinding.ActivityMainBinding;
import com.royrodriguez.transitionbutton.TransitionButton;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    public MainActivity() {
        super(TransitionMode.HORIZON);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.transitionButton.setOnClickListener(nomalLogin);
        binding.signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), SignUpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });
    }

    private View.OnClickListener nomalLogin = v -> {
        binding.transitionButton.startAnimation();
        // Do your networking task or background work here.
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            boolean isSuccessful = true;
            // Choose a stop animation if your call was succesful or not
            if (isSuccessful) {
                binding.transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, () -> {
                    Intent intent = new Intent(getBaseContext(), NewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                });
            } else {
                binding.transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
            }
        }, 2000);
    };//nomalLogin

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), IntroLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }




}