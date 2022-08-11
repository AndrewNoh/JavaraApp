package com.kosmo.zabara.fragment.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kosmo.zabara.R;
import com.kosmo.zabara.api.dto.ShowDTO;
import com.kosmo.zabara.api.util.CHANGE_UTIL;
import com.kosmo.zabara.api.util.TIME_MAXIMUM;
import com.kosmo.zabara.databinding.ItemShowCardBinding;
import com.kosmo.zabara.fragment.map.KakaoMapViewFragment;

import java.util.List;


public class DetailRecycleAdapter extends RecyclerView.Adapter<DetailRecycleAdapter.DetailViewHolder> {

    private ItemShowCardBinding binding;
    private Context context;
    List<ShowDTO> items;

    public DetailRecycleAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemShowCardBinding.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View view = binding.getRoot();
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return new DetailRecycleAdapter.DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        holder.onBind(items.get(position));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFriendList(List<ShowDTO> list) {
        this.items = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class DetailViewHolder extends RecyclerView.ViewHolder {
        FrameLayout card;
        ImageView img;
        TextView title;
        TextView addr;
        TextView time;
        TextView money;
        TextView heart;
        ImageView accept;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            card = binding.showCard;
            img = binding.showImg;
            title = binding.showTitle;
            addr = binding.showAddr;
            time = binding.showTime;
            money = binding.showMoney;
            heart = binding.showLikeCount;
            accept = binding.showAccept;

        }

        public void onBind(ShowDTO item) {
            if (item.getStatus().equals("END")){accept.setVisibility(View.VISIBLE);
            }

            String imageUrl = "http://192.168.0.38:8080/marketapp/resources/assets/img/product_img/";
            Glide.with(context).load(imageUrl + item.getImagename()).into(img);
            String result = item.getTitle();
            if(item.getTitle().length() > 14){
                result = item.getTitle().substring(0,13)+"...";
            }
            title.setText(result);
            addr.setText(item.getSimpleaddress());
            time.setText(TIME_MAXIMUM.formatTimeString(item.getPostdate()));
            money.setText(CHANGE_UTIL.intToWon(item.getUpper_price()));
            heart.setText(item.getLikes()+"");
            card.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putString("boardNo", item.getAuction_no());
                KakaoMapViewFragment kakaoMapViewFragment = new KakaoMapViewFragment();
                kakaoMapViewFragment.setArguments(bundle);
                ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .add(R.id.detail_fragment, kakaoMapViewFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }
    }
}
