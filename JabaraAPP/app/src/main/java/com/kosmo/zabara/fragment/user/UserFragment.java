package com.kosmo.zabara.fragment.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.sdk.user.UserApiClient;
import com.kosmo.zabara.IntroLoginActivity;
import com.kosmo.zabara.R;
import com.kosmo.zabara.api.dto.PayDTO;
import com.kosmo.zabara.api.dto.UserDTO;
import com.kosmo.zabara.api.service.UserService;
import com.kosmo.zabara.api.util.CHANGE_UTIL;
import com.kosmo.zabara.databinding.FragmentUserBinding;

import java.util.HashMap;
import java.util.Map;

import kr.co.bootpay.android.Bootpay;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class UserFragment extends Fragment {
    private Context context;
    private FragmentUserBinding binding;
    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create())
            .baseUrl("http://192.168.0.38:9090/market/").build();
    UserService service = retrofit.create(UserService.class);
    String result = "";
    ObjectMapper mapper = new ObjectMapper();


    private static UserFragment instance = new UserFragment();

    private UserFragment() {
    }

    public static UserFragment getInstance() {
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        binding.logOut.setOnClickListener(view -> {
            UserApiClient.getInstance().logout(throwable -> null);
            Intent intent = new Intent(context, IntroLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });
        binding.userPay.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.item_newcustom2_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            EditText editText = dialog.findViewById(R.id.dialog_edit);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)) {
                        result = CHANGE_UTIL.intToComma(charSequence.toString());
                        editText.setText(result);
                        editText.setSelection(result.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            FrameLayout mDialogOk = dialog.findViewById(R.id.frmOk2);
            mDialogOk.setOnClickListener(v1 -> {
                if (editText.getText().toString().length() != 0) {
                    Double money = Double.valueOf(String.valueOf(editText.getText()).replaceAll(",", ""));
                    bootPay(money);
                }else{
                    Toast.makeText(context, "금액을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();
            });
            FrameLayout mDialogNo = dialog.findViewById(R.id.frmNo2);
            mDialogNo.setOnClickListener(v12 -> {
                Toast.makeText(context, "충전이 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
            dialog.show();


        });

        SharedPreferences preferences = context.getSharedPreferences("usersInfo", Context.MODE_PRIVATE);
        String email = preferences.getString("email", "");
        Call<Map> call = service.callUserInfo(email);
        call.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                if (response.isSuccessful()) {
                    Map map = response.body();
                    UserDTO result = mapper.convertValue(map.get("userDTO"), new TypeReference<UserDTO>() {
                    });
                    PayDTO payDTO = mapper.convertValue(map.get("payDTO"), new TypeReference<PayDTO>() {
                    });
                    String imageUrl = "http://192.168.0.38:8080/marketapp/resources/assets/img/zabaraImg/" + result.getProfile_img();
                    Glide.with(context).load(imageUrl).into(binding.mypageProfile);
                    binding.mypageUsername.setText(result.getNickname());
                    binding.payMoney.setText(CHANGE_UTIL.intToWon(payDTO.getBalance())
                    );
                }
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {

            }

        });
        binding.userLikes.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "like");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.userPurchase.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "purchase");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();

        });
        binding.userSell.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "sell");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });


        return binding.getRoot();
    }

    private void bootPay(Double money) {
        BootUser user = new BootUser().setPhone("010-1234-5678"); // 구매자 정보
        BootExtra extra = new BootExtra()
                .setCardQuota("0,2,3"); // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)

        Payload payload = new Payload();
        payload.setApplicationId("62d0d242e38c3000235afa9a")
                .setOrderName("자바라 페이")
                .setPg(payload.getPg())
                .setMethod(payload.getMethod())
                .setOrderId("1234")
                .setPrice(money)
                .setUser(user)
                .setExtra(extra);

        Bootpay.init(requireActivity().getSupportFragmentManager(), requireActivity().getApplicationContext())
                .setPayload(payload)
                .setEventListener(new BootpayEventListener() {
                    @Override
                    public void onCancel(String data) {
                        Log.d("bootpay", "cancel: " + data);
                    }

                    @Override
                    public void onError(String data) {
                        Log.d("bootpay", "error: " + data);
                    }

                    @Override
                    public void onClose(String data) {
                        Log.d("bootpay", "close: " + data);
                        Bootpay.removePaymentWindow();
                    }

                    @Override
                    public void onIssued(String data) {
                        Log.d("bootpay", "issued: " + data);
                    }

                    @Override
                    public boolean onConfirm(String data) {
                        Log.d("bootpay", "confirm: " + data);
                        return true;
                    }

                    @Override
                    public void onDone(String data) {
                        SharedPreferences preferences = context.getSharedPreferences("usersInfo", Context.MODE_PRIVATE);
                        String email = preferences.getString("email", "");
                        Map map = new HashMap();
                        map.put("email", email);
                        map.put("balance", money.intValue());
                        Call<Map> call = service.deposit(map);
                        call.enqueue(new Callback<Map>() {
                            @Override
                            public void onResponse(Call<Map> call, Response<Map> response) {
                                Map map = response.body();
                                PayDTO payDTO = mapper.convertValue(map.get("payDTO"), new TypeReference<PayDTO>() {
                                });
                                binding.payMoney.setText(CHANGE_UTIL.intToWon(payDTO.getBalance()));
                                Toast.makeText(context, payDTO.getDeposit() + "원이 충전이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<Map> call, Throwable t) {

                            }
                        });
                    }

                }).requestPayment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }
}