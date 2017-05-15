package com.xiaomabao.weidian.presenters;

import android.util.Log;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.services.ShopService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.ShopListActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by de on 2016/9/28.
 */
public class ShopListPresenter {
    ShopListActivity mView;
    ShopService mService;

    public ShopListPresenter(ShopListActivity view, ShopService service) {
        mService = service;
        mView = view;
    }

    public void deleteShop(HashMap<String, String> hashMap, int position) {
        Log.e("data", hashMap.toString());
        mService.getApi()
                .delete_shop_share(hashMap)
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
                        if (status.status == 1) {
                            XmbPopubWindow.showAlert(mView, status.info);
                            mView.setDeleteCallback(position);
                        } else {
                            XmbPopubWindow.showAlert(mView, status.info);
                        }
                    }

                });

    }

    public void setDefaultShop(HashMap<String, String> hashMap) {
        Log.e("data", hashMap.toString());
        mService.getApi()
                .set_default_shop(hashMap)
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
                        if (status.status == 1) {
                            mView.setDefaultCallback(hashMap.get("share_id"));
                        }
                    }
                });

    }
}
