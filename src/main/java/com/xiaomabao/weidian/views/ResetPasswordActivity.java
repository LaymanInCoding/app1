package com.xiaomabao.weidian.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.presenters.ResetPasswordPresenter;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.CommonUtil;
import com.xiaomabao.weidian.util.InputSoftUtil;
import com.xiaomabao.weidian.util.MD5Utils;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ResetPasswordActivity extends AppCompatActivity {
    @BindString(R.string.sms_verification) String toolbarTitle;
    @BindString(R.string.next_step) String toolbarRightText;

    @BindView(R.id.toolbar_title)
    TextView toolBarTitleTextView;

    @BindView(R.id.toolbar_right)
    TextView toolBarRightTextView;

    @BindView(R.id.user_password)
    EditText userPasswordEditText;

    @BindView(R.id.user_re_password)
    EditText userRePasswordEditText;

    private String phone = "";
    private String phone_code = "";
    private UserService mUserService;
    private ResetPasswordPresenter mPresenter;

    @OnClick(R.id.back) void back() {
        finish();
    }
    @OnClick(R.id.toolbar_right) void next() {
        if(!CommonUtil.isMobilePhone(phone)){
            XmbPopubWindow.showAlert(this, Const.MOBILE_REGEX_ERROR);
            return;
        }
        if(!CommonUtil.validCode(phone_code)){
            XmbPopubWindow.showAlert(this, Const.CODE_REGEX_ERROR);
            return;
        }
        String user_password = userPasswordEditText.getText().toString();
        String user_re_password = userRePasswordEditText.getText().toString();
        if(!CommonUtil.validPass(user_password)){
            XmbPopubWindow.showAlert(this, Const.CODE_REGEX_ERROR);
            return;
        }
        if(!CommonUtil.validPass(user_re_password)){
            XmbPopubWindow.showAlert(this, Const.CODE_REGEX_ERROR);
            return;
        }
        if(!user_re_password.equals(user_password)){
            XmbPopubWindow.showAlert(this, Const.PASSWORD_NEQUAL_ERROR);
            return;
        }
        if (userPasswordEditText.isFocused()){
            InputSoftUtil.hideSoftInput(this,userPasswordEditText);
        }else{
            InputSoftUtil.hideSoftInput(this,userRePasswordEditText);
        }
        mPresenter.updatePassword(UserService.gen_reset_password_params(phone,phone_code, MD5Utils.getMD5Code(user_password)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        displayTitle();
        initApiInfo();
    }

    public void displayTitle(){
        toolBarTitleTextView.setText(toolbarTitle);
        toolBarRightTextView.setText(toolbarRightText);
        phone = getIntent().getStringExtra("phone");
        phone_code = getIntent().getStringExtra("phone_code");
    }

    public void initApiInfo(){
        mUserService = new UserService();
        mPresenter = new ResetPasswordPresenter(this, mUserService);
    }

    public void callback(){
        Observable<Long> observable = Observable.timer(2, TimeUnit.SECONDS, Schedulers.io());
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Long) -> {
                    setResult(RESULT_OK);
                    finish();
                } );
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
