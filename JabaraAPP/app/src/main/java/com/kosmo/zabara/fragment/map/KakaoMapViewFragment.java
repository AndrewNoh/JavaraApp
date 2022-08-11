package com.kosmo.zabara.fragment.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosmo.zabara.R;
import com.kosmo.zabara.api.dto.BoardDTO;
import com.kosmo.zabara.api.dto.ImageDTO;
import com.kosmo.zabara.api.dto.TenBoardDTO;
import com.kosmo.zabara.api.dto.UserDTO;
import com.kosmo.zabara.api.service.BoardService;
import com.kosmo.zabara.api.util.CHANGE_UTIL;
import com.kosmo.zabara.api.util.TIME_MAXIMUM;
import com.kosmo.zabara.databinding.FragmentKakaoMapViewBinding;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class KakaoMapViewFragment extends Fragment {

    private FragmentKakaoMapViewBinding binding;
    private Context contextNullSafe;
    Bundle bundle;
    String boardNo;
    SlidrInterface slidrInterface;
    SharedPreferences preferences;
    String result = "";

    String email;
    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create())
            .baseUrl("http://192.168.0.38:9090/market/").build();
    BoardService boardService = retrofit.create(BoardService.class);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKakaoMapViewBinding.inflate(inflater, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        bundle = this.getArguments();
        if (bundle != null)
            boardNo = bundle.getString("boardNo");
        preferences = contextNullSafe.getSharedPreferences("usersInfo", Context.MODE_PRIVATE);
        email = preferences.getString("email", "");

        //최초 실행
        initRetrofit();
        //입찰 낙찰
        successfulBid();

        binding.mapViewHeart.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Map map = new HashMap();
                map.put("auction_no", boardNo);
                map.put("email", email);
                map.put("isLike", true);
                Call<Map> call = boardService.likeOne(map);
                call.enqueue(new Callback<Map>() {
                    @Override
                    public void onResponse(Call<Map> call, Response<Map> response) {
                        Map map = response.body();
                        boolean islike = (boolean) map.get("updateLike");
                        if (islike) {
                            Toast.makeText(contextNullSafe, "관심목록에 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map> call, Throwable t) {
                    }
                });
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Map map = new HashMap();
                map.put("auction_no", boardNo);
                map.put("email", email);
                map.put("isLike", false);
                Call<Map> call = boardService.likeOne(map);
                call.enqueue(new Callback<Map>() {
                    @Override
                    public void onResponse(Call<Map> call, Response<Map> response) {
                        Map map = response.body();
                        if ((boolean) map.get("deleteLike")) {
                            Toast.makeText(contextNullSafe, "관심목록에서 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map> call, Throwable t) {
                    }
                });
            }
        });

        binding.mapViewImage.setIndicatorAnimation(IndicatorAnimationType.SWAP); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.mapViewImage.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.mapViewImage.startAutoCycle();
        return binding.getRoot();
    }


    private void initRetrofit() {
        Map map = new HashMap();
        map.put("auction_no", boardNo);
        map.put("email", email);
        Call<Map> call = boardService.viewSelect(map);
        final Dialog dialog = new Dialog(contextNullSafe);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_custom_loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        call.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                Map map = response.body();
                ObjectMapper mapper = new ObjectMapper();
                List<ImageDTO> images = mapper.convertValue(map.get("imageList"), new TypeReference<List<ImageDTO>>() {
                });
                BoardDTO boardNoselectOne = mapper.convertValue(map.get("boardNoselectOne"), new TypeReference<BoardDTO>() {
                });
                UserDTO userInfo = mapper.convertValue(map.get("userInfo"), new TypeReference<UserDTO>() {
                });
                List<TenBoardDTO> getAuctionTen = mapper.convertValue(map.get("getAuctionTen"), new TypeReference<List<TenBoardDTO>>() {
                });
                List<TenBoardDTO> getRecommendTen = mapper.convertValue(map.get("getRecommendTen"), new TypeReference<List<TenBoardDTO>>() {
                });
                if (userInfo.getEmail().equals(email)) {
                    binding.mapViewAcc.setVisibility(View.VISIBLE);
                    binding.mapViewBid.setVisibility(View.GONE);
                    binding.mapViewIdText.setVisibility(View.GONE);
                    binding.mapViewIdView.setVisibility(View.GONE);
                    binding.mapViewRecyclerview.setVisibility(View.GONE);
                } else {
                    binding.mapViewAcc.setVisibility(View.GONE);
                    binding.mapViewBid.setVisibility(View.VISIBLE);
                }
                if (boardNoselectOne.getStatus().equals("END")) {
                    binding.mapViewBid.setVisibility(View.GONE);
                    binding.mapViewAcc.setVisibility(View.GONE);
                    binding.mapViewCha.setVisibility(View.VISIBLE);
                }

                boolean isLike = (boolean) map.get("isLike");
                KakaoSliderAdapter adapter = new KakaoSliderAdapter(contextNullSafe);
                String imageUrl = "http://192.168.0.38:8080/marketapp/resources/assets/img/product_img/";
                for (ImageDTO getImage : images)
                    adapter.addItem(imageUrl + getImage.getImageName());
                binding.mapViewImage.setSliderAdapter(adapter);
                String profileUrl = "http://192.168.0.38:8080/marketapp/resources/assets/img/zabaraImg/";
                Glide.with(contextNullSafe).load(profileUrl + userInfo.getProfile_img()).into(binding.mapViewProfile);
                binding.mapViewId.setText((String) boardNoselectOne.getNickName());
                binding.mapViewSimpleAdr.setText((String) boardNoselectOne.getSimpleaddress());
                binding.mapViewTitle.setText((String) boardNoselectOne.getTitle());
                binding.mapViewCategory.setText((String) boardNoselectOne.getCategory());
                String time = TIME_MAXIMUM.formatTimeString((Date) boardNoselectOne.getPostDate());
                binding.mapViewTime.setText(time);
                binding.mapViewContent.setText((String) boardNoselectOne.getContent());
                binding.mapViewIdText.setText((String) boardNoselectOne.getNickName() + "님의 판매 상품");
                binding.mapViewHeart.setLiked(isLike);
                binding.mapViewMoney.setText(CHANGE_UTIL.intToWon(boardNoselectOne.getUpper_Price()));
                binding.mapViewStartMoney.setText(CHANGE_UTIL.intToWon(boardNoselectOne.getBase_Price()));

                RecyclerView recyclerView = binding.mapViewRecyclerview;
                KakaoRecycleAdapter kakaoRecycleAdapter = new KakaoRecycleAdapter(contextNullSafe);
                recyclerView.setLayoutManager(new GridLayoutManager(contextNullSafe, 2));
                recyclerView.addItemDecoration(new ItemDecoration((Activity) contextNullSafe));
                kakaoRecycleAdapter.setFriendList(getAuctionTen);
                recyclerView.setAdapter(kakaoRecycleAdapter);

                RecyclerView recommendView = binding.mapViewRecommendview;
                KakaoRecycleAdapter RecommendRecycleAdapter = new KakaoRecycleAdapter(contextNullSafe);
                recommendView.setLayoutManager(new GridLayoutManager(contextNullSafe, 2));
                recommendView.addItemDecoration(new ItemDecoration((Activity) contextNullSafe));
                RecommendRecycleAdapter.setFriendList(getRecommendTen);
                recommendView.setAdapter(RecommendRecycleAdapter);
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (slidrInterface == null)
            slidrInterface = Slidr.replace(binding.contentContainer, new SlidrConfig.Builder().position(SlidrPosition.LEFT).build());
    }


    @SuppressLint("SetTextI18n")
    private void successfulBid() {
        //낙찰
        binding.mapViewAcc.setOnClickListener(view -> {
            int upMoney = CHANGE_UTIL.wonToInt(binding.mapViewMoney.getText().toString());
            int baMoney = CHANGE_UTIL.wonToInt(binding.mapViewStartMoney.getText().toString());
            if (upMoney == baMoney) {
                Toast.makeText(contextNullSafe, "현재 입찰자가 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                final Dialog dialog = new Dialog(contextNullSafe);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.item_newcustom_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                TextView title = dialog.findViewById(R.id.dialog_title);
                title.setText("낙찰");
                TextView content = dialog.findViewById(R.id.dialog_content);
                content.setText("정말로 " + binding.mapViewMoney.getText() + "에 낙찰하시겠습니까?");

                FrameLayout mDialogOk = dialog.findViewById(R.id.frmOk);
                mDialogOk.setOnClickListener(v1 -> {
                    Map map = new HashMap();
                    map.put("auction_no", boardNo);
                    map.put("status", "END");
                    Call<Map> call = boardService.changeStatus(map);
                    call.enqueue(new Callback<Map>() {
                        @Override
                        public void onResponse(Call<Map> call, Response<Map> response) {
                            Toast.makeText(contextNullSafe, "낙찰이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            binding.mapViewAcc.setVisibility(View.GONE);
                            binding.mapViewCha.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailure(Call<Map> call, Throwable t) {
                        }
                    });
                    dialog.cancel();
                });
                FrameLayout mDialogNo = dialog.findViewById(R.id.frmNo);
                mDialogNo.setOnClickListener(v12 -> dialog.dismiss());
                dialog.show();
            }
        });
        //입찰
        binding.mapViewBid.setOnClickListener(view -> {
            int upMoney = CHANGE_UTIL.wonToInt(binding.mapViewMoney.getText().toString());
            int baMoney = CHANGE_UTIL.wonToInt(binding.mapViewStartMoney.getText().toString());
            final Dialog dialog = new Dialog(contextNullSafe);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.item_newcustom2_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            TextView title = dialog.findViewById(R.id.dialog_title2);
            title.setText("입찰");

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
                if (editText.getText() == null || "".contentEquals(editText.getText()))
                    dialog.cancel();
                int money = CHANGE_UTIL.commaToInt(editText.getText().toString());
                if (baMoney >= money) {
                    Toast.makeText(contextNullSafe, "경매 시작가보다 높은 금액을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (upMoney >= money) {
                    Toast.makeText(contextNullSafe, "최고 입찰가보다 높은 금액을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Map map = new HashMap();
                    map.put("auction_no", boardNo);
                    map.put("email", email);
                    map.put("newprice", money);
                    Call<Map> call = boardService.updatePrice(map);
                    call.enqueue(new Callback<Map>() {
                        @Override
                        public void onResponse(Call<Map> call, Response<Map> response) {
                            binding.mapViewMoney.setText(CHANGE_UTIL.intToWon(money+""));
                            Toast.makeText(contextNullSafe, "입찰에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Map> call, Throwable t) {

                        }
                    });
                }
                dialog.cancel();
            });
            FrameLayout mDialogNo = dialog.findViewById(R.id.frmNo2);
            mDialogNo.setOnClickListener(v12 -> dialog.dismiss());
            dialog.show();
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.kakao_location_btn).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.kakao_layer_btn).setVisibility(View.VISIBLE);
        }catch (Exception e){

        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }


    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null)
            return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }
}