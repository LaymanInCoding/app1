package com.xiaomabao.weidian.presenters;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.ShopCode;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.PayResultActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PayResultPresenter {

    public PayResultActivity mView;
    public UserService mService;

    public PayResultPresenter(PayResultActivity view, UserService service) {
        mView = view;
        mService = service;
    }

    public void get_shop_code(HashMap<String,String> hashMap) {
        mService.getApi()
                .get_shop_code(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShopCode>() {
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
                    public void onNext(ShopCode response) {
                        if(response.status == 0){
                            XmbPopubWindow.showAlert(mView, response.info);
                        }else{
                            mView.setView(response.data.code);
                        }
                    }
                });
    }

}
