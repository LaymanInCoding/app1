package com.xiaomabao.weidian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.OrderType;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderTitleAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int NormalViewType = 0x01;
    private final int SelectViewType = 0x02;
    private Context context;
    private List<OrderType> orderTypeList;

    public OrderTitleAdapter(Context context,List<OrderType> orderTypeList){
        this.context = context;
        this.orderTypeList = orderTypeList;
    }

    private OnItemClickListener onItemClickListener;

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == NormalViewType){
            view = LayoutInflater.from(context).inflate(R.layout.item_order_type_normal,parent,false);
            return new NormalViewHolder(view);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.item_order_type_selected,parent,false);
            return new SelectedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OrderType orderType = orderTypeList.get(position);
        if(orderType.selected == NormalViewType){
            ((SelectedViewHolder)(holder)).selectedCategoryTextView.setText(orderType.order_type_name);
        }else{
            ((NormalViewHolder)(holder)).normalCategoryTextView.setText(orderType.order_type_name);
        }
        if(onItemClickListener != null){
            holder.itemView.setOnClickListener((View v) -> onItemClickListener.onItemClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return orderTypeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(orderTypeList.get(position).selected == 1){
            return SelectViewType;
        }else{
            return NormalViewType;
        }
    }

    public static class NormalViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.normal_order_text)
        TextView normalCategoryTextView;

        public NormalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class SelectedViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.select_order_text)
        TextView selectedCategoryTextView;
        public SelectedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
