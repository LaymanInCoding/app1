package com.xiaomabao.weidian.presenters;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Goods;
import com.xiaomabao.weidian.services.GoodsService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.BrandTopicActivity;

import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by de on 2016/9/7.
 */
public class BrandGoodsPresenter {
    BrandTopicActivity mView;
    GoodsService mService;

    public BrandGoodsPresenter(BrandTopicActivity view, GoodsService service) {
        mView = view;
        mService = service;
    }

    public void getBrandGoodsList(HashMap<String,String> hashMap) {
        mService.getApi()
                .brand_goods_list(hashMap)
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

}
