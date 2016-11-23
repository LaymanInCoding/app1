package com.xiaomabao.weidian.presenters;


import android.util.Log;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.SMSVerificationActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SMSVerificationPresenter {
    SMSVerificationActivity mView;
    UserService mUser;

    public SMSVerificationPresenter(SMSVerificationActivity view, UserService user) {
        mUser = user;
        mView = view;
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
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(Status status) {
                        XmbPopubWindow.showAlert(mView,status.info);
                    }
                });
    }

    public void validCode(HashMap<String,String> hashMap) {
        mUser.getApi()
                .valid_code(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Status>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("data",e.getMessage());
                        XmbPopubWindow.hideLoading();
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(Status status) {
                        if (status.status==0) {
                            XmbPopubWindow.showAlert(mView, status.info);
                        }else{
                            mView.jumpToResetPasswordActivity();
                        }
                    }
                });
    }

}
