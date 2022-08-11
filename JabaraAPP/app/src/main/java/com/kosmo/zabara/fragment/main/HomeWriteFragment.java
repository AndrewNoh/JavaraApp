package com.kosmo.zabara.fragment.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kosmo.zabara.R;
import com.kosmo.zabara.databinding.FragmentHomeWriteBinding;

public class HomeWriteFragment extends Fragment {

    private FragmentHomeWriteBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);

        binding = FragmentHomeWriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onPause() {
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
        super.onPause();
    }
}