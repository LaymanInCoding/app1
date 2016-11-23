package com.xiaomabao.weidian.presenters;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.services.ShopService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.ShopNameEditActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShopNameEditPresenter {
    ShopNameEditActivity mView;
    ShopService mService;

    public ShopNameEditPresenter(ShopNameEditActivity view, ShopService service) {
        mService = service;
        mView = view;
    }

    public void updateShopName(HashMap<String,String> hashMap) {
        mService.getApi()
                .update_name(hashMap)
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
                            AppContext.setShopName(mView,hashMap.get("shop_name"));
                            mView.finishView();
                        }
                    }
                });
    }

}
