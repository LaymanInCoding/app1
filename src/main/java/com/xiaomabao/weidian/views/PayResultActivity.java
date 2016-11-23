package com.xiaomabao.weidian.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.presenters.PayResultPresenter;
import com.xiaomabao.weidian.services.UserService;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayResultActivity extends AppCompatActivity {
    @BindString(R.string.pay_success) String toolbarTitle;
    @BindView(R.id.toolbar_title)
    TextView toolBarTitleTextView;
    @BindView(R.id.code)
    TextView codeTextView;
    @BindView(R.id.anim_loading)
    View anim_loading;

    @OnClick(R.id.back) void back() {
        finish();
    }
    @OnClick(R.id.finish) void finishe() {
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        ButterKnife.bind(this);
        toolBarTitleTextView.setText(toolbarTitle);
        initApi();
    }

    UserService mService;
    PayResultPresenter mPresenter;

    protected void initApi(){
        mService = new UserService();
        mPresenter = new PayResultPresenter(this,mService);
        mPresenter.get_shop_code(UserService.gen_get_shop_code_params(getIntent().getStringExtra("mobile"),getIntent().getStringExtra("order_sn")));
    }

    public void setView(String code){
        anim_loading.setVisibility(View.GONE);
        codeTextView.setText(code);
    }

}
