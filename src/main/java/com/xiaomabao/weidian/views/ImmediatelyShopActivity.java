package com.xiaomabao.weidian.views;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImmediatelyShopActivity extends AppCompatActivity {
    @BindView(R.id.kdm)
    ImageView kdmImageView;
    @OnClick(R.id.back) void back() {
        finish();
    }
    @OnClick(R.id.immediately_shop) void immediately_shop() {
        startActivity(new Intent(this,ReceiptAddressActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immediately_shop);
        ButterKnife.bind(this);
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyymmddHHmm");
        String date = sDateFormat.format(new java.util.Date());
        Glide.with(this)
                .load("http://weidian.xiaomabao.com/static/images/kaidianm.png?v="+date)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(kdmImageView);
    }

}
