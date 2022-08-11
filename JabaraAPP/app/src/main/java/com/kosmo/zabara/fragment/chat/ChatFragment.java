package com.kosmo.zabara.fragment.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosmo.zabara.R;
import com.kosmo.zabara.api.dto.ChatDTO;
import com.kosmo.zabara.api.dto.ChatRoomDTO;
import com.kosmo.zabara.api.service.ChatService;
import com.kosmo.zabara.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ChatRecycleAdapter adapter;
    SharedPreferences preferences;
    private Context contextNullSafe;
    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create())
            .baseUrl("http://192.168.0.38:9090/market/").build();
    ChatService chatService = retrofit.create(ChatService.class);
    List<ChatRoomDTO> items;
    String email;

    private static ChatFragment instance = new ChatFragment();

    private ChatFragment() {
    }

    public static ChatFragment getInstance() {
        return instance;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        if (contextNullSafe == null) getContextNullSafety();

        preferences = contextNullSafe.getSharedPreferences("usersInfo", Context.MODE_PRIVATE);
        email = preferences.getString("email", "");

        RecyclerView recyclerView = binding.recyclerView;
        adapter = new ChatRecycleAdapter(contextNullSafe, getChildFragmentManager());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(contextNullSafe, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(contextNullSafe, DividerItemDecoration.VERTICAL));
        if (getActivity().findViewById(R.id.bottomNavigationView).getVisibility() == View.GONE)
            getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
        items = new ArrayList<>();
        initRetrofit();
        adapter.setFriendList(items);
        binding.contactSwipeRefresh.setOnRefreshListener(() -> {
            adapter.notifyDataSetChanged();
            /* 업데이트가 끝났음을 알림 */
            binding.contactSwipeRefresh.setRefreshing(false);
        });
        return binding.getRoot();
    }

    private void initRetrofit() {
        Map map = new HashMap();
        map.put("email", email);
        Call<Map> call = chatService.selectList(map);
        call.enqueue(new Callback<Map>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                Map map = response.body();
                ObjectMapper mapper = new ObjectMapper();
                List<ChatDTO> chatlist = mapper.convertValue(map.get("chatlist"), new TypeReference<List<ChatDTO>>() {
                });
                List<ChatDTO> unreadcount = mapper.convertValue(map.get("unreadcount"), new TypeReference<List<ChatDTO>>() {
                });
                Map<Integer, Integer> unread = new HashMap();
                for (ChatDTO dto : unreadcount) {
                    unread.put(dto.getRoom_no(), dto.getUnreadcount());
                }

                for (ChatDTO chatDTO : chatlist) {
                    items.add(new ChatRoomDTO(
                            map.get("profileimage").equals(chatDTO.getSenduserprofileimg()) ? chatDTO.getWriteuserprofileimg() : chatDTO.getSenduserprofileimg(),
                            map.get("userNickname").equals(chatDTO.getSendusernickname()) ? chatDTO.getWriteusernickname() : chatDTO.getSendusernickname(),
                            chatDTO.getChatcontent(),
                            chatDTO.getSendtime(),
                            unread.get(chatDTO.getRoom_no()) == null ? 0 : unread.get(chatDTO.getRoom_no()),
                            chatDTO.getRoom_no(),
                            chatDTO.getAuction_no(),
                            email,
                            chatDTO.getUserno(),
                            chatDTO.getWriteuserno()
                    ));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {

            }
        });

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