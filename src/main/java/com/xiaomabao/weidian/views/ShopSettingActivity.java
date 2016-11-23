package com.xiaomabao.weidian.views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.ShopBase;
import com.xiaomabao.weidian.presenters.ShopSettingPresenter;
import com.xiaomabao.weidian.services.ShopService;
import com.xiaomabao.weidian.util.BitmapUtils;
import com.xiaomabao.weidian.util.InputSoftUtil;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShopSettingActivity extends AppCompatActivity implements CropHandler {

    @BindString(R.string.shop_setting_title) String toolbarTitle;

    @BindView(R.id.toolbar_title)
    TextView toolBarTitleTextView;
    @BindView(R.id.save_shop)
    TextView save_shop_info;
    @BindView(R.id.shop_background)
    ImageView shopBackgroundImageView;
    @BindView(R.id.shop_avatar)
    ImageView shopAvatarImageView;
    @BindView(R.id.shop_name)
    EditText shopNameEditText;
    @BindView(R.id.shop_desc)
    EditText shopDescEditText;

    @OnClick(R.id.back) void back() {
        finish();
    }

    @OnClick(R.id.shop_avatar_container) void showPickAvatar() {
        InputSoftUtil.hideSoftInput(this,shopNameEditText);
        InputSoftUtil.hideSoftInput(this,shopDescEditText);
        current_img_type = 0;
        XmbPopubWindow.showPickPhotoWindow(this);
    }
    @OnClick(R.id.shop_background_container) void showPickBackground() {
        InputSoftUtil.hideSoftInput(this,shopNameEditText);
        InputSoftUtil.hideSoftInput(this,shopDescEditText);
        current_img_type = 1;
        XmbPopubWindow.showPickPhotoWindow(this);
    }
    @OnClick(R.id.save_shop) void saveShopInfo(){
        XmbPopubWindow.showTranparentLoading(this);
        String shop_name = shopNameEditText.getText().toString();
        String shop_description = shopDescEditText.getText().toString();
        if(shop_name.trim().equals("")){
            XmbPopubWindow.hideLoading();
            XmbPopubWindow.showAlert(this,"请输入店名");
            return ;
        }
        if (position != -1){
            mPresenter.updateShop(AppContext.getToken(this),shareId,shop_name,shop_description,shopAvatarFile,shopBackgroundFile,position);
        }else{
            mPresenter.addShop(AppContext.getToken(this),shop_name,shop_description,shopAvatarFile,shopBackgroundFile);
            setResult(RESULT_OK);
        }
    }

    private int position = -1;
    private String shopName = "";
    private String shareId = "";
    private String shopDescription = "";
    private String shopAvatarUrl = "";
    private String shopBackgroundUrl = "";
    private int current_img_type = 0;

    private File shopAvatarFile,shopBackgroundFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_setting);
        ButterKnife.bind(this);
        position = getIntent().getIntExtra("position",-1);
        if (position != -1){
            ShopBase.ShopBaseInfo.ShopShareInfo shareInfo = AppContext.instance().getShopShareInfoArrayList().get(position);
            shareId = shareInfo.id;
            shopName = shareInfo.shop_name;
            shopDescription = shareInfo.shop_description;
            shopAvatarUrl = shareInfo.shop_avatar;
            shopBackgroundUrl = shareInfo.shop_background;
        }
        displayTitle();
        initApi();
        setView();
    }

    ShopService mService;
    ShopSettingPresenter mPresenter;
    private void initApi(){
        mService = new ShopService();
        mPresenter = new ShopSettingPresenter(this,mService);
    }

    public void displayTitle(){
        toolBarTitleTextView.setText(toolbarTitle);
        save_shop_info.setVisibility(View.VISIBLE);
    }

    private void setView(){
        shopNameEditText.setText(shopName);
        if (shopName.length() != 0){
            shopNameEditText.setSelection(shopName.length());
        }
        shopDescEditText.setText(shopDescription);
        Glide.with(this)
                .load(shopAvatarUrl)
                .placeholder(R.mipmap.mitouxiang)
                .dontAnimate()
                .error(R.mipmap.mitouxiang)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(shopAvatarImageView);
        Glide.with(this)
                .load(shopBackgroundUrl)
                .dontAnimate()
                .placeholder(R.mipmap.index_top_bg)
                .error(R.mipmap.index_top_bg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(shopBackgroundImageView);
    }

    @Override
    public void onResume(){
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        CropHelper.handleResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (getCropParams() != null)
            CropHelper.clearCachedCropFile(getCropParams().uri);
        super.onDestroy();
    }

    @Override
    public void onPhotoCropped(Uri uri) {
        Bitmap bitmap = BitmapUtils.decodeUriAsBitmap(this,uri);
        if(current_img_type == 0){
            Glide.clear(shopAvatarImageView);
            shopAvatarImageView.setImageBitmap(bitmap);
            AppContext.setCurrentUpdateVersion(this);
            try {
                shopAvatarFile = new File(BitmapUtils.saveMyBitmap(bitmap,"shop_avatar",this));
            } catch (IOException e) {
                XmbPopubWindow.showAlert(this,"图片位置不存在~");
            }
        }else{
            Glide.clear(shopBackgroundImageView);
            shopBackgroundImageView.setImageBitmap(bitmap);
            try {
               shopBackgroundFile = new File(BitmapUtils.saveMyBitmap(bitmap,"shop_background",this));
            } catch (IOException e) {
                XmbPopubWindow.showAlert(this,"图片位置不存在~");
            }
        }
    }

    @Override
    public void onCropCancel() {
    }

    @Override
    public void onCropFailed(String message) {
        XmbPopubWindow.showAlert(this,"裁减图片失败~");
    }

    @Override
    public CropParams getCropParams() {
        CropParams mCropParams = new CropParams();
        if(current_img_type == 1){
            mCropParams.aspectX = 750;
            mCropParams.aspectY = 400;
            mCropParams.outputX = 750;
            mCropParams.outputY = 400;
        }else{
            mCropParams.aspectX = 1000;
            mCropParams.aspectY = 999;
            mCropParams.outputX = 200;
            mCropParams.outputY = 200;
        }
        return mCropParams;
    }

    public void finishView(){
        AppContext.setCurrentUpdateVersion(this);
        Observable<Long> observable = Observable.timer(2, TimeUnit.SECONDS, Schedulers.io());
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Long) -> {
                    finish();
                } );
    }

    @Override
    public Activity getContext() {
        return this;
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
