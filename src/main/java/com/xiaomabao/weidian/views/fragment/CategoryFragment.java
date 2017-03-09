package com.xiaomabao.weidian.views.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.adapters.BrandAdapter;
import com.xiaomabao.weidian.adapters.GoodsAdapter;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Brand;
import com.xiaomabao.weidian.models.Goods;
import com.xiaomabao.weidian.rx.RxManager;
import com.xiaomabao.weidian.services.GoodsService;
import com.xiaomabao.weidian.ui.EndlessRecyclerOnScrollListener;
import com.xiaomabao.weidian.ui.HeaderViewRecyclerAdapter;
import com.xiaomabao.weidian.util.LogUtils;
import com.xiaomabao.weidian.util.XmbIntent;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.BrandTopicActivity;
import com.xiaomabao.weidian.views.WebViewActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by de on 2017/3/6.
 */
public class CategoryFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.anim_loading)
    View loadingView;
    private View mRootView;

    private GoodsAdapter mGoodsAdapter;
    private BrandAdapter mBrandAdapter;

    private CategoryPresenter mPresenter;
    private GoodsService mService;

    private int page = 1;
    private int brand_page = 1;
    private String sort = "default";
    private String cat_id = "";
    private String type = "";
    private String keyword = "";
    private List<Goods> goodsList = new ArrayList<>();
    private List<Brand> brandsList = new ArrayList<>();
    private RxManager mRxManager = new RxManager();

    private View mHeaderView;
    private RecyclerView mHeaderRecyclerView;

    private HeaderViewRecyclerAdapter goodsHeaderAdapter, brandHeaderAdapter;
    LinearLayoutManager layoutManagerGood = new LinearLayoutManager(getContext());
    EndlessRecyclerOnScrollListener goodListener = new EndlessRecyclerOnScrollListener(layoutManagerGood) {
        @Override
        public void onLoadMore(int currentPage) {
            page += 1;
            requestGoodsList(0);
        }
    };
    LinearLayoutManager layoutManagerBrand = new LinearLayoutManager(getContext());
    EndlessRecyclerOnScrollListener brandListener = new EndlessRecyclerOnScrollListener(layoutManagerBrand) {
        @Override
        public void onLoadMore(int currentPage) {
            brand_page += 1;
            requestBrandList();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, mRootView);
        loadingView.setVisibility(View.GONE);
        cat_id = getArguments().getString("category_id");
        type = getArguments().getString("type");
        LogUtils.loge(cat_id);
        LogUtils.loge(type);
        initApiInfo();
        initView();
        mRxManager.on(Const.GOODS_SORT_TYPE, new Action1<String>() {
            @Override
            public void call(String s) {
                sort = s;
                requestGoodsList(1);
            }
        });
        return mRootView;
    }

    private void initApiInfo() {
        mService = new GoodsService();
        mPresenter = new CategoryPresenter(this, mService);
    }

    private void initView() {
        if (type.equals("brand")) {
            setBrandRecyclerView();
            requestBrandList();
        } else {
            setGoodsRecyclerView();
            requestGoodsList(1);
        }
    }
    private void setBrandRecyclerView(){
        mBrandAdapter = new BrandAdapter(getActivity(), brandsList, 0);
        mBrandAdapter.setOnClickListener((int position) -> {
            Brand brand = brandsList.get(position);
            String type = brand.type;
            if (type.equals("0")) {
                Intent intent = new Intent(getContext(), BrandTopicActivity.class);
                intent.putExtra("topic_id", brand.topic_id);
                intent.putExtra("title", brand.title);
                intent.putExtra("share_url", brand.share_url);
                intent.putExtra("share_vip_url", brand.share_vip_url);
                intent.putExtra("banner", brand.banner);
                intent.putExtra("type", "normal");
                startActivity(intent);
            } else if (type.equals("1")) {
                Intent intent1 = new Intent(getContext(), WebViewActivity.class);
                intent1.putExtra("share_desc", brand.share_desc);
                intent1.putExtra("name", brand.title);
                intent1.putExtra("url", brand.url);
                startActivity(intent1);
            }
        });
        brandHeaderAdapter = new HeaderViewRecyclerAdapter(mBrandAdapter);
        layoutManagerBrand.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManagerBrand);
        mRecyclerView.setAdapter(brandHeaderAdapter);
    }
    private void setGoodsRecyclerView() {
        mGoodsAdapter = new GoodsAdapter(getContext(), goodsList);
        mGoodsAdapter.setOnClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                XmbIntent.startIntentGoodsPreview(getContext(), goodsList.get(position).goods_name, goodsList.get(position).buy_url, goodsList.get(position).share_url);
            }

            @Override
            public void toggleSale(int position) {
                XmbPopubWindow.showTranparentLoading(getContext());
                if (goodsList.get(position).goods_sale_status == 0) {
                    mPresenter.onSale(GoodsService.gen_on_sale_params(AppContext.getToken(getContext()), goodsList.get(position).goods_id), position);
                } else {
                    mPresenter.offSale(GoodsService.gen_off_sale_params(AppContext.getToken(getContext()), goodsList.get(position).goods_id), position);
                }
            }

            @Override
            public void shareGood(int position) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("title", goodsList.get(position).goods_name);
                hashMap.put("desc", "【有人@你】你有一个分享尚未点击");
                hashMap.put("url", goodsList.get(position).share_url);
                XmbPopubWindow.showShopChooseWindow(getActivity(), hashMap, "Show_Goods_Share", "0");
//                XmbPopubWindow.showGoodsShare(CategoryChooseActivity.this, hashMap);
            }
        });
        goodsHeaderAdapter = new HeaderViewRecyclerAdapter(mGoodsAdapter);
        layoutManagerGood.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManagerGood);
        mRecyclerView.setAdapter(goodsHeaderAdapter);
    }

    public void requestGoodsList(int type) {
        if (type == 1) {
            page = 1;
            loadingView.setVisibility(View.VISIBLE);
            goodsHeaderAdapter.removeFooterView();
            mRecyclerView.removeOnScrollListener(goodListener);
            goodsList.clear();
            goodsHeaderAdapter.notifyDataSetChanged();
        }
        mPresenter.getGoodsList(GoodsService.gen_goods_list_params(AppContext.getToken(this.getContext()), cat_id, sort, page, keyword));
    }

    public void requestBrandList() {
        mPresenter.getBrandList(GoodsService.gen_brand_list_params(AppContext.getToken(this.getContext()), brand_page));
    }

    public void goodsResponse(List<Goods> list) {

        if (list.size() < 20) {
            goodsHeaderAdapter.removeFooterView();
            mRecyclerView.removeOnScrollListener(goodListener);
        } else {
            goodsHeaderAdapter.removeFooterView();
            goodsHeaderAdapter.addFooterView(LayoutInflater.from(getContext()).inflate(R.layout.load_more_anim, mRecyclerView, false));
            mRecyclerView.removeOnScrollListener(goodListener);
            goodListener = new EndlessRecyclerOnScrollListener(layoutManagerGood) {
                @Override
                public void onLoadMore(int currentPage) {
                    page += 1;
                    requestGoodsList(0);
                }
            };
            mRecyclerView.addOnScrollListener(goodListener);
        }
        goodsList.addAll(list);
        goodsHeaderAdapter.notifyDataSetChanged();
        loadingView.setVisibility(View.GONE);
    }

    public void handlerBrandRecycleView(List<Brand> list) {
        if (list.size() < 20) {
            brandHeaderAdapter.removeFooterView();
            mRecyclerView.removeOnScrollListener(brandListener);
        } else {
            brandHeaderAdapter.removeFooterView();
            brandHeaderAdapter.addFooterView(LayoutInflater.from(getContext()).inflate(R.layout.load_more_anim, mRecyclerView, false));
            mRecyclerView.removeOnScrollListener(brandListener);
            brandListener = new EndlessRecyclerOnScrollListener(layoutManagerBrand) {
                @Override
                public void onLoadMore(int currentPage) {
                    brand_page += 1;
                    requestBrandList();
                }
            };
            mRecyclerView.addOnScrollListener(brandListener);
        }
        brandsList.addAll(list);
        brandHeaderAdapter.notifyDataSetChanged();
        loadingView.setVisibility(View.GONE);
    }
}
