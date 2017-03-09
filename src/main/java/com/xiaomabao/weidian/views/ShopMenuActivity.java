package com.xiaomabao.weidian.views;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
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
import com.xiaomabao.weidian.models.ShopBase;
import com.xiaomabao.weidian.models.ShopStatistics;
import com.xiaomabao.weidian.models.UpdateInfo;
import com.xiaomabao.weidian.presenters.CommonPresenter;
import com.xiaomabao.weidian.presenters.ShopMenuPresenter;
import com.xiaomabao.weidian.services.CommonService;
import com.xiaomabao.weidian.services.ShopService;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 店铺 主页面
 */
public class ShopMenuActivity extends AppCompatActivity {
    private int SETTING_REQUEST_CODE = 0x01;
    private int EDIT_REQUEST_CODE = 0x02;
    private int SHOP_SETTING_CODE = 0x03;
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


    @OnClick(R.id.setting_container)
    void jumpToSettingActivity() {
        startActivityForResult(new Intent(this, SettingActivity.class), SETTING_REQUEST_CODE);
    }

    @OnClick(R.id.shop_avatar)
    void jumpToShopSettingActivity() {
        if (AppContext.instance().getShopShareInfoArrayList().size() == 0) {
            startActivityForResult(new Intent(this, ShopSettingActivity.class), SHOP_SETTING_CODE);
        } else {
            startActivityForResult(new Intent(this, ShopListActivity.class), EDIT_REQUEST_CODE);
        }
    }

    @OnClick(R.id.my_shop_container)
    void jumpToMyShopActivity() {
        startActivity(new Intent(this, MyShopActivity.class));
    }

    @OnClick(R.id.my_profit_container)
    void jumpToMyProfitActivity() {
        startActivity(new Intent(this, ProfitActivity.class));
    }

    @OnClick(R.id.category_choose_container)
    void jumpToCategoryChooseActivity() {
        startActivity(new Intent(this, CategoryChooseActivity.class));
    }

    @OnClick(R.id.order_container)
    void jumpToOrderListActivity() {
        startActivity(new Intent(this, OrderListActivity.class));
    }

    @OnClick(R.id.vip_container)
    void jumpToVipActivity() {
        startActivity(new Intent(this, VipGoodsActivity.class));
    }

    @OnClick(R.id.invite_friend_container)
    void jumpToInviteFriendActivity() {
        startActivity(new Intent(this, InviteFriendActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_menu);
        ButterKnife.bind(this);
        top_container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppContext.width * 400 / 750));
        initApi();
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
            shop_name.setText(AppContext.getShopName(this).equals("") ? "请点击头像设置店铺信息" : AppContext.getShopName(this));
            Glide.with(this)
                    .load(AppContext.getShopAvater(this))
                    .placeholder(R.mipmap.mitouxiang)
                    .dontAnimate()
                    .error(R.mipmap.mitouxiang)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(shop_avatar);
            Glide.with(this)
                    .load(AppContext.getShopBackground(this))
                    .dontAnimate()
                    .placeholder(R.mipmap.index_top_bg)
                    .error(R.mipmap.index_top_bg)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(shop_background);
        }
    }

    private void initApi() {
        mService = new ShopService();
//        mPresenter = new ShopMenuPresenter(this, mService);
        mPresenter.getShopBaseInfo(ShopService.gen_base_info_params(AppContext.getToken(this)));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus == true && isFocused == false) {
            CommonPresenter commonPresenter = new CommonPresenter(this, new CommonService());
            commonPresenter.check_update(CommonService.gen_update_params("android"));
            commonPresenter.check_search_update();
            isFocused = true;
        }
    }

    public void displayShopBaseInfo() {
        setView();
    }

    public void displayShopProfit(ShopStatistics.ShopStatisticsInfo shopStatisticsInfo) {
        totalProfitTextView.setText(shopStatisticsInfo.profit_total);
        todayProfitTextView.setText(shopStatisticsInfo.day_wait_profit);
        todayOrdersTextView.setText(shopStatisticsInfo.day_orders_cnt);
        todayVisitsTextView.setText(shopStatisticsInfo.visit_total);
    }

    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (System.currentTimeMillis() - doubleExitBeginTime > 2000) {
                Toast.makeText(this, "再按一次返回退出程序", Toast.LENGTH_SHORT).show();
                doubleExitBeginTime = System.currentTimeMillis();
            } else {
                finish();
                if (isServiceWork(this, "STOP_UPDATE_SERVICE")) {
                    Intent stopIntent = new Intent();
                    stopIntent.setAction("STOP_UPDATE_SERVICE");
                    stopService(stopIntent);
                }
                System.exit(0);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        } else if (requestCode == SHOP_SETTING_CODE) {
            if (resultCode == RESULT_OK) {
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getShopProfitInfo(ShopService.gen_statistics_info_params(AppContext.getToken(this)));
        setView();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
