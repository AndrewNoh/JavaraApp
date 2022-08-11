package com.kosmo.zabara.fragment.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosmo.zabara.api.dto.ShowDTO;
import com.kosmo.zabara.api.service.BoardService;
import com.kosmo.zabara.databinding.FragmentDetailBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class DetailFragment extends Fragment {
    private Context context;
    private FragmentDetailBinding binding;
    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create())
            .baseUrl("http://192.168.0.38:9090/market/").build();
    BoardService service = retrofit.create(BoardService.class);
    ObjectMapper mapper = new ObjectMapper();
    SharedPreferences preferences;
    String email;
    Bundle bundle;
    String choseMenu;
    String category = null;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        bundle = this.getArguments();
        if (bundle != null)
            choseMenu = bundle.getString("choseMenu");
        switch (choseMenu) {
            case "sell":
                binding.insertText.setText("판매내역");
                break;
            case "like":
                binding.insertText.setText("관심목록");
                break;
            case"category":
                binding.insertText.setText("카테고리 ("+bundle.getString("category")+")");
                category = bundle.getString("category");
                break;
            default:
                binding.insertText.setText("입찰내역");
        }
        initRetrofit();
        requireActivity().setActionBar(binding.detailToolbar);
        requireActivity().getActionBar().setDisplayHomeAsUpEnabled(true);// show back button
        requireActivity().getActionBar().setDisplayShowTitleEnabled(false);
        binding.detailToolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        return binding.getRoot();
    }

    private void initRetrofit() {
        preferences = context.getSharedPreferences("usersInfo", Context.MODE_PRIVATE);
        email = preferences.getString("email", "");
        Map map = new HashMap();
        map.put("email", email);
        map.put("choseMenu", choseMenu);
        if (category!=null) {
            map.put("category",category);
        }
        Call<Map> call = service.choseMenu(map);
        call.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                Map map = response.body();
                List<ShowDTO> itemList = mapper.convertValue(map.get("showDTO"), new TypeReference<List<ShowDTO>>() {
                });
                RecyclerView recyclerView = binding.detailRecyclerView;
                DetailRecycleAdapter adapter = new DetailRecycleAdapter(context);
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                adapter.setFriendList(itemList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {

            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}