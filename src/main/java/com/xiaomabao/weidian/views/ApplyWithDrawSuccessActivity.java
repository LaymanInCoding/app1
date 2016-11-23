package com.xiaomabao.weidian.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.services.ShopService;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplyWithDrawSuccessActivity extends AppCompatActivity {
    @BindString(R.string.withdraw_result) String toolbarTitle;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitleText;
    @BindView(R.id.money)
    TextView moneyTextView;

    String money ="";

    @OnClick(R.id.back) void back() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_widthdraw_success);
        ButterKnife.bind(this);
        displayTitle();
    }

    public void displayTitle(){
        toolbarTitleText.setText(toolbarTitle);
        money = getIntent().getStringExtra("money");
        money = String.format("%.2f", Double.parseDouble(money));
        moneyTextView.setText(money);
    }

    @Override
    public void onResume(){
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
