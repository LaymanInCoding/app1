package com.xiaomabao.weidian.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.adapters.ChildCategoryAdapter;
import com.xiaomabao.weidian.adapters.GoodsAdapter;
import com.xiaomabao.weidian.models.Category;
import com.xiaomabao.weidian.models.Goods;
import com.xiaomabao.weidian.models.SubCategory;
import com.xiaomabao.weidian.presenters.SubCategoryPresenter;
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

/**
 * Created by ming on 2017/4/25.
 */
public class SubCatrgoryActivity extends AppCompatActivity {

    @BindView(R.id.search_text)
    TextView mSearchText;
    @BindView(R.id.toolbar_right)
    TextView mToolbarRight;
    @BindView(R.id.toolbar_right_share)
    ImageView share_imageView;
    @BindView(R.id.goods_recyclerView)
    RecyclerView mGoodsRecyclerView;
    @BindView(R.id.sort_default)
    RelativeLayout mSortDefault;
    @BindView(R.id.sort_profit)
    RelativeLayout mSortProfit;
    @BindView(R.id.sort_price_high)
    RelativeLayout mSortPriceHigh;
    @BindView(R.id.sort_price_low)
    RelativeLayout mSortPriceLow;
    @BindView(R.id.no_goods_container)
    View no_goods_container;
    @BindView(R.id.sort_container)
    RelativeLayout mSortContainer;
    @BindView(R.id.anim_loading)
    View mLoadingView;
    @BindView(R.id.onekey_share)
    ImageView onekey_share;
    @BindView(R.id.sort_default_check)
    View defaultCheck;
    @BindView(R.id.sort_price_high_check)
    View priceHighCheck;
    @BindView(R.id.sort_price_low_check)
    View priceLowCheck;
    @BindView(R.id.sort_profit_check)
    View profitCheck;


    private String type;
    private String cat_id;
    private String sort = "default";
    private String keyword = "";
    private int page = 1;

    private List<Goods> mGoodsList = new ArrayList<>();
    private List<Category.ChildCategory> mCategoryList = new ArrayList<>();

    private GoodsService mService;
    private SubCategoryPresenter mPresenter;

    private HeaderViewRecyclerAdapter goodsHeaderAdapter;
    private GoodsAdapter goodsAdapter;
    private ChildCategoryAdapter categoryAdapter;

    private View mView;  //headerView
    private RecyclerView recyclerView;  //headerRecyclerView

    private Context mContext;
    LinearLayoutManager layoutManagerGood = new LinearLayoutManager(this);
    EndlessRecyclerOnScrollListener goodListener = new EndlessRecyclerOnScrollListener(layoutManagerGood) {
        @Override
        public void onLoadMore(int currentPage) {
            page += 1;
            requestData();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategory);
        ButterKnife.bind(this);
        mContext = this;
        cat_id = getIntent().getStringExtra("cat_id");
        type = getIntent().getStringExtra("type");
        mView = LayoutInflater.from(this).inflate(R.layout.activity_goods_header, null);
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerViewSecond);
        initApiInfo();
        goodsAdapter = new GoodsAdapter(this, mGoodsList);
        if (type.equals("vip")) {
            goodsAdapter.setIsVip(true);
            onekey_share.setVisibility(View.GONE);
            mToolbarRight.setVisibility(View.GONE);
            share_imageView.setVisibility(View.VISIBLE);
        } else {
            mToolbarRight.setVisibility(View.VISIBLE);
            share_imageView.setVisibility(View.GONE);
        }
        goodsAdapter.setOnClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (AppContext.isLogin()) {
                    XmbIntent.startIntentGoodsPreview(mContext, mGoodsList.get(position).goods_name,
                            mGoodsList.get(position).buy_url, mGoodsList.get(position).share_url);
                } else {
                    startActivity(new Intent(mContext, PhoneLoginActivity.class));
                }
            }

            @Override
            public void toggleSale(int position) {
                if (!AppContext.isLogin()) {
                    startActivity(new Intent(mContext, PhoneLoginActivity.class));
                } else {
                    XmbPopubWindow.showTranparentLoading(SubCatrgoryActivity.this);
                    if (mGoodsList.get(position).goods_sale_status == 0) {
                        mPresenter.onSale(GoodsService.gen_on_sale_params(AppContext.getToken(mContext), mGoodsList.get(position).goods_id), position);
                    } else {
                        mPresenter.offSale(GoodsService.gen_off_sale_params(AppContext.getToken(mContext), mGoodsList.get(position).goods_id), position);
                    }
                }
            }

            @Override
            public void shareGood(int position) {
                if (AppContext.isLogin()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("title", mGoodsList.get(position).goods_name);
                    hashMap.put("desc", "【有人@你】你有一个分享尚未点击");
                    hashMap.put("url", mGoodsList.get(position).share_url);
                    XmbPopubWindow.showShopChooseWindow(SubCatrgoryActivity.this, hashMap, "Show_Goods_Share", "0");
                } else {
                    startActivity(new Intent(mContext, PhoneLoginActivity.class));
                }
            }
        });
        goodsHeaderAdapter = new HeaderViewRecyclerAdapter(goodsAdapter);
        categoryAdapter = new ChildCategoryAdapter(this, mCategoryList);
        categoryAdapter.setOnClickListener((int position) -> {
            cat_id = mCategoryList.get(position).cat_id;
            keyword = "";
            page = 1;
            requestData();
        });
        layoutManagerGood.setOrientation(LinearLayoutManager.VERTICAL);
        mGoodsRecyclerView.setHasFixedSize(true);
        mGoodsRecyclerView.setLayoutManager(layoutManagerGood);
        mGoodsRecyclerView.setAdapter(goodsHeaderAdapter);
        GridLayoutManager layoutManagerChild = new GridLayoutManager(this, 4);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManagerChild);
        recyclerView.setAdapter(categoryAdapter);
        requestData();
    }

    private void requestData() {
        if (page == 1) {
            mLoadingView.setVisibility(View.VISIBLE);
        }
        if (type.equals("normal")) {
            mPresenter.getGoodsList(GoodsService.gen_goods_list2_params(AppContext.getToken(this), cat_id, sort, page, keyword));
        } else {
            mPresenter.getVipGoodsList(GoodsService.gen_vip_goods_list_params(AppContext.getToken(this), cat_id, sort, page, keyword));
        }
    }

    private void initApiInfo() {
        mService = new GoodsService();
        mPresenter = new SubCategoryPresenter(this, mService);
    }

    public void goodsResponse(SubCategory data) {
        mLoadingView.setVisibility(View.GONE);
        if (page == 1) {
            mGoodsList.clear();
            if (data.category.size() != 0) {
                mCategoryList.clear();
                mCategoryList.addAll(data.category);
            }
            categoryAdapter.notifyDataSetChanged();
            if (data.goods.size() == 0) {
                no_goods_container.setVisibility(View.VISIBLE);
                goodsHeaderAdapter.removeHeaderView();
            } else {
                goodsHeaderAdapter.removeFooterView();
                mGoodsRecyclerView.removeOnScrollListener(goodListener);
                goodsHeaderAdapter.notifyDataSetChanged();
                if (goodsHeaderAdapter.getHeaderCount() == 0) {
                    goodsHeaderAdapter.addHeaderView(mView);
                }
                no_goods_container.setVisibility(View.GONE);
            }
        }
        if (data.goods.size() < 20) {
            goodsHeaderAdapter.removeFooterView();
            mGoodsRecyclerView.removeOnScrollListener(goodListener);
        } else {
            goodsHeaderAdapter.removeFooterView();
            goodsHeaderAdapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.load_more_anim, mGoodsRecyclerView, false));
            mGoodsRecyclerView.removeOnScrollListener(goodListener);
            goodListener = new EndlessRecyclerOnScrollListener(layoutManagerGood) {
                @Override
                public void onLoadMore(int currentPage) {
                    page += 1;
                    requestData();
                }
            };
            mGoodsRecyclerView.addOnScrollListener(goodListener);
        }
        mGoodsList.addAll(data.goods);
        goodsHeaderAdapter.notifyDataSetChanged();
    }

    public void refreshGoodsRecycleView(int position) {
        if (mGoodsList.get(position).goods_sale_status == 1) {
            mGoodsList.get(position).goods_sale_status = 0;
        } else {
            mGoodsList.get(position).goods_sale_status = 1;
        }
        goodsHeaderAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.search_text, R.id.toolbar_right, R.id.sort_default, R.id.sort_profit, R.id.toolbar_right_share,
            R.id.sort_price_high, R.id.sort_price_low, R.id.sort_container, R.id.back, R.id.onekey_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_text:
                Intent intent = new Intent();
                intent.setClass(this, SearchActivity.class);
                if (type.equals("normal")) {
                    intent.putExtra("vip", "0");
                } else {
                    intent.putExtra("vip", "1");
                }
                startActivity(intent);
                overridePendingTransition(R.anim.pop_right_in, R.anim.pop_right_out);
                break;
            case R.id.toolbar_right:
                mSortContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.sort_default:
                sort = "default";
                page = 1;
                defaultCheck.setVisibility(View.VISIBLE);
                priceHighCheck.setVisibility(View.GONE);
                priceLowCheck.setVisibility(View.GONE);
                profitCheck.setVisibility(View.GONE);
                requestData();
                mSortContainer.setVisibility(View.GONE);
                break;
            case R.id.sort_profit:
                sort = "profit";
                page = 1;
                defaultCheck.setVisibility(View.GONE);
                priceHighCheck.setVisibility(View.GONE);
                priceLowCheck.setVisibility(View.GONE);
                profitCheck.setVisibility(View.VISIBLE);
                requestData();
                mSortContainer.setVisibility(View.GONE);
                break;
            case R.id.sort_price_high:
                sort = "price_high";
                page = 1;
                defaultCheck.setVisibility(View.GONE);
                priceHighCheck.setVisibility(View.VISIBLE);
                priceLowCheck.setVisibility(View.GONE);
                profitCheck.setVisibility(View.GONE);
                requestData();
                mSortContainer.setVisibility(View.GONE);
                break;
            case R.id.sort_price_low:
                sort = "price_low";
                page = 1;
                defaultCheck.setVisibility(View.GONE);
                priceHighCheck.setVisibility(View.GONE);
                priceLowCheck.setVisibility(View.VISIBLE);
                profitCheck.setVisibility(View.GONE);
                requestData();
                mSortContainer.setVisibility(View.GONE);
                break;
            case R.id.sort_container:
                mSortContainer.setVisibility(View.GONE);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.onekey_share:
                if (AppContext.isLogin()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("title", AppContext.getShopName(mContext));
                    hashMap.put("desc", AppContext.getShopDescription(mContext));
                    hashMap.put("url", AppContext.getShopShareVipUrl(mContext));
                    XmbPopubWindow.showShopChooseWindow(SubCatrgoryActivity.this, hashMap, "Show_Share", "3");
                } else {
                    startActivity(new Intent(mContext, PhoneLoginActivity.class));
                }
                break;
            case R.id.toolbar_right_share:
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("title", AppContext.getShopName(this));
                hashMap.put("desc", AppContext.getShopDescription(this));
                hashMap.put("url", AppContext.getShopShareVipUrl(this));
                XmbPopubWindow.showShopChooseWindow(this, hashMap, "Show_Share", "1");
                break;
        }
    }
}
