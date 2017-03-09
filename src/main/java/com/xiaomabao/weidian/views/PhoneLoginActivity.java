package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.presenters.PhoneLoginPresenter;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.CommonUtil;
import com.xiaomabao.weidian.util.InputSoftUtil;
import com.xiaomabao.weidian.util.LogUtils;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhoneLoginActivity extends AppCompatActivity {

    private int REQUEST_ACCOUNT_LOGIN_CODE = 0X01;
    private int REQUEST_REG_CODE = 0x02;

    @BindString(R.string.phone_login)
    String toolbarTitle;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitleText;
    @BindView(R.id.user_phone)
    EditText userPhoneEditText;
    @BindView(R.id.user_code)
    EditText userCodeEditText;
    @BindView(R.id.save_shop)
    TextView registTextView;

    private UserService mUserService;
    private PhoneLoginPresenter mPhoneLoginPresenter;

    @OnClick(R.id.save_shop)
    void jumpToRegister() {
        startActivityForResult(new Intent(PhoneLoginActivity.this, RegisterActivity.class), REQUEST_REG_CODE);
    }

    @OnClick(R.id.back)
    void back() {
        finish();
    }

    @OnClick(R.id.login_submit)
    void login() {
        String username = userPhoneEditText.getText().toString();
        if (!CommonUtil.isMobilePhone(username)) {
            XmbPopubWindow.showAlert(this, Const.MOBILE_REGEX_ERROR);
            return;
        }
        String phone_code = userCodeEditText.getText().toString();
        if (!CommonUtil.validCode(phone_code)) {
            XmbPopubWindow.showAlert(this, Const.CODE_REGEX_ERROR);
            return;
        }
        if (userPhoneEditText.isFocused()) {
            InputSoftUtil.hideSoftInput(this, userPhoneEditText);
        } else if (userCodeEditText.isFocused()) {
            InputSoftUtil.hideSoftInput(this, userCodeEditText);
        }
        XmbPopubWindow.showTranparentLoading(PhoneLoginActivity.this);
        mPhoneLoginPresenter.checkLogin(UserService.gen_phone_login_params(username, phone_code));
    }

    @OnClick(R.id.get_phone_code)
    void get_phone_code() {
        String username = userPhoneEditText.getText().toString();
        if (!CommonUtil.isMobilePhone(username)) {
            XmbPopubWindow.showAlert(this, Const.MOBILE_REGEX_ERROR);
            return;
        }
        XmbPopubWindow.showTranparentLoading(PhoneLoginActivity.this);
        mPhoneLoginPresenter.sendCode(UserService.gen_send_code_params(username));
    }

    @OnClick(R.id.account_login)
    void account_login() {
        startActivityForResult(new Intent(PhoneLoginActivity.this, AccountLoginActivity.class), REQUEST_ACCOUNT_LOGIN_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        ButterKnife.bind(this);
        displayTitle();
        initApiInfo();
    }

    public void displayTitle() {
        toolbarTitleText.setText(toolbarTitle);
        registTextView.setVisibility(View.VISIBLE);
        registTextView.setText("注册");
        userPhoneEditText.setText(AppContext.getLoginPhone(this));
        if (!AppContext.getLoginPhone(this).equals("")) {
            userPhoneEditText.setSelection(AppContext.getLoginPhone(this).length());
        }
    }

    public void initApiInfo() {
        mUserService = new UserService();
        mPhoneLoginPresenter = new PhoneLoginPresenter(this, mUserService);
    }

    public void jumpToShopIndex() {
        setResult(RESULT_OK);
        finish();
//        startActivity(new Intent(PhoneLoginActivity.this, MainActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACCOUNT_LOGIN_CODE) {
            if (resultCode == RESULT_OK) {
                LogUtils.loge("loginfinish");
                setResult(RESULT_OK);
                finish();
            }
        }
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
