package com.xiaomabao.weidian.presenters;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.GuideActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CheckTokenPresenter {
    GuideActivity mView;
    UserService mUser;

    public CheckTokenPresenter(GuideActivity view, UserService user) {
        mUser = user;
        mView = view;
    }

    public void checkToken(HashMap<String,String> hashMap) {
        mUser.getApi()
                .refresh_token(hashMap)
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
                        if(status.status == 0){
                            XmbPopubWindow.showAlert(mView,status.info);
                            mView.jumpToShopIndexAndResetToken();
                        }else{
                            mView.jumpToShopIndex();
                        }
                    }
                });
    }

}
