package com.xiaomabao.weidian.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.WithdrawRecord;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.ViewHolder> {

    private Context context;
    private List<WithdrawRecord.Withdraw> recordList;

    public WithdrawAdapter(Context context, List<WithdrawRecord.Withdraw> recordList){
        this.context = context;
        this.recordList = recordList;
    }

    @Override
    public WithdrawAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_withdraw_record,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WithdrawAdapter.ViewHolder holder, int position) {
        WithdrawRecord.Withdraw record = recordList.get(position);
        holder.applyTimeTextView.setText(record.apply_time);
        holder.applyMoneyTextView.setText(record.apply_money);
        if (record.withdraw_status_code == 0){
            holder.statusDescTextView.setTextColor(Color.rgb(231,96,87));
        }else{
            holder.statusDescTextView.setTextColor(Color.rgb(133,199,83));
        }
        holder.statusDescTextView.setText(record.withdraw_status_desc);
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.apply_time)TextView applyTimeTextView;
        @BindView(R.id.apply_money)TextView applyMoneyTextView;
        @BindView(R.id.withdraw_status_desc)TextView statusDescTextView;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
