package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.adapters.OrderAdapter;
import com.xiaomabao.weidian.adapters.OrderTitleAdapter;
import com.xiaomabao.weidian.models.OrderList;
import com.xiaomabao.weidian.models.OrderType;
import com.xiaomabao.weidian.presenters.OrderListPresenter;
import com.xiaomabao.weidian.services.OrderService;
import com.xiaomabao.weidian.util.InputSoftUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.xiaomabao.weidian.ui.EndlessRecyclerOnScrollListener;
import com.xiaomabao.weidian.ui.HeaderViewRecyclerAdapter;

public class OrderListActivity extends AppCompatActivity {

    @BindView(R.id.titleRecyclerView)
    RecyclerView titleRecyclerView;
    @BindView(R.id.orderRecyclerView)
    RecyclerView orderRecyclerView;
    @BindView(R.id.anim_loading)
    View loadView;
    @BindView(R.id.anim_loading_full)
    View loadFullView;
    @BindView(R.id.search_text)
    EditText searchEditText;
    @BindView(R.id.no_order_container)
    View noOrderContainerView;
    private EndlessRecyclerOnScrollListener listener;
    HeaderViewRecyclerAdapter headerAdapter;
    LinearLayoutManager orderLayoutManager;
    OrderTitleAdapter titleAdapter;
    OrderAdapter orderAdapter;
    List<OrderType> orderTypeList = new ArrayList<>();
    List<OrderList.Order> orderList = new ArrayList<>();
    int page = 1;
    String order_type = "all";
    String order_sn = "";

    @OnClick(R.id.back) void back() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        ButterKnife.bind(this);
        initApi();
        initRecycleView();
    }

    OrderService mService;
    OrderListPresenter mPresenter;

    public void initApi(){
        mService = new OrderService();
        mPresenter = new OrderListPresenter(this,mService);
        mPresenter.order_types(OrderService.gen_order_type_params(AppContext.getToken(this)));
        mPresenter.order_list(OrderService.gen_order_list_params(AppContext.getToken(this),order_sn,order_type,page));
    }

    public void initRecycleView(){
        titleAdapter = new OrderTitleAdapter(this,orderTypeList);
        titleAdapter.setOnClickListener((position)-> {
            for(int i=0; i < orderTypeList.size(); i++){
                if (i == position){
                    orderTypeList.get(i).selected = 1;
                }else{
                    orderTypeList.get(i).selected = 0;
                }
            }
            if(searchEditText.isFocused()) {
                InputSoftUtil.hideSoftInput(this, searchEditText);
                searchEditText.clearFocus();
            }
            titleAdapter.notifyDataSetChanged();
            order_type = orderTypeList.get(position).order_type_code;
            page = 1;
            orderList.clear();
            orderAdapter.notifyDataSetChanged();
            loadView.setVisibility(View.VISIBLE);
            mPresenter.order_list(OrderService.gen_order_list_params(AppContext.getToken(this),order_sn,order_type,page));
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        titleRecyclerView.setHasFixedSize(true);
        titleRecyclerView.setLayoutManager(layoutManager);
        titleRecyclerView.setAdapter(titleAdapter);
        orderAdapter = new OrderAdapter(this,orderList);
        orderAdapter.setOnClickListener((position -> {
            Intent intent = new Intent(OrderListActivity.this,OrderDetailActivity.class);
            intent.putExtra("order_sn",orderList.get(position).order_sn);
            startActivity(intent);
        }));
        headerAdapter = new HeaderViewRecyclerAdapter(orderAdapter);
        orderLayoutManager = new LinearLayoutManager(this);
        orderLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        orderRecyclerView.setHasFixedSize(true);
        orderRecyclerView.setLayoutManager(orderLayoutManager);
        orderRecyclerView.setAdapter(headerAdapter);

        searchEditText.setOnKeyListener((view,keyCode,keyEvent)-> {
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                order_sn = searchEditText.getText().toString();
                if(!order_sn.trim().equals("")){
                    for (int i = 0; i < orderTypeList.size() ; i++ ){
                        if (i == 0){
                            orderTypeList.get(i).selected = 1;
                        }else{
                            orderTypeList.get(i).selected = 0;
                        }
                    }
                    order_type = "all";
                    page = 1;
                    loadView.setVisibility(View.VISIBLE);
                    mPresenter.order_list(OrderService.gen_order_list_params(AppContext.getToken(this),order_sn.trim(),order_type,page));
                }else{
                    order_sn = "";
                }
                InputSoftUtil.hideSoftInput(this,view);
                return true;
            }else{
                return false;
            }
        });
    }

    public void loadOrderTypes(List<OrderType> orderTypes){
        orderTypeList.clear();
        orderTypeList.addAll(orderTypes);
        titleAdapter.notifyDataSetChanged();
    }

    public void loadOrderList(List<OrderList.Order> orders){
        if (page == 1 && orders.size() == 0){
            noOrderContainerView.setVisibility(View.VISIBLE);
        }else{
            noOrderContainerView.setVisibility(View.GONE);
        }
        if(page == 1){
            orderList.clear();
        }
        if (orders.size() < 20){
            headerAdapter.removeFooterView();
            orderRecyclerView.removeOnScrollListener(listener);
        }else{
            headerAdapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.load_more_anim, orderRecyclerView, false));
            listener  = new EndlessRecyclerOnScrollListener(orderLayoutManager) {
                @Override
                public void onLoadMore(int currentPage) {
                    page += 1;
                    mPresenter.order_list(OrderService.gen_order_list_params(AppContext.getToken(OrderListActivity.this),order_sn,order_type,page));
                }
            };
            orderRecyclerView.addOnScrollListener(listener);
        }
        orderList.addAll(orders);
        orderAdapter.notifyDataSetChanged();
        loadView.setVisibility(View.GONE);
        loadFullView.setVisibility(View.GONE);
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
