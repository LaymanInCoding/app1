package com.xiaomabao.weidian.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import android.webkit.JavascriptInterface;
import android.widget.TextView;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.util.MD5Utils;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import org.w3c.dom.Text;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by de on 2016/9/19.
 */
public class WebViewActivity extends AppCompatActivity {
    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.brand_title)
    TextView mTextView;
    @BindView(R.id.loading_anim)
    View loadingView;
    private String param;
    private String order_sn;
    private String order_amount;
    private String body;

    HashMap<String, String> wx_params = new HashMap<>();
    IWXAPI api;

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private BroadcastReceiver wx_success_callback = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mWebView.loadUrl("http://weidian.xiaomabao.com/flow/pay_success/" + order_sn);
        }
    };

    @OnClick(R.id.back)
    void back() {
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wx_success_callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_brand_webview);
        ButterKnife.bind(this);
        IntentFilter wx_filter = new IntentFilter(Const.WX_CALLBACK);
        registerReceiver(wx_success_callback, wx_filter);
        if (intent.getStringExtra("url").contains("cart/confirm") ||
                intent.getStringExtra("share_desc").equals("")) {
            findViewById(R.id.toolbar_right).setVisibility(View.GONE);
        }
        mTextView.setText(intent.getStringExtra("name"));
        mWebView.loadUrl(intent.getStringExtra("url"));
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(this), "xmbapp");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String s, Bitmap bitmap) {
                loadingView.setVisibility(View.VISIBLE);
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
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
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
