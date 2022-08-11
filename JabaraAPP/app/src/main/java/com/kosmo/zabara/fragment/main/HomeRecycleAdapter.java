package com.kosmo.zabara.fragment.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kosmo.zabara.R;
import com.kosmo.zabara.api.dto.MainBoardDTO;
import com.kosmo.zabara.api.service.BoardService;
import com.kosmo.zabara.api.util.CHANGE_UTIL;
import com.kosmo.zabara.api.util.TIME_MAXIMUM;
import com.kosmo.zabara.databinding.ItemMainLayoutBinding;
import com.kosmo.zabara.fragment.map.KakaoMapViewFragment;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.ramotion.foldingcell.FoldingCell;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HomeRecycleAdapter extends RecyclerView.Adapter<HomeRecycleAdapter.HomeViewHolder> {
    private List<MainBoardDTO> items;
    private ItemMainLayoutBinding itemBinding;
    private Context context;

    public HomeRecycleAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = ItemMainLayoutBinding.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View view = itemBinding.getRoot();
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return new HomeViewHolder(view);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setFriendList(List<MainBoardDTO> list) {
        this.items = list;
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        holder.onBind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {

        ImageView subImage;
        TextView subTitle;
        TextView subTime;
        TextView subMoney;
        SliderView main_item;
        ImageView main_profile_image;
        TextView main_username;
        TextView main_title;
        TextView main_category;
        TextView main_time;
        TextView main_content;
        TextView main_money;
        HomeSliderAdapter adapter;
        TextView heart;
        ImageView share;
        Button button;

        //속성에 private을 역시 붙이지 않는다.
        public HomeViewHolder(@NonNull View itemView) {//itemView는 onCreateViewHolder()에서 전개한 아이템뷰
            super(itemView);
            final FoldingCell fc = itemBinding.foldingCell;
            fc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fc.toggle(false);
                }
            });
            subImage = itemBinding.subImage;
            subTitle = itemBinding.subTitle;
            subTime = itemBinding.subTime;
            subMoney = itemBinding.subMoney;
            main_item = itemBinding.mainItem;
            main_profile_image = itemBinding.mainProfileImage;
            main_username = itemBinding.mainUsername;
            main_title = itemBinding.mainTitle;
            main_category = itemBinding.mainCategory;
            main_time = itemBinding.mainTime;
            main_content = itemBinding.mainContent;
            main_money = itemBinding.mainMoney;
            heart = itemBinding.mainHeart;
            share = itemBinding.mainShare;
            button = itemBinding.mainBtn;
        }

        void onBind(MainBoardDTO item) {
            String profileUrl = "http://192.168.0.38:8080/marketapp/resources/assets/img/zabaraImg/" + item.getProfileImageResId();
            String imageUrl = "http://192.168.0.38:8080/marketapp/resources/assets/img/product_img/" + item.getItemImageResId().get(0);
            Glide.with(context).load(imageUrl).into(subImage);
            subTitle.setText(item.getTitle());
            String time = TIME_MAXIMUM.formatTimeString(item.getPostDate());
            subTime.setText(time);
            String money = CHANGE_UTIL.intToWon(item.getMoney());
            subMoney.setText(money);
            adapter = new HomeSliderAdapter(context);
            for (String getImage : item.getItemImageResId())
                adapter.addItem("http://192.168.0.38:8080/marketapp/resources/assets/img/product_img/" + getImage);
            main_item.setSliderAdapter(adapter);
            main_item.setIndicatorAnimation(IndicatorAnimationType.DROP);
            main_item.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
            main_item.setIndicatorSelectedColor(Color.CYAN);
            main_item.startAutoCycle();
            Glide.with(context).load(profileUrl).into(main_profile_image);
            main_username.setText(item.getUsername());
            main_title.setText(item.getTitle());
            main_category.setText(item.getMainCategory());
            main_time.setText(time);
            main_content.setText(item.getContent());
            heart.setText(item.getLike());
            main_money.setText(money);
            share.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                // String으로 받아서 넣기
                String sendMessage = "우리동네 좋은 물건 자바라 - http://192.168.0.38:8080/marketapp/board/shareView.do?no=" + item.getAuction_no();
                intent.putExtra(Intent.EXTRA_TEXT, sendMessage);
                Intent shareIntent = Intent.createChooser(intent, "share");
                context.startActivity(shareIntent);
            });
            button.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putString("boardNo", item.getAuction_no());
                KakaoMapViewFragment kakaoMapViewFragment = new KakaoMapViewFragment();
                kakaoMapViewFragment.setArguments(bundle);
                ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .add(R.id.home, kakaoMapViewFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }
    }
}
