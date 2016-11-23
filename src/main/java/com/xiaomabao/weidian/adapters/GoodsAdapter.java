package com.xiaomabao.weidian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.Goods;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {

    private Context context;
    private List<Goods> goodsList;

    public GoodsAdapter(Context context,List<Goods> goodsList){
        this.context = context;
        this.goodsList = goodsList;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
        void toggleSale(int position);
        void shareGood(int position);
    }

    @Override
    public GoodsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goods,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoodsAdapter.ViewHolder holder, int position) {
        Goods goods = goodsList.get(position);
        Glide.with(context)
                .load(goods.goods_thumb)
                .placeholder(R.mipmap.good_preload)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.goodsThumbImageView);
        holder.goodsNameTextView.setText(goods.goods_name);
        holder.goodsPriceTextView.setText(goods.weidian_goods_price);
        holder.goodsProfitTextView.setText(goods.weidian_goods_profit);
        holder.goodsNumberTextView.setText("库存:"+goods.goods_number);
        if (goods.goods_sale_status == 1){
            holder.goodsSaleStatusTextView.setText("已上架");
            holder.goodsStatusImageView.setImageResource(R.mipmap.on_sale);
        }else{
            holder.goodsSaleStatusTextView.setText("未上架");
            holder.goodsStatusImageView.setImageResource(R.mipmap.add_sale);
        }
        if(onItemClickListener != null){
            holder.itemView.setOnClickListener((view) -> onItemClickListener.onItemClick(position));
            holder.toggleSaleView.setOnClickListener((view) -> onItemClickListener.toggleSale(position));
            holder.goodsSaleView.setOnClickListener((view) -> onItemClickListener.shareGood(position));
        }
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.goods_thumb)ImageView goodsThumbImageView;
        @BindView(R.id.goods_sale_status_image)ImageView goodsStatusImageView;
        @BindView(R.id.goods_name)TextView goodsNameTextView;
        @BindView(R.id.goods_price)TextView goodsPriceTextView;
        @BindView(R.id.goods_profit)TextView goodsProfitTextView;
        @BindView(R.id.goods_number)TextView goodsNumberTextView;
        @BindView(R.id.goods_sale_status)TextView goodsSaleStatusTextView;
        @BindView(R.id.toggle_sale_container)View toggleSaleView;
        @BindView(R.id.share_good_container)View goodsSaleView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
