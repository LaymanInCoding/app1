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
import com.xiaomabao.weidian.util.LogUtils;
import com.xiaomabao.weidian.util.SharedPreferencesUtil;

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



    ArrayList<ImageView> images = new ArrayList<>();
    int[] imagesResId = {R.mipmap.guide_page_01, R.mipmap.guide_page_02, R.mipmap.guide_page_03};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        initView();
    }

    protected void initView() {
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(imagesResId[i]);
            images.add(imageView);
            if (i == 2) {
                imageView.setOnClickListener((view) -> {
                    AppContext.setIsGuide(this);
                    AppContext.setCartUrl(this);
                    startActivity(new Intent(GuideActivity.this, MainActivity.class));
                    finish();
                });
            }
        }
        viewPager.setAdapter(pagerAdapter);
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

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
