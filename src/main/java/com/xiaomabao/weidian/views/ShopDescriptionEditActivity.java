package com.xiaomabao.weidian.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.presenters.ShopDescriptionEditPresenter;
import com.xiaomabao.weidian.services.ShopService;
import com.xiaomabao.weidian.util.InputSoftUtil;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopDescriptionEditActivity extends AppCompatActivity {
    @BindString(R.string.shop_description) String toolbarTitle;
    @BindString(R.string.save_no_space) String toolbarRightTitle;

    @BindView(R.id.toolbar_title)
    TextView toolBarTitleTextView;
    @BindView(R.id.toolbar_right)
    TextView toolBarRightTextView;
    @BindView(R.id.shop_desc)
    EditText shopDescEditText;
    @BindView(R.id.shop_description_left)
    TextView shopDescriptionLeftTextView;

    ShopService mService;
    ShopDescriptionEditPresenter mPresenter;


    @OnClick(R.id.back) void back() {
        finish();
    }
    @OnClick(R.id.toolbar_right)void editShopDescription(){
        String shop_description = shopDescEditText.getText().toString();
        if(shop_description.equals("")){
            XmbPopubWindow.showAlert(this, Const.SHOP_NAME_EMPTY);
        }else{
            XmbPopubWindow.showTranparentLoading(this);
            InputSoftUtil.hideSoftInput(this,shopDescEditText);
            mPresenter.updateShopDescription(ShopService.gen_update_description_params(AppContext.getToken(this),shop_description));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_description_edit);
        ButterKnife.bind(this);
        displayTitle();
        initApi();
        bindEvent();
    }

    public void displayTitle(){
        toolBarTitleTextView.setText(toolbarTitle);
        toolBarRightTextView.setText(toolbarRightTitle);
        String desc = AppContext.getShopDescription(this);
        shopDescEditText.setText(desc);
        shopDescEditText.setSelection(desc.length());
        shopDescriptionLeftTextView.setText(desc.length() + "/48");
    }

    public void initApi(){
        mService = new ShopService();
        mPresenter = new ShopDescriptionEditPresenter(this,mService);
    }

    public void bindEvent(){
        shopDescEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                shopDescriptionLeftTextView.setText(charSequence.length() + "/48");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void finishView(){
        finish();
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
