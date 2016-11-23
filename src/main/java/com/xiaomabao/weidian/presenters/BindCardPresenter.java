package com.xiaomabao.weidian.presenters;

import android.util.Log;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.services.ProfitService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.BindCardActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BindCardPresenter {
    BindCardActivity mView;
    ProfitService mService;

    public BindCardPresenter(BindCardActivity view, ProfitService service) {
        mView = view;
        mService = service;
    }

    public void bind_card(HashMap<String,String> hashMap) {
        mService.getApi()
                .bind_card(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<com.xiaomabao.weidian.models.BindCard>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("error",e.getMessage());
                        XmbPopubWindow.hideLoading();
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(com.xiaomabao.weidian.models.BindCard response) {
                        if(response.status == 1) {
                            AppContext.setCardBindStatus(mView, Integer.parseInt(response.data.card_bind_status));
                            AppContext.setCardRealName(mView,response.data.real_name);
                            AppContext.setCardNo(mView,response.data.card_no);
                            AppContext.setDepositBank(mView,response.data.deposit_bank);
                            AppContext.setBranchBank(mView,response.data.branch_bank);
                            mView.bindCardCallback();
                        }else{
                            XmbPopubWindow.showAlert(mView, response.info);
                        }
                    }
                });
    }
}
