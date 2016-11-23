package com.xiaomabao.weidian.presenters;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.services.ProfitService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.ApplyProfitActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ApplyProfitPresenter {
    ApplyProfitActivity mView;
    ProfitService mService;

    public ApplyProfitPresenter(ApplyProfitActivity view, ProfitService service) {
        mView = view;
        mService = service;
    }

    public void apply_withdraw(HashMap<String,String> hashMap) {
        mService.getApi()
                .apply_withdraw(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Status>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(Status response) {
                        if(response.status == 1) {
                            mView.withdrawSuccessResponse(hashMap.get("apply_money"));
                        }else{
                            XmbPopubWindow.showAlert(mView, response.info);
                        }
                    }
                });
    }
}
