package com.xiaomabao.weidian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.ShopBase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by de on 2016/12/22.
 */
public class ShopChooseAdapter extends RecyclerView.Adapter<ShopChooseAdapter.ViewHolder> {

    private ArrayList<ShopBase.ShopBaseInfo.ShopShareInfo> mDataList;
    private Context mContext;
    private OnItemChooseListener mListener;


    public interface OnItemChooseListener {
        void onItemChoose(int position);
    }

    public void setOnItemChooseListener(OnItemChooseListener listener) {
        mListener = listener;
    }

    public ShopChooseAdapter(ArrayList<ShopBase.ShopBaseInfo.ShopShareInfo> dataList, Context context) {
        this.mDataList = dataList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_shopchoose_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDataList.size() == 0) {
            holder.mNoShopTv.setVisibility(View.VISIBLE);
        }
        holder.mNoShopTv.setVisibility(View.GONE);
        ShopBase.ShopBaseInfo.ShopShareInfo shopShareInfo = mDataList.get(position);
        holder.mShopName.setText(shopShareInfo.shop_name);
        if (shopShareInfo.is_default.equals("1")) {
            holder.mChooseButton.setImageResource(R.mipmap.shop_choose);
        }
        holder.mShopContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemChoose(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.shop_name)
        TextView mShopName;
        @BindView(R.id.choose_button)
        ImageView mChooseButton;
        @BindView(R.id.shop_container)
        RelativeLayout mShopContainer;
        @BindView(R.id.no_shop_msg)
        TextView mNoShopTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
