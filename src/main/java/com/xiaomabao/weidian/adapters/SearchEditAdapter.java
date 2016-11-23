package com.xiaomabao.weidian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaomabao.weidian.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by de on 2016/10/12.
 */
public class SearchEditAdapter extends RecyclerView.Adapter<SearchEditAdapter.ViewHolder> {

    private List<String> mData;
    private Context mContext;
    private OnItemClickListener mListener;

    public SearchEditAdapter(Context context, List<String> data) {
        mData = data;
        mContext = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.span_list_item.setText(mData.get(position));
        if (mListener != null) {
            holder.span_list_item.setOnClickListener(view -> mListener.onItemClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.search_span_item)
        TextView span_list_item;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
