package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.util.CommonUtil;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPasswordActivity extends AppCompatActivity {
    private int REQUEST_SMS_CODE = 0X01;

    @BindString(R.string.back_password_title) String toolbarTitle;
    @BindString(R.string.next_step) String toolbarRightText;

    @BindView(R.id.toolbar_title)
    TextView toolBarTitleTextView;
    @BindView(R.id.user_account)
    EditText userAccountEditText;
    @BindView(R.id.toolbar_right)
    TextView toolBarRightTextView;

    @OnClick(R.id.back) void back() {
        finish();
    }
    @OnClick(R.id.toolbar_right) void next() {
        String phone = userAccountEditText.getText().toString();
        if(!CommonUtil.isMobilePhone(phone)){
            XmbPopubWindow.showAlert(this, Const.MOBILE_REGEX_ERROR);
            return;
        }
        Intent intent = new Intent(ForgetPasswordActivity.this,SMSVerificationActivity.class);
        intent.putExtra("phone",phone);
        startActivityForResult(intent,REQUEST_SMS_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        displayTitle();
    }

    public void displayTitle(){
        toolBarTitleTextView.setText(toolbarTitle);
        toolBarRightTextView.setText(toolbarRightText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SMS_CODE) {
            if(resultCode==RESULT_OK) {
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
