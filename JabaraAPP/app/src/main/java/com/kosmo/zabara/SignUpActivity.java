package com.kosmo.zabara;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kosmo.zabara.databinding.ActivitySignUpBinding;

public class SignUpActivity extends BaseActivity {
    public SignUpActivity() {
        super(TransitionMode.HORIZON);
    }
    private ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}