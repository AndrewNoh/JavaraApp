package com.kosmo.zabara.fragment.map;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kosmo.zabara.R;
import com.kosmo.zabara.api.dto.BoardDTO;
import com.kosmo.zabara.api.dto.CategoryDTO;
import com.kosmo.zabara.api.dto.ImageDTO;
import com.kosmo.zabara.api.dto.UserDTO;
import com.kosmo.zabara.api.service.BoardService;
import com.kosmo.zabara.api.util.CHANGE_UTIL;
import com.kosmo.zabara.api.util.TIME_MAXIMUM;
import com.kosmo.zabara.databinding.FragmentKakaoBottomSheet2Binding;
import com.kosmo.zabara.databinding.FragmentKakaoBottomSheetBinding;
import com.kosmo.zabara.databinding.FragmentKakaoMapBinding;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class KakaoMapFragment extends Fragment implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener, MapView.POIItemEventListener {
    private FragmentKakaoMapBinding binding;
    private FragmentKakaoBottomSheetBinding bottomBinding;
    private FragmentKakaoBottomSheet2Binding bottomBinding2;
    private Context contextNullSafe;
    private MapView mapView;
    List<CategoryDTO> list;
    List<UserDTO> userLists;
    List<BoardDTO> boardList;
    List<List<ImageDTO>> imageList;
    String boardNo = null;

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create())
            .baseUrl("http://192.168.0.38:9090/market/").build();
    BoardService boardService = retrofit.create(BoardService.class);
    SharedPreferences preferences;
    String email;
    List<String> imageLists;
    ArrayList<MapPOIItem> markerArr;
    int btn_status = 0;

    private static KakaoMapFragment instance = new KakaoMapFragment();

    private KakaoMapFragment() {
    }

    public static KakaoMapFragment getInstance() {
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKakaoMapBinding.inflate(inflater, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        //맵뷰 init
        initView();
        initRestApi();

        //현재 위치
        binding.kakaoLocationBtn.setOnClickListener(v -> {
            if (btn_status == 0) {
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                binding.kakaoLocationImg.setImageResource(R.drawable.tracking_heading);
                btn_status++;
            } else if (btn_status == 1) {
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                binding.kakaoLocationImg.setImageResource(R.drawable.compass);
                btn_status++;
            } else {
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
                binding.kakaoLocationImg.setImageResource(R.drawable.tracking_on);
                btn_status = 0;
            }
        });

        //바텀시트
        bottomBinding2 = FragmentKakaoBottomSheet2Binding.inflate(inflater, container, false);
        bottomBinding = FragmentKakaoBottomSheetBinding.inflate(inflater, container, false);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(contextNullSafe);
        bottomSheetDialog.setContentView(bottomBinding.getRoot());
        binding.kakaoLayerBtn.setOnClickListener(v -> {
            bottomSheetDialog.show();
        });
        bottomBinding.mapBtn.setOnClickListener(v -> {
            mapView.setMapType(MapView.MapType.Standard);
            bottomSheetDialog.dismiss();
        });
        bottomBinding.satelliteBtn.setOnClickListener(v -> {
            mapView.setMapType(MapView.MapType.Satellite);
            bottomSheetDialog.dismiss();
        });
        bottomBinding.hybridBtn.setOnClickListener(v -> {
            mapView.setMapType(MapView.MapType.Hybrid);
            bottomSheetDialog.dismiss();
        });
        addCategorySpinner();
        bottomBinding.spinner.setAdapter(new CategoryAdapter(contextNullSafe, list));
        bottomBinding.spinner.setPrompt("Select your favorite Planet!");
        bottomBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                bottomSheetDialog.dismiss();
                CategoryDTO categoryDTO = (CategoryDTO) adapterView.getItemAtPosition(position);
                mapView.removeAllPOIItems();
                if (position != 0) {
                    markerArr = new ArrayList<>();
                    for (int i = 0; i < boardList.size(); i++) {
                        BoardDTO boardDTO = boardList.get(i);
                        if (categoryDTO.getName().equals(boardDTO.getCategory()) || position == 1) {
                            MapPOIItem marker = new MapPOIItem();
                            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(boardDTO.getLatitude(), boardDTO.getLongitude()));
                            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                            switch (boardDTO.getCategory()) {
                                case "인기매물":
                                    marker.setCustomImageResourceId(R.drawable.category_star);
                                    break;
                                case "중고차":
                                    marker.setCustomImageResourceId(R.drawable.category_car);
                                    break;
                                case "디지털기기":
                                    marker.setCustomImageResourceId(R.drawable.category_digital);
                                    break;
                                case "가구/인테리어":
                                    marker.setCustomImageResourceId(R.drawable.category_furniture);
                                    break;
                                case "유아동":
                                    marker.setCustomImageResourceId(R.drawable.category_baby);
                                    break;
                                case "유아도서":
                                    marker.setCustomImageResourceId(R.drawable.category_baby_book);
                                    break;
                                case "생활가전":
                                    marker.setCustomImageResourceId(R.drawable.category_life);
                                    break;
                                case "생활/가공식품":
                                    marker.setCustomImageResourceId(R.drawable.category_life_style);
                                    break;
                                case "스포츠/레저":
                                    marker.setCustomImageResourceId(R.drawable.category_sports);
                                    break;
                                case "여성잡화":
                                    marker.setCustomImageResourceId(R.drawable.category_bag);
                                    break;
                                case "여성의류":
                                    marker.setCustomImageResourceId(R.drawable.category_women);
                                    break;
                                case "남성패션/잡화":
                                    marker.setCustomImageResourceId(R.drawable.category_man);
                                    break;
                                case "게임/취미":
                                    marker.setCustomImageResourceId(R.drawable.category_hobby);
                                    break;
                                case "뷰티/미용":
                                    marker.setCustomImageResourceId(R.drawable.category_beatuty);
                                    break;
                                case "반려동물용품":
                                    marker.setCustomImageResourceId(R.drawable.category_pet);
                                    break;
                                case "도서/티켓/음반":
                                    marker.setCustomImageResourceId(R.drawable.category_book);
                                    break;
                                case "식물":
                                    marker.setCustomImageResourceId(R.drawable.category_plant);
                                    break;
                                default:
                                    marker.setCustomImageResourceId(R.drawable.category_etc);
                            }
                            marker.setItemName(boardDTO.getTitle());
                            markerArr.add(marker);
                        }
                    }
                    mapView.addPOIItems(markerArr.toArray(new MapPOIItem[markerArr.size()]));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return binding.getRoot();
    }

    private void addCategorySpinner() {
        list = new ArrayList<>();
        CategoryDTO initCategory = new CategoryDTO();
        initCategory.setName("카테고리를 선택하세요");
        initCategory.setImage(R.drawable.category_list);
        list.add(initCategory);
        CategoryDTO category_all = new CategoryDTO();
        category_all.setName("전체보기");
        category_all.setImage(R.drawable.category_all);
        list.add(category_all);
        CategoryDTO category_star = new CategoryDTO();
        category_star.setName("인기매물");
        category_star.setImage(R.drawable.category_star);
        list.add(category_star);
        CategoryDTO category_car = new CategoryDTO();
        category_car.setName("중고차");
        category_car.setImage(R.drawable.category_car);
        list.add(category_car);
        CategoryDTO category_digital = new CategoryDTO();
        category_digital.setName("디지털기기");
        category_digital.setImage(R.drawable.category_digital);
        list.add(category_digital);
        CategoryDTO category_life = new CategoryDTO();
        category_life.setName("생활가전");
        category_life.setImage(R.drawable.category_life);
        list.add(category_life);
        CategoryDTO category_furniture = new CategoryDTO();
        category_furniture.setName("가구/인테리어");
        category_furniture.setImage(R.drawable.category_furniture);
        list.add(category_furniture);
        CategoryDTO category_baby = new CategoryDTO();
        category_baby.setName("유아통");
        category_baby.setImage(R.drawable.category_baby);
        list.add(category_baby);
        CategoryDTO category_baby_book = new CategoryDTO();
        category_baby_book.setName("유아도서");
        category_baby_book.setImage(R.drawable.category_baby_book);
        list.add(category_baby_book);
        CategoryDTO category_life_style = new CategoryDTO();
        category_life_style.setName("생활/가공식품");
        category_life_style.setImage(R.drawable.category_life_style);
        list.add(category_life_style);
        CategoryDTO category_sports = new CategoryDTO();
        category_sports.setName("스포츠/레저");
        category_sports.setImage(R.drawable.category_sports);
        list.add(category_sports);
        CategoryDTO category_bag = new CategoryDTO();
        category_bag.setName("여성잡화");
        category_bag.setImage(R.drawable.category_bag);
        list.add(category_bag);
        CategoryDTO category_women = new CategoryDTO();
        category_women.setName("여성의류");
        category_women.setImage(R.drawable.category_women);
        list.add(category_women);
        CategoryDTO category_man = new CategoryDTO();
        category_man.setName("남성패션/잡화");
        category_man.setImage(R.drawable.category_man);
        list.add(category_man);
        CategoryDTO category_hobby = new CategoryDTO();
        category_hobby.setName("게임/취미");
        category_hobby.setImage(R.drawable.category_hobby);
        list.add(category_hobby);
        CategoryDTO category_beatuty = new CategoryDTO();
        category_beatuty.setName("뷰티/미용");
        category_beatuty.setImage(R.drawable.category_beatuty);
        list.add(category_beatuty);
        CategoryDTO category_pet = new CategoryDTO();
        category_pet.setName("반려동물용품");
        category_pet.setImage(R.drawable.category_pet);
        list.add(category_pet);
        CategoryDTO category_book = new CategoryDTO();
        category_book.setName("도서/티켓/음반");
        category_book.setImage(R.drawable.category_book);
        list.add(category_book);
        CategoryDTO category_plant = new CategoryDTO();
        category_plant.setName("식물");
        category_plant.setImage(R.drawable.category_plant);
        list.add(category_plant);
        CategoryDTO category_etc = new CategoryDTO();
        category_etc.setName("기타");
        category_etc.setImage(R.drawable.category_etc);
        list.add(category_etc);

    }

    private void initRestApi() {
        preferences = contextNullSafe.getSharedPreferences("usersInfo", Context.MODE_PRIVATE);
        email = preferences.getString("email", "");
        Map map = new HashMap();
        map.put("email", email);
        Call<Map> call = boardService.selectList(map);
        call.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                if (response.isSuccessful()) {
                    Map map = response.body();
                    ObjectMapper mapper = new ObjectMapper();
                    userLists = mapper.convertValue(map.get("userLists"), new TypeReference<List<UserDTO>>() {
                    });
                    imageList = mapper.convertValue(map.get("imageList"), new TypeReference<List<List<ImageDTO>>>() {
                    });
                    boardList = mapper.convertValue(map.get("boardList"), new TypeReference<List<BoardDTO>>() {
                    });
                }
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {

            }
        });
    }

    private void initView() {
        mapView = new MapView(contextNullSafe);
        ViewGroup mapViewContainer = binding.mapView;
        mapViewContainer.addView(mapView);
        mapView.setZoomLevel(4, true);
        mapView.setPOIItemEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.marker_gif, new MapPOIItem.ImageOffset(30, 30));
        mapView.setCustomCurrentLocationMarkerImage(R.drawable.marker_gif, new MapPOIItem.ImageOffset(30, 0));
        mapView.setCustomCurrentLocationMarkerDirectionImage(R.drawable.custom_map_present_direction, new MapPOIItem.ImageOffset(65, 65));
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(contextNullSafe);
        if (ActivityCompat.checkSelfPermission(contextNullSafe, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contextNullSafe, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(contextNullSafe, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();    // 권한요청이 거절된 경우
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()), false);
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

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        final BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(contextNullSafe);
        if (bottomBinding2.getRoot() != null) {
            ViewGroup parentViewGroup = (ViewGroup) bottomBinding2.getRoot().getParent();
            if (null != parentViewGroup) {
                parentViewGroup.removeView(bottomBinding2.getRoot());
            }
        }
        bottomSheetDialog2.setContentView(bottomBinding2.getRoot());
        for (int i = 0; i < boardList.size(); i++) {
            BoardDTO boardDTO = boardList.get(i);
            if (mapPOIItem.getItemName().equals(boardDTO.getTitle())) {
                boardNo = boardDTO.getAuction_no();
                List<ImageDTO> bundleImageDTO = imageList.get(i);
                bottomBinding2.kakaoBottomCategory.setText(boardDTO.getCategory());
                String imageUrl = "http://192.168.0.38:8080/marketapp/resources/assets/img/product_img/" + bundleImageDTO.get(0).getImageName();
                Glide.with(contextNullSafe).load(imageUrl).into(bottomBinding2.kakaoBottomImg);
                bottomBinding2.kakaoBottomTime.setText(TIME_MAXIMUM.formatTimeString(boardDTO.getPostDate()));
                bottomBinding2.kakaoBottomMoney.setText(CHANGE_UTIL.intToWon(boardDTO.getUpper_Price()));
                if (boardDTO.getTitle().length() >= 10) {
                    String str = boardDTO.getTitle().substring(0, 10) + "...";
                    bottomBinding2.kakaoBottomTitle.setText(str);
                } else {
                    bottomBinding2.kakaoBottomTitle.setText(boardDTO.getTitle());
                }

            }
        }
        bottomSheetDialog2.show();

        bottomBinding2.bottomBindingView.setOnClickListener(view -> {
            bottomSheetDialog2.dismiss();
            Bundle bundle = new Bundle();
            bundle.putString("boardNo", boardNo);
            KakaoMapViewFragment kakaoMapViewFragment = new KakaoMapViewFragment();
            kakaoMapViewFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .add(R.id.kakao_map, kakaoMapViewFragment)
                    .addToBackStack(null)
                    .commit();
            getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
            getActivity().findViewById(R.id.kakao_location_btn).setVisibility(View.GONE);
            getActivity().findViewById(R.id.kakao_layer_btn).setVisibility(View.GONE);
        });

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

}