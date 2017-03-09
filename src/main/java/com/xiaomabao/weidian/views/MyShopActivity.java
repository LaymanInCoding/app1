package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.adapters.ShopGoodsAdapter;
import com.xiaomabao.weidian.models.ShopGoods;
import com.xiaomabao.weidian.presenters.MyShopPresenter;
import com.xiaomabao.weidian.services.GoodsService;
import com.xiaomabao.weidian.util.XmbIntent;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.xiaomabao.weidian.ui.EndlessRecyclerOnScrollListener;
import com.xiaomabao.weidian.ui.HeaderViewRecyclerAdapter;

public class MyShopActivity extends AppCompatActivity {

    GoodsService mService;
    MyShopPresenter mPresenter;
    ShopGoodsAdapter shopGoodsAdapter;
    List<ShopGoods> shopGoodsList = new ArrayList<>();

    @BindView(R.id.shop_background)
    ImageView shopBackgroundImageView;
    @BindView(R.id.shop_avatar)
    ImageView shopAvatarImageView;
    @BindView(R.id.shop_name)
    TextView shopNameTextView;
    @BindView(R.id.shop_desc)
    TextView shopDescTextView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.anim_loading)
    View animLoadingView;
    @BindView(R.id.no_goods_container)
    View no_goods_container;

    @OnClick(R.id.back) void back() {
        finish();
    }
    @OnClick(R.id.share) void share() {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("title",AppContext.getShopName(this));
        hashMap.put("desc",AppContext.getShopDescription(this));
        hashMap.put("logo",AppContext.getShopAvater(this));
        XmbPopubWindow.showShopChooseWindow(this,hashMap,"Show_Share","0");
//        XmbPopubWindow.showShare(this,AppContext.getShopShareQr(this),AppContext.getShopShareUrl(this),hashMap);
    }
    @OnClick(R.id.preview) void preview() {
        startActivity(new Intent(MyShopActivity.this,ShopPreviewActivity.class));
    }
    private int page = 1;
    private HeaderViewRecyclerAdapter goodsHeaderAdapter;
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    EndlessRecyclerOnScrollListener goodListener  = new EndlessRecyclerOnScrollListener(layoutManager) {
        @Override
        public void onLoadMore(int currentPage) {
            page += 1;
            mPresenter.shop_goods(GoodsService.gen_shop_goods_params(AppContext.getToken(MyShopActivity.this),page));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_shop);
        ButterKnife.bind(this);
        setView();
        initRecycleView();
        initApiInfo();
    }

    public void initApiInfo(){
        mService = new GoodsService();
        mPresenter = new MyShopPresenter(this, mService);
        mPresenter.shop_goods(GoodsService.gen_shop_goods_params(AppContext.getToken(this),page));
    }

    private void setView(){
        shopNameTextView.setText(AppContext.getShopName(this));
        shopDescTextView.setText(AppContext.getShopDescription(this));
        Glide.with(this)
                .load(AppContext.getShopAvater(this))
                .placeholder(R.mipmap.mitouxiang)
                .dontAnimate()
                .error(R.mipmap.mitouxiang)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(shopAvatarImageView);
        Glide.with(this)
                .load(AppContext.getShopBackground(this))
                .dontAnimate()
                .placeholder(R.mipmap.index_top_bg)
                .error(R.mipmap.index_top_bg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(shopBackgroundImageView);
    }

    private void initRecycleView(){
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        shopGoodsAdapter = new ShopGoodsAdapter(this,shopGoodsList);
        goodsHeaderAdapter = new HeaderViewRecyclerAdapter(shopGoodsAdapter);
        goodsHeaderAdapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.load_more_anim, recyclerView, false));
        recyclerView.addOnScrollListener(goodListener);
        shopGoodsAdapter.setOnClickListener(new ShopGoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                XmbIntent.startIntentGoodsPreview(MyShopActivity.this,shopGoodsList.get(position).goods_name,shopGoodsList.get(position).preview_url,shopGoodsList.get(position).share_url);
            }

            @Override
            public void offSale(int position) {
                mPresenter.off_sale(GoodsService.gen_off_sale_params(AppContext.getToken(MyShopActivity.this),shopGoodsList.get(position).goods_id),position);
            }

            @Override
            public void shareGood(int position) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("title",shopGoodsList.get(position).goods_name);
                hashMap.put("desc","【有人@你】你有一个分享尚未点击");
                hashMap.put("logo",AppContext.getShopAvater(MyShopActivity.this));
                hashMap.put("url",shopGoodsList.get(position).share_url);
                XmbPopubWindow.showShopChooseWindow(MyShopActivity.this,hashMap,"Show_Goods_Share","0");
//                XmbPopubWindow.showGoodsShare(MyShopActivity.this,hashMap);
            }
        });
        recyclerView.setAdapter(goodsHeaderAdapter);
    }

    public void notifyAddRecycleView(List<ShopGoods> shopGoods){
        if(shopGoods.size() == 0 && page == 1){
            no_goods_container.setVisibility(View.VISIBLE);
        }else{
            no_goods_container.setVisibility(View.GONE);
        }
        if (shopGoods.size() < 20){
            goodsHeaderAdapter.removeFooterView();
            recyclerView.removeOnScrollListener(goodListener);
        }
        shopGoodsList.addAll(shopGoods);
        goodsHeaderAdapter.notifyDataSetChanged();
        animLoadingView.setVisibility(View.GONE);
    }

    public void notifyDelRecycleView(int position){
        shopGoodsList.remove(position);
        goodsHeaderAdapter.notifyDataSetChanged();
        animLoadingView.setVisibility(View.GONE);
        if(shopGoodsList.size() == 0){
            no_goods_container.setVisibility(View.VISIBLE);
        }else{
            no_goods_container.setVisibility(View.GONE);
        }
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
