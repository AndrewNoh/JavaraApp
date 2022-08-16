package com.kosmo.zabara;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.kosmo.zabara.api.dto.UserDTO;
import com.kosmo.zabara.api.service.UserService;
import com.kosmo.zabara.api.util.PermissionUtils;
import com.kosmo.zabara.databinding.ActivityIntroLoginBinding;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.Map;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class IntroLoginActivity extends AppCompatActivity {
    private ActivityIntroLoginBinding binding;
    final Handler handler = new Handler();
    private String[] requestPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private PermissionUtils permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroLoginBinding.inflate(getLayoutInflater());
        permissionUtils = new PermissionUtils(this,this,requestPermissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!permissionUtils.checkPermission())
                permissionUtils.requestPermissions();
        }////////////////if

        binding.kakaoButton.setOnClickListener(kakaoLogin);
        binding.naverButton.setOnClickListener(naverLogin);

        binding.loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        });
        setContentView(binding.getRoot());
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private View.OnClickListener kakaoLogin = v -> {
        // Start the loading animation when the user tap the button
        binding.kakaoButton.startAnimation();
        // Do your networking task or background work here.
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(IntroLoginActivity.this))
            login();
        else
            accountLogin();
    };//nomalLogin

    private View.OnClickListener naverLogin = v -> {
        // Start the loading animation when the user tap the button
        binding.naverButton.startAnimation();
        // Do your networking task or background work here.

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            boolean isSuccessful = true;
            // Choose a stop animation if your call was succesful or not
            if (isSuccessful) {
                binding.naverButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, () -> {
                    Intent intent = new Intent(getBaseContext(), NewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                });
            } else {
                binding.naverButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
            }
        }, 2000);
    };//nomalLogin

    public void login() {
        String TAG = "login()";
        UserApiClient.getInstance().loginWithKakaoTalk(IntroLoginActivity.this, (oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "로그인 실패", error);
                handler.postDelayed(() -> {
                    binding.kakaoButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                }, 2000);
            } else if (oAuthToken != null) {
                Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());

                UserApiClient.getInstance().me((user, throwable) -> {
                    if (user != null) {
                        // 유저 정보가 정상 전달 되었을 경우
                        String email = Objects.requireNonNull(user.getKakaoAccount()).getEmail();
                        String password = Objects.requireNonNull(user.getKakaoAccount()).getEmail() + "kakaoLogin";
                        Retrofit retrofit = new Retrofit.Builder()
                                .addConverterFactory(JacksonConverterFactory.create())
                                .baseUrl("http://192.168.0.38:9090/market/").build();
                        UserService service = retrofit.create(UserService.class);
                        UserDTO dto = new UserDTO();
                        dto.setEmail(email);
                        dto.setPassword(password);
                        Call<Map<String, Boolean>> call = service.isLogin(dto);
                        call.enqueue(new Callback<Map<String, Boolean>>() {
                            @SneakyThrows
                            @Override
                            public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {
                                if (response.isSuccessful()) {
                                    Map<String, Boolean> result = response.body();
                                    boolean isLogin = result.get("isLogin");
                                    if (isLogin) {//회원
                                        //컨텐츠 화면(MainActivity)으로 전환
                                        handler.postDelayed(() -> {
                                            binding.kakaoButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, () -> {
                                                Intent intent = new Intent(getBaseContext(), NewActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                startActivity(intent);
                                            });
                                        }, 2000);
                                        //다른 화면에서 로그인 여부 판단을 위한 아이디 저장
                                        SharedPreferences preferences = getSharedPreferences("usersInfo", MODE_PRIVATE);
                                        preferences.edit().putString("email", email).commit();
                                        preferences.edit().putString("password", password).commit();

                                    } else {//비회원
                                        new AlertDialog.Builder(IntroLoginActivity.this)
                                                .setIcon(android.R.drawable.ic_lock_lock)
                                                .setTitle("로그인")
                                                .setMessage("아이디와 비번이 불일치합니다")
                                                .setPositiveButton("확인", null).show();
                                    }
                                } else {
                                    Log.i("com.kosmo.kosmoapp", "응답에러:" + response.errorBody().string());
                                }
                                SystemClock.sleep(2000);
                            }

                            @Override
                            public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    }
                    if (throwable != null) {
                        // 로그인 시 오류 났을 때
                        // 키해시가 등록 안 되어 있으면 오류 납니다.
                        Log.w(TAG, "invoke: " + throwable.getLocalizedMessage());
                    }
                    return null;
                });
            }
            return null;
        });
    }

    public void accountLogin() {
        String TAG = "accountLogin()";
        UserApiClient.getInstance().loginWithKakaoAccount(IntroLoginActivity.this, (oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "로그인 실패", error);
                handler.postDelayed(() -> {
                    binding.kakaoButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                }, 2000);
            } else if (oAuthToken != null) {
                Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());

                UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                    @Override
                    public Unit invoke(User user, Throwable throwable) {
                        if (user != null) {
                            // 유저 정보가 정상 전달 되었을 경우
                            String email = Objects.requireNonNull(user.getKakaoAccount()).getEmail();
                            String password = Objects.requireNonNull(user.getId()).toString();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .addConverterFactory(JacksonConverterFactory.create())
                                    .baseUrl("http://192.168.0.38:9090/market/").build();
                            UserService service = retrofit.create(UserService.class);
                            UserDTO dto = new UserDTO();
                            dto.setEmail(email);
                            dto.setPassword(password);
                            Call<Map<String, Boolean>> call = service.isLogin(dto);
                            call.enqueue(new Callback<Map<String, Boolean>>() {
                                @SneakyThrows
                                @Override
                                public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {
                                    if (response.isSuccessful()) {
                                        Map<String, Boolean> result = response.body();
                                        boolean isLogin = result.get("isLogin");
                                        if (isLogin) {//회원
                                            //컨텐츠 화면(MainActivity)으로 전환
                                            handler.postDelayed(() -> {
                                                binding.kakaoButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, () -> {
                                                    Intent intent = new Intent(getBaseContext(), NewActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                    startActivity(intent);
                                                });
                                            }, 2000);
                                            //다른 화면에서 로그인 여부 판단을 위한 아이디 저장
                                            SharedPreferences preferences = getSharedPreferences("usersInfo", MODE_PRIVATE);
                                            preferences.edit().putString("email", email).commit();
                                            preferences.edit().putString("password", password).commit();

                                        } else {//비회원
                                            new AlertDialog.Builder(IntroLoginActivity.this)
                                                    .setIcon(android.R.drawable.ic_lock_lock)
                                                    .setTitle("로그인")
                                                    .setMessage("아이디와 비번이 불일치합니다")
                                                    .setPositiveButton("확인", null).show();
                                        }
                                    } else {
                                        Log.i("com.kosmo.kosmoapp", "응답에러:" + response.errorBody().string());
                                    }
                                    SystemClock.sleep(2000);
                                }

                                @Override
                                public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                        }
                        if (throwable != null) {
                            // 로그인 시 오류 났을 때
                            // 키해시가 등록 안 되어 있으면 오류 납니다.
                            Log.w(TAG, "invoke: " + throwable.getLocalizedMessage());
                        }
                        return null;
                    }
                });
            }
            return null;
        });
    }
}