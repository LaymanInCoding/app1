package com.xiaomabao.weidian.views.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.rx.RxBus;
import com.xiaomabao.weidian.rx.RxManager;
import com.xiaomabao.weidian.util.LogUtils;
import com.xiaomabao.weidian.views.WebViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by de on 2017/3/6.
 */
public class ShoppingCartFragment extends Fragment {
    private View mRootView;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.back)
    ImageView backImg;
    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.loading_anim)
    View loadingView;
    private Activity mContext;
    private RxManager mRxManager = new RxManager();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_my_cart, container, false);
        }
        ButterKnife.bind(this, mRootView);
        toolbar_title.setText("购物车");
        loadingView.setVisibility(View.VISIBLE);
        backImg.setVisibility(View.GONE);
        mRxManager.on(Const.REFRESH_CART, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    webView.reload();
                }
            }
        });
        return mRootView;
    }

    @Override
    public void onDestroy() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.loge(AppContext.getCartUrl(getContext()));
        if (!AppContext.getCartUrl(getContext()).equals("")) {
            webView.addJavascriptInterface(new JavaScriptObject(mContext), "xmbapp");
            webView.loadUrl(AppContext.getCartUrl(getContext()));
            webView.setWebViewClient(new WebViewClient() {
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String s) {
                    loadingView.setVisibility(View.GONE);
                }
            });
            WebSettings settings = webView.getSettings();
//            settings.setAllowFileAccess(true);
//            settings.setAppCacheEnabled(true);
//            settings.setDatabaseEnabled(true);
            settings.setJavaScriptEnabled(true);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
    }

    public class JavaScriptObject {
        public Context context;

        public JavaScriptObject(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void switchTab() {
            RxBus.getInstance().post(Const.JUMP_TO_FPAGE, true);
        }

        @JavascriptInterface
        public void openWebView(String url, String title) {
            Intent intent = new Intent();
            intent.putExtra("url", url);
            intent.putExtra("name", title);
            intent.setClass(mContext, WebViewActivity.class);
            startActivity(intent);
        }
    }
}

