package com.xiaomabao.weidian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.OrderList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private List<OrderList.Order> orders;

    public OrderAdapter(Context context, List<OrderList.Order> orders){
        this.context = context;
        this.orders = orders;
    }

    private OnItemClickListener onItemClickListener;

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order,parent,false));
    }

    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, int position) {
        OrderList.Order order = orders.get(position);
        holder.addTimeTextView.setText(order.add_time);
        holder.orderStatusTextView.setText(order.order_status);
        if (onItemClickListener != null){
            holder.orderDetailTextView.setOnClickListener((view)->onItemClickListener.onItemClick(position));
        }
        holder.goodsContainerLayout.removeAllViews();
        List<OrderList.Goods> goods = order.order_goods;
        for (int i = 0; i < goods.size(); i++){
            LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_order_good,holder.goodsContainerLayout,false);
            ImageView goodsThumbImageView = (ImageView) layout.findViewById(R.id.goods_thumb);
            Glide.with(context)
                    .load(goods.get(i).goods_thumb)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(goodsThumbImageView);
            TextView goodsNameTextView = (TextView) layout.findViewById(R.id.goods_name);
            goodsNameTextView.setText(goods.get(i).goods_name);
            TextView goodsPriceTextView = (TextView) layout.findViewById(R.id.goods_price);
            goodsPriceTextView.setText(goods.get(i).goods_price);
            TextView goodsNumberTextView = (TextView) layout.findViewById(R.id.goods_number);
            goodsNumberTextView.setText("x " + goods.get(i).goods_number);
            holder.goodsContainerLayout.addView(layout);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.add_time)TextView addTimeTextView;
        @BindView(R.id.order_status)TextView orderStatusTextView;
        @BindView(R.id.order_detail)TextView orderDetailTextView;
        @BindView(R.id.goods_container)LinearLayout goodsContainerLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
