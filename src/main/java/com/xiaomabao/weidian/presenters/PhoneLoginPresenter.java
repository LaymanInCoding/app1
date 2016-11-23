package com.xiaomabao.weidian.presenters;


import android.util.Log;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.models.UserLogin;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.PhoneLoginActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PhoneLoginPresenter {
    PhoneLoginActivity loginView;
    UserService mUser;

    public PhoneLoginPresenter(PhoneLoginActivity view, UserService user) {
        mUser = user;
        loginView = view;
    }

    public void checkLogin(HashMap<String,String> hashMap) {
        mUser.getApi()
                .login_by_code(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserLogin>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.hideLoading();
                        XmbPopubWindow.showAlert(loginView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(UserLogin userLogin) {
                        if(userLogin.status == 0){
                            XmbPopubWindow.showAlert(loginView,userLogin.info);
                        }else{
                            AppContext.saveShopBaseInfo(loginView,userLogin.data);
                            loginView.jumpToShopIndex();
                        }
                    }
                });
    }

    public void sendCode(HashMap<String,String> hashMap) {
        mUser.getApi()
                .send_login_code(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Status>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.hideLoading();
                        XmbPopubWindow.showAlert(loginView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(Status status) {
                        XmbPopubWindow.showAlert(loginView,status.info);
                    }
                });
    }

}
