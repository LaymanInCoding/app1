package com.xiaomabao.weidian.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.rx.RxBus;
import com.xiaomabao.weidian.util.Device;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.yxp.permission.util.lib.PermissionUtil;
import com.yxp.permission.util.lib.callback.PermissionResultCallBack;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {
    @BindString(R.string.setting_title)
    String toolbarTitle;

    @BindView(R.id.toolbar_title)
    TextView toolBarTitleTextView;
    @BindView(R.id.current_version)
    TextView currentVersionTextView;

    @OnClick(R.id.back)
    void back() {
        finish();
    }

    @OnClick(R.id.about_weekday_go)
    void jumpToAbout() {
        startActivity(new Intent(SettingActivity.this, AboutActivity.class));
    }

    @OnClick(R.id.clear_cache_container)
    void clear_cache() {
        new Thread(() -> Glide.get(SettingActivity.this).clearDiskCache()).start();
        XmbPopubWindow.showAlert(this, Const.CLEAR_CACHE_SUCCESS);
    }

    @OnClick(R.id.logout_container)
    void logout() {
        AppContext.clearLoginInfo(this);
        setResult(RESULT_OK);
        removeCookie(this);
        RxBus.getInstance().post(Const.LOG_IN_OUT, true);
        finish();
    }

    @OnClick(R.id.tel_container)
    void tel() {
        PermissionUtil.getInstance().request(new String[]{Manifest.permission.CALL_PHONE},
                new PermissionResultCallBack() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + "01085170751");
                        intent.setData(data);
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionGranted(String... strings) {

                    }

                    @Override
                    public void onPermissionDenied(String... strings) {

                    }

                    @Override
                    public void onRationalShow(String... strings) {

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        displayTitle();
    }

    public void displayTitle() {
        toolBarTitleTextView.setText(toolbarTitle);
        currentVersionTextView.setText(Device.getVersion(this));
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void removeCookie(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }

}
