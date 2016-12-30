package com.xiaomabao.weidian.presenters;

import android.app.Service;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Bean;
import com.xiaomabao.weidian.models.BeanStatus;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.services.BeanService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.MyShareBeanActivity;
import com.xiaomabao.weidian.views.SendBeanActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by de on 2016/12/21.
 */
public class BeanPresenter {
    SendBeanActivity mView1;
    MyShareBeanActivity mView;
    BeanService mService;

    public BeanPresenter(MyShareBeanActivity view, BeanService service) {
        mView = view;
        mService = service;
    }

    public BeanPresenter(SendBeanActivity view1, BeanService service) {
        mView1 = view1;
        mService = service;
    }

    public void get_bean_info(HashMap<String, String> hashMap) {
        mService.getApi()
                .getMyBean(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(Bean bean) {
                        mView.recordsResponse(bean);
                    }
                });
    }

    public void send_bean(HashMap<String, String> hashMap) {
        mService.getApi()
                .sendBean(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BeanStatus>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(BeanStatus status) {
                        Logger.e(status.toString());
                        if (status.code == 200) {
                            mView1.callback(status.info);
                        } else
                            mView1.callbackError(status.info);
                    }
                });
    }
}
