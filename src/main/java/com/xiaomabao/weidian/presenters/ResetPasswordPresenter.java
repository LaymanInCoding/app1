package com.xiaomabao.weidian.presenters;


import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.ResetPasswordActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ResetPasswordPresenter {
    ResetPasswordActivity mView;
    UserService mUser;

    public ResetPasswordPresenter(ResetPasswordActivity view, UserService user) {
        mUser = user;
        mView = view;
    }

    public void updatePassword(HashMap<String,String> hashMap) {
        mUser.getApi()
                .reset_password(hashMap)
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
                        if (status.status == 1){
                            mView.callback();
                        }
                    }
                });
    }

}
