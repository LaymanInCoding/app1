package com.xiaomabao.weidian.views;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.tencent.smtt.sdk.WebChromeClient;

import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import android.widget.TextView;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.rx.RxBus;
import com.xiaomabao.weidian.util.LogUtils;
import com.xiaomabao.weidian.util.MD5Utils;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GoodsPreviewActivity extends AppCompatActivity {
    //    @BindString(R.string.goods_detail) String goods_detail;

    @BindView(R.id.loading_anim)
    View loadAnimView;
    @BindView(R.id.network_error)
    View networkErrorView;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitleTextView;

    private String param;
    private String order_sn;
    private String order_amount;
    private String body;
    private WebView webView;
    private String message;
    HashMap<String, String> wx_params = new HashMap<>();
    IWXAPI api;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                XmbPopubWindow.showAlert(GoodsPreviewActivity.this, message);
                Observable.timer(1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((Long) -> {
                            finish();
                        });
            }
        }
    };

    @OnClick(R.id.back)
    void back() {
        finish();
    }

    @OnClick(R.id.toolbar_right)
    void share() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("title", getIntent().getStringExtra("goods_name"));
        hashMap.put("desc", "【有人@你】你有一个分享尚未点击");
        hashMap.put("url", getIntent().getStringExtra("share_url"));
        XmbPopubWindow.showShopChooseWindow(GoodsPreviewActivity.this, hashMap, "Show_Goods_Share", "0");
//        XmbPopubWindow.showGoodsShare(GoodsPreviewActivity.this, hashMap);
    }

    @OnClick(R.id.reload_btn)
    void reload() {
        networkErrorView.setVisibility(View.GONE);
        initWebView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_preview);
        webView = (WebView) findViewById(R.id.webview);
        ButterKnife.bind(this);
        CookieManager manager = CookieManager.getInstance();
//        manager.removeAllCookie();
        manager.setAcceptCookie(true);
        CookieSyncManager.createInstance(this);
        initWebView();
        IntentFilter wx_filter = new IntentFilter(Const.WX_CALLBACK);
        registerReceiver(wx_success_callback, wx_filter);
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private BroadcastReceiver wx_success_callback = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            webView.loadUrl("http://weidian.xiaomabao.com/flow/pay_success/" + order_sn);
        }
    };

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    protected void initWebView() {
//        param = ("token=" + AppContext.getToken(this) + "&" + "device_id=" + AppContext.getDeviceId(this));
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(new MyJavaScriptInterface(this), "xmbapp");
        webView.loadUrl(getIntent().getStringExtra("buy_url"));
        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                toolbarTitleTextView.setText(title);
            }

        };
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loadAnimView.setVisibility(View.VISIBLE);
            }


            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {

                resend.sendToTarget();
            }

            public void onPageFinished(WebView view, String url) {
                LogUtils.loge("finish");
                loadAnimView.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT < 21) {
                    CookieSyncManager.getInstance().sync();
                } else {
                    CookieManager.getInstance().flush();
                }
//                CookieSyncManager.getInstance().sync();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wx_success_callback);
    }

    private class MyJavaScriptInterface {
        Context mContext;


        MyJavaScriptInterface(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public void call_pay(String sn, String amount, String bd) {

            order_sn = sn;
            order_amount = amount;
            body = bd;
            wx_params = gen_wx_pay_param();
            api = WXAPIFactory.createWXAPI(mContext, Const.WEIXIN_APP_ID);
//            Log.e("JSON INFO",order_sn+order_amount+body);

            new WXAsyncTask().execute();
        }

        @JavascriptInterface
        public void showMessage(String msg) {
            message = msg;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RxBus.getInstance().post(Const.REFRESH_CART, true);
                    mHandler.sendEmptyMessage(0x123);
                }
            }).start();
        }
    }

    private class WXAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            String entity = gen_wx_pay_param_xml();
            HttpURLConnection connection = null;
            try {
                //Create connection
                URL send_url = new URL(url);
                connection = (HttpURLConnection) send_url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/json");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setConnectTimeout(30000);
                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());

                wr.write(entity.getBytes("UTF-8"));
                wr.flush();
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }
                HashMap<String, String> hashMap = decodeXml(response.toString());
                rd.close();
                return hashMap.get("prepay_id");

            } catch (Exception e) {

                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return "";
        }

        @Override
        protected void onPreExecute() {
//            XmbPopubWindow.showTranparentLoading(GoodsPreviewActivity.this);
        }

        @Override
        protected void onPostExecute(String prepay_id) {
            gen_wx_pay_req(prepay_id);
        }

    }

    protected HashMap<String, String> gen_wx_pay_param() {
        String nonceStr = MD5Utils.getMessageDigest(String.valueOf(new Random().nextInt(10000)).getBytes());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("appid", Const.WEIXIN_APP_ID);
        hashMap.put("body", body);
        hashMap.put("mch_id", Const.WEIXIN_MCH_ID);
        hashMap.put("nonce_str", nonceStr);
        hashMap.put("notify_url", "http://weidian.xiaomabao.com/payment/wx_app_notify");
        hashMap.put("out_trade_no", order_sn);
        hashMap.put("total_fee", (int) (Double.parseDouble(order_amount) * 100) + "");
        hashMap.put("trade_type", "APP");
        hashMap.put("sign", getWXPackageSign(hashMap));
        return hashMap;
    }

    private String getWXPackageSign(HashMap<String, String> hashMap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("appid=" + hashMap.get("appid") + "&");
        stringBuilder.append("body=" + hashMap.get("body") + "&");
        stringBuilder.append("mch_id=" + hashMap.get("mch_id") + "&");
        stringBuilder.append("nonce_str=" + hashMap.get("nonce_str") + "&");
        stringBuilder.append("notify_url=" + hashMap.get("notify_url") + "&");
        stringBuilder.append("out_trade_no=" + hashMap.get("out_trade_no") + "&");
        stringBuilder.append("total_fee=" + hashMap.get("total_fee") + "&");
        stringBuilder.append("trade_type=" + hashMap.get("trade_type") + "&");
        stringBuilder.append("key=" + Const.WEIXIN_API_KEY);
        return MD5Utils.getMessageDigest(stringBuilder.toString().getBytes()).toUpperCase();
    }

    protected String gen_wx_pay_param_xml() {
        return toXml(wx_params);
    }

    private String toXml(HashMap<String, String> hashMap) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iter = hashMap.entrySet().iterator();
        Log.e("HASH_MAP", hashMap.toString());
        stringBuilder.append("<xml>");
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();
            String val = entry.getValue().toString();
            stringBuilder.append("<" + key + ">");
            stringBuilder.append(val);
            stringBuilder.append("</" + key + ">");
        }
        stringBuilder.append("</xml>");
        return stringBuilder.toString();
    }

    protected void gen_wx_pay_req(String prepay_id) {
        String nonceStr = MD5Utils.getMessageDigest(String.valueOf(new Random().nextInt(10000)).getBytes());
        PayReq request = new PayReq();
        request.appId = wx_params.get("appid");
        request.partnerId = wx_params.get("mch_id");
        request.prepayId = prepay_id;
        request.packageValue = "Sign=WXPay";
        request.nonceStr = nonceStr;
        request.timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        request.sign = getAppPackageSign(request.appId, nonceStr, request.packageValue, request.partnerId, request.prepayId, request.timeStamp);
        api.sendReq(request);
    }

    private String getAppPackageSign(String appid, String nonce_str, String packageVal, String partnerId, String prepayId, String timeStamp) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("appid=" + appid + "&");
        stringBuilder.append("noncestr=" + nonce_str + "&");
        stringBuilder.append("package=" + packageVal + "&");
        stringBuilder.append("partnerid=" + partnerId + "&");
        stringBuilder.append("prepayid=" + prepayId + "&");
        stringBuilder.append("timestamp=" + timeStamp + "&");
        stringBuilder.append("key=" + Const.WEIXIN_API_KEY);
        return MD5Utils.getMessageDigest(stringBuilder.toString().getBytes()).toUpperCase();
    }

    public HashMap<String, String> decodeXml(String content) {

        try {
            HashMap<String, String> xml = new HashMap<>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if ("xml".equals(nodeName) == false) {
                            //实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.getMessage());
        }
        return null;

    }


}
