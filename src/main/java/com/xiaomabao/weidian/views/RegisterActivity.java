package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.presenters.RegisterPresenter;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.CommonUtil;
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

public class RegisterActivity extends AppCompatActivity {
    @BindString(R.string.new_user_register) String toolbarTitle;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitleText;

    @BindView(R.id.user_phone)
    EditText userPhoneEditText;
    @BindView(R.id.user_code)
    EditText userCodeEditText;
    @BindView(R.id.user_password)
    EditText userPasswordEditText;
    @BindView(R.id.invitation_code)
    EditText invitationCodeEditText;
//    @BindView(R.id.shop_code)
//    EditText shopCodeEditText;
    @BindView(R.id.shop_agreement)
    TextView shopAgreementTextView;
    @BindView(R.id.checked_img)
    ImageView checkedImageView;

    private UserService mService;
    private RegisterPresenter mPresenter;
    int agreement = 1;

    @OnClick(R.id.back) void back() {
        finish();
    }
//    @OnClick(R.id.get_shop_code) void get_shop_code() {
//        startActivity(new Intent(this,ImmediatelyShopActivity.class));
//    }
    @OnClick(R.id.checked_img) void agree() {
        if (agreement == 1){
            agreement = 0;
            checkedImageView.setImageResource(R.mipmap.pay_normal);
        }else{
            checkedImageView.setImageResource(R.mipmap.pay_selected);
            agreement = 1;
        }
    }
    @OnClick(R.id.shop_agreement) void jumpToAgreement() {
        startActivity(new Intent(this,AgreementActivity.class));
    }
    @OnClick(R.id.get_phone_code) void get_phone_code() {
        String username = userPhoneEditText.getText().toString();
        if(!CommonUtil.isMobilePhone(username)){
            XmbPopubWindow.showAlert(this, Const.MOBILE_REGEX_ERROR);
            return;
        }
        XmbPopubWindow.showTranparentLoading(RegisterActivity.this);
        mPresenter.sendCode(UserService.gen_send_code_params(username));
    }
    @OnClick(R.id.reg_submit) void register() {
        if (agreement == 0){
            XmbPopubWindow.showAlert(this, Const.AGREEMENT);
            return;
        }
        String username = userPhoneEditText.getText().toString();
        String user_code = userCodeEditText.getText().toString();
        String user_password = userPasswordEditText.getText().toString();
        String invitation_code = invitationCodeEditText.getText().toString();
//        String shop_code = shopCodeEditText.getText().toString();
        if(!CommonUtil.isMobilePhone(username)){
            XmbPopubWindow.showAlert(this, Const.MOBILE_REGEX_ERROR);
            return;
        }
        if(!CommonUtil.validPass(user_password)){
            XmbPopubWindow.showAlert(this, Const.PASSWORD_REGEX_ERROR);
            return;
        }
        if(!CommonUtil.validCode(user_code)){
            XmbPopubWindow.showAlert(this, Const.CODE_REGEX_ERROR);
            return;
        }
//        if(!CommonUtil.validInviteCode(shop_code)){
//            XmbPopubWindow.showAlert(this, Const.SHOP_CODE_REGEX_ERROR);
//            return;
//        }
        XmbPopubWindow.showTranparentLoading(RegisterActivity.this);
        mPresenter.register(UserService.gen_register_params(username,user_code, MD5Utils.getMD5Code(user_password),invitation_code));
//        mPresenter.register(UserService.gen_register_params(username,user_code, MD5Utils.getMD5Code(user_password)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        displayTitle();
        initApiInfo();
    }

    public void initApiInfo(){
        mService = new UserService();
        mPresenter = new RegisterPresenter(this, mService);
    }

    public void callback(){
        Observable<Long> observable = Observable.timer(2, TimeUnit.SECONDS, Schedulers.io());
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Long) -> {
                    startActivity(new Intent(RegisterActivity.this,PhoneLoginActivity.class));
                    finish();
                } );
    }

    public void displayTitle(){
        toolbarTitleText.setText(toolbarTitle);
        shopAgreementTextView.setText(Html.fromHtml(getString(R.string.shop_agreement)));
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
