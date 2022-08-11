package com.kosmo.zabara.fragment.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.core.content.ContextCompat;

import com.kosmo.zabara.R;
import com.kosmo.zabara.api.dto.CategoryDTO;
import com.kosmo.zabara.databinding.ItemSpinnerLayoutBinding;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    private Context context;
    private List<CategoryDTO> categoryList;
    private ItemSpinnerLayoutBinding binding;

    public CategoryAdapter(Context context, List<CategoryDTO> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return categoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        binding = ItemSpinnerLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.spinnerImg.setImageResource(categoryList.get(i).getImage());
        binding.spinnerText.setText(categoryList.get(i).getName());
        binding.spinnerText.setTextColor(ContextCompat.getColor(context, R.color.white));
        return binding.getRoot();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getDropDownView(int i, View convertView, ViewGroup parent) {
        binding = ItemSpinnerLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        if(i==0){
            binding.spinnerBg.setBackgroundColor(R.color.backColor);
        }
        binding.spinnerImg.setImageResource(categoryList.get(i).getImage());
        binding.spinnerText.setText(categoryList.get(i).getName());
        return binding.getRoot();
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }
}
