package com.kosmo.zabara.fragment.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
import com.kosmo.zabara.R;
import com.kosmo.zabara.api.dto.ShowDTO;
import com.kosmo.zabara.api.service.BoardService;
import com.kosmo.zabara.databinding.FragmentSearchBinding;
import com.kosmo.zabara.fragment.user.DetailRecycleAdapter;
import com.kosmo.zabara.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SearchFragment extends Fragment {

    private Context contextNullSafe;
    TextView result, confidence;
    ImageView imageView, picture;
    int imageSize = 224;
    SharedPreferences preferences;
    String email;
    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create())
            .baseUrl("http://192.168.0.38:9090/market/").build();
    BoardService service = retrofit.create(BoardService.class);
    ObjectMapper mapper = new ObjectMapper();


    private FragmentSearchBinding binding;

    @SuppressLint("MissingPermission")
    ActivityResultLauncher<Intent> registerResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), (result) -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent intent = result.getData();
                    Bitmap image = (Bitmap) intent.getExtras().get("data");
                    int dimension = Math.min(image.getWidth(), image.getHeight());
                    image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                    imageView.setImageBitmap(image);
                    image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                    classifyImage(image);
                    if (binding.searchRecyclerView.getVisibility() == View.VISIBLE)
                        binding.searchRecyclerView.setVisibility(View.GONE);
                    binding.searchScroll.setVisibility(View.VISIBLE);
                }
            });
    @SuppressLint("MissingPermission")
    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), result -> {
                if (result) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    registerResult.launch(cameraIntent);
                } else {
                    Toast.makeText(contextNullSafe, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();    // 권한요청이 거절된 경우
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
        requireActivity().setActionBar(binding.searchToolbar);
        requireActivity().getActionBar().setDisplayHomeAsUpEnabled(true);// show back button
        requireActivity().getActionBar().setDisplayShowTitleEnabled(false);
        binding.searchToolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        result = binding.result;
        confidence = binding.confidence;
        imageView = binding.imageView;
        picture = binding.button;
        result.setOnClickListener(view -> {
            String title = result.getText().toString();
            searchProcess(title);
        });
        picture.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(contextNullSafe, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                registerResult.launch(cameraIntent);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        binding.searchInput.setOnEditorActionListener((view, actionId, keyEvent) -> {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    InputMethodManager inputMethodManager = (InputMethodManager) contextNullSafe.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    String title = binding.searchInput.getText().toString();
                    searchProcess(title);
                    break;
                default:
                    // 기본 엔터키 동작
                    return false;
            }
            return true;
        });

        return binding.getRoot();
    }

    private void searchProcess(String content) {
        preferences = contextNullSafe.getSharedPreferences("usersInfo", Context.MODE_PRIVATE);
        email = preferences.getString("email", "");
        Map map = new HashMap();
        map.put("email", email);
        map.put("choseMenu", "search");
        map.put("title", content);
        Call<Map> call = service.choseMenu(map);
        call.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                Map map = response.body();
                List<ShowDTO> itemList = mapper.convertValue(map.get("showDTO"), new TypeReference<List<ShowDTO>>() {
                });
                if (itemList.size() != 0) {
                    RecyclerView recyclerView = binding.searchRecyclerView;
                    DetailRecycleAdapter adapter = new DetailRecycleAdapter(contextNullSafe);
                    recyclerView.setLayoutManager(new LinearLayoutManager(contextNullSafe, LinearLayoutManager.VERTICAL, false));
                    recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                    adapter.setFriendList(itemList);
                    recyclerView.setAdapter(adapter);
                    if (binding.searchScroll.getVisibility() == View.VISIBLE)
                        binding.searchScroll.setVisibility(View.GONE);
                    binding.searchRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(contextNullSafe, "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {
                Log.i("실행", "왜안돼");
            }
        });
    }


    public void classifyImage(Bitmap image) {
        try {
            Model model = Model.newInstance(contextNullSafe.getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"휴대폰", "Apple", "아이폰13", "아이폰"};
            result.setText(classes[maxPos]);

            String s = "";
            for (int i = 0; i < classes.length; i++) {
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
            }
            confidence.setText(s);
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.contextNullSafe = context;
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

}