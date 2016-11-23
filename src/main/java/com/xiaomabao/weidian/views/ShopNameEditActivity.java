package com.xiaomabao.weidian.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.presenters.ShopNameEditPresenter;
import com.xiaomabao.weidian.services.ShopService;
import com.xiaomabao.weidian.util.InputSoftUtil;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShopNameEditActivity extends AppCompatActivity {
    ShopService mService;
    ShopNameEditPresenter mPresenter;

    @BindString(R.string.shop_name) String toolbarTitle;
    @BindString(R.string.save_no_space) String toolbarRightTitle;

    @BindView(R.id.toolbar_title)
    TextView toolBarTitleTextView;
    @BindView(R.id.toolbar_right)
    TextView toolBarRightTextView;
    @BindView(R.id.shop_name)
    EditText shopNameEditText;
    @BindView(R.id.clear_text)
    ImageView clearImageView;

    @OnClick(R.id.back) void back() {
        finish();
    }
    @OnClick(R.id.clear_text)void clearText(){
        shopNameEditText.setText("");
    }
    @OnClick(R.id.toolbar_right)void editShopName(){
        String shopName = shopNameEditText.getText().toString();
        if(shopName.equals("")){
            XmbPopubWindow.showAlert(this, Const.SHOP_NAME_EMPTY);
        }else{
            XmbPopubWindow.showTranparentLoading(this);
            InputSoftUtil.hideSoftInput(this,shopNameEditText);
            mPresenter.updateShopName(ShopService.gen_update_name_params(AppContext.getToken(this),shopName));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopname_edit);
        ButterKnife.bind(this);
        displayTitle();
        bindEvent();
        initApi();
    }

    public void displayTitle(){
        toolBarTitleTextView.setText(toolbarTitle);
        toolBarRightTextView.setText(toolbarRightTitle);
        shopNameEditText.setText(AppContext.getShopName(this));
        if(!AppContext.getShopName(this).equals("")) {
            shopNameEditText.setSelection(AppContext.getShopName(this).length());
        }
    }

    public void initApi(){
        mService = new ShopService();
        mPresenter = new ShopNameEditPresenter(this,mService);
    }

    public void bindEvent(){
        shopNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 0){
                    clearImageView.setVisibility(View.GONE);
                }else{
                    clearImageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
