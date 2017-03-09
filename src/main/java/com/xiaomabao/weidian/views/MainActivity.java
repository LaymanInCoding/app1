package com.xiaomabao.weidian.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.TabEntity;
import com.xiaomabao.weidian.rx.RxManager;
import com.xiaomabao.weidian.util.LogUtils;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.fragment.CategoryChooseFragment;
import com.xiaomabao.weidian.views.fragment.CategoryTestFragment;
import com.xiaomabao.weidian.views.fragment.MineFragment;
import com.xiaomabao.weidian.views.fragment.ShoppingCartFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by de on 2017/3/6.
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tab_layout)
    CommonTabLayout mTabLayout;

    private String mTitles[] = {"选品上架", "购物车", "我的"};
    private int mUnChooseIcon[] = {R.mipmap.category_unchoose, R.mipmap.cart_unchoose, R.mipmap.me_unchoose};
    private int mChooseIcon[] = {R.mipmap.category_choose, R.mipmap.cart_choose, R.mipmap.me_choose};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private long doubleExitBeginTime;

    //    private CategoryChooseFragment mCategoryChooseFragment;
    private CategoryChooseFragment mCategoryChooseFragment;

    private ShoppingCartFragment mShoppingCartFragment;
    private MineFragment mMineFragment;
    private static int tabLayoutHeight;
    private RxManager mRxManager = new RxManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_activity);
        ButterKnife.bind(this);
        initTab();
        initFragment(savedInstanceState);
        mTabLayout.measure(0, 0);
        tabLayoutHeight = mTabLayout.getMeasuredHeight();
        //监听菜单显示或隐藏
        mRxManager.on(Const.MENU_SHOW_HIDE, new Action1<Boolean>() {
            @Override
            public void call(Boolean hideOrShow) {
                startAnimation(hideOrShow);
            }
        });
        mRxManager.on(Const.JUMP_TO_FPAGE, new Action1<Boolean>() {
            @Override
            public void call(Boolean hideOrShow) {
                switchToPage(0);
                mTabLayout.setCurrentTab(0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxManager.clear();
    }

    private void initTab() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mUnChooseIcon[i], mChooseIcon[i]));
        }
        mTabLayout.setTabData(mTabEntities);
        //点击监听
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switchToPage(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }

    private void initFragment(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int currentTabPosition = 0;
        if (savedInstanceState != null) {
            mCategoryChooseFragment = (CategoryChooseFragment) getSupportFragmentManager()
                    .findFragmentByTag("categoryChooseFragment");
            mShoppingCartFragment = (ShoppingCartFragment) getSupportFragmentManager()
                    .findFragmentByTag("shoppingCartFragment");
            mMineFragment = (MineFragment) getSupportFragmentManager()
                    .findFragmentByTag("mineFragment");
            currentTabPosition = savedInstanceState.getInt(Const.HOME_CURRENT_TAB_POSITION);
        } else {
            mCategoryChooseFragment = new CategoryChooseFragment();
            mShoppingCartFragment = new ShoppingCartFragment();
            mMineFragment = new MineFragment();
            transaction.add(R.id.content_fl, mCategoryChooseFragment, "categoryChooseFragment");
            transaction.add(R.id.content_fl, mShoppingCartFragment, "shoppingCartFragment");
            transaction.add(R.id.content_fl, mMineFragment, "mineFragment");
        }
        transaction.commit();
        switchToPage(currentTabPosition);
        mTabLayout.setCurrentTab(currentTabPosition);
    }

    public void switchTo(int position) {
        mTabLayout.setCurrentTab(position);
    }

    private void switchToPage(int position) {
        LogUtils.logd("主页菜单position" + position);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (position) {
            //首页
            case 0:
                transaction.hide(mShoppingCartFragment);
                transaction.hide(mMineFragment);
                transaction.show(mCategoryChooseFragment);
                transaction.commitAllowingStateLoss();
                break;
            //购物车
            case 1:
                transaction.hide(mCategoryChooseFragment);
                transaction.hide(mMineFragment);
                transaction.show(mShoppingCartFragment);
                transaction.commitAllowingStateLoss();
                break;
            //我的
            case 2:
                transaction.hide(mCategoryChooseFragment);
                transaction.hide(mShoppingCartFragment);
                transaction.show(mMineFragment);
                transaction.commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogUtils.loge("onSavedInstance进来了1");
        super.onSaveInstanceState(outState);
        if (mTabLayout != null) {
            LogUtils.loge("onSaveInstanceState进来了2");
            outState.putInt(Const.HOME_CURRENT_TAB_POSITION, mTabLayout.getCurrentTab());
        }
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

    /**
     * 菜单显示隐藏动画
     *
     * @param showOrHide
     */
    private void startAnimation(boolean showOrHide) {
        final ViewGroup.LayoutParams layoutParams = mTabLayout.getLayoutParams();
        ValueAnimator valueAnimator;
        ObjectAnimator alpha;
        if (!showOrHide) {
            valueAnimator = ValueAnimator.ofInt(tabLayoutHeight, 0);
            alpha = ObjectAnimator.ofFloat(mTabLayout, "alpha", 1, 0);
        } else {
            valueAnimator = ValueAnimator.ofInt(0, tabLayoutHeight);
            alpha = ObjectAnimator.ofFloat(mTabLayout, "alpha", 0, 1);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.height = (int) valueAnimator.getAnimatedValue();
                mTabLayout.setLayoutParams(layoutParams);
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(valueAnimator, alpha);
        animatorSet.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
