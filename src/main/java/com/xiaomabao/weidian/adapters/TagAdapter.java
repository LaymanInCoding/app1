package com.xiaomabao.weidian.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.ui.FlowTagLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by de on 2016/10/10.
 */
public class TagAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<String> mDataList;

    public TagAdapter(Context context, ArrayList<String> data){
        this.mContext = context;
        this.mDataList = data;
    }

    @Override
    public int getCount() {
        if (mDataList!=null){
        return mDataList.size();
        }else{
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_tag,null);
        TextView textView = (TextView) view.findViewById(R.id.tv_tag);
        String t = mDataList.get(position);
        textView.setText(t);
        view.setTag(t);
        return view;
    }
}
