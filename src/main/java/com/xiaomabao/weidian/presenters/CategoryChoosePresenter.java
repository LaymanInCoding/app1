package com.xiaomabao.weidian.presenters;

import android.util.Log;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Brand;
import com.xiaomabao.weidian.models.Category;
import com.xiaomabao.weidian.models.Goods;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.services.GoodsService;
import com.xiaomabao.weidian.util.LogUtils;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.CategoryChooseActivity;
import com.xiaomabao.weidian.views.fragment.CategoryChooseFragment;

import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CategoryChoosePresenter {
    CategoryChooseFragment mView;
    GoodsService mService;

    public CategoryChoosePresenter(CategoryChooseFragment view, GoodsService service) {
        mService = service;
        mView = view;
    }

    public void getBrandList(HashMap<String, String> hashMap) {
        LogUtils.loge(hashMap.toString());
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
                        XmbPopubWindow.showAlert(mView.getContext(), Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(List<Brand> brands) {
                        mView.handlerBrandRecycleView(brands);
                    }
                });
    }

    public void getCategoryList(HashMap<String, String> hashMap) {
        LogUtils.loge(hashMap.toString());
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
                        XmbPopubWindow.showAlert(mView.getContext(), Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(List<Category> categories) {
                        mView.handlerRecycleView(categories);
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
                        XmbPopubWindow.showAlert(mView.getContext(), Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(Status Status) {
                        XmbPopubWindow.showAlert(mView.getContext(), Status.info);
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
                        XmbPopubWindow.showAlert(mView.getContext(), Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(Status Status) {
                        XmbPopubWindow.showAlert(mView.getContext(), Status.info);
                        mView.refreshGoodsRecycleView(position);
                    }
                });
    }

    public void getGoodsList(HashMap<String, String> hashMap) {
        if (AppContext.isLogin()) {
            LogUtils.loge(hashMap.toString());
            mService.getApi()
                    .goods_list(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Goods>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            XmbPopubWindow.showAlert(mView.getContext(), Const.NET_ERROR_MSG);
                        }

                        @Override
                        public void onNext(List<Goods> goods) {
                            mView.goodsResponse(goods);
                        }
                    });
        } else {
            LogUtils.loge(hashMap.toString());
            mService.getApi()
                    .goods_list_v2(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Goods>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            XmbPopubWindow.showAlert(mView.getContext(), Const.NET_ERROR_MSG);
                        }

                        @Override
                        public void onNext(List<Goods> goods) {
                            mView.goodsResponse(goods);
                        }
                    });
        }
    }

}
