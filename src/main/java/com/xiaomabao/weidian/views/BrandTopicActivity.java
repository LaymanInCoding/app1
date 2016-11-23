package com.xiaomabao.weidian.views;

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


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.adapters.VipGoodsAdapter;
import com.xiaomabao.weidian.models.Goods;
import com.xiaomabao.weidian.presenters.BrandGoodsPresenter;
import com.xiaomabao.weidian.services.GoodsService;
import com.xiaomabao.weidian.ui.EndlessRecyclerOnScrollListener;
import com.xiaomabao.weidian.ui.HeaderViewRecyclerAdapter;
import com.xiaomabao.weidian.util.XmbIntent;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BrandTopicActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_title)
    TextView toolbarTitleTextView;
    @BindView(R.id.anim_loading)
    View animLoading;
    @BindView(R.id.medicine_content)
    RecyclerView mRecyclerView;
    @BindView(R.id.no_goods_container)
    LinearLayout noGoodsContainer;

    private List<Goods> goodsList = new ArrayList<>();
    private EndlessRecyclerOnScrollListener goodsListener;
    private int page = 1;
    private String topic_id = "";
    private String banner = "";
    private String keyword = "";
    private String share_url = "";
    private String share_vip_url = "";
    private String title = "";
    private String type = "";

    GoodsService mService;
    BrandGoodsPresenter mPresenter;
    VipGoodsAdapter mAdapter;
    HeaderViewRecyclerAdapter goodsHeaderAdapter;
    LinearLayoutManager layoutManager;

    @OnClick(R.id.back)
    void back() {
        finish();
    }

    @OnClick(R.id.toolbar_share)
    void share() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("title", title);
        hashMap.put("desc", "听说只有长得好看的人才能看到这个分享哦！！");
        Log.e("URL",AppContext.getShopAvater(BrandTopicActivity.this));
        if (type.equals("normal")){
            hashMap.put("url", share_url);
        }else {
            hashMap.put("url", share_vip_url);
        }
        XmbPopubWindow.showShopChooseWindow(BrandTopicActivity.this,hashMap,"Show_Goods_Share","1");
//        XmbPopubWindow.showGoodsShare(BrandTopicActivity.this, hashMap);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        ButterKnife.bind(this);
        initApiInfo();
        initParam();
        setView();
    }

    protected void initParam(){
        topic_id = getIntent().getStringExtra("topic_id");
        share_url = getIntent().getStringExtra("share_url");
        share_vip_url = getIntent().getStringExtra("share_vip_url");
        type = getIntent().getStringExtra("type");
        banner = getIntent().getStringExtra("banner");
        title = getIntent().getStringExtra("title");
        toolbarTitleTextView.setText(title);
    }

    public void initApiInfo() {
        mService = new GoodsService();
        mPresenter = new BrandGoodsPresenter(this, mService);
    }

    private void setView() {
        mAdapter = new VipGoodsAdapter(this, goodsList);
        mAdapter.setOnClickListener(new VipGoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                XmbIntent.startIntentGoodsPreview(BrandTopicActivity.this, goodsList.get(position).goods_name, goodsList.get(position).buy_url, goodsList.get(position).share_url);
            }

            @Override
            public void shareGood(int position) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("title", goodsList.get(position).goods_name);
                hashMap.put("desc", "【有人@你】你有一个分享尚未点击");
                hashMap.put("url", goodsList.get(position).share_url);

                XmbPopubWindow.showShopChooseWindow(BrandTopicActivity.this,hashMap,"Show_Goods_Share","1");
            }
        });


        goodsHeaderAdapter = new HeaderViewRecyclerAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(goodsHeaderAdapter);
        LinearLayout bannerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_brand,mRecyclerView,false);
        ImageView bannerImageView = (ImageView) bannerLayout.findViewById(R.id.banner);
        goodsHeaderAdapter.addHeaderView(bannerLayout);
        Glide.with(this)
                .load(banner)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(bannerImageView);
        request();
    }

    public void goodsResponse(List<Goods> goodList) {
        if (goodList.size() < 20) {
            goodsHeaderAdapter.removeFooterView();
            mRecyclerView.removeOnScrollListener(goodsListener);
            goodsListener = null;
        } else {
            goodsHeaderAdapter.removeFooterView();
            goodsHeaderAdapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.load_more_anim, mRecyclerView, false));
            mRecyclerView.removeOnScrollListener(goodsListener);
            goodsListener = new EndlessRecyclerOnScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int currentPage) {
                    request();
                }
            };
            mRecyclerView.addOnScrollListener(goodsListener);
        }
        goodsList.addAll(goodList);
        goodsHeaderAdapter.notifyDataSetChanged();
        animLoading.setVisibility(View.GONE);
    }

    protected void request() {
        if (page == 1) {
            animLoading.setVisibility(View.VISIBLE);
            goodsList.clear();
            goodsHeaderAdapter.notifyDataSetChanged();
        }
        mPresenter.getBrandGoodsList(GoodsService.gen_brand_goods_list_params(AppContext.getToken(this), topic_id, type, "default", page, keyword));
        page += 1;
    }
}
