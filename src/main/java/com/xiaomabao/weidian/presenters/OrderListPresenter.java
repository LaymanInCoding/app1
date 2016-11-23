package com.xiaomabao.weidian.presenters;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.OrderList;
import com.xiaomabao.weidian.models.OrderType;
import com.xiaomabao.weidian.services.OrderService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.OrderListActivity;

import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OrderListPresenter {

    private OrderService mService;
    private OrderListActivity mView;

    public OrderListPresenter(OrderListActivity view, OrderService service) {
        mService = service;
        mView = view;
    }

    public void order_types(HashMap<String,String> hashMap) {
        mService.getApi()
                .order_type(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<OrderType>>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(List<OrderType> orderTypeList) {
                        mView.loadOrderTypes(orderTypeList);
                    }
                });
    }

    public void order_list(HashMap<String,String> hashMap) {
        mService.getApi()
                .order_list(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderList>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(OrderList response) {
                        if (response.status == 1){
                            mView.loadOrderList(response.data);
                        }else{
                            XmbPopubWindow.showAlert(mView, response.info);
                        }
                    }
                });
    }

}
