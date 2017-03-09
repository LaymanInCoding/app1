package com.xiaomabao.weidian.presenters;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Brand;
import com.xiaomabao.weidian.models.Category;
import com.xiaomabao.weidian.models.Goods;
import com.xiaomabao.weidian.services.GoodsService;
import com.xiaomabao.weidian.util.LogUtils;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.VipGoodsActivity;

import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VipGoodsPresenter {
    VipGoodsActivity mView;
    GoodsService mService;

    public VipGoodsPresenter(VipGoodsActivity view, GoodsService service) {
        mView = view;
        mService = service;
    }

    public void getBrandList(HashMap<String,String> hashMap) {
        mService.getApi()
                .brand_list(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Brand>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(List<Brand> brands) {
                        mView.handlerBrandRecycleView(brands);
                    }
                });
    }

    public void getVipGoodsList(HashMap<String,String> hashMap) {
        LogUtils.loge(hashMap.toString());
        mService.getApi()
                .vip_goods_list(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Goods>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(List<Goods> goods) {
                        mView.goodsResponse(goods);
                    }
                });
    }

    public void getCategoryList(HashMap<String,String> hashMap) {
        mService.getApi()
                .category_list(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Category>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(List<Category> categories) {
                        mView.handlerRecycleView(categories);
                    }
                });
    }
}
