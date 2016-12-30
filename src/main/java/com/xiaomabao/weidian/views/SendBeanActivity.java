package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.Const;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.presenters.BeanPresenter;
import com.xiaomabao.weidian.services.BeanService;
import com.xiaomabao.weidian.util.CommonUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by de on 2016/12/21.
 */
public class SendBeanActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.use_num_et)
    EditText mUseNumEt;
    @BindView(R.id.send_num)
    EditText mSendNumEt;
    @BindView(R.id.my_bean_num)
    TextView mMyBeanNum;

    private String friend_account;
    private String send_num;
    private String beanNum;
    private BeanPresenter mPresenter;
    private BeanService mService;

    public void setToolbarTitle() {
        mToolbarTitle.setText("赠送好友");
    }

    public void initApi() {
        mService = new BeanService();
        mPresenter = new BeanPresenter(this, mService);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_bean);
        beanNum = getIntent().getStringExtra("bean_num");
        ButterKnife.bind(this);
        initApi();
        setToolbarTitle();
        initConfig();
    }

    public void request() {
        mPresenter.send_bean(BeanService.gen_sendBean_param(AppContext.getToken(this), send_num, friend_account));
    }

    private void initConfig() {
        mMyBeanNum.setText(beanNum);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void callback(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        sendBroadcast(new Intent(Const.INTENT_ACTION_REFRESH_BEAN));
        Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Long) -> {
                    startActivity(new Intent(this, MyShareBeanActivity.class));
                    finish();
                });
    }

    public void callbackError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.back, R.id.submit_textview, R.id.use_help})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.submit_textview:
                friend_account = mUseNumEt.getText().toString();
                send_num = mSendNumEt.getText().toString();
                if (CommonUtil.isMobilePhone(friend_account) && !send_num.equals("")) {
                    request();
                } else if (mSendNumEt.getText().toString().equals("")) {
                    showToast("请输入赠送数量");
                } else {
                    showToast("请输入正确的手机号码");
                }
                break;
            case R.id.use_help:
                Intent intent = new Intent(this, ShareBeanHelpActivity.class);
                startActivity(intent);
                break;
        }
    }
}
