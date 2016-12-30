package com.xiaomabao.weidian.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.services.BeanService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by de on 2016/12/20.
 */
public class ShareBeanHelpActivity extends AppCompatActivity {

    @BindView(R.id.webview)
    WebView mWebview;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bean_help);
        ButterKnife.bind(this);
        mToolbarTitle.setText("使用帮助");
        mWebview.loadUrl(BeanService.Common_Base_Url + "common/bean_agreement");
    }

    @OnClick(R.id.back)
    public void onClick() {
        finish();
    }
}
