package com.kosmo.zabara.fragment.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.kosmo.zabara.R;
import com.kosmo.zabara.api.dto.BoardDTO;
import com.kosmo.zabara.api.dto.ImageDTO;
import com.kosmo.zabara.api.dto.MainBoardDTO;
import com.kosmo.zabara.api.dto.UserDTO;
import com.kosmo.zabara.api.service.BoardService;
import com.kosmo.zabara.api.service.UserService;
import com.kosmo.zabara.databinding.FragmentHomeBinding;
import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeRecycleAdapter adapter;
    private Context contextNullSafe;
    private FusedLocationProviderClient fusedLocationClient;    // 현재 위치를 가져오기 위한 변수
    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create())
            .baseUrl("http://192.168.0.38:9090/market/").build();
    BoardService boardService = retrofit.create(BoardService.class);
    UserService service = retrofit.create(UserService.class);
    SharedPreferences preferences;
    String email;
    List<String> imageLists;
    private static HomeFragment instance = new HomeFragment();

    private HomeFragment() {
    }

    public static HomeFragment getInstance() {
        return instance;
    }

    @SuppressLint("MissingPermission")
    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), result -> {
                if (result) {
                    getLocation();
                } else {
                    Toast.makeText(contextNullSafe, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();    // 권한요청이 거절된 경우
                }
            });

    public String[] getCityName(Double latitude, double longitude) {
        String gugun = "위치값 x";
        String fullCityList = "위치값 x";
        String[] cityNames = new String[2];
        Geocoder geocoder = new Geocoder(contextNullSafe);
        List<Address> cityList = null;
        try {
            cityList = geocoder.getFromLocation(latitude, longitude, 5);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cityList != null) {
            if (cityList.size() == 0) {
                Toast.makeText(getContext(), " 현재위치에서 검색된 주소정보가 없습니다. ", Toast.LENGTH_SHORT).show();
            } else {
                Address address = cityList.get(0);
                fullCityList = address.getAddressLine(0);
                // 수성구
                gugun = address.getSubLocality();
                cityNames = new String[]{fullCityList, gugun};
            }
        }
        return cityNames;
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        if (binding.homeToolbar.getVisibility() != View.VISIBLE) {
            binding.homeToolbar.setVisibility(View.VISIBLE);
            binding.fab.setVisibility(View.VISIBLE);
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(contextNullSafe);
        RecyclerView recyclerView = binding.recyclerView;
        adapter = new HomeRecycleAdapter(contextNullSafe);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(contextNullSafe, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        List<MainBoardDTO> items = new Vector<>();
        imageLists = new ArrayList<>();
        //아이템 입력
        preferences = contextNullSafe.getSharedPreferences("usersInfo", Context.MODE_PRIVATE);
        email = preferences.getString("email", "");
        Map map = new HashMap();
        map.put("email", email);
        Call<Map> call = boardService.selectList(map);
        final Dialog loadingDialog = new Dialog(contextNullSafe);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.item_custom_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.show();
        call.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                if (response.isSuccessful()) {
                    Map map = response.body();
                    ObjectMapper mapper = new ObjectMapper();

                    binding.btnHomeGeo.setText(String.valueOf(map.get("simpleAddress")));
                    List<UserDTO> userLists = mapper.convertValue(map.get("userLists"), new TypeReference<List<UserDTO>>() {
                    });
                    List<List<ImageDTO>> imageList = mapper.convertValue(map.get("imageList"), new TypeReference<List<List<ImageDTO>>>() {
                    });
                    List<BoardDTO> boardList = mapper.convertValue(map.get("boardList"), new TypeReference<List<BoardDTO>>() {
                    });
                    for (int i = 0; i < boardList.size(); i++) {
                        BoardDTO boardDTO = boardList.get(i);
                        List<ImageDTO> imageDTOS = imageList.get(i);
                        UserDTO userDTO = userLists.get(i);
                        for (ImageDTO dto : imageDTOS) {
                            imageLists.add(dto.getImageName());
                        }
                        items.add(new MainBoardDTO(
                                boardDTO.getLikes(),
                                boardDTO.getAuction_no(),
                                imageLists,
                                boardDTO.getTitle(),
                                boardDTO.getUpper_Price(),
                                boardDTO.getPostDate(),
                                userDTO.getProfile_img(),
                                userDTO.getNickname(),
                                boardDTO.getContent(),
                                boardDTO.getCategory()
                        ));
                        imageLists = new ArrayList<>();
                    }
                    adapter.notifyDataSetChanged();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {
                loadingDialog.dismiss();
            }
        });
        adapter.setFriendList(items);
        FloatingActionButton fab = binding.fab;
        fab.attachToRecyclerView(recyclerView);

        fab.setOnClickListener(view -> {
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home, new HomeWriteFragment())
                    .addToBackStack(null)
                    .commit();
        });

        //클릭시 위치 변경
        binding.btnHomeGeo.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(contextNullSafe);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.item_newcustom_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            TextView title = dialog.findViewById(R.id.dialog_title);
            title.setText("위치변경");
            TextView content = dialog.findViewById(R.id.dialog_content);
            content.setText("현재 위치로 위치를 변경하시겠습니까?");

            FrameLayout mDialogOk = dialog.findViewById(R.id.frmOk);
            mDialogOk.setOnClickListener(v1 -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                getLocation();
                dialog.cancel();
            });
            FrameLayout mDialogNo = dialog.findViewById(R.id.frmNo);
            mDialogNo.setOnClickListener(v12 -> dialog.dismiss());
            dialog.show();
        });

        //메뉴 버튼 클릭
        binding.materialToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.search: {
                    getChildFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.home, new SearchFragment())
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                case R.id.category: {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.home_ly, new CategoryFragment())
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                case R.id.alaram: {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.home_ly, new KeywordFragment())
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
            }
            return false;
        });

        //새로고침침
        binding.contactSwipeRefresh.setOnRefreshListener(() -> {
            adapter.notifyDataSetChanged();
            /* 업데이트가 끝났음을 알림 */
            binding.contactSwipeRefresh.setRefreshing(false);
        });
        return binding.getRoot();
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(contextNullSafe, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contextNullSafe, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(contextNullSafe, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();    // 권한요청이 거절된 경우
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // 파라미터로 받은 location을 통해 위도, 경도 정보를 텍스트뷰에 set.
                        preferences = contextNullSafe.getSharedPreferences("usersInfo", Context.MODE_PRIVATE);
                        email = preferences.getString("email", "");
                        String[] cityNames = getCityName(location.getLatitude(), location.getLongitude());
                        Map<String, String> map = new HashMap<>();
                        map.put("email", email);
                        map.put("simpleAddr", cityNames[1]);
                        map.put("nowAddress", cityNames[0]);
                        Call<Map<String, Boolean>> callAddr = service.userAddr(map);
                        callAddr.enqueue(new Callback<Map<String, Boolean>>() {
                            @Override
                            public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {
                                if (response.isSuccessful()) {
                                    Map<String, Boolean> result = response.body();
                                    boolean isUpdate = result.get("isUpdate");
                                    if (isUpdate) {
                                        binding.btnHomeGeo.setText(cityNames[1]);
                                    } else {
                                        Toast.makeText(contextNullSafe, "주소 변경 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {
                                Toast.makeText(contextNullSafe, "주소 변경 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
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