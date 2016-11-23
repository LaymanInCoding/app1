package com.xiaomabao.weidian.presenters;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.services.ShopService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.ShopDescriptionEditActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShopDescriptionEditPresenter {
    ShopDescriptionEditActivity mView;
    ShopService mService;

    public ShopDescriptionEditPresenter(ShopDescriptionEditActivity view, ShopService service) {
        mService = service;
        mView = view;
    }

    public void updateShopDescription(HashMap<String,String> hashMap) {
        mService.getApi()
                .update_description(hashMap)
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
                            AppContext.setShopDescription(mView,hashMap.get("shop_description"));
                            mView.finishView();
                        }
                    }
                });
    }

}
