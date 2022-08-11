package com.kosmo.zabara.fragment.chat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kosmo.zabara.R;
import com.kosmo.zabara.api.dto.ChatRoomDTO;
import com.kosmo.zabara.databinding.ItemChatLayoutBinding;

import java.text.SimpleDateFormat;
import java.util.List;

public class ChatRecycleAdapter extends RecyclerView.Adapter<ChatRecycleAdapter.ChatViewHolder> {
    private Context context;
    private ItemChatLayoutBinding binding;
    private List<ChatRoomDTO> items;
    private int chatNo = -1;
    FragmentManager childFragmentManager;

    public ChatRecycleAdapter(Context context, FragmentManager childFragmentManager) {
        this.context = context;
        this.childFragmentManager = childFragmentManager;
    }

    @NonNull
    @Override
    public ChatRecycleAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemChatLayoutBinding.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View view = binding.getRoot();
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return new ChatRecycleAdapter.ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecycleAdapter.ChatViewHolder holder, int position) {
        holder.onBind(items.get(position));
    }

    public void deleteItem(int position) {
        this.items.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFriendList(List<ChatRoomDTO> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView chatProfile;
        TextView chatId;
        TextView chatContent;
        TextView sendTime;
        TextView isRead;
        TextView roomno;
        Bundle bundle;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatProfile = binding.chatProfile;
            chatId = binding.chatId;
            chatContent = binding.chatContent;
            sendTime = binding.sendTime;
            isRead = binding.isread;
            roomno = binding.roomno;

            itemView.setOnLongClickListener(view -> {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.item_newcustom_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                TextView title = dialog.findViewById(R.id.dialog_title);
                title.setText(chatId.getText());
                TextView content = dialog.findViewById(R.id.dialog_content);
                content.setText("대화방 나가기");

                FrameLayout mDialogOk = dialog.findViewById(R.id.frmOk);
                mDialogOk.setOnClickListener(v1 -> {
                    deleteItem(getAbsoluteAdapterPosition());
                    dialog.cancel();
                });
                FrameLayout mDialogNo = dialog.findViewById(R.id.frmNo);
                mDialogNo.setOnClickListener(v12 -> dialog.dismiss());
                dialog.show();
                return true;
            });
        }

        void onBind(ChatRoomDTO item) {
            String profileUrl = "http://192.168.0.38:8080/marketapp/resources/assets/img/zabaraImg/";
            Glide.with(context).load(profileUrl + item.getProfileImg()).into(chatProfile);
            chatId.setText(item.getUserNickname());
            if (item.getLastContent().length() > 20) {
                chatContent.setText(item.getLastContent().substring(0, 16) + "...");
            } else {
                chatContent.setText(item.getLastContent());
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("a hh:mm");
            sendTime.setText(dateFormat.format(item.getSendTime()));
            if (item.getIsRead() != 0) {
                isRead.setVisibility(View.VISIBLE);
                isRead.setText(String.valueOf(item.getIsRead()));
            }
            roomno.setText(String.valueOf(item.getRoomNo()));

            bundle = new Bundle();
            bundle.putString("email", item.getEmail());
            bundle.putInt("room_no", item.getRoomNo());
            bundle.putInt("auction_no", item.getAuction_no());
            bundle.putInt("writeuserno", item.getWriteuserno());
            bundle.putInt("senduserno",item.getSenduserno());
            bundle.putString("otherNickname", item.getUserNickname());
            ChatViewFragment chatViewFragment = new ChatViewFragment();
            chatViewFragment.setArguments(bundle);
            itemView.setOnClickListener(view -> {
                ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.home_ly, chatViewFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }
    }
}
