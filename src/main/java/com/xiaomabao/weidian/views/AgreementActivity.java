package com.xiaomabao.weidian.views;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.R;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgreementActivity extends AppCompatActivity{
    @BindString(R.string.agreement) String agreement;
    @BindView(R.id.webview)WebView webView;
    @BindView(R.id.loading_anim)View loadAnimView;
    @BindView(R.id.network_error)View networkErrorView;
    @BindView(R.id.toolbar_title)TextView toolbarTitleTextView;

    @OnClick(R.id.back)void back(){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        ButterKnife.bind(this);
        initWebView();
        toolbarTitleTextView.setText(agreement);
    }

    protected void initWebView(){
        webView.loadUrl("http://vapi.xiaomabao.com/common/agreement");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                loadAnimView.setVisibility(View.VISIBLE);
            }

            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                loadAnimView.setVisibility(View.GONE);
            }

            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
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
