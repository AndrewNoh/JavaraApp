package com.kosmo.zabara.fragment.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kosmo.zabara.R;
import com.kosmo.zabara.api.dto.TenBoardDTO;
import com.kosmo.zabara.api.util.CHANGE_UTIL;
import com.kosmo.zabara.databinding.ItemKakaoViewBinding;

import java.util.List;

public class KakaoRecycleAdapter extends RecyclerView.Adapter<KakaoRecycleAdapter.KakaoViewHolder> {
    private List<TenBoardDTO> items;
    private ItemKakaoViewBinding binding;
    private Context context;

    public KakaoRecycleAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public KakaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemKakaoViewBinding.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View view = binding.getRoot();
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return new KakaoRecycleAdapter.KakaoViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFriendList(List<TenBoardDTO> list) {
        this.items = list;
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull KakaoViewHolder holder, int position) {
        holder.onBind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class KakaoViewHolder extends RecyclerView.ViewHolder {
        CardView item_kakao_view;
        ImageView mapRecycleImg;
        TextView mapRecycleMoney;
        TextView mapRecycleTitle;

        public KakaoViewHolder(@NonNull View itemView) {
            super(itemView);
            item_kakao_view = binding.itemKakaoView;
            mapRecycleImg = binding.mapRecycleImg;
            mapRecycleMoney = binding.mapRecycleMoney;
            mapRecycleTitle = binding.mapRecycleTitle;
        }

        void onBind(TenBoardDTO item) {
            String imageUrl = "http://192.168.0.38:8080/marketapp/resources/assets/img/product_img/";
            Glide.with(context).load(imageUrl + item.getImagename()).sizeMultiplier(0.5f).into(mapRecycleImg);
            mapRecycleMoney.setText(CHANGE_UTIL.intToWon(item.getUpper_price()));
            mapRecycleTitle.setText((String) item.getTitle());
            item_kakao_view.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putString("boardNo", item.getAuction_no());
                KakaoMapViewFragment kakaoMapViewFragment = new KakaoMapViewFragment();
                kakaoMapViewFragment.setArguments(bundle);
                ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.kakao_map_view_fragment, kakaoMapViewFragment)
                        .commit();
            });
        }
    }
}
