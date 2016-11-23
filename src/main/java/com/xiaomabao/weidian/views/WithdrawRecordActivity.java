package com.xiaomabao.weidian.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.adapters.WithdrawAdapter;
import com.xiaomabao.weidian.models.WithdrawRecord;
import com.xiaomabao.weidian.presenters.WithdrawRecordPresenter;
import com.xiaomabao.weidian.services.ProfitService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.xiaomabao.weidian.ui.EndlessRecyclerOnScrollListener;
import com.xiaomabao.weidian.ui.HeaderViewRecyclerAdapter;


public class WithdrawRecordActivity extends AppCompatActivity {
    @BindString(R.string.withdraw_record) String toolbarTitle;

    @BindView(R.id.toolbar_title)
    TextView toolBarTitleTextView;
    @BindView(R.id.presented_withdraw)
    TextView presentedWithdrawTextView;
    @BindView(R.id.anim_loading)
    View loadView;
    @BindView(R.id.no_withdraw_container)
    View noWithdrawContainer;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    List<WithdrawRecord.Withdraw> recordsList = new ArrayList<>();
    HeaderViewRecyclerAdapter adapter;
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    int page = 1;
    EndlessRecyclerOnScrollListener listener  = new EndlessRecyclerOnScrollListener(layoutManager) {
        @Override
        public void onLoadMore(int currentPage) {
            page += 1;
            mPresenter.withdraw_record(ProfitService.gen_withdraw_record_params(AppContext.getToken(WithdrawRecordActivity.this),page));
        }
    };

    @OnClick(R.id.back) void back() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_record);
        ButterKnife.bind(this);
        displayTitle();
        initApi();
        initRecycleView();
    }

    protected void initRecycleView(){
        WithdrawAdapter withdrawAdapter = new WithdrawAdapter(this,recordsList);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HeaderViewRecyclerAdapter(withdrawAdapter);
        recyclerView.setAdapter(adapter);
    }

    public void displayTitle(){
        toolBarTitleTextView.setText(toolbarTitle);
    }

    public void reloadRecycleView(List<WithdrawRecord.Withdraw> list){
        if(page == 1 && list.size() == 0){
            hideLoading();
            noWithdrawContainer.setVisibility(View.VISIBLE);
            return;
        }
        if(list.size() < 20){
            adapter.removeFooterView();
            recyclerView.removeOnScrollListener(listener);
        }else{
            adapter.removeFooterView();
            adapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.load_more_anim, recyclerView, false));
            recyclerView.removeOnScrollListener(listener);
            listener  = new EndlessRecyclerOnScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int currentPage) {
                    page += 1;
                    mPresenter.withdraw_record(ProfitService.gen_withdraw_record_params(AppContext.getToken(WithdrawRecordActivity.this),page));
                }
            };
            recyclerView.addOnScrollListener(listener);
        }
        recordsList.addAll(list);
        adapter.notifyDataSetChanged();
        hideLoading();
    }

    ProfitService mService;
    WithdrawRecordPresenter mPresenter;

    protected void hideLoading(){
        loadView.setVisibility(View.GONE);
    }

    protected void initApi(){
        mService = new ProfitService();
        mPresenter = new WithdrawRecordPresenter(this,mService);
        mPresenter.withdraw_record(ProfitService.gen_withdraw_record_params(AppContext.getToken(this),page));
    }

    public void reloadPresented(String presentedTotal){
        presentedWithdrawTextView.setText(presentedTotal);
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
