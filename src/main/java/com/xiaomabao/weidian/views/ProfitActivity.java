package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.ShopProfit;
import com.xiaomabao.weidian.presenters.ProfitPresenter;
import com.xiaomabao.weidian.services.ShopService;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfitActivity extends AppCompatActivity {
    @BindString(R.string.title_my_profit)
    String toolbarTitle;


    protected final int REQUEST_BINDCARD_CODE = 0x01;

    @BindView(R.id.toolbar_title)
    TextView toolbarTextView;
    @BindView(R.id.available_balance)
    TextView availableBalanceTextView;
    @BindView(R.id.presenting_profit)
    TextView presentingProfitTextView;
    @BindView(R.id.waiting_profit)
    TextView waitingProfitTextView;
    @BindView(R.id.loading_anim)
    View loadView;

    @OnClick(R.id.back)
    void back() {
        finish();
    }

    @OnClick(R.id.presented_profit_container)
    void jumpToWithdrawRecord() {
        Intent intent = new Intent(ProfitActivity.this, WithdrawRecordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.my_bean_container)
    public void onClick() {
        Intent intent = new Intent(ProfitActivity.this, MyShareBeanActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.apply_profit)
    void apply_profit() {
        if (AppContext.getCardBindStatus(this).equals("1")) {
            Intent intent = new Intent(ProfitActivity.this, ApplyProfitActivity.class);
            intent.putExtra("balance", profit.data.available_balance);
            startActivity(intent);
        } else {
            startActivityForResult(new Intent(this, BindCardActivity.class), REQUEST_BINDCARD_CODE);
        }
    }

    ShopProfit profit;

    ShopService mService;
    ProfitPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit);
        ButterKnife.bind(this);
        setView();
    }

    protected void setView() {
        toolbarTextView.setText(toolbarTitle);
    }

    protected void initApi() {
        mService = new ShopService();
        mPresenter = new ProfitPresenter(this, mService);
        mPresenter.profit_info(ShopService.gen_profit_info_params(AppContext.getToken(this)));
    }

    public void loadView(ShopProfit profit) {
        this.profit = profit;
        availableBalanceTextView.setText(profit.data.available_balance);
        presentingProfitTextView.setText(profit.data.presenting_withdraw);
        waitingProfitTextView.setText(profit.data.waiting_profit);
        loadView.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BINDCARD_CODE) {
            if (resultCode == RESULT_OK) {
                apply_profit();
            }
        }
    }

    public void onResume() {
        super.onResume();
        initApi();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
