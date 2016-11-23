package com.xiaomabao.weidian.presenters;

import android.util.Log;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.OrderDetail;
import com.xiaomabao.weidian.services.OrderService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.OrderDetailActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OrderDetailPresenter {

    private OrderService mService;
    private OrderDetailActivity mView;

    public OrderDetailPresenter(OrderDetailActivity view, OrderService service) {
        mService = service;
        mView = view;
    }

    public void order_detail(HashMap<String,String> hashMap) {
        mService.getApi()
                .order_detail(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderDetail>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("data",e.getMessage());
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(OrderDetail response) {
                        if(response.status == 0){
                            XmbPopubWindow.showAlert(mView,response.info);
                        }else{
                            mView.setView(response.data);
                        }
                    }
                });
    }

}
