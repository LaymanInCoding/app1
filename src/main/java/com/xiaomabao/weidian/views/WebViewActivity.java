package com.xiaomabao.weidian.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import org.w3c.dom.Text;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by de on 2016/9/19.
 */
public class WebViewActivity extends Activity {
    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.brand_title)
    TextView mTextView;

    @OnClick(R.id.back)
    void back() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
        finish();
    }

    @OnClick(R.id.toolbar_right)
    void share() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("title", getIntent().getStringExtra("name"));
        hashMap.put("desc", getIntent().getStringExtra("share_desc"));
        hashMap.put("url", getIntent().getStringExtra("url"));
        XmbPopubWindow.showShopChooseWindow(this, hashMap, "Show_Goods_Share", "0");
//        XmbPopubWindow.showGoodsShare(this,hashMap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_brand_webview);
        ButterKnife.bind(this);
        mTextView.setText(intent.getStringExtra("name"));
        mWebView.loadUrl(intent.getStringExtra("url"));
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }
}
