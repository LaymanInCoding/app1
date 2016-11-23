package com.xiaomabao.weidian.presenters;

import android.app.Activity;
import android.util.Log;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.HKUpdateInfo;
import com.xiaomabao.weidian.models.UpdateInfo;
import com.xiaomabao.weidian.services.CommonService;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import java.util.HashMap;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommonPresenter {
    Activity mView;
    CommonService mService;

    public CommonPresenter(Activity view, CommonService service) {
        mView = view;
        mService = service;
    }

    //檢查App更新
    public void check_update(HashMap<String, String> hashMap) {
        mService.getApi()
                .getUpdateInfo(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateInfo>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("error", e.getMessage());
                        XmbPopubWindow.hideLoading();
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(UpdateInfo updateInfo) {
                        if (Float.valueOf(updateInfo.latest_version) > AppContext.getVersionName(mView)) {
                            XmbPopubWindow.showUpdateWindow(mView, updateInfo);
                        }
                    }
                });
    }

    //檢查搜索列表更新
    public void check_search_update() {
        mService.getApi()
                .getHotSearchInfo()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HKUpdateInfo>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("error", e.getMessage());
                        XmbPopubWindow.hideLoading();
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(HKUpdateInfo hkUpdateInfo) {
//                        if(hkUpdateInfo.data.version>AppContext.instance())
//                        mView.startService();
                    }
                });

    }
}
