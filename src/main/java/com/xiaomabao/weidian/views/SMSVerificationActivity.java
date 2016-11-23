package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.presenters.SMSVerificationPresenter;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.CommonUtil;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SMSVerificationActivity extends AppCompatActivity {

    private int REQUEST_RESET_PASSWORD_CODE = 0x01;

    private String phone = "";
    private UserService mUserService;
    private SMSVerificationPresenter mPresenter;

    @BindString(R.string.sms_verification) String toolbarTitle;
    @BindString(R.string.next_step) String toolbarRightText;

    @BindView(R.id.toolbar_title)
    TextView toolBarTitleTextView;

    @BindView(R.id.toolbar_right)
    TextView toolBarRightTextView;

    @BindView(R.id.user_code)
    EditText userCodeTextView;

    @OnClick(R.id.back) void back() {
        finish();
    }
    @OnClick(R.id.toolbar_right) void next() {
        if(!CommonUtil.isMobilePhone(phone)){
            XmbPopubWindow.showAlert(this, Const.MOBILE_REGEX_ERROR);
            return;
        }
        String phone_code = userCodeTextView.getText().toString();
        if(!CommonUtil.validCode(phone_code)){
            XmbPopubWindow.showAlert(this, Const.CODE_REGEX_ERROR);
            return;
        }
        XmbPopubWindow.showTranparentLoading(this);
        mPresenter.validCode(UserService.gen_valid_code_params(phone,phone_code));
    }

    @OnClick(R.id.get_phone_code) void get_phone_code() {

        if(!CommonUtil.isMobilePhone(phone)){
            XmbPopubWindow.showAlert(this, Const.MOBILE_REGEX_ERROR);
            return;
        }
        XmbPopubWindow.showTranparentLoading(this);
        mPresenter.sendCode(UserService.gen_send_code_params(phone));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);
        ButterKnife.bind(this);
        displayTitle();
        initApiInfo();
    }

    public void displayTitle(){
        toolBarTitleTextView.setText(toolbarTitle);
        toolBarRightTextView.setText(toolbarRightText);
        phone = getIntent().getStringExtra("phone");
    }

    public void initApiInfo(){
        mUserService = new UserService();
        mPresenter = new SMSVerificationPresenter(this, mUserService);
    }

    public void jumpToResetPasswordActivity(){
        Intent intent = new Intent(SMSVerificationActivity.this,ResetPasswordActivity.class);
        intent.putExtra("phone",phone);
        intent.putExtra("phone_code",userCodeTextView.getText().toString());
        startActivityForResult(intent,REQUEST_RESET_PASSWORD_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_RESET_PASSWORD_CODE) {
            if(resultCode==RESULT_OK) {
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
