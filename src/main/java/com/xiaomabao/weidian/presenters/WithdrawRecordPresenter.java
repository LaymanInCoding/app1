package com.xiaomabao.weidian.presenters;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.WithdrawRecord;
import com.xiaomabao.weidian.services.ProfitService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.WithdrawRecordActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WithdrawRecordPresenter {
    WithdrawRecordActivity mView;
    ProfitService mService;

    public WithdrawRecordPresenter(WithdrawRecordActivity view, ProfitService service) {
        mView = view;
        mService = service;
    }

    public void withdraw_record(HashMap<String,String> hashMap) {
        mService.getApi()
                .withdraw_record(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WithdrawRecord>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(WithdrawRecord response) {
                        if(response.status == 1) {
                            mView.reloadPresented(response.total);
                            mView.reloadRecycleView(response.data);
                        }else{
                            XmbPopubWindow.showAlert(mView, response.info);
                        }
                    }
                });
    }
}
