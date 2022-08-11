package com.kosmo.zabara.fragment.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.kosmo.zabara.databinding.ItemSliderLayoutBinding;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class KakaoSliderAdapter extends SliderViewAdapter<KakaoSliderAdapter.FilesSliderViewHolder> {

    private Context context;
    private ItemSliderLayoutBinding binding;
    private List<String> mSliderItems = new ArrayList<>();

    public KakaoSliderAdapter(Context context) {
        this.context = context;
    }

    public void renewItems(List sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(String getItemImageResId) {
        this.mSliderItems.add(getItemImageResId);
        notifyDataSetChanged();
    }

    @Override
    public FilesSliderViewHolder onCreateViewHolder(ViewGroup parent) {
        binding = ItemSliderLayoutBinding.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View view = binding.getRoot();
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return new KakaoSliderAdapter.FilesSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FilesSliderViewHolder viewHolder, int position) {
        String getItemImageResId = mSliderItems.get(position);
        Glide.with(viewHolder.itemView)
                .load(getItemImageResId)
                .centerCrop()
                .into(viewHolder.main_item);
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    class FilesSliderViewHolder extends ViewHolder {

        ImageView main_item;

        public FilesSliderViewHolder(View itemView) {
            super(itemView);
            main_item = binding.mainItem;
        }
    }
}
