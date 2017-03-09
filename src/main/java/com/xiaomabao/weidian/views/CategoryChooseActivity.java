package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.adapters.BrandAdapter;
import com.xiaomabao.weidian.adapters.CategoryAdapter;
import com.xiaomabao.weidian.adapters.ChildCategoryAdapter;
import com.xiaomabao.weidian.adapters.GoodsAdapter;
import com.xiaomabao.weidian.models.Brand;
import com.xiaomabao.weidian.models.Category;
import com.xiaomabao.weidian.models.Goods;
import com.xiaomabao.weidian.presenters.CategoryChoosePresenter;
import com.xiaomabao.weidian.services.GoodsService;
import com.xiaomabao.weidian.util.InputSoftUtil;
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

import org.w3c.dom.Text;

public class CategoryChooseActivity extends AppCompatActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerViewGoods;
//    @BindView(R.id.recyclerViewSecond)
//    RecyclerView recyclerViewChild;
    @BindView(R.id.goods_recyclerView)
    RecyclerView recyclerViewGood;
    @BindView(R.id.brand_recyclerView)
    RecyclerView recyclerViewBrand;
    @BindView(R.id.anim_loading)
    View loadingView;
    @BindView(R.id.anim_loading_good)
    View loadingGoodView;
    @BindView(R.id.sort_container)
    View sortContainerView;
    @BindView(R.id.search_text)
    TextView searchText;
    @BindView(R.id.toolbar_right)
    View toolbar_right;
    @BindView(R.id.sort_default_check)
    View defaultCheck;
    @BindView(R.id.sort_price_high_check)
    View priceHighCheck;
    @BindView(R.id.sort_price_low_check)
    View priceLowCheck;
    @BindView(R.id.sort_profit_check)
    View profitCheck;
    @BindView(R.id.no_goods_container)
    View no_goods_container;
    @BindView(R.id.onekey_share)
    ImageView onekey_share;


    @OnClick(R.id.back)
    void back() {
        finish();
    }

    @OnClick(R.id.sort_container)
    void hideSortView() {
        sortContainerView.setVisibility(View.GONE);
    }

    @OnClick(R.id.toolbar_right)
    void showSortView() {
        sortContainerView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.sort_default)
    void sortByDefault() {
        sort = "default";
        page = 1;
        defaultCheck.setVisibility(View.VISIBLE);
        priceHighCheck.setVisibility(View.GONE);
        priceLowCheck.setVisibility(View.GONE);
        profitCheck.setVisibility(View.GONE);
        requestGoodsList(1);
        hideSortView();
    }

    @OnClick(R.id.onekey_share)
    void showShareView() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("title", AppContext.getShopName(this));
        hashMap.put("desc", AppContext.getShopDescription(this));
        hashMap.put("url", AppContext.getShopShareVipUrl(this));
        XmbPopubWindow.showShopChooseWindow(this, hashMap, "Show_Share", "3");
    }

    @OnClick(R.id.sort_price_high)
    void sortByPriceHigh() {
        sort = "price_high";
        page = 1;
        defaultCheck.setVisibility(View.GONE);
        priceHighCheck.setVisibility(View.VISIBLE);
        priceLowCheck.setVisibility(View.GONE);
        profitCheck.setVisibility(View.GONE);
        requestGoodsList(1);
        hideSortView();
    }

    @OnClick(R.id.sort_price_low)
    void sortByPriceLow() {
        sort = "price_low";
        page = 1;
        defaultCheck.setVisibility(View.GONE);
        priceHighCheck.setVisibility(View.GONE);
        priceLowCheck.setVisibility(View.VISIBLE);
        profitCheck.setVisibility(View.GONE);
        requestGoodsList(1);
        hideSortView();
    }

    @OnClick(R.id.sort_profit)
    void sortByProfit() {
        sort = "profit";
        page = 1;
        defaultCheck.setVisibility(View.GONE);
        priceHighCheck.setVisibility(View.GONE);
        priceLowCheck.setVisibility(View.GONE);
        profitCheck.setVisibility(View.VISIBLE);
        requestGoodsList(1);
        hideSortView();
    }

    private CategoryAdapter categoryAdapter;
    private ChildCategoryAdapter childCategoryAdapter;
    private List<Category> categories = new ArrayList<>();
    private List<Category.ChildCategory> categoriesChild = new ArrayList<>();

    private List<Goods> goodsList = new ArrayList<>();
    private List<Brand> brandsList = new ArrayList<>();

    GoodsService mService;
    CategoryChoosePresenter mPresenter;

    private String cat_id = "-999999";
    private String sort = "default";
    private int page = 1, brand_page = 1;
    private String keyword = "";

    private LayoutInflater mInflater;
    private View mView;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_choose);
        ButterKnife.bind(this);
        mInflater = LayoutInflater.from(this);
        initApiInfo();
        initRecycleView();
    }

    private HeaderViewRecyclerAdapter goodsHeaderAdapter, brandHeaderAdapter;
    LinearLayoutManager layoutManagerGood = new LinearLayoutManager(this);
    EndlessRecyclerOnScrollListener goodListener = new EndlessRecyclerOnScrollListener(layoutManagerGood) {
        @Override
        public void onLoadMore(int currentPage) {
            page += 1;
            requestGoodsList(0);
        }
    };
    LinearLayoutManager layoutManagerBrand = new LinearLayoutManager(this);
    EndlessRecyclerOnScrollListener brandListener = new EndlessRecyclerOnScrollListener(layoutManagerBrand) {
        @Override
        public void onLoadMore(int currentPage) {
            brand_page += 1;
            requestBrandList();
        }
    };

    private void initRecycleView() {
        mView = mInflater.inflate(R.layout.activity_goods_header,null);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerViewSecond);
        categoryAdapter = new CategoryAdapter(this, categories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewGoods.setHasFixedSize(true);
        recyclerViewGoods.setLayoutManager(layoutManager);
        recyclerViewGoods.setAdapter(categoryAdapter);

        categoryAdapter.setOnClickListener((int position) -> {
            for (int i = 0; i < categories.size(); i++) {
                if (i == position) {
                    categories.get(position).selected = 1;
                } else {
                    categories.get(i).selected = 0;
                }
            }
            if (categories.get(position).type.equals("brand")) {
                toolbar_right.setVisibility(View.INVISIBLE);
                recyclerViewGood.setVisibility(View.GONE);
                no_goods_container.setVisibility(View.GONE);
                recyclerViewBrand.setVisibility(View.VISIBLE);
            } else if (!categories.get(position).cat_id.equals(cat_id)) {
                toolbar_right.setVisibility(View.VISIBLE);
                cat_id = categories.get(position).cat_id;
                recyclerViewBrand.setVisibility(View.GONE);
                recyclerViewGood.setVisibility(View.VISIBLE);
                keyword = "";
                requestGoodsList(1);
            } else {
                toolbar_right.setVisibility(View.VISIBLE);
                recyclerViewGood.setVisibility(View.VISIBLE);
                no_goods_container.setVisibility(View.GONE);
                recyclerViewBrand.setVisibility(View.GONE);
            }
            if (searchText.isFocused()) {
                InputSoftUtil.hideSoftInput(this, searchText);
                searchText.clearFocus();
            }
            categoryAdapter.notifyDataSetChanged();
            handlerChildCategory(categories.get(position).child);
        });

        childCategoryAdapter = new ChildCategoryAdapter(this, categoriesChild);
        childCategoryAdapter.setOnClickListener((int position) -> {
            if (!categoriesChild.get(position).cat_id.equals(cat_id)) {
                cat_id = categoriesChild.get(position).cat_id;
                keyword = "";
                requestGoodsList(1);
            }
            if (searchText.isFocused()) {
                InputSoftUtil.hideSoftInput(this, searchText);
                searchText.clearFocus();
            }
        });
        GridLayoutManager layoutManagerChild = new GridLayoutManager(this, 4);
//        recyclerViewChild.setHasFixedSize(true);
//        recyclerViewChild.setLayoutManager(layoutManagerChild);
//        recyclerViewChild.setAdapter(childCategoryAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManagerChild);
        mRecyclerView.setAdapter(childCategoryAdapter);

        GoodsAdapter goodsAdapter = new GoodsAdapter(this, goodsList);
        goodsAdapter.setOnClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                XmbIntent.startIntentGoodsPreview(CategoryChooseActivity.this, goodsList.get(position).goods_name, goodsList.get(position).buy_url, goodsList.get(position).share_url);
            }

            @Override
            public void toggleSale(int position) {
                XmbPopubWindow.showTranparentLoading(CategoryChooseActivity.this);
                if (goodsList.get(position).goods_sale_status == 0) {
                    mPresenter.onSale(GoodsService.gen_on_sale_params(AppContext.getToken(CategoryChooseActivity.this), goodsList.get(position).goods_id), position);
                } else {
                    mPresenter.offSale(GoodsService.gen_off_sale_params(AppContext.getToken(CategoryChooseActivity.this), goodsList.get(position).goods_id), position);
                }
            }

            @Override
            public void shareGood(int position) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("title", goodsList.get(position).goods_name);
                hashMap.put("desc", "【有人@你】你有一个分享尚未点击");
                hashMap.put("url", goodsList.get(position).share_url);
                XmbPopubWindow.showShopChooseWindow(CategoryChooseActivity.this, hashMap, "Show_Goods_Share", "0");
//                XmbPopubWindow.showGoodsShare(CategoryChooseActivity.this, hashMap);
            }
        });

        goodsHeaderAdapter = new HeaderViewRecyclerAdapter(goodsAdapter);
        layoutManagerGood.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewGood.setHasFixedSize(true);
        recyclerViewGood.setLayoutManager(layoutManagerGood);
        recyclerViewGood.setAdapter(goodsHeaderAdapter);
        BrandAdapter brandAdapter = new BrandAdapter(this, brandsList, 0);
        brandAdapter.setOnClickListener((int position) -> {
            Brand brand = brandsList.get(position);
            String type = brand.type;
            if (type.equals("0")) {
                Intent intent = new Intent(CategoryChooseActivity.this, BrandTopicActivity.class);
                intent.putExtra("topic_id", brand.topic_id);
                intent.putExtra("title", brand.title);
                intent.putExtra("share_url", brand.share_url);
                intent.putExtra("share_vip_url", brand.share_vip_url);
                intent.putExtra("banner", brand.banner);
                intent.putExtra("type", "normal");
                startActivity(intent);
            } else if (type.equals("1")) {
                Intent intent1 = new Intent(CategoryChooseActivity.this, WebViewActivity.class);
                intent1.putExtra("share_desc", brand.share_desc);
                intent1.putExtra("name", brand.title);
                intent1.putExtra("url", brand.url);
                startActivity(intent1);
            }
        });
        brandHeaderAdapter = new HeaderViewRecyclerAdapter(brandAdapter);
        layoutManagerBrand.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewBrand.setHasFixedSize(true);
        recyclerViewBrand.setLayoutManager(layoutManagerBrand);
        recyclerViewBrand.setAdapter(brandHeaderAdapter);

        mPresenter.getCategoryList(GoodsService.gen_category_list_params(AppContext.getToken(this)));
        requestBrandList();


        searchText.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(this, SearchActivity.class);
            intent.putExtra("vip", "0");
            startActivity(intent);
            this.overridePendingTransition(R.anim.pop_right_in, R.anim.pop_right_out);
        });
    }

    public void requestGoodsList(int type) {
        recyclerViewGood.setVisibility(View.VISIBLE);
        recyclerViewBrand.setVisibility(View.GONE);
        if (type == 1) {
            page = 1;
            loadingGoodView.setVisibility(View.VISIBLE);
            goodsHeaderAdapter.removeFooterView();
            recyclerViewGood.removeOnScrollListener(goodListener);
            goodsList.clear();
            goodsHeaderAdapter.notifyDataSetChanged();
        }
        mPresenter.getGoodsList(GoodsService.gen_goods_list_params(AppContext.getToken(this), cat_id, sort, page, keyword));
    }

    public void requestBrandList() {
        mPresenter.getBrandList(GoodsService.gen_brand_list_params(AppContext.getToken(this), brand_page));
    }

    public void initApiInfo() {
        mService = new GoodsService();
//        mPresenter = new CategoryChoosePresenter(this, mService);
    }

    public void handlerRecycleView(List<Category> categoriesList) {
        categories.addAll(categoriesList);
        categoryAdapter.notifyDataSetChanged();
        handlerChildCategory(categoriesList.get(0).child);
    }

    public void handlerChildCategory(List<Category.ChildCategory> list) {
        categoriesChild.clear();
        goodsHeaderAdapter.notifyDataSetChanged();
        categoriesChild.addAll(list);
        if (categoriesChild.size() == 0) {
//            recyclerViewChild.setVisibility(View.GONE);
            //removeHeaderView();
            goodsHeaderAdapter.removeHeaderView();
        } else {
            //addHeaderView();
            if (goodsHeaderAdapter.getHeaderCount() == 0){
                goodsHeaderAdapter.addHeaderView(mView);
            }
//            recyclerViewChild.setVisibility(View.VISIBLE);
        }
        childCategoryAdapter.notifyDataSetChanged();
    }

    public void handlerBrandRecycleView(List<Brand> list) {
        if (list.size() < 20) {
            brandHeaderAdapter.removeFooterView();
            recyclerViewBrand.removeOnScrollListener(brandListener);
        } else {
            brandHeaderAdapter.removeFooterView();
            brandHeaderAdapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.load_more_anim, recyclerViewBrand, false));
            recyclerViewBrand.removeOnScrollListener(brandListener);
            brandListener = new EndlessRecyclerOnScrollListener(layoutManagerBrand) {
                @Override
                public void onLoadMore(int currentPage) {
                    brand_page += 1;
                    requestBrandList();
                }
            };
            recyclerViewBrand.addOnScrollListener(brandListener);
        }
        brandsList.addAll(list);
        brandHeaderAdapter.notifyDataSetChanged();
        hideLoading();
    }


    public void goodsResponse(List<Goods> list) {
        if (page == 1 && list.size() == 0) {
            no_goods_container.setVisibility(View.VISIBLE);
        } else {
            no_goods_container.setVisibility(View.GONE);
        }
        if (list.size() < 20) {
            goodsHeaderAdapter.removeFooterView();
            recyclerViewGood.removeOnScrollListener(goodListener);
        } else {
            goodsHeaderAdapter.removeFooterView();
            goodsHeaderAdapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.load_more_anim, recyclerViewGood, false));
            recyclerViewGood.removeOnScrollListener(goodListener);
            goodListener = new EndlessRecyclerOnScrollListener(layoutManagerGood) {
                @Override
                public void onLoadMore(int currentPage) {
                    page += 1;
                    requestGoodsList(0);
                }
            };
            recyclerViewGood.addOnScrollListener(goodListener);
        }
        goodsList.addAll(list);
        goodsHeaderAdapter.notifyDataSetChanged();
        hideLoading();
    }

    public void refreshGoodsRecycleView(int position) {
        if (goodsList.get(position).goods_sale_status == 1) {
            goodsList.get(position).goods_sale_status = 0;
        } else {
            goodsList.get(position).goods_sale_status = 1;
        }
        goodsHeaderAdapter.notifyDataSetChanged();
        hideLoading();
    }

    public void hideLoading() {
        loadingView.setVisibility(View.GONE);
        loadingGoodView.setVisibility(View.GONE);
        onekey_share.setVisibility(View.VISIBLE);
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
