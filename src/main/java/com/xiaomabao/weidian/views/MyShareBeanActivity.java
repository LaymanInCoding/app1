package com.xiaomabao.weidian.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.Const;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.adapters.MyBeanAdapter;
import com.xiaomabao.weidian.models.Bean;
import com.xiaomabao.weidian.presenters.BeanPresenter;
import com.xiaomabao.weidian.services.BeanService;
import com.xiaomabao.weidian.ui.EndlessRecyclerOnScrollListener;
import com.xiaomabao.weidian.ui.HeaderViewRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by de on 2016/12/20.
 */
public class MyShareBeanActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.my_bean_num)
    TextView mMyBeanNum;
    @BindView(R.id.bean_msg)
    TextView mBeanMsg;
    @BindView(R.id.no_bean_record_img)
    ImageView mNoBeanRecordImg;
    @BindView(R.id.bean_recycler)
    RecyclerView mBeanRecycler;
    @BindView(R.id.anim_loading)
    View animLoading;
    @BindView(R.id.no_bean_container)
    View mNoBeanContainer;

    private ArrayList<Bean.BeanRecord> beanList = new ArrayList<>();
    private BeanService mService;
    private BeanPresenter mPresenter;
    private int page = 1;
    private HeaderViewRecyclerAdapter beanHeaderAdapter;
    private LinearLayoutManager layoutManager;
    private EndlessRecyclerOnScrollListener goodsListener;
    private MyBeanAdapter mAdapter;
    private String mBeanNum;
    private BroadcastReceiver receiver;
    private IntentFilter filter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bean);
        ButterKnife.bind(this);
        setToolbarTitle();
        initApi();
        setView();
        registBroad();
    }

    private void registBroad() {
        filter = new IntentFilter(Const.INTENT_ACTION_REFRESH_BEAN);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("Refresh", "refresh");
                animLoading.setVisibility(View.VISIBLE);
                page = 1;
                request();
            }
        };
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void setToolbarTitle() {
        mToolbarTitle.setText("我的共享豆");
    }

    protected void initApi() {
        mService = new BeanService();
        mPresenter = new BeanPresenter(this, mService);
    }

    private void setView() {
        mAdapter = new MyBeanAdapter(this, beanList);
        beanHeaderAdapter = new HeaderViewRecyclerAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBeanRecycler.setHasFixedSize(true);
        mBeanRecycler.setLayoutManager(layoutManager);
        mBeanRecycler.setAdapter(beanHeaderAdapter);
        LinearLayout headerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.header_bean_list, mBeanRecycler, false);
        beanHeaderAdapter.addHeaderView(headerLayout);
        request();
    }

    public void recordsResponse(Bean bean) {
        mBeanNum = bean.number;
        mMyBeanNum.setText(bean.number);
        List<Bean.BeanRecord> recordList = bean.records;
        mBeanRecycler.setVisibility(View.VISIBLE);
        mNoBeanContainer.setVisibility(View.GONE);
        if (recordList.size() == 0) {
            mBeanRecycler.setVisibility(View.GONE);
            mNoBeanContainer.setVisibility(View.VISIBLE);
        }
        if (recordList.size() < 20) {
            beanHeaderAdapter.removeFooterView();
            mBeanRecycler.removeOnScrollListener(goodsListener);
            goodsListener = null;
        } else {
            beanHeaderAdapter.removeFooterView();
            beanHeaderAdapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.load_more_anim, mBeanRecycler, false));
            mBeanRecycler.removeOnScrollListener(goodsListener);
            goodsListener = new EndlessRecyclerOnScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int currentPage) {
                    page += 1;
                    request();
                }
            };
            mBeanRecycler.addOnScrollListener(goodsListener);
        }
        beanList.addAll(recordList);
        beanHeaderAdapter.notifyDataSetChanged();
        animLoading.setVisibility(View.GONE);
    }

    protected void request() {
        if (page == 1) {
            animLoading.setVisibility(View.VISIBLE);
            beanList.clear();
            beanHeaderAdapter.notifyDataSetChanged();
        }
        mPresenter.get_bean_info(BeanService.gen_getBean_param(AppContext.getToken(this), page));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @OnClick({R.id.back, R.id.send_bean, R.id.use_help})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.send_bean:
                Intent intent = new Intent(this, SendBeanActivity.class);
                intent.putExtra("bean_num", mBeanNum);
                startActivity(intent);
                break;
            case R.id.use_help:
                Intent intent1 = new Intent(this, ShareBeanHelpActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
