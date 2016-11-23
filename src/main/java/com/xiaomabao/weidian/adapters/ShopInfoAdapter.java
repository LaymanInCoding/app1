package com.xiaomabao.weidian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.ShopBase;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by de on 2016/9/27.
 */
public class ShopInfoAdapter extends RecyclerView.Adapter<ShopInfoAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ShopBase.ShopBaseInfo.ShopShareInfo> dataList;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void OnDeleteShop(int position);

        void OnEditShop(int position);

        void OnSetDefault(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public ShopInfoAdapter(Context context, ArrayList<ShopBase.ShopBaseInfo.ShopShareInfo> shopShareInfos) {
        mContext = context;
        dataList = shopShareInfos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_shop_choose, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.e("onBindViewHolder", "onBindViewHolder");
        ShopBase.ShopBaseInfo.ShopShareInfo shareInfo = dataList.get(position);
        if (dataList.size() == 1) {
            shareInfo.is_default="1";
            holder.default_text.setText("默认");
            holder.default_image.setImageResource(R.mipmap.shop_default);
        } else if (!shareInfo.is_default.equals("1")) {
            holder.default_text.setText("设为默认");
            holder.default_image.setImageResource(R.mipmap.shop_no_default);
        } else {
            holder.default_text.setText("默认");
            holder.default_image.setImageResource(R.mipmap.shop_default);
        }
        holder.shop_name.setText(shareInfo.shop_name);
        if (mOnItemClickListener != null) {
            holder.shop_eidt.setOnClickListener((View v) -> mOnItemClickListener.OnEditShop(position));
            holder.shop_delete.setOnClickListener((View v) -> mOnItemClickListener.OnDeleteShop(position));
            holder.set_default.setOnClickListener((View v) -> {
                if (!shareInfo.is_default.equals("1")) {
                    mOnItemClickListener.OnSetDefault(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.shop_name)
        TextView shop_name;
        @BindView(R.id.edit_shop)
        LinearLayout shop_eidt;
        @BindView(R.id.delete_shop)
        LinearLayout shop_delete;
        @BindView(R.id.set_default)
        LinearLayout set_default;
        @BindView(R.id.default_image)
        ImageView default_image;
        @BindView(R.id.default_text)
        TextView default_text;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
