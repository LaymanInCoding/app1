package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.R;


import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartWeidianActivity extends AppCompatActivity {

    private int REQUEST_LOGIN_CODE = 0x01;
    private int REQUEST_REG_CODE = 0x02;


    @OnClick(R.id.login_btn)
    void jumpToLogin() {
        startActivityForResult(new Intent(StartWeidianActivity.this, PhoneLoginActivity.class), REQUEST_REG_CODE);
    }

    @OnClick(R.id.register_btn)
    void jumpToRegister() {
        startActivityForResult(new Intent(StartWeidianActivity.this, RegisterActivity.class), REQUEST_LOGIN_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_weidian);
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN_CODE) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        } else if (requestCode == REQUEST_REG_CODE) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}



