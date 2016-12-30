package com.xiaomabao.weidian.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.Bean;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by de on 2016/12/20.
 */
public class MyBeanAdapter extends RecyclerView.Adapter<MyBeanAdapter.BeanHolder> {

    private ArrayList<Bean.BeanRecord> mArrayList;
    private Context mContext;

    public MyBeanAdapter(Context context, ArrayList<Bean.BeanRecord> list) {
        this.mContext = context;
        this.mArrayList = list;
    }


    @Override
    public BeanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bean, parent, false);
        return new BeanHolder(view);
    }

    @Override
    public void onBindViewHolder(BeanHolder holder, int position) {
        Bean.BeanRecord record = mArrayList.get(position);
        holder.mItemBeanTime.setText(record.record_time);
        holder.mItemBeanTitle.setText(record.record_desc);
        holder.mItemBeanValue.setText(record.record_val);
        holder.mItemBeanTime.setTextColor(Color.parseColor("#999999"));
        holder.mItemBeanTitle.setTextColor(Color.parseColor("#555555"));
        if (record.record_type.equals("0")) {
            holder.mItemBeanValue.setTextColor(Color.parseColor("#ff6363"));
            holder.mItemImg.setImageResource(R.mipmap.bean_shopping_given);
        } else if (record.record_type.equals("1")) {
            holder.mItemBeanTime.setTextColor(Color.parseColor("#bbbbbb"));
            holder.mItemBeanTitle.setTextColor(Color.parseColor("#bbbbbb"));
            holder.mItemBeanValue.setTextColor(Color.parseColor("#bbbbbb"));
            holder.mItemImg.setImageResource(R.mipmap.bean_already_used);
        } else if (record.record_type.equals("2")) {
            holder.mItemBeanValue.setTextColor(Color.parseColor("#a6dc6b"));
            holder.mItemImg.setImageResource(R.mipmap.bean_grow_natural);
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class BeanHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_img)
        ImageView mItemImg;
        @BindView(R.id.item_bean_title)
        TextView mItemBeanTitle;
        @BindView(R.id.item_bean_time)
        TextView mItemBeanTime;
        @BindView(R.id.item_value)
        TextView mItemBeanValue;

        public BeanHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
