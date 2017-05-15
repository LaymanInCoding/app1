package com.xiaomabao.weidian.presenters;

import android.util.Log;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.ShareInfo;
import com.xiaomabao.weidian.models.ShopAvatarStatus;
import com.xiaomabao.weidian.services.ShopService;
import com.xiaomabao.weidian.util.Device;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.ShopSettingActivity;
import com.xiaomabao.weidian.views.fragment.MineFragment;

import java.io.File;

import retrofit.mime.TypedFile;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShopSettingPresenter {
    ShopSettingActivity mView;
    ShopService mService;

    public ShopSettingPresenter(ShopSettingActivity view, ShopService service) {
        mService = service;
        mView = view;
    }

    public void updateAvatar(String token, File shop_avatar){
        TypedFile shop_avatar_typed = new TypedFile("image/*", shop_avatar);
        mService.getApi()
                .update_avatar(token,shop_avatar_typed, Device.get_deviceId(AppContext.appContext),Device.getDeviceName())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShopAvatarStatus>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.hideLoading();
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(ShopAvatarStatus status) {
                        XmbPopubWindow.showAlert(mView,status.info);
                        if(status.status == 1){
                            AppContext.setShopAvater(mView,status.path);
                        }
                    }
                });
    }

    public void updateShop(String token,String share_id,String shop_name,String shop_description, File shop_avatar,File shop_background,int position){
        TypedFile shop_avatar_typed = shop_avatar != null ? new TypedFile("image/*", shop_avatar) : null;
        TypedFile shop_background_typed = shop_background != null ? new TypedFile("image/*", shop_background) : null;
        mService.getApi()
                .update_shop_share(token,share_id,shop_name,shop_description,shop_avatar_typed,shop_background_typed,Device.get_deviceId(AppContext.appContext),Device.getDeviceName())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShareInfo>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.hideLoading();
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(ShareInfo shareInfo) {
                        XmbPopubWindow.showAlert(mView,"修改成功");
                        if (shareInfo.status ==1) {
                            AppContext.instance().updateShopShareInfoByIndex(position,shareInfo.data);
                            mView.setResult(mView.RESULT_OK);
                            mView.finishView();
                            MineFragment.animTime = 1;
                        }
                    }
                });

    }
    public void addShop(String token,String shop_name,String shop_description, File shop_avatar,File shop_background){
        TypedFile shop_avatar_typed = shop_avatar != null ? new TypedFile("image/*", shop_avatar) : null;
        TypedFile shop_background_typed = shop_background != null ? new TypedFile("image/*", shop_background) : null;
        mService.getApi()
                .add_shop_share(token,shop_name,shop_description,shop_avatar_typed,shop_background_typed,Device.get_deviceId(AppContext.appContext),Device.getDeviceName())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShareInfo>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        XmbPopubWindow.hideLoading();
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(ShareInfo shareInfo) {
                        XmbPopubWindow.showAlert(mView,"添加成功");
                        if (shareInfo.status ==1) {
                            AppContext.instance().addeShopShareInfoByIndex(shareInfo.data);
                            mView.setResult(mView.RESULT_OK);
                            mView.finishView();
                            MineFragment.animTime = 1;
                        }
                    }
                });

    }

    public void updateBackground(String token, File shop_background){
        TypedFile shop_background_typed = new TypedFile("image/*", shop_background);
        mService.getApi()
                .update_background(token,shop_background_typed, Device.get_deviceId(AppContext.appContext),Device.getDeviceName())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShopAvatarStatus>() {
                    @Override
                    public void onCompleted() {
                        XmbPopubWindow.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("data",e.getMessage());
                        XmbPopubWindow.hideLoading();
                        XmbPopubWindow.showAlert(mView, Const.NET_ERROR_MSG);
                    }

                    @Override
                    public void onNext(ShopAvatarStatus status) {
                        XmbPopubWindow.showAlert(mView,status.info);
                        if(status.status == 1){
                            AppContext.setShopBackground(mView,status.path);
                        }
                    }
                });
    }

}
