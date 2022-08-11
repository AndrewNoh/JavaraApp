package com.kosmo.zabara.fragment.chat;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosmo.zabara.R;
import com.kosmo.zabara.api.dto.ChatDTO;
import com.kosmo.zabara.api.dto.ShowDTO;
import com.kosmo.zabara.api.dto.UserDTO;
import com.kosmo.zabara.api.service.ChatService;
import com.kosmo.zabara.api.service.UserService;
import com.kosmo.zabara.api.util.CHANGE_UTIL;
import com.kosmo.zabara.databinding.FragmentChatViewBinding;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import gun0912.tedbottompicker.TedBottomPicker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class ChatViewFragment extends Fragment {

    private FragmentChatViewBinding binding;
    private Bundle bundle;
    private Context context;
    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create())
            .baseUrl("http://192.168.0.38:9090/market/").build();
    private ChatService service = retrofit.create(ChatService.class);
    private UserService userService = retrofit.create(UserService.class);
    private ObjectMapper mapper = new ObjectMapper();
    InputMethodManager inputMethodManager;
    RecyclerView recyclerView;
    ChatViewRecycleAdapter adapter;
    private WebSocketClient mWebSocketClient;
    String email, otherNickname, profile, phonenumber, result;

    int auction_no, room_no, writeuserno, senduserno;
    int unread = 1;
    UserDTO myInfomation;

    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    public static final String LOCAL_BROADCAST = "com.xfhy.casualweather.LOCAL_BROADCAST";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        bundle = this.getArguments();
        binding = FragmentChatViewBinding.inflate(inflater, container, false);
        getBundle();
        initRetrofit();
        bottomFunction();
        createWebSocketClient();
        connectBroadeCast();
        binding.chatBtnSend.setOnClickListener(view -> {
            if (binding.chatCommentWrite.getText() != null) {
                String content = binding.chatCommentWrite.getText().toString();
                insertRightContent(content);
                mWebSocketClient.send("서버로부터받은 메시지" + room_no + ":" + binding.chatCommentWrite.getText().toString());
                sendStrMSG(content);
                binding.chatCommentWrite.setText("");
            }
        });
        binding.bottomGallery.setOnClickListener(view -> {
            TedBottomPicker.with((FragmentActivity) context)
                    .show(this::sendImgMSG);
        });
        binding.bottomCall.setOnClickListener(view -> {
            ChatDTO sendMSGDTO = new ChatDTO();
            String content = "::명함::";
            sendMSGDTO.setChatcontent(content);
            sendMSGDTO.setViewType(ChatViewRecycleAdapter.RIGHT_CONTENT);
            sendMSGDTO.setSendtime(new Timestamp(System.currentTimeMillis()));
            sendMSGDTO.setUnread_count(unread);
            sendMSGDTO.setPhonenumber(myInfomation.getPhonenumber());
            sendMSGDTO.setNickname(myInfomation.getNickname());
            sendMSGDTO.setProfile_img(myInfomation.getProfile_img());
            adapter.addItem(sendMSGDTO);
            recyclerView.scrollToPosition(adapter.items.size() - 1);
            mWebSocketClient.send("서버로부터받은 전화번호공유 메시지" + room_no + ":전화번호 공유를 요청하였습니다.");
            sendStrMSG(content);
            binding.chatBottomsheet.setVisibility(View.GONE);
            binding.chatBtnPlus.setVisibility(View.VISIBLE);
            binding.chatBtnCancle.setVisibility(View.GONE);

        });
        binding.chatCall.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phonenumber));
            getActivity().startActivity(intent);
        });
        binding.bottomCard.setOnClickListener(view -> {
            connectAccount();
        });
        return binding.getRoot();
    }

    private void connectBroadeCast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localReceiver = new LocalReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(LOCAL_BROADCAST);   //Add action
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    private void connectAccount() {
        Map map = new HashMap();
        map.put("userno", myInfomation.getUserno());
        Call<Map> getbalance = userService.getbalance(map);
        getbalance.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                Map map = response.body();
                String balance = (String) map.get("balance");
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.item_newcustom2_layout);
                TextView dialogText = dialog.findViewById(R.id.dialog_title2);
                TextView balanceText = dialog.findViewById(R.id.balance_text);
                balanceText.setVisibility(View.VISIBLE);
                balanceText.setText("잔액 : " + CHANGE_UTIL.intToWon(balance));
                dialogText.setText("송금하기");
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
                        int Wonbalance = CHANGE_UTIL.commaToInt(editText.getText().toString());
                        if (Integer.parseInt(balance) < Wonbalance) {
                            Toast.makeText(context, "잔액이 부족합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Map okMap = new HashMap();
                            okMap.put("userno", myInfomation.getUserno());
                            okMap.put("withdraw", Wonbalance);
                            Call<Map> withraw = userService.remittance(okMap);
                            withraw.enqueue(new Callback<Map>() {
                                @Override
                                public void onResponse(Call<Map> call, Response<Map> response) {
                                    String str = Wonbalance + "원이 송금 되었습니다.";
                                    sendStrMSG(str);
                                    insertRightContent(str);
                                    mWebSocketClient.send("서버로부터받은 송금 메시지" + room_no + ":" + Wonbalance + "원을 보내셨습니다.");
                                }

                                @Override
                                public void onFailure(Call<Map> call, Throwable t) {
                                }
                            });
                        }
                    } else {
                        Toast.makeText(context, "금액을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                    dialog.cancel();
                });
                FrameLayout mDialogNo = dialog.findViewById(R.id.frmNo2);
                mDialogNo.setOnClickListener(v12 -> {
                    Toast.makeText(context, "송금이 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
                dialog.show();
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {

            }
        });


    }

    private void sendImgMSG(Uri uri) {
        File file = new File(uri.getPath());
        Retrofit retrofit2 = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl("http://192.168.0.38:8080/marketapp/").build();
        ChatService service2 = retrofit2.create(ChatService.class);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        MultipartBody.Part chatimg = MultipartBody.Part.createFormData("chatimg", file.getName(), requestFile);
        RequestBody roomno = RequestBody.create(String.valueOf(room_no), MediaType.parse("text/plain"));
        RequestBody senduserno = RequestBody.create(String.valueOf(myInfomation.getUserno()), MediaType.parse("text/plain"));
        RequestBody unread_count = RequestBody.create(String.valueOf(unread), MediaType.parse("text/plain"));
        RequestBody chatcontent = RequestBody.create("사진", MediaType.parse("text/plain"));
        Call<Map> call = service2.insertimgfromapp(chatimg, roomno, senduserno, unread_count, chatcontent);
        call.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                ChatDTO sendMSGDTO = new ChatDTO();
                sendMSGDTO.setChatcontent("바로출력");
                sendMSGDTO.setUri(uri);
                sendMSGDTO.setUnread_count(unread);
                sendMSGDTO.setViewType(ChatViewRecycleAdapter.RIGHT_CONTENT);
                sendMSGDTO.setSendtime(new Timestamp(System.currentTimeMillis()));
                adapter.addItem(sendMSGDTO);
                recyclerView.scrollToPosition(adapter.items.size() - 1);
                mWebSocketClient.send("서버로부터받은 img 메시지" + room_no + ":" + file.getName());
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {
            }
        });
    }

    public void insertRightContent(String str) {
        ChatDTO sendMSGDTO = new ChatDTO();
        sendMSGDTO.setChatcontent(str);
        sendMSGDTO.setViewType(ChatViewRecycleAdapter.RIGHT_CONTENT);
        sendMSGDTO.setSendtime(new Timestamp(System.currentTimeMillis()));
        sendMSGDTO.setUnread_count(unread);
        adapter.addItem(sendMSGDTO);
        recyclerView.scrollToPosition(adapter.items.size() - 1);
    }

    private void insertLeftContent(String str) {
        ChatDTO sendMSGDTO = new ChatDTO();
        sendMSGDTO.setChatcontent(str);
        if (adapter.items.get(adapter.getItemCount() - 1).getViewType() == ChatViewRecycleAdapter.LEFT_CONTENT) {
            sendMSGDTO.setRoom_no(room_no);
            sendMSGDTO.setViewType(ChatViewRecycleAdapter.LEFT_CONTENT_IMG);
        } else {
            sendMSGDTO.setViewType(ChatViewRecycleAdapter.LEFT_CONTENT);
            sendMSGDTO.setProfile_img(profile);
            sendMSGDTO.setNickname(otherNickname);
        }
        sendMSGDTO.setSendtime(new Timestamp(System.currentTimeMillis()));
        adapter.addItem(sendMSGDTO);
        recyclerView.scrollToPosition(adapter.items.size() - 1);
    }

    private void sendStrMSG(String content) {
        Map map = new HashMap();
        map.put("room_no", room_no);
        map.put("senduserno", myInfomation.getUserno());
        map.put("unread_count", unread);
        map.put("chatcontent", content);
        Call<Map> call = service.insertchat(map);
        call.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {
            }
        });
    }


    private void getBundle() {
        otherNickname = bundle.getString("otherNickname");
        email = bundle.getString("email");
        room_no = bundle.getInt("room_no");
        auction_no = bundle.getInt("auction_no");
        senduserno = bundle.getInt("senduserno");
        writeuserno = bundle.getInt("writeuserno");
    }

    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI("ws://192.168.0.38:8080/marketapp/chat-ws.do/" + room_no + "/websocket");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String message) {
                getActivity().runOnUiThread(() -> {
                    if (message.startsWith("서버로부터받은 메시지")) {
                        insertLeftContent(message.substring(message.lastIndexOf(":") + 1));
                    } else if (message.startsWith("서버로부터받은 img 메시지")) {
                        ChatDTO sendMSGDTO = new ChatDTO();
                        sendMSGDTO.setChatcontent("사진");
                        sendMSGDTO.setImg(message.substring(message.lastIndexOf(":") + 1));
                        if (adapter.items.get(adapter.getItemCount() - 1).getViewType() == ChatViewRecycleAdapter.LEFT_CONTENT) {
                            sendMSGDTO.setViewType(ChatViewRecycleAdapter.LEFT_CONTENT_IMG);
                        } else {
                            sendMSGDTO.setViewType(ChatViewRecycleAdapter.LEFT_CONTENT);
                            sendMSGDTO.setProfile_img(profile);
                            sendMSGDTO.setNickname(otherNickname);
                        }
                        sendMSGDTO.setSendtime(new Timestamp(System.currentTimeMillis()));
                        adapter.addItem(sendMSGDTO);
                        recyclerView.scrollToPosition(adapter.items.size() - 1);
                    } else if (message.startsWith("서버로부터받은 전화번호공유 메시지")) {
                        ChatDTO sendMSGDTO = new ChatDTO();
                        sendMSGDTO.setChatcontent("::명함::");
                        if (adapter.items.get(adapter.getItemCount() - 1).getViewType() == ChatViewRecycleAdapter.LEFT_CONTENT) {
                            sendMSGDTO.setViewType(ChatViewRecycleAdapter.LEFT_CONTENT_IMG);
                        } else {
                            sendMSGDTO.setViewType(ChatViewRecycleAdapter.LEFT_CONTENT);
                            sendMSGDTO.setProfile_img(profile);
                            sendMSGDTO.setNickname(otherNickname);
                        }
                        sendMSGDTO.setNickname(otherNickname);
                        sendMSGDTO.setPhonenumber(phonenumber);
                        sendMSGDTO.setProfile_img(profile);
                        sendMSGDTO.setSendtime(new Timestamp(System.currentTimeMillis()));
                        adapter.addItem(sendMSGDTO);
                        recyclerView.scrollToPosition(adapter.items.size() - 1);
                    } else if (message.startsWith("서버로부터받은 송금 메시지")) {
                        String balanceMessage = message.substring(message.lastIndexOf(":") + 1).replaceAll("원을 보내셨습니다.", "원이 송금 되었습니다.");
                        insertLeftContent(balanceMessage);
                    } else if (message.startsWith("서버로부터받은 입금 메시지")) {
                        String balanceMessage = message.substring(message.lastIndexOf(":") + 1);
                        insertLeftContent(balanceMessage);
                    } else if (message.startsWith("Hello from Javara Website")) {
                        for (ChatDTO dto : adapter.items) {
                            if (dto.getUnread_count() == 1) {
                                dto.setUnread_count(0);
                            }
                        }
                        unread = 0;
                        mWebSocketClient.send("Open from " + Build.MANUFACTURER + " " + Build.MODEL);
                    } else if (message.startsWith("Close from Javara Website")) {
                        unread = 1;
                    } else if (message.startsWith("Open from Javara Website")) {
                        unread = 0;
                    }
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("Websocket", "Closed ");
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    private void bottomFunction() {
        binding.chatBtnPlus.setOnClickListener(view -> {
            if (inputMethodManager.isActive())
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            binding.chatBottomsheet.setVisibility(View.VISIBLE);
            binding.chatBtnPlus.setVisibility(View.GONE);
            binding.chatBtnCancle.setVisibility(View.VISIBLE);
        });
        binding.chatBtnCancle.setOnClickListener(view -> {
            binding.chatBottomsheet.setVisibility(View.GONE);
            binding.chatBtnPlus.setVisibility(View.VISIBLE);
            binding.chatBtnCancle.setVisibility(View.GONE);
            binding.chatCommentWrite.requestFocus();
            if (inputMethodManager.isActive())
                inputMethodManager.showSoftInput(binding.chatCommentWrite, 0);
        });
        binding.chatCommentWrite.setOnClickListener(view -> {
            if (binding.chatBottomsheet.getVisibility() == View.VISIBLE)
                binding.chatBottomsheet.setVisibility(View.GONE);
            if (binding.chatBtnCancle.getVisibility() == View.VISIBLE) {
                binding.chatBtnPlus.setVisibility(View.VISIBLE);
                binding.chatBtnCancle.setVisibility(View.GONE);
            }
        });

    }

    private void initRetrofit() {
        Map map = new HashMap();
        map.put("email", email);
        map.put("room_no", room_no);
        map.put("auction_no", auction_no);
        Call<Map> call = service.joinChat(map);
        call.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                Map map = response.body();
                List<ChatDTO> chatMsgList = mapper.convertValue(map.get("chatMsgList"), new TypeReference<List<ChatDTO>>() {
                });
                ShowDTO showDTO = mapper.convertValue(map.get("chatConnBoard"), new TypeReference<ShowDTO>() {
                });
                myInfomation = mapper.convertValue(map.get("myInfomation"), new TypeReference<UserDTO>() {
                });
                int myno = myInfomation.getUserno();
                binding.chatTitleId.setText(otherNickname);
                binding.chatTitle.setText(showDTO.getTitle());
                String imageUrl = "http://192.168.0.38:8080/marketapp/resources/assets/img/product_img/";
                Glide.with(context).load(imageUrl + showDTO.getImagename()).into(binding.chatImg);
                binding.chatMoney.setText(CHANGE_UTIL.intToWon(showDTO.getUpper_price()));

                recyclerView = binding.chatRecyclerview;
                adapter = new ChatViewRecycleAdapter(context, Glide.with(context));
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

                int temp = 0;
                for (ChatDTO dto : chatMsgList) {
                    if (myno == dto.getSenduserno()) {
                        dto.setViewType(ChatViewRecycleAdapter.RIGHT_CONTENT);
                        temp = dto.getSenduserno();
                    } else {
                        if (temp == dto.getSenduserno())
                            dto.setViewType(ChatViewRecycleAdapter.LEFT_CONTENT_IMG);
                        else
                            dto.setViewType(ChatViewRecycleAdapter.LEFT_CONTENT);
                        profile = dto.getProfile_img();
                        phonenumber = dto.getPhonenumber();
                        temp = dto.getSenduserno();
                    }
                }
                adapter.setFriendList(chatMsgList);
                Objects.requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(chatMsgList.size() - 1);
                // recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> recyclerView.scrollToPosition(chatMsgList.size() - 1));
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {

            }
        });

    }

    @Override
    public void onPause() {
        if (mWebSocketClient.isOpen()) {
            mWebSocketClient.send("Close from " + Build.MANUFACTURER + " " + Build.MODEL);
            mWebSocketClient.close();
        }
        super.onPause();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!action.equals(LOCAL_BROADCAST)) {
                return;
            }
            String str = intent.getStringExtra("str");
            insertRightContent(str);
            sendStrMSG(str);
            mWebSocketClient.send("서버로부터받은 입금 메시지" + room_no + ":" + str);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);    //Cancellation of broadcasting registration
    }

}