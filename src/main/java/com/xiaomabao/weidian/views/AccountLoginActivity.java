package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.presenters.AccountLoginPresenter;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.CommonUtil;
import com.xiaomabao.weidian.util.InputSoftUtil;
import com.xiaomabao.weidian.util.LogUtils;
import com.xiaomabao.weidian.util.MD5Utils;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountLoginActivity extends AppCompatActivity {

    @BindString(R.string.username_login) String toolbarTitle;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitleText;
    @BindView(R.id.user_account)
    EditText userAccountEditText;
    @BindView(R.id.user_password)
    EditText userPasswordEditText;

    UserService mUserService;
    private AccountLoginPresenter mAccountLoginPresenter;


    @OnClick(R.id.back) void back() {
        finish();
    }
    @OnClick(R.id.login_submit) void login() {
        String username = userAccountEditText.getText().toString();
        if(!CommonUtil.isMobilePhone(username)){
            XmbPopubWindow.showAlert(this, Const.MOBILE_REGEX_ERROR);
            return;
        }
        String raw_password = userPasswordEditText.getText().toString();
        if(!CommonUtil.validPass(raw_password)){
            XmbPopubWindow.showAlert(this, Const.PASSWORD_REGEX_ERROR);
            return;
        }
        String password = MD5Utils.getMD5Code(raw_password);
        if (userAccountEditText.isFocused()){
            InputSoftUtil.hideSoftInput(this,userAccountEditText);
        }else if(userPasswordEditText.isFocused()){
            InputSoftUtil.hideSoftInput(this,userPasswordEditText);
        }
        XmbPopubWindow.showTranparentLoading(AccountLoginActivity.this);
        AppContext.setLoginPhone(this,username);
        AppContext.setLoginPassword(this,raw_password);
        mAccountLoginPresenter.checkLogin(UserService.gen_account_login_params(username,password));
    }
    @OnClick(R.id.forget_password) void forget_password() {
        startActivity(new Intent(AccountLoginActivity.this,ForgetPasswordActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);
        ButterKnife.bind(this);
        displayTitle();
        initApiInfo();
    }

    public void displayTitle(){
        toolbarTitleText.setText(toolbarTitle);
        userAccountEditText.setText(AppContext.getLoginPhone(this));
        userPasswordEditText.setText(AppContext.getLoginPassword(this));
        if(!AppContext.getLoginPhone(this).equals("")) {
            userAccountEditText.setSelection(AppContext.getLoginPhone(this).length());
        }
    }

    public void initApiInfo(){
        mUserService = new UserService();
        mAccountLoginPresenter = new AccountLoginPresenter(this, mUserService);
    }

    public void jumpToShopIndex(){
        setResult(RESULT_OK);
        LogUtils.loge("AccountAcFinish");
        finish();
//        startActivity(new Intent(AccountLoginActivity.this,MainActivity.class));
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
