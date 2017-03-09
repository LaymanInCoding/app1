package com.xiaomabao.weidian.views.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Category;
import com.xiaomabao.weidian.rx.RxBus;
import com.xiaomabao.weidian.services.GoodsService;
import com.xiaomabao.weidian.util.MyUtils;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by de on 2017/3/6.
 */
public class CategoryTestFragment extends Fragment {

    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tab_title)
    TabLayout mTabTitle;
    @BindView(R.id.toolbar_right)
    TextView sortTv;
    @BindView(R.id.sort_container)
    View sortContainerView;
    @BindView(R.id.sort_default_check)
    View defaultCheck;
    @BindView(R.id.sort_price_high_check)
    View priceHighCheck;
    @BindView(R.id.sort_price_low_check)
    View priceLowCheck;
    @BindView(R.id.sort_profit_check)
    View profitCheck;

    @OnClick(R.id.sort_container)
    void hideSortView() {
        sortContainerView.setVisibility(View.GONE);
    }

    @OnClick(R.id.toolbar_right)
    void showSortView() {
        sortContainerView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.fab)
    void showShareView() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("title", AppContext.getShopName(getContext()));
        hashMap.put("desc", AppContext.getShopDescription(getContext()));
        hashMap.put("url", AppContext.getShopShareVipUrl(getContext()));
        XmbPopubWindow.showShopChooseWindow(getActivity(), hashMap, "Show_Share", "3");
    }

    @OnClick(R.id.sort_default)
    void sortByDefault() {
        sort = "default";
        RxBus.getInstance().post(Const.GOODS_SORT_TYPE,sort);
        defaultCheck.setVisibility(View.VISIBLE);
        priceHighCheck.setVisibility(View.GONE);
        priceLowCheck.setVisibility(View.GONE);
        profitCheck.setVisibility(View.GONE);
        hideSortView();
    }



    @OnClick(R.id.sort_price_high)
    void sortByPriceHigh() {
        sort = "price_high";
        RxBus.getInstance().post(Const.GOODS_SORT_TYPE,sort);
        defaultCheck.setVisibility(View.GONE);
        priceHighCheck.setVisibility(View.VISIBLE);
        priceLowCheck.setVisibility(View.GONE);
        profitCheck.setVisibility(View.GONE);
        hideSortView();
    }

    @OnClick(R.id.sort_price_low)
    void sortByPriceLow() {
        sort = "price_low";
        RxBus.getInstance().post(Const.GOODS_SORT_TYPE,sort);
        defaultCheck.setVisibility(View.GONE);
        priceHighCheck.setVisibility(View.GONE);
        priceLowCheck.setVisibility(View.VISIBLE);
        profitCheck.setVisibility(View.GONE);
        hideSortView();
    }

    @OnClick(R.id.sort_profit)
    void sortByProfit() {
        sort = "profit";
        RxBus.getInstance().post(Const.GOODS_SORT_TYPE,sort);
        defaultCheck.setVisibility(View.GONE);
        priceHighCheck.setVisibility(View.GONE);
        priceLowCheck.setVisibility(View.GONE);
        profitCheck.setVisibility(View.VISIBLE);
        hideSortView();
    }

    private String sort = "";
    private View mRootView;
    GoodsService mService;
    CategoryChooseTestPresenter mPresenter;
    private List<Category> categories = new ArrayList<>();
    private List<String> categoryName = new ArrayList<>();
    List<Fragment> mCategoryFragmentList = new ArrayList<>();
    private BaseFragmentAdapter fragmentAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.category_test_layout, container, false);
        }
        ButterKnife.bind(this, mRootView);
        initApiInfo();
        request();
        return mRootView;
    }

    private void initApiInfo() {
        mService = new GoodsService();
        mPresenter = new CategoryChooseTestPresenter(this, mService);
    }

    private void request() {
        mPresenter.getCategoryList(GoodsService.gen_category_list_params(AppContext.getToken(getContext())));
    }

    public void handleTabTitle(List<Category> categorys) {
        if (categorys != null) {
            categories.addAll(categorys);
            for (int i = 0; i < categorys.size(); i++) {
                categoryName.add(categorys.get(i).cat_name);
                mCategoryFragmentList.add(createCategoryFragment(categorys.get(i)));
            }
            if (fragmentAdapter == null) {
                fragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager(), mCategoryFragmentList, categoryName);
            } else {
                //刷新
                fragmentAdapter.setFragments(getChildFragmentManager(), mCategoryFragmentList, categoryName);
            }
            mViewPager.setAdapter(fragmentAdapter);
            mTabTitle.setupWithViewPager(mViewPager);
            MyUtils.dynamicSetTabLayoutMode(mTabTitle);
            setPageChangeListener();
        }
    }

    private CategoryFragment createCategoryFragment(Category category) {
        CategoryFragment categoryFragment = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("category_id", category.cat_id);
        bundle.putString("type", category.type);
        categoryFragment.setArguments(bundle);
        return categoryFragment;
    }

    private void setPageChangeListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    sortTv.setVisibility(View.INVISIBLE);
                } else {
                    sortTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
