package com.xiaomabao.weidian.listeners;
import android.widget.TextView;

public class SwipeRefreshListener{

    public TextView hintTextView;

    public SwipeRefreshListener(TextView textView){
        hintTextView = textView;
        hintTextView.setText("下拉刷新");
    }

    public void onNormal() {
        hintTextView.setText("下拉刷新");
    }

    public void onLoose() {
        hintTextView.setText("松手刷新");
    }

    public void onRefresh() {
        hintTextView.setText("正在刷新，请等待");
    }

    public void refreshCallback(){
        hintTextView.setText("下拉刷新");
    }
}
