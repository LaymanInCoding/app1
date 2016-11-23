package com.xiaomabao.weidian.presenters;

import android.util.Log;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.HotKey;
import com.xiaomabao.weidian.models.SearchGood;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.services.GoodsService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.SearchActivity;

import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by de on 2016/10/9.
 */
public class SearchGoodsPresenter {
    SearchActivity mView;
    GoodsService mService;

    public SearchGoodsPresenter(SearchActivity view, GoodsService service) {
        mView = view;
        mService = service;
    }

    public void getSearchList(HashMap<String, String> hashMap) {
        mService.getApi()
                .goods_search(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchGood>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(SearchGood goods) {
                        mView.goodsResponse(goods);
                    }
                });
    }

    public void onSale(HashMap<String, String> hashMap, int position) {
        mService.getApi()
                .on_sale(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Status>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(Status Status) {
                        XmbPopubWindow.showAlert(mView, Status.info);
                        mView.refreshGoodsRecycleView(position);
                    }
                });
    }

    public void offSale(HashMap<String, String> hashMap, int position) {
        mService.getApi()
                .off_sale(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Status>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(Status Status) {
                        XmbPopubWindow.showAlert(mView, Status.info);
                        mView.refreshGoodsRecycleView(position);
                    }
                });
    }

    public void getHotSearch(HashMap<String, String> hashMap) {
        mService.getApi()
                .search_hot_words(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HotKey>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(HotKey hotKey) {
                        if (hotKey.status == 1) {
                            mView.hotKeyParse(hotKey);
                        } else {
                            XmbPopubWindow.showAlert(mView, hotKey.info);
                        }
                    }
                });
    }

}
