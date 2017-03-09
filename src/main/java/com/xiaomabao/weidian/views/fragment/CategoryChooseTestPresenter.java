package com.xiaomabao.weidian.views.fragment;

import android.util.Log;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Brand;
import com.xiaomabao.weidian.models.Category;
import com.xiaomabao.weidian.models.Goods;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.services.GoodsService;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by de on 2017/3/6.
 */
public class CategoryChooseTestPresenter {
    CategoryTestFragment mView;
    GoodsService mService;

    public CategoryChooseTestPresenter(CategoryTestFragment view, GoodsService service) {
        mService = service;
        mView = view;
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
                        XmbPopubWindow.showAlert(mView.getContext(), Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(List<Category> categories) {
                        mView.handleTabTitle(categories);
                    }
                });
    }
}
