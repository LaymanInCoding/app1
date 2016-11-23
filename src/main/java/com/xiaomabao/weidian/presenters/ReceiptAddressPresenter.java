package com.xiaomabao.weidian.presenters;

import android.util.Log;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.ApplyShopCode;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.ReceiptAddressActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReceiptAddressPresenter {
    ReceiptAddressActivity mView;
    UserService mService;

    public ReceiptAddressPresenter(ReceiptAddressActivity view, UserService service) {
        mView = view;
        mService = service;
    }

    public void apply_shop_code(HashMap<String, String> hashMap) {
        mService.getApi()
                .apply_shop_code(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ApplyShopCode>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(ApplyShopCode response) {
                        Log.e("data", response.toString());
                        if (response.status == 1) {
                            mView.jumpToPay(response.data);
                        } else {
                            XmbPopubWindow.showAlert(mView, response.info);
                        }
                    }
                });
    }

    public void sendCode(HashMap<String, String> hashMap) {
        mService.getApi()
                .send_code(hashMap)
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
                        XmbPopubWindow.showAlert(mView, status.info);
                    }
                });
    }
}
