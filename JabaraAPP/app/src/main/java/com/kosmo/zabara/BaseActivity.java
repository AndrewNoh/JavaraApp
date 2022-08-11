package com.kosmo.zabara;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

abstract class BaseActivity extends AppCompatActivity {

    private TransitionMode transitionMode;

    public BaseActivity(TransitionMode transitionMode) {
        this.transitionMode = transitionMode;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (transitionMode) {
            case HORIZON:
                overridePendingTransition(R.anim.horizon_enter, R.anim.none);
                break;
            case VERTICAL:
                overridePendingTransition(R.anim.vertical_enter, R.anim.none);
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        switch (transitionMode) {
            case HORIZON:
                overridePendingTransition(R.anim.none, R.anim.horizon_exit);
                break;
            case VERTICAL:
                overridePendingTransition(R.anim.none, R.anim.vertical_exit);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing()) {
            switch (transitionMode) {
                case HORIZON:
                    overridePendingTransition(R.anim.none, R.anim.horizon_exit);
                    break;
                case VERTICAL:
                    overridePendingTransition(R.anim.none, R.anim.vertical_exit);
                    break;
            }
        }
    }

    enum TransitionMode {
        NONE,
        HORIZON,
        VERTICAL;
    }

}