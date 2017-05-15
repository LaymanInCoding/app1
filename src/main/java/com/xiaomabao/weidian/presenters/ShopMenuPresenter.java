package com.xiaomabao.weidian.presenters;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.ShopBase;
import com.xiaomabao.weidian.models.ShopStatistics;
import com.xiaomabao.weidian.services.ShopService;
import com.xiaomabao.weidian.util.LogUtils;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.ShopMenuActivity;
import com.xiaomabao.weidian.views.fragment.MineFragment;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShopMenuPresenter {
    MineFragment mView;
    ShopService mService;

    public ShopMenuPresenter(MineFragment view, ShopService service) {
        mService = service;
        mView = view;
    }

    public void getShopBaseInfo(HashMap<String, String> hashMap) {
        Log.e("HashMap", hashMap.toString());
        mService.getApi()
                .base_info(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShopBase>() {
                               @Override
                               public void onCompleted() {
                                   XmbPopubWindow.hideLoading();
                               }

                               @Override
                               public void onError(Throwable e) {
                                   XmbPopubWindow.hideLoading();
                               }

                               @Override
                               public void onNext(ShopBase shopMenu) {
                                   if (shopMenu.status == 0) {
                                       LogUtils.loge("setView");
//                                       XmbPopubWindow.showAlert(mView.getContext(), shopMenu.info);
                                       mView.displayShopBaseInfo();
                                   } else {
                                       AppContext.instance().setShopShareInfoArrayList(shopMenu.data.shop_share_info);
                                       AppContext.resetShopInfo(mView.getContext(), shopMenu);
                                       mView.displayShopBaseInfo();
                                   }
                               }
                           }

                );
    }

    public void getShopProfitInfo(HashMap<String, String> hashMap) {
        mService.getApi()
                .statistics_info(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShopStatistics>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.hideLoading();
                        XmbPopubWindow.showAlert(mView.getContext(), Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(ShopStatistics shopStatistics) {
                        if (shopStatistics.status == 0) {
                            mView.clearShopProfit();
                        } else {
                            mView.displayShopProfit(shopStatistics.data);
                        }
                    }
                });
    }

}
