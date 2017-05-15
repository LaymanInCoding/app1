package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.adapters.BrandAdapter;
import com.xiaomabao.weidian.adapters.CategoryAdapter;
import com.xiaomabao.weidian.adapters.ChildCategoryAdapter;
import com.xiaomabao.weidian.adapters.VipGoodsAdapter;
import com.xiaomabao.weidian.models.Brand;
import com.xiaomabao.weidian.models.Category;
import com.xiaomabao.weidian.models.Goods;
import com.xiaomabao.weidian.presenters.VipGoodsPresenter;
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

public class VipGoodsActivity extends AppCompatActivity {
    @BindView(R.id.anim_loading)
    View animLoading;
    @BindView(R.id.recyclerGoodsView)
    RecyclerView recyclerView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerViewFirst;
    @BindView(R.id.brand_recyclerView)
    RecyclerView recyclerViewBrand;
    //    @BindView(R.id.recyclerViewSecond)
//    RecyclerView recyclerViewChild;
    @BindView(R.id.search_text)
    TextView searchTextView;
    @BindView(R.id.no_goods_container)
    View no_goods_container;
    private EndlessRecyclerOnScrollListener goodListener;
    private LayoutInflater mInflater;
    private View mView;
    private RecyclerView mRecyclerView;


    LinearLayoutManager layoutManagerBrand = new LinearLayoutManager(this);
    EndlessRecyclerOnScrollListener brandListener = new EndlessRecyclerOnScrollListener(layoutManagerBrand) {
        @Override
        public void onLoadMore(int currentPage) {
            brand_page += 1;
            requestBrandList();
        }
    };

    private List<Goods> goodList = new ArrayList<>();
    private List<Brand> brandsList = new ArrayList<>();

    @OnClick(R.id.back)
    void back() {
        finish();
    }

    @OnClick(R.id.toolbar_right)
    void share() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("title", AppContext.getShopName(this));
        hashMap.put("desc", AppContext.getShopDescription(this));
        hashMap.put("url", AppContext.getShopShareVipUrl(this));
        XmbPopubWindow.showShopChooseWindow(this, hashMap, "Show_Share", "1");
//        XmbPopubWindow.showShare(this, AppContext.getShopShareVipQr(this), AppContext.getShopShareVipUrl(this), hashMap);
    }

    GoodsService mService;
    VipGoodsPresenter mPresenter;
    VipGoodsAdapter goodsAdapter;
    HeaderViewRecyclerAdapter goodsHeaderAdapter, brandHeaderAdapter;
    LinearLayoutManager layoutManager;

    private CategoryAdapter categoryAdapter;
    private ChildCategoryAdapter childCategoryAdapter;
    private List<Category> categories = new ArrayList<>();
    private List<Category.ChildCategory> categoriesChild = new ArrayList<>();
    private String cat_id = "-999999";
    private int page = 1, brand_page = 1;
    private String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        mInflater = LayoutInflater.from(this);
        ButterKnife.bind(this);
        initApiInfo();
        setView();

    }

    public void requestBrandList() {
        mPresenter.getBrandList(GoodsService.gen_brand_list_params(AppContext.getToken(this), brand_page));
    }

    public void initApiInfo() {
        mService = new GoodsService();
        mPresenter = new VipGoodsPresenter(this, mService);
    }

    private void setView() {
        mView = mInflater.inflate(R.layout.activity_goods_header, null);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerViewSecond);
        goodsAdapter = new VipGoodsAdapter(this, goodList);
        goodsAdapter.setOnClickListener(new VipGoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                XmbIntent.startIntentGoodsPreview(VipGoodsActivity.this, goodList.get(position).goods_name, goodList.get(position).buy_url, goodList.get(position).share_url);
            }

            @Override
            public void shareGood(int position) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("title", goodList.get(position).goods_name);
                hashMap.put("desc", "【有人@你】你有一个分享尚未点击");
                hashMap.put("url", goodList.get(position).share_url);
                XmbPopubWindow.showShopChooseWindow(VipGoodsActivity.this, hashMap, "Show_Goods_Share", "1");

            }
        });
        goodsHeaderAdapter = new HeaderViewRecyclerAdapter(goodsAdapter);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(goodsHeaderAdapter);
        mPresenter.getCategoryList(GoodsService.gen_category_list_params(AppContext.getToken(this)));
        BrandAdapter brandAdapter = new BrandAdapter(this, brandsList, 0);
        brandAdapter.setOnClickListener((int position) -> {
            Brand brand = brandsList.get(position);
            String type = brand.type;
            if (type.equals("0")) {
                Intent intent = new Intent(VipGoodsActivity.this, BrandTopicActivity.class);
                intent.putExtra("topic_id", brand.topic_id);
                intent.putExtra("title", brand.title);
                intent.putExtra("share_url", brand.share_url);
                intent.putExtra("share_vip_url", brand.share_vip_url);
                intent.putExtra("banner", brand.banner);
                intent.putExtra("type", "vip");
                startActivity(intent);
            } else if (type.equals("1")) {
                Intent intent1 = new Intent(VipGoodsActivity.this, WebViewActivity.class);
                intent1.putExtra("share_desc", brand.share_desc);
                intent1.putExtra("url", brand.url);
                intent1.putExtra("name", brand.title);
                startActivity(intent1);
            }
        });
        brandHeaderAdapter = new HeaderViewRecyclerAdapter(brandAdapter);
        layoutManagerBrand.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewBrand.setHasFixedSize(true);

        recyclerViewBrand.setLayoutManager(layoutManagerBrand);
        recyclerViewBrand.setAdapter(brandHeaderAdapter);

        requestBrandList();

        categoryAdapter = new CategoryAdapter(this, categories);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewFirst.setHasFixedSize(true);
        recyclerViewFirst.setLayoutManager(layoutManager1);
        recyclerViewFirst.setAdapter(categoryAdapter);

        categoryAdapter.setOnClickListener((int position) -> {
            for (int i = 0; i < categories.size(); i++) {
                if (i == position) {
                    categories.get(position).selected = 1;
                } else {
                    categories.get(i).selected = 0;
                }
            }
            if (categories.get(position).type.equals("brand")) {
                recyclerView.setVisibility(View.GONE);
                no_goods_container.setVisibility(View.GONE);
                recyclerViewBrand.setVisibility(View.VISIBLE);
            } else if (!categories.get(position).cat_id.equals(cat_id)) {
                page = 1;
                cat_id = categories.get(position).cat_id;
                recyclerViewBrand.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                keyword = "";
                request();
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                no_goods_container.setVisibility(View.GONE);
                recyclerViewBrand.setVisibility(View.GONE);
            }
            categoryAdapter.notifyDataSetChanged();
            handlerChildCategory(categories.get(position).child);
        });

        childCategoryAdapter = new ChildCategoryAdapter(this, categoriesChild);
        childCategoryAdapter.setOnClickListener((int position) -> {
            if (categoriesChild.get(position).show_son.equals("0")) {
                cat_id = categoriesChild.get(position).cat_id;
                page = 1;
                keyword = "";
                request();
            } else {
                Intent intent = new Intent(VipGoodsActivity.this, SubCatrgoryActivity.class);
                intent.putExtra("cat_id", categoriesChild.get(position).cat_id);
                intent.putExtra("type", "vip");
                startActivity(intent);
            }
        });
        GridLayoutManager layoutManagerChild = new GridLayoutManager(this, 4);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManagerChild);
        mRecyclerView.setAdapter(childCategoryAdapter);

        searchTextView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(this, SearchActivity.class);
            intent.putExtra("vip", "1");
            startActivity(intent);
            this.overridePendingTransition(R.anim.pop_right_in, R.anim.pop_right_out);
        });
    }

    public void handlerChildCategory(List<Category.ChildCategory> list) {
        categoriesChild.clear();
        goodsHeaderAdapter.notifyDataSetChanged();
        categoriesChild.addAll(list);
        if (categoriesChild.size() == 0) {
            goodsHeaderAdapter.removeHeaderView();
        } else {
            if (goodsHeaderAdapter.getHeaderCount() == 0) {
                goodsHeaderAdapter.addHeaderView(mView);
            }
        }
        childCategoryAdapter.notifyDataSetChanged();
    }

    protected void request() {
        if (page == 1) {
            recyclerViewBrand.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            animLoading.setVisibility(View.VISIBLE);
            goodList.clear();
            goodsHeaderAdapter.notifyDataSetChanged();
        }
        goodsHeaderAdapter.removeFooterView();
        recyclerView.removeOnScrollListener(goodListener);
        mPresenter.getVipGoodsList(GoodsService.gen_vip_goods_list_params(AppContext.getToken(this), cat_id, "default", page, keyword));
        page += 1;
    }

    public void goodsResponse(List<Goods> goodsList) {
        if (goodList.size() == 0 && goodsList.size() == 0) {
            no_goods_container.setVisibility(View.VISIBLE);
        } else {
            no_goods_container.setVisibility(View.GONE);
        }
        if (goodsList.size() < 20) {
            goodsHeaderAdapter.removeFooterView();
            recyclerView.removeOnScrollListener(goodListener);
            goodListener = null;
        } else {
            goodsHeaderAdapter.removeFooterView();
            goodsHeaderAdapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.load_more_anim, recyclerView, false));
            recyclerView.removeOnScrollListener(goodListener);
            goodListener = new EndlessRecyclerOnScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int currentPage) {
                    request();
                }
            };
            recyclerView.addOnScrollListener(goodListener);
        }
        goodList.addAll(goodsList);
        goodsHeaderAdapter.notifyDataSetChanged();
        animLoading.setVisibility(View.GONE);
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
        animLoading.setVisibility(View.GONE);
    }

    public void handlerRecycleView(List<Category> categoriesList) {
        categories.addAll(categoriesList);
        categoryAdapter.notifyDataSetChanged();
        handlerChildCategory(categoriesList.get(0).child);
        recyclerViewFirst.setVisibility(View.VISIBLE);
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
