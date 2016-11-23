package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.presenters.CheckTokenPresenter;
import com.xiaomabao.weidian.services.UserService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GuideActivity extends AppCompatActivity {
    @BindView(R.id.viewpager)
    ViewPager viewPager;


    private UserService mService;
    private CheckTokenPresenter mPresenter;

    ArrayList<ImageView> images = new ArrayList<>();
    int[] imagesResId = {R.mipmap.guide_page_01, R.mipmap.guide_page_02, R.mipmap.guide_page_03};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        initApiInfo();
        initView();
    }

    public void checkIsLogin() {
        String token = AppContext.getToken(this);
        if (!token.equals("")) {
            mPresenter.checkToken(UserService.gen_refresh_token_params(token));
        } else {
            Observable.timer(1, TimeUnit.SECONDS, Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Long -> {
                        startActivity(new Intent(GuideActivity.this, StartWeidianActivity.class));
                        finish();
                    });
        }
    }

    protected void initView() {
        if (AppContext.getIsGuide(this)) {
            Observable.timer(1, TimeUnit.SECONDS, Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((Long) -> {
                        checkIsLogin();
                    });
            return;
        }
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(imagesResId[i]);
            images.add(imageView);
            if (i == 2) {
                imageView.setOnClickListener((view) -> {
                    AppContext.setIsGuide(this);
                    startActivity(new Intent(GuideActivity.this, StartWeidianActivity.class));
                    finish();
                });
            }
        }
        viewPager.setAdapter(pagerAdapter);
    }

    public void initApiInfo() {
        mService = new UserService();
        mPresenter = new CheckTokenPresenter(this, mService);
    }

    PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {

            return images.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(images.get(position % images.size()));

        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(images.get(position % images.size()), 0);
            return images.get(position % images.size());
        }

    };


    public void jumpToShopIndex() {
        startActivity(new Intent(this, ShopMenuActivity.class));
        finish();
    }

    public void jumpToLoginView() {
        Observable<Long> observable = Observable.timer(3, TimeUnit.SECONDS, Schedulers.io());
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Long) -> {
                    startActivity(new Intent(GuideActivity.this, PhoneLoginActivity.class));
                    finish();
                });
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
