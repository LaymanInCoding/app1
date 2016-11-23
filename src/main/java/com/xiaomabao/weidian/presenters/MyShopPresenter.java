package com.xiaomabao.weidian.presenters;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.ShopGoods;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.services.GoodsService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.MyShopActivity;

import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyShopPresenter {
    MyShopActivity mView;
    GoodsService mService;

    public MyShopPresenter(MyShopActivity view, GoodsService service) {
        mService = service;
        mView = view;
    }

    public void shop_goods(HashMap<String,String> hashMap) {
        mService.getApi()
                .shop_goods(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ShopGoods>>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(List<ShopGoods> shopGoodsList) {
                        mView.notifyAddRecycleView(shopGoodsList);
                    }
                });
    }

    public void off_sale(HashMap<String,String> hashMap,int position) {
        mService.getApi()
                .off_sale(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Status>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(Status status) {
                        XmbPopubWindow.showAlert(mView,status.info);
                        if (status.status == 1){
                            mView.notifyDelRecycleView(position);
                        }
                    }
                });
    }

}
