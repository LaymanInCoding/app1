package com.xiaomabao.weidian.views.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
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
import com.xiaomabao.weidian.ui.RiseNumberTextView;
import com.xiaomabao.weidian.util.ImageLoaderUtil;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by de on 2017/3/6.
 */
public class MineFragment extends Fragment {
    public static int animTime = 1;

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
    RiseNumberTextView totalProfitTextView;
    @BindView(R.id.today_profit)
    RiseNumberTextView todayProfitTextView;
    @BindView(R.id.today_orders)
    RiseNumberTextView todayOrdersTextView;
    @BindView(R.id.total_visits)
    RiseNumberTextView todayVisitsTextView;

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
                if (aBoolean) {
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
            ImageLoaderUtil.loadImage(getContext(), shop_avatar);
            ImageLoaderUtil.loadBackground(getContext(), shop_background);
        } else {
            shop_name.setText(AppContext.getShopName(getContext()).equals("") ? "请点击头像设置店铺信息" : AppContext.getShopName(getContext()));
            ImageLoaderUtil.loadImageByUrl(getContext(), shop_avatar, AppContext.getShopAvater(getContext()));
            ImageLoaderUtil.loadBackgroundByUrl(getContext(), shop_background, AppContext.getShopBackground(getContext()));
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
        shop_name.setText("请登录,当前状态未登录!");
        ImageLoaderUtil.loadImage(getContext(), shop_avatar);
        ImageLoaderUtil.loadBackground(getContext(), shop_background);
        totalProfitTextView.setText("0.00");
        todayProfitTextView.setText("0.00");
        todayOrdersTextView.setText("0");
        todayVisitsTextView.setText("0");
    }

    private void setRiseNumber(float totalProfit, float todayProfit) {
        totalProfitTextView.setDuration(1500);
        totalProfitTextView.setNumber(totalProfit);
        totalProfitTextView.start();
        todayProfitTextView.setDuration(1500);
        todayProfitTextView.setNumber(todayProfit);
        todayProfitTextView.start();
    }

    private void setRiseNumber(int orderNumber, int visitNumber) {
        todayOrdersTextView.setDuration(1500);
        todayOrdersTextView.setNumber(orderNumber);
        todayOrdersTextView.start();
        todayVisitsTextView.setDuration(1500);
        todayVisitsTextView.setNumber(visitNumber);
        todayVisitsTextView.start();
    }

    public void displayShopProfit(ShopStatistics.ShopStatisticsInfo shopStatisticsInfo) {
        setRiseNumber(Float.parseFloat(shopStatisticsInfo.profit_total),
                Float.parseFloat(shopStatisticsInfo.day_wait_profit));
        setRiseNumber(Integer.parseInt(shopStatisticsInfo.day_orders_cnt),
                Integer.parseInt(shopStatisticsInfo.visit_total));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Logger.d("hiddenchange");
        super.onHiddenChanged(hidden);
        if (hidden == false && isFocused == false) {
            CommonPresenter commonPresenter = new CommonPresenter(getActivity(), new CommonService());
            commonPresenter.check_update(CommonService.gen_update_params("android"));
            commonPresenter.check_search_update();
            mPresenter.getShopProfitInfo(ShopService.gen_statistics_info_params(AppContext.getToken(getContext())));
            setView();
            isFocused = true;
            animTime = 2;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (animTime == 1) {
//            mPresenter.getShopProfitInfo(ShopService.gen_statistics_info_params(AppContext.getToken(getContext())));
            setView();
            animTime = 2;
        }
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
