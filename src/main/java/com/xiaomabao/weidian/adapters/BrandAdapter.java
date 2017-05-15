package com.xiaomabao.weidian.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.Brand;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> {

    private Context context;
    private List<Brand> brandList;
    private LinearLayout.LayoutParams layoutParams;
    private int type = 0;


    public BrandAdapter(Activity context, List<Brand> brandList,int type){
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        this.context = context;
        this.brandList = brandList;
        this.type = type;
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width * 350 / 750);
        layoutParams.setMargins(20,0,20,0);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    @Override
    public BrandAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_brand,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BrandAdapter.ViewHolder holder, int position) {
        final Brand brand = brandList.get(position);
        holder.bannerImageView.setLayoutParams(layoutParams);
        Glide.with(context)
                .load(brand.banner)
                .placeholder(R.mipmap.brand_preload)
                .crossFade()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.bannerImageView);
        if(onItemClickListener != null){
            holder.itemView.setOnClickListener((view) -> onItemClickListener.onItemClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.banner) ImageView bannerImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
