package com.xiaomabao.weidian.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.adapters.ShopInfoAdapter;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.presenters.ShopListPresenter;
import com.xiaomabao.weidian.rx.RxBus;
import com.xiaomabao.weidian.services.ShopService;
import com.xiaomabao.weidian.ui.HeaderViewRecyclerAdapter;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.fragment.MineFragment;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by de on 2016/9/27.
 */
public class ShopListActivity extends Activity {
    public static final int REQUEST_CODE = 0x01;
    public static final int REQUEST_SAVE = 0x02;
    public boolean is_delete = true;
    @BindString(R.string.shop_setting_title)
    String toolbarTitle;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar_title)
    TextView toolBarTitleTextView;

    @OnClick(R.id.back)
    void back() {
        finish();
    }

    HeaderViewRecyclerAdapter mRecyclerAdapter;

    public void displayTitle() {
        toolBarTitleTextView.setText(toolbarTitle);
    }

    private ShopListPresenter mPresenter;
    private ShopService mService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_settingv2);
        ButterKnife.bind(this);
        initApi();
        displayTitle();
        setShopList();
    }

    public void initApi() {
        mService = new ShopService();
        mPresenter = new ShopListPresenter(this, mService);
    }

    public void setShopList() {
        ShopInfoAdapter shopInfoAdapter = new ShopInfoAdapter(this, AppContext.instance().getShopShareInfoArrayList());
        shopInfoAdapter.setOnItemClickListener(new ShopInfoAdapter.OnItemClickListener() {
            @Override
            public void OnDeleteShop(int position) {
                if (AppContext.instance().getShopShareInfoArrayList().size() == 1) {
                    XmbPopubWindow.showAlert(ShopListActivity.this, "请保留一个店铺哟");
                    return;
                }
                new AlertDialog.Builder(ShopListActivity.this).setMessage("确定删除吗?")
                        .setCancelable(true).setNegativeButton("取消", null).setPositiveButton("确定", (dialog, which) -> {
                    mPresenter.deleteShop(ShopService.gen_delete_shop_params(AppContext.getToken(ShopListActivity.this), AppContext.instance().getShopShareInfoArrayList().get(position).id), position);
                }).show();
            }

            @Override
            public void OnEditShop(int position) {
                Intent intent = new Intent(ShopListActivity.this, ShopSettingActivity.class);
                intent.putExtra("position", position);
                startActivityForResult(intent, REQUEST_CODE);
            }

            @Override
            public void OnSetDefault(int position) {
                mPresenter.setDefaultShop(ShopService.gen_set_default_params(AppContext.getToken(ShopListActivity.this), AppContext.instance().getShopShareInfoArrayList().get(position).id));
                mRecyclerAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerAdapter = new HeaderViewRecyclerAdapter(shopInfoAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        View footView = LayoutInflater.from(this).inflate(R.layout.footer_shop_list, recyclerView, false);
        //添加新店铺信息
        footView.findViewById(R.id.add_new_shop).setOnClickListener(v -> startActivityForResult(new Intent(this, ShopSettingActivity.class), REQUEST_SAVE));
        mRecyclerAdapter.addFooterView(footView);
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    public void refreshInfo() {
        mRecyclerAdapter.notifyDataSetChanged();
    }

    public void setDefaultCallback(String share_id) {
        AppContext.instance().setDefaultShareInfo(share_id);
        mRecyclerAdapter.notifyDataSetChanged();
        MineFragment.animTime = 1;
//        setResult(RESULT_OK);
    }

    public void setDeleteCallback(int position) {
        AppContext.instance().removeShopShareInfoByIndex(position);
        if (AppContext.instance().getShopShareInfoArrayList().size() == 1) {
            RxBus.getInstance().post(Const.LOG_IN_OUT, false);
            MineFragment.animTime = 1;
        }
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("requestCode", requestCode + "");
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    refreshInfo();
                }
                break;
            case REQUEST_SAVE:
                if (resultCode == RESULT_OK) {
                    refreshInfo();
                }
        }
    }
}
