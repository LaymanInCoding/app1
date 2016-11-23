package com.xiaomabao.weidian.presenters;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.ShopProfit;
import com.xiaomabao.weidian.services.ShopService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.ProfitActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfitPresenter {
    ProfitActivity mView;
    ShopService mService;

    public ProfitPresenter(ProfitActivity view, ShopService service) {
        mView = view;
        mService = service;
    }

    public void profit_info(HashMap<String,String> hashMap) {
        mService.getApi()
                .profit_info(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShopProfit>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(ShopProfit profit) {
                        if(profit.status == 1) {
                            mView.loadView(profit);
                        }else{
                            XmbPopubWindow.showAlert(mView, profit.info);
                        }
                    }
                });
    }
}
