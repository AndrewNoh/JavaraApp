package com.kosmo.zabara.fragment.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.kosmo.zabara.R;
import com.kosmo.zabara.databinding.FragmentCategoryBinding;
import com.kosmo.zabara.fragment.user.DetailFragment;

public class CategoryFragment extends Fragment {

    private Context context;

    private FragmentCategoryBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        requireActivity().setActionBar(binding.categoryBar);
        requireActivity().getActionBar().setDisplayHomeAsUpEnabled(true);// show back button
        requireActivity().getActionBar().setDisplayShowTitleEnabled(false);
        binding.categoryBar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        binding.categoryCar.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "중고차");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryDigital.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "디지털기기");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryLife.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "생활가전");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryFurniture.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "가구/인테리어");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryBaby.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "유아동");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryBabyBook.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "유아도서");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryLifeStyle.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "생활/가공식품");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categorySports.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "스포츠/레저");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryBag.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "여성잡화");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryWomen.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "여성의류");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryMan.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "남성패션/잡화");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryHobby.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "게임/취미");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryBeauty.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "뷰티/미용");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryPet.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "반려동물용품");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryBook.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "도서/티켓/음반");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryPlant.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "식물");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.categoryEtc.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("choseMenu", "category");
            bundle.putString("category", "기타");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.home_ly, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });


        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }
}