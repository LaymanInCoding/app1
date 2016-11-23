package com.xiaomabao.weidian.presenters;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.UserLogin;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.AccountLoginActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AccountLoginPresenter {
    AccountLoginActivity loginView;
    UserService mUser;

    public AccountLoginPresenter(AccountLoginActivity view, UserService user) {
        mUser = user;
        loginView = view;
    }

    public void checkLogin(HashMap<String,String> hashMap) {
        mUser.getApi()
                .login(hashMap)
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
                            AppContext.setLoginPassword(loginView,"");
                            XmbPopubWindow.showAlert(loginView,userLogin.info);
                        }else{
                            AppContext.saveShopBaseInfo(loginView,userLogin.data);
                            loginView.jumpToShopIndex();
                        }
                    }
                });
    }

}
