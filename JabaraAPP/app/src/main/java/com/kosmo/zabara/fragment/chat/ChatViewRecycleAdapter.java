package com.kosmo.zabara.fragment.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.kosmo.zabara.api.dto.ChatDTO;
import com.kosmo.zabara.api.service.UserService;
import com.kosmo.zabara.databinding.ItemChatLeftLayoutBinding;
import com.kosmo.zabara.databinding.ItemChatLeftNoLayoutBinding;
import com.kosmo.zabara.databinding.ItemChatRightLayoutBinding;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ChatViewRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    public List<ChatDTO> items;
    private ItemChatRightLayoutBinding rightBinding;
    private ItemChatLeftLayoutBinding leftBinding;
    private ItemChatLeftNoLayoutBinding imgBinding;
    public static final int LEFT_CONTENT = 0;
    public static final int RIGHT_CONTENT = 1;
    public static final int LEFT_CONTENT_IMG = 3;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("a hh:mm");
    String profileUrl = "http://192.168.0.38:8080/marketapp/resources/assets/img/zabaraImg/";
    String imageUrl = "http://192.168.0.38:8080/marketapp/resources/assets/img/chat_img/";
    private RequestManager glide;
    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create())
            .baseUrl("http://192.168.0.38:9090/market/").build();
    UserService service = retrofit.create(UserService.class);
    ViewGroup parent;

    public ChatViewRecycleAdapter(Context context, RequestManager glide) {
        this.context = context;
        this.glide = glide;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (viewType == LEFT_CONTENT) {
            leftBinding = ItemChatLeftLayoutBinding.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            View leftView = leftBinding.getRoot();
            leftView.setLayoutParams(params);
            return new LeftViewHolder(leftView);
        } else if (viewType == LEFT_CONTENT_IMG) {
            imgBinding = ItemChatLeftNoLayoutBinding.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            View imgView = imgBinding.getRoot();
            imgView.setLayoutParams(params);
            return new NoImgViewHolder(imgView);
        } else {
            rightBinding = ItemChatRightLayoutBinding.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            View rightView = rightBinding.getRoot();
            rightView.setLayoutParams(params);
            return new RightViewHolder(rightView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int i = viewHolder.getAbsoluteAdapterPosition();
        Log.i("위치", i + "번째");
        if (viewHolder instanceof RightViewHolder)
            ((RightViewHolder) viewHolder).onBind(items.get(i));
        else if (viewHolder instanceof NoImgViewHolder)
            ((NoImgViewHolder) viewHolder).onBind(items.get(i));
        else
            ((LeftViewHolder) viewHolder).onBind(items.get(i));

    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItem(ChatDTO item) {
        items.add(item);
        notifyItemInserted(items.size());
    }

    public void refreshItem() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFriendList(List<ChatDTO> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    public class RightViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView time;
        TextView unread;
        ImageView msgImg;
        CardView rightGone;
        CardView callView;
        TextView nickname;
        TextView phonenumber;
        ImageView profileImge;

        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            content = rightBinding.msgContent;
            time = rightBinding.msgTime;
            unread = rightBinding.msgUnread;
            msgImg = rightBinding.msgImg;
            rightGone = rightBinding.rightGone;
            callView = rightBinding.callView;
            nickname = rightBinding.callNickname;
            phonenumber = rightBinding.callPhoneNumber;
            profileImge = rightBinding.callProfileImg;
        }

        void onBind(ChatDTO item) {
            content.setText(item.getChatcontent());
            time.setText(dateFormat.format(item.getSendtime()));
            if (item.getUnread_count() != 1)
                unread.setVisibility(View.GONE);
            else
                unread.setVisibility(View.VISIBLE);
            if (item.getImg() != null && item.getChatcontent().equals("사진")) {
                glide.load(imageUrl + item.getImg()).into(msgImg);
                rightGone.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
            } else {
                glide.clear(msgImg);
                rightGone.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
            }
            if (item.getUri() != null) {
                if (item.getChatcontent().equals("바로출력")) {
                    msgImg.setImageURI(item.getUri());
                    rightGone.setVisibility(View.VISIBLE);
                    content.setVisibility(View.GONE);
                } else {
                    rightGone.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                }
            }
            if (item.getChatcontent().equals("::명함::")) {
                glide.load(profileUrl + item.getProfile_img()).into(profileImge);
                nickname.setText(item.getNickname());
                phonenumber.setText(item.getPhonenumber());
                content.setVisibility(View.GONE);
                callView.setVisibility(View.VISIBLE);
            } else {
                glide.clear(profileImge);
                content.setVisibility(View.VISIBLE);
                callView.setVisibility(View.GONE);
            }
        }

    }


    public class NoImgViewHolder extends RecyclerView.ViewHolder {
        TextView no_content;
        TextView no_time;
        ImageView no_img;
        CardView no_img_gone;
        CardView callView;
        TextView nickname;
        TextView phonenumber;
        ImageView profileImge;
        ImageView plusbtn;
        AppCompatButton balanceBtn;

        public NoImgViewHolder(@NonNull View itemView) {
            super(itemView);
            no_content = imgBinding.msgNoContent;
            no_time = imgBinding.msgNoTime;
            no_img = imgBinding.imgImage;
            no_img_gone = imgBinding.imgGone;
            callView = imgBinding.callView;
            nickname = imgBinding.callNickname;
            phonenumber = imgBinding.callPhoneNumber;
            profileImge = imgBinding.callProfileImg;
            plusbtn = imgBinding.chatBtnPlus;
            balanceBtn = imgBinding.balanceBtn;
        }

        void onBind(ChatDTO item) {
            no_content.setText(item.getChatcontent());
            if (item.getChatcontent().contains("원이 송금 되었습니다.")) {
                balanceBtn.setVisibility(View.VISIBLE);
            } else {
                balanceBtn.setVisibility(View.GONE);
            }
            no_time.setText(dateFormat.format(item.getSendtime()));
            if (item.getImg() != null && item.getChatcontent().equals("사진")) {
                glide.load(imageUrl + item.getImg()).into(no_img);
                no_img_gone.setVisibility(View.VISIBLE);
                no_content.setVisibility(View.GONE);
            } else {
                glide.clear(no_img);
                no_img_gone.setVisibility(View.GONE);
                no_content.setVisibility(View.VISIBLE);
            }
            if (item.getChatcontent().equals("::명함::")) {
                glide.load(profileUrl + item.getProfile_img()).into(profileImge);
                nickname.setText(item.getNickname());
                phonenumber.setText(item.getPhonenumber());
                no_content.setVisibility(View.GONE);
                callView.setVisibility(View.VISIBLE);
            } else {
                glide.clear(profileImge);
                no_content.setVisibility(View.VISIBLE);
                callView.setVisibility(View.GONE);
            }
            plusbtn.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + item.getPhonenumber()));
                context.startActivity(intent);
            });
            balanceBtn.setOnClickListener(view -> {
                SharedPreferences preferences = context.getSharedPreferences("usersInfo", Context.MODE_PRIVATE);
                String email = preferences.getString("email", "");
                Map map = new HashMap();
                map.put("email", email);
                String balanceStr = no_content.getText().toString();
                map.put("balance", balanceStr.substring(0, balanceStr.indexOf("원")));
                Call<Map> call = service.deposit(map);
                call.enqueue(new Callback<Map>() {
                    @Override
                    public void onResponse(Call<Map> call, Response<Map> response) {
                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(parent.getContext());
                        final Intent intent = new Intent(ChatViewFragment.LOCAL_BROADCAST);
                        intent.putExtra("str", balanceStr.substring(0, balanceStr.indexOf("원")) + "원이 입급되었습니다.");   //Notify fragment to call the queryCity() method
                        localBroadcastManager.sendBroadcast(intent);
                        no_content.setText("송금이 완료되었습니다.");
                        balanceBtn.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<Map> call, Throwable t) {

                    }
                });
            });
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder {

        ImageView user_profile;
        TextView user_id;
        TextView user_content;
        TextView user_time;
        ImageView user_img;
        CardView leftGone;
        CardView callView;
        TextView nickname;
        TextView phonenumber;
        ImageView profileImge;
        ImageView plusbtn;
        AppCompatButton balanceBtn;

        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            user_profile = leftBinding.msgUserProfile;
            user_id = leftBinding.msgUserId;
            user_content = leftBinding.msgUserContent;
            user_time = leftBinding.msgUserTime;
            user_img = leftBinding.userImg;
            leftGone = leftBinding.leftGone;
            callView = leftBinding.callView;
            nickname = leftBinding.callNickname;
            phonenumber = leftBinding.callPhoneNumber;
            profileImge = leftBinding.callProfileImg;
            plusbtn = leftBinding.chatBtnPlus;
            balanceBtn = leftBinding.balanceBtn;
        }

        void onBind(ChatDTO item) {
            glide.load(profileUrl + item.getProfile_img()).into(user_profile);
            user_id.setText(item.getNickname());
            user_content.setText(item.getChatcontent());
            if (item.getChatcontent().contains("원이 송금 되었습니다.")) {
                balanceBtn.setVisibility(View.VISIBLE);
            } else {
                balanceBtn.setVisibility(View.GONE);
            }
            user_time.setText(dateFormat.format(item.getSendtime()));
            if (item.getImg() != null && item.getChatcontent().equals("사진")) {
                glide.load(imageUrl + item.getImg()).into(user_img);
                leftGone.setVisibility(View.VISIBLE);
                user_content.setVisibility(View.GONE);
            } else {
                glide.clear(user_img);
                leftGone.setVisibility(View.GONE);
                user_content.setVisibility(View.VISIBLE);
            }
            if (item.getChatcontent().equals("::명함::")) {
                glide.load(profileUrl + item.getProfile_img()).into(profileImge);
                nickname.setText(item.getNickname());
                phonenumber.setText(item.getPhonenumber());
                user_content.setVisibility(View.GONE);
                callView.setVisibility(View.VISIBLE);
            } else {
                glide.clear(profileImge);
                user_content.setVisibility(View.VISIBLE);
                callView.setVisibility(View.GONE);
            }
            plusbtn.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + item.getPhonenumber()));
                context.startActivity(intent);
            });
            balanceBtn.setOnClickListener(view -> {
                SharedPreferences preferences = context.getSharedPreferences("usersInfo", Context.MODE_PRIVATE);
                String email = preferences.getString("email", "");
                Map map = new HashMap();
                map.put("email", email);
                map.put("chatcontent", "송금이 완료되었습니다.");
                map.put("room_no", item.getRoom_no());
                map.put("rownum", getAbsoluteAdapterPosition());
                String balanceStr = user_content.getText().toString();
                map.put("balance", balanceStr.substring(0, balanceStr.indexOf("원")));
                Call<Map> call = service.deposit(map);
                call.enqueue(new Callback<Map>() {
                    @Override
                    public void onResponse(Call<Map> call, Response<Map> response) {
                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(parent.getContext());
                        final Intent intent = new Intent(ChatViewFragment.LOCAL_BROADCAST);
                        intent.putExtra("str", balanceStr.substring(0, balanceStr.indexOf("원")) + "원을 받으셨습니다.");   //Notify fragment to call the queryCity() method
                        localBroadcastManager.sendBroadcast(intent);
                        user_content.setText("송금이 완료되었습니다.");
                        balanceBtn.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<Map> call, Throwable t) {

                    }
                });
            });
        }
    }

}
