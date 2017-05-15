package com.xiaomabao.weidian.presenters;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Goods;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.models.SubCategory;
import com.xiaomabao.weidian.services.GoodsService;
import com.xiaomabao.weidian.util.LogUtils;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.SubCatrgoryActivity;

import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ming on 2017/4/25.
 */
public class SubCategoryPresenter {
    private SubCatrgoryActivity mView;
    private GoodsService mService;

    public SubCategoryPresenter(SubCatrgoryActivity view, GoodsService service) {
        mService = service;
        mView = view;
    }

    public void getGoodsList(HashMap<String, String> hashMap) {
        if (AppContext.isLogin()) {
            LogUtils.loge(hashMap.toString());
            mService.getApi()
                    .goods_list2(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SubCategory>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                        }

                        @Override
                        public void onNext(SubCategory goods) {
                            mView.goodsResponse(goods);
                        }
                    });
        } else {
            LogUtils.loge(hashMap.toString());
            mService.getApi()
                    .goods_list_vv2(hashMap)
                    .subscribeOn(Schedulers.io())
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
//                            mView.goodsResponse(goods);
                        }
                    });
        }
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

    public void getVipGoodsList(HashMap<String,String> hashMap) {
        LogUtils.loge(hashMap.toString());
        mService.getApi()
                .vip_goods_list2(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SubCategory>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(SubCategory goods) {
                        mView.goodsResponse(goods);
                    }
                });
    }

}
