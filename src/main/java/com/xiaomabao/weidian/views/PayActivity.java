package com.xiaomabao.weidian.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.PayResult;
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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayActivity extends AppCompatActivity {
    @BindString(R.string.pay_btn_hint) String toolbarTitle;
    @BindView(R.id.toolbar_title)
    TextView toolBarTitleTextView;
    @BindView(R.id.order_amount)
    TextView order_amount;
    @BindView(R.id.zfb_checked_img)
    ImageView zfb_checked_img;
    @BindView(R.id.wx_checked_img)
    ImageView wx_checked_img;

    IWXAPI api;
    public static final int SDK_PAY_FLAG = 0x01;
    int pay_type = 1;
    String mobile,order_sn;
    HashMap<String,String> wx_params = new HashMap<>();
    @OnClick(R.id.back) void back() {
        finish();
    }
    @OnClick(R.id.confirm_btn) void pay() {
        if (pay_type == 1){
            final String payInfo = getIntent().getStringExtra("ali_sign");
            Runnable payRunnable = ()-> {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            };
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        }else{
            new WXAsyncTask().execute();
        }
    }
    @OnClick(R.id.wx_container) void wx_choose(){
        pay_type = 2;
        zfb_checked_img.setImageResource(R.mipmap.pay_normal);
        wx_checked_img.setImageResource(R.mipmap.pay_selected);
    }
    @OnClick(R.id.zfb_container) void zfb_choose(){
        pay_type = 1;
        zfb_checked_img.setImageResource(R.mipmap.pay_selected);
        wx_checked_img.setImageResource(R.mipmap.pay_normal);
    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Intent intent = new Intent(PayActivity.this,PayResultActivity.class);
                        intent.putExtra("mobile",mobile);
                        intent.putExtra("order_sn",order_sn);
                        startActivity(intent);
                        finish();
                    } else {
                        XmbPopubWindow.showAlert(PayActivity.this,"支付异常,请稍后重试~");
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wx_success_callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);

        api = WXAPIFactory.createWXAPI(this, Const.WEIXIN_APP_ID);
        toolBarTitleTextView.setText(toolbarTitle);
        order_amount.setText(getIntent().getStringExtra("order_amount_formatted"));
        wx_params = gen_wx_pay_param();
        IntentFilter wx_filter = new IntentFilter(Const.WX_CALLBACK);
        registerReceiver(wx_success_callback,wx_filter);
        mobile = getIntent().getStringExtra("mobile");
        order_sn = getIntent().getStringExtra("order_sn");
    }

    protected BroadcastReceiver wx_success_callback = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intent2 = new Intent(PayActivity.this,PayResultActivity.class);
            intent2.putExtra("mobile",mobile);
            intent2.putExtra("order_sn",order_sn);
            startActivity(intent2);
            finish();
        }
    };

    protected String gen_wx_pay_param_xml(){
        return toXml(wx_params);
    }

    protected HashMap<String,String> gen_wx_pay_param(){
        String nonceStr = MD5Utils.getMessageDigest(String.valueOf(new Random().nextInt(10000)).getBytes());
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("appid",Const.WEIXIN_APP_ID);
        hashMap.put("body","微店大礼包");
        hashMap.put("mch_id",Const.WEIXIN_MCH_ID);
        hashMap.put("nonce_str",nonceStr);
        hashMap.put("notify_url","http://vapi.xiaomabao.com/payment/wechat_notify");
        hashMap.put("out_trade_no",getIntent().getStringExtra("order_sn"));
        hashMap.put("total_fee",(int)(Double.parseDouble(getIntent().getStringExtra("order_amount")) * 100) + "");
        hashMap.put("trade_type","APP");
        hashMap.put("sign",getWXPackageSign(hashMap));
        return hashMap;
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
                connection = (HttpURLConnection)send_url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/json");
                connection.setUseCaches (false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setConnectTimeout(30000);
                //Send request
                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream ());

                wr.write(entity.getBytes("UTF-8"));
                wr.flush ();
                wr.close ();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                }
                HashMap<String,String> hashMap = decodeXml(response.toString());
                rd.close();
                return hashMap.get("prepay_id");

            } catch (Exception e) {
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
            }
            return "";
        }

        @Override
        protected void onPreExecute(){
            XmbPopubWindow.showTranparentLoading(PayActivity.this);
        }

        @Override
        protected void onPostExecute(String prepay_id){
            gen_wx_pay_req(prepay_id);
        }

    }

    protected void gen_wx_pay_req(String prepay_id){
        String nonceStr = MD5Utils.getMessageDigest(String.valueOf(new Random().nextInt(10000)).getBytes());
        PayReq request = new PayReq();
        request.appId = wx_params.get("appid");
        request.partnerId = wx_params.get("mch_id");
        request.prepayId= prepay_id;
        request.packageValue = "Sign=WXPay";
        request.nonceStr=nonceStr;
        request.timeStamp= String.valueOf(System.currentTimeMillis()/1000);
        request.sign= getAppPackageSign(request.appId,nonceStr,request.packageValue,request.partnerId,request.prepayId,request.timeStamp);
        api.sendReq(request);
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

    private String toXml(HashMap<String,String> hashMap){
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iter = hashMap.entrySet().iterator();
        stringBuilder.append("<xml>");
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();
            String val = entry.getValue().toString();
            stringBuilder.append("<"+key+">");
            stringBuilder.append(val);
            stringBuilder.append("</"+key+">");
        }
        stringBuilder.append("</xml>");
        return stringBuilder.toString();
    }

    private String getWXPackageSign(HashMap<String,String> hashMap){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("appid="+hashMap.get("appid")+"&");
        stringBuilder.append("body="+hashMap.get("body")+"&");
        stringBuilder.append("mch_id="+hashMap.get("mch_id")+"&");
        stringBuilder.append("nonce_str="+hashMap.get("nonce_str")+"&");
        stringBuilder.append("notify_url="+hashMap.get("notify_url")+"&");
        stringBuilder.append("out_trade_no="+hashMap.get("out_trade_no")+"&");
        stringBuilder.append("total_fee="+hashMap.get("total_fee")+"&");
        stringBuilder.append("trade_type="+hashMap.get("trade_type")+"&");
        stringBuilder.append("key="+Const.WEIXIN_API_KEY);
        return MD5Utils.getMessageDigest(stringBuilder.toString().getBytes()).toUpperCase();
    }

    private String getAppPackageSign(String appid,String nonce_str,String packageVal,String partnerId,String prepayId,String timeStamp){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("appid="+appid+"&");
        stringBuilder.append("noncestr="+nonce_str+"&");
        stringBuilder.append("package="+packageVal+"&");
        stringBuilder.append("partnerid="+partnerId+"&");
        stringBuilder.append("prepayid="+prepayId+"&");
        stringBuilder.append("timestamp="+timeStamp+"&");
        stringBuilder.append("key="+Const.WEIXIN_API_KEY);
        return MD5Utils.getMessageDigest(stringBuilder.toString().getBytes()).toUpperCase();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        XmbPopubWindow.hideLoading();
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
