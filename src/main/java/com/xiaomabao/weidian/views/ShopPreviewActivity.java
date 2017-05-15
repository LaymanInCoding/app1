package com.xiaomabao.weidian.views;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopPreviewActivity extends AppCompatActivity {

    @BindView(R.id.webview)WebView webView;
    @BindView(R.id.loading_anim)View loadAnimView;
    @BindView(R.id.network_error)View networkErrorView;

    @OnClick(R.id.back)void back(){
        finish();
    }
    @OnClick(R.id.reload_btn)void reload(){
        networkErrorView.setVisibility(View.GONE);
        initWebView();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_preview);
        ButterKnife.bind(this);
        initWebView();
    }

    protected void initWebView(){
        LogUtils.loge(AppContext.getShopPreviewUrl(this));
        webView.loadUrl(AppContext.getShopPreviewUrl(this));
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                loadAnimView.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String s) {
                return true;

            }
            @Override

            public void onPageFinished(WebView view, String url) {
                loadAnimView.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebViewClient.a a) {
                super.onReceivedError(view, request, a);
                loadAnimView.setVisibility(View.GONE);
                networkErrorView.setVisibility(View.VISIBLE);
            }

        });
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
