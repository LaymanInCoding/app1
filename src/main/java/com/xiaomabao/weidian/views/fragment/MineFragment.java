package com.xiaomabao.weidian.views.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.ShopStatistics;
import com.xiaomabao.weidian.presenters.CommonPresenter;
import com.xiaomabao.weidian.presenters.ShopMenuPresenter;
import com.xiaomabao.weidian.rx.RxManager;
import com.xiaomabao.weidian.services.CommonService;
import com.xiaomabao.weidian.services.ShopService;
import com.xiaomabao.weidian.util.LogUtils;
import com.xiaomabao.weidian.views.InviteFriendActivity;
import com.xiaomabao.weidian.views.MyShopActivity;
import com.xiaomabao.weidian.views.OrderListActivity;
import com.xiaomabao.weidian.views.PhoneLoginActivity;
import com.xiaomabao.weidian.views.ProfitActivity;
import com.xiaomabao.weidian.views.SettingActivity;
import com.xiaomabao.weidian.views.ShopListActivity;
import com.xiaomabao.weidian.views.ShopSettingActivity;
import com.xiaomabao.weidian.views.VipGoodsActivity;
import com.xiaomabao.weidian.views.WebViewActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by de on 2017/3/6.
 */
public class MineFragment extends Fragment {

    private int SETTING_REQUEST_CODE = 0x01;
    private int EDIT_REQUEST_CODE = 0x02;
    private int SHOP_SETTING_CODE = 0x03;
    private int LOGIN_CODE = 0x04;

    private boolean isFocused = false;

    @BindView(R.id.top_container)
    RelativeLayout top_container;
    private long doubleExitBeginTime;
    @BindView(R.id.shop_background)
    ImageView shop_background;
    @BindView(R.id.shop_avatar)
    ImageView shop_avatar;
    @BindView(R.id.shop_name)
    TextView shop_name;

    @BindView(R.id.total_profit)
    TextView totalProfitTextView;
    @BindView(R.id.today_profit)
    TextView todayProfitTextView;
    @BindView(R.id.today_orders)
    TextView todayOrdersTextView;
    @BindView(R.id.total_visits)
    TextView todayVisitsTextView;

    ShopMenuPresenter mPresenter;
    ShopService mService;

    private View mView;
    private RxManager mRxManager;

    private void toLogin() {
        startActivityForResult(new Intent(getContext(), PhoneLoginActivity.class), LOGIN_CODE);
    }

    @OnClick(R.id.setting_container)
    void jumpToSettingActivity() {
        if (AppContext.isLogin()) {
            startActivityForResult(new Intent(getContext(), SettingActivity.class), SETTING_REQUEST_CODE);
        } else {
            toLogin();
        }
    }

    @OnClick(R.id.shop_avatar)
    void jumpToShopSettingActivity() {
        if (AppContext.isLogin()) {
            if (AppContext.instance().getShopShareInfoArrayList().size() == 0) {
                startActivityForResult(new Intent(getContext(), ShopSettingActivity.class), SHOP_SETTING_CODE);
            } else {
                startActivityForResult(new Intent(getContext(), ShopListActivity.class), EDIT_REQUEST_CODE);
            }
        } else {
            toLogin();
        }
    }

    @OnClick(R.id.my_shop_container)
    void jumpToMyShopActivity() {
        if (AppContext.isLogin()) {
            startActivity(new Intent(getContext(), MyShopActivity.class));
        } else {
            toLogin();
        }
    }

    @OnClick(R.id.my_profit_container)
    void jumpToMyProfitActivity() {
        if (AppContext.isLogin()) {
            startActivity(new Intent(getContext(), ProfitActivity.class));
        } else {
            toLogin();
        }
    }

    @OnClick(R.id.category_choose_container)
    void jumpToCategoryChooseActivity() {
        startActivity(new Intent(getContext(), CategoryChooseFragment.class));
    }

    @OnClick(R.id.order_container)
    void jumpToOrderListActivity() {
        if (AppContext.isLogin()) {
            startActivity(new Intent(getContext(), OrderListActivity.class));
        } else {
            toLogin();
        }
    }

    @OnClick(R.id.vip_container)
    void jumpToVipActivity() {
        if (AppContext.isLogin()) {
            startActivity(new Intent(getContext(), VipGoodsActivity.class));
        } else {
            toLogin();
        }
    }

    @OnClick(R.id.me_container)
    void jumpTpWebActivity() {
        if (AppContext.isLogin()) {
            Intent intent = new Intent();
            intent.putExtra("url", AppContext.getUcenterUrl(getContext()));
            intent.putExtra("name", "个人中心");
            intent.putExtra("share_desc", "");
            intent.setClass(getContext(), WebViewActivity.class);
            startActivity(intent);
        } else {
            toLogin();
        }
    }

    @OnClick(R.id.invite_friend_container)
    void jumpToInviteFriendActivity() {
        if (AppContext.isLogin()) {
            startActivity(new Intent(getContext(), InviteFriendActivity.class));
        } else {
            toLogin();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApi();
        mRxManager = new RxManager();
        mRxManager.on(Const.LOG_IN_OUT, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (AppContext.getToken(getContext()).equals("")) {
                    LogUtils.loge("logout");
                    clearShopProfit();
                } else {
                    LogUtils.loge(AppContext.getToken(getContext()));
                    mPresenter.getShopBaseInfo(ShopService.gen_base_info_params(AppContext.getToken(getContext())));
                    mPresenter.getShopProfitInfo(ShopService.gen_statistics_info_params(AppContext.getToken(getContext())));
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRxManager.clear();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.loge("MeFragment");
        if (mView == null)
            mView = inflater.inflate(R.layout.activity_shop_menu, container, false);
        ButterKnife.bind(this, mView);
        top_container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppContext.width * 400 / 750));
        mPresenter.getShopBaseInfo(ShopService.gen_base_info_params(AppContext.getToken(getContext())));
        mPresenter.getShopProfitInfo(ShopService.gen_statistics_info_params(AppContext.getToken(getContext())));
        setView();
        return mView;
    }

    private void setView() {
        if (AppContext.instance().getShopShareInfoArrayList().size() == 0) {
            shop_name.setText("请点击头像设置店铺信息");
            Glide.with(this)
                    .load(R.mipmap.mitouxiang)
                    .placeholder(R.mipmap.mitouxiang)
                    .dontAnimate()
                    .error(R.mipmap.mitouxiang)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(shop_avatar);
            Glide.with(this)
                    .load(R.mipmap.index_top_bg)
                    .dontAnimate()
                    .placeholder(R.mipmap.index_top_bg)
                    .error(R.mipmap.index_top_bg)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(shop_background);
        } else {
            shop_name.setText(AppContext.getShopName(getContext()).equals("") ? "请点击头像设置店铺信息" : AppContext.getShopName(getContext()));
            Glide.with(this)
                    .load(AppContext.getShopAvater(getContext()))
                    .placeholder(R.mipmap.mitouxiang)
                    .dontAnimate()
                    .error(R.mipmap.mitouxiang)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(shop_avatar);
            Glide.with(this)
                    .load(AppContext.getShopBackground(getContext()))
                    .dontAnimate()
                    .placeholder(R.mipmap.index_top_bg)
                    .error(R.mipmap.index_top_bg)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(shop_background);
        }
    }

    private void initApi() {
        mService = new ShopService();
        mPresenter = new ShopMenuPresenter(this, mService);
    }

    public void displayShopBaseInfo() {
        setView();
    }

    public void clearShopProfit() {
        setView();
        totalProfitTextView.setText("0.00");
        todayProfitTextView.setText("0.00");
        todayOrdersTextView.setText("0");
        todayVisitsTextView.setText("0");
    }

    public void displayShopProfit(ShopStatistics.ShopStatisticsInfo shopStatisticsInfo) {
        totalProfitTextView.setText(shopStatisticsInfo.profit_total);
        todayProfitTextView.setText(shopStatisticsInfo.day_wait_profit);
        todayOrdersTextView.setText(shopStatisticsInfo.day_orders_cnt);
        todayVisitsTextView.setText(shopStatisticsInfo.visit_total);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false && isFocused == false) {
            CommonPresenter commonPresenter = new CommonPresenter(getActivity(), new CommonService());
            commonPresenter.check_update(CommonService.gen_update_params("android"));
            commonPresenter.check_search_update();
            isFocused = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getShopProfitInfo(ShopService.gen_statistics_info_params(AppContext.getToken(getContext())));
        setView();
        MobclickAgent.onResume(getActivity());
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_CODE) {
            if (resultCode == Activity.RESULT_OK) {
            }
        }
    }
}
