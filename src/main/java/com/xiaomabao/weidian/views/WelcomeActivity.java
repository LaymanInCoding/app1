package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.presenters.CheckTokenPresenter;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.LogUtils;
import com.xiaomabao.weidian.util.SharedPreferencesUtil;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by de on 2017/3/9.
 */
public class WelcomeActivity extends AppCompatActivity {

    private UserService mService;
    private CheckTokenPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApiInfo();
        switchToPage();
    }

    public void initApiInfo() {
        mService = new UserService();
        mPresenter = new CheckTokenPresenter(this, mService);
    }

    private void switchToPage() {
        if (AppContext.getIsGuide(this)) {
            Observable.timer(1, TimeUnit.SECONDS, Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((Long) -> {
                        String token = AppContext.getToken(this);
                        LogUtils.loge(token);
                        mPresenter.checkToken(UserService.gen_refresh_token_params(token));
                    });
        } else {
            Observable.timer(1, TimeUnit.SECONDS, Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Long) -> {
                    startActivity(new Intent(this, GuideActivity.class));
                    finish();
                });
        }
    }

    public void jumpToShopIndexAndResetToken() {
        Observable<Long> observable = Observable.timer(1, TimeUnit.SECONDS, Schedulers.io());
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Long) -> {
                    SharedPreferencesUtil.remove(this, "token");
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
    }

    public void jumpToShopIndex() {
        Observable<Long> observable = Observable.timer(1, TimeUnit.SECONDS, Schedulers.io());
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Long) -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
    }
}
