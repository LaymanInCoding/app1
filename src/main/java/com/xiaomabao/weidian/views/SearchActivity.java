package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.adapters.GoodsAdapter;
import com.xiaomabao.weidian.adapters.SearchEditAdapter;
import com.xiaomabao.weidian.adapters.TagAdapter;
import com.xiaomabao.weidian.adapters.VipGoodsAdapter;
import com.xiaomabao.weidian.models.Goods;
import com.xiaomabao.weidian.models.HotKey;
import com.xiaomabao.weidian.models.SearchGood;
import com.xiaomabao.weidian.presenters.SearchGoodsPresenter;
import com.xiaomabao.weidian.services.GoodsService;
import com.xiaomabao.weidian.ui.EndlessRecyclerOnScrollListener;
import com.xiaomabao.weidian.ui.FlowTagLayout;
import com.xiaomabao.weidian.ui.HeaderViewRecyclerAdapter;
import com.xiaomabao.weidian.util.InputSoftUtil;
import com.xiaomabao.weidian.util.XmbIntent;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by de on 2016/10/8.
 */
public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.search_edit_text)
    EditText searchEditText;
    @BindView(R.id.clear_text)
    ImageView clearImageView;
    @BindView(R.id.anim_loading)
    View animLoading;
    @BindView(R.id.no_goods_container)
    View no_goods_container;
    @BindView(R.id.hot_recommend)
    FlowTagLayout hot_container;
    @BindView(R.id.history_container)
    FlowTagLayout history_container;
    @BindView(R.id.no_history_tv)
    TextView no_search_history;
    @BindView(R.id.history_layout)
    RelativeLayout history_layout;
    @BindView(R.id.bottom_tag_container)
    RelativeLayout bottom_tag_container;
    @BindView(R.id.search_result_layout)
    RelativeLayout search_result_layout;
    @BindView(R.id.search_list)
    RecyclerView search_list_recyclerview;
    @BindView(R.id.search_span_list)
    RecyclerView search_span_list_rv;
    @BindView(R.id.anim_loading_hot)
    View hotAnimLoading;
    @BindView(R.id.back)
    LinearLayout back_layout;
    @BindView(R.id.cancel)
    LinearLayout cancel_layout;

    @OnClick(R.id.clear_text)
    void clearText() {
        searchEditText.setText("");
    }

    @OnClick(R.id.clear_history)
    void clearHistory() {
        XmbPopubWindow.showAlert(this,"删除历史记录成功");
        AppContext.instance().getRealmData().clear();
        AppContext.instance().deleteRealmData();
        Log.d("Realm_Delete", AppContext.instance().getRealmData().toString());
        historyAdapter.notifyDataSetChanged();
        no_search_history.setVisibility(View.VISIBLE);
        history_layout.setVisibility(View.GONE);
    }

    @OnClick(R.id.cancel)
    void cancel() {
        finish();
        overridePendingTransition(R.anim.pop_right_in, R.anim.pop_right_out);
    }
    @OnClick(R.id.back)
    void back(){
        back_layout.setVisibility(View.GONE);
        animLoading.setVisibility(View.GONE);
        cancel_layout.setVisibility(View.VISIBLE);
        bottom_tag_container.setVisibility(View.VISIBLE);
        search_result_layout.setVisibility(View.GONE);
    }

    private SearchGoodsPresenter mPresenter;
    private GoodsService mService;
    private VipGoodsAdapter goodsAdapter;
    private GoodsAdapter goodsAdapterNormal;
    private SearchEditAdapter searchEditAdapter;


    private String keywords = "";
    private String cat_id = "-999999";
    private String type = "";
    private int page = 1;
    private ArrayList<String> mHotSearch = new ArrayList<>();
    private ArrayList<String> mHistorySearch = AppContext.instance().getRealmData();
    private List<Goods> goodList = new ArrayList<>();
    private List<String> mSearchList = new ArrayList<>();

    private EndlessRecyclerOnScrollListener goodListener;
    private LinearLayoutManager layoutManager, layoutManager1;
    private HeaderViewRecyclerAdapter goodsHeaderAdapter, normalGoodsHeaderAdapter;
    private TagAdapter mAdapter;
    private TagAdapter historyAdapter;

    private List<HotKey.HotList.GoodInfo> list;//热词请求数据list

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        type = getIntent().getStringExtra("vip");
        initApi();
        requestHot();
        initView();
        bindEvent();
    }

    //解析热词相关操作
    public void requestHot() {
        hotAnimLoading.setVisibility(View.VISIBLE);
        mPresenter.getHotSearch(GoodsService.gen_hot_search_list_params(AppContext.getToken(this), ""));
    }

    public void hotKeyParse(HotKey hotKey) {
        list = hotKey.data.hot;
        for (int i = 0; i < list.size(); i++) {
            HotKey.HotList.GoodInfo info = list.get(i);
            mHotSearch.add(info.title);
            Log.e("Info", info.title);
            mAdapter.notifyDataSetChanged();
            hotAnimLoading.setVisibility(View.GONE);
        }
    }

    //   ————————————————————————————————————————
    public void initView() {
        if (AppContext.instance().getRealmData().size() != 0) {
            no_search_history.setVisibility(View.GONE);
            history_layout.setVisibility(View.VISIBLE);
        }
        //输入搜索字符EditText相关设置
        layoutManager1 = new LinearLayoutManager(this);
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        searchEditAdapter = new SearchEditAdapter(this, mSearchList);
        search_span_list_rv.setLayoutManager(layoutManager1);
        search_span_list_rv.setAdapter(searchEditAdapter);
        mSearchList.add("asdasdsad2");
        mSearchList.add("asdasdsad1212");
        mSearchList.add("asdasdsad1");
        mSearchList.add("asdasd");
        searchEditAdapter.notifyDataSetChanged();
        searchEditAdapter.setOnItemClickListener(position -> {
            // TODO: 2016/10/12 根据position获取list所在position字符串，进行Search网络请求(类似点击标签事件)
        });

        //非Vip商品搜索设置
        goodsAdapterNormal = new GoodsAdapter(this, goodList);
        goodsAdapterNormal.setOnClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                XmbIntent.startIntentGoodsPreview(SearchActivity.this, goodList.get(position).goods_name, goodList.get(position).buy_url, goodList.get(position).share_url);
            }

            @Override
            public void toggleSale(int position) {
                XmbPopubWindow.showTranparentLoading(SearchActivity.this);
                if (goodList.get(position).goods_sale_status == 0) {
                    mPresenter.onSale(GoodsService.gen_on_sale_params(AppContext.getToken(SearchActivity.this), goodList.get(position).goods_id), position);
                } else {
                    mPresenter.offSale(GoodsService.gen_off_sale_params(AppContext.getToken(SearchActivity.this), goodList.get(position).goods_id), position);
                }
            }

            @Override
            public void shareGood(int position) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("title", goodList.get(position).goods_name);
                hashMap.put("desc", "【有人@你】你有一个分享尚未点击");
                hashMap.put("url", goodList.get(position).share_url);
                XmbPopubWindow.showShopChooseWindow(SearchActivity.this, hashMap, "Show_Goods_Share", "0");
//                XmbPopubWindow.showGoodsShare(CategoryChooseActivity.this, hashMap);
            }
        });
        //Vip商品搜索设置
        goodsAdapter = new VipGoodsAdapter(this, goodList);
        goodsAdapter.setOnClickListener(new VipGoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                XmbIntent.startIntentGoodsPreview(SearchActivity.this, goodList.get(position).goods_name, goodList.get(position).buy_url, goodList.get(position).share_url);
            }

            @Override
            public void shareGood(int position) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("title", goodList.get(position).goods_name);
                hashMap.put("desc", "【有人@你】你有一个分享尚未点击");
                hashMap.put("url", goodList.get(position).share_url);
                XmbPopubWindow.showShopChooseWindow(SearchActivity.this, hashMap, "Show_Goods_Share", "1");
            }
        });
        goodsHeaderAdapter = new HeaderViewRecyclerAdapter(goodsAdapter);
        normalGoodsHeaderAdapter = new HeaderViewRecyclerAdapter(goodsAdapterNormal);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        search_list_recyclerview.setHasFixedSize(true);
        search_list_recyclerview.setLayoutManager(layoutManager);
        if (type.equals("1")) {
            search_list_recyclerview.setAdapter(goodsHeaderAdapter);
        } else {
            search_list_recyclerview.setAdapter(normalGoodsHeaderAdapter);
        }
        //热搜相关设置
        mAdapter = new TagAdapter(this, mHotSearch);
        hot_container.setAdapter(mAdapter);
        hot_container.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_NONE);
        hot_container.setOnTagClickListener((parent1, view1, position1) -> {
            View hotTag = parent1.getAdapter().getView(position1, null, null);
            String tag = (String) hotTag.getTag();
            InputSoftUtil.hideSoftInput(this, hotTag);
            no_search_history.setVisibility(View.GONE);
            history_layout.setVisibility(View.VISIBLE);
            check(tag);
            page = 1;
            HotKey.HotList.GoodInfo brand = list.get(position1);
            if (type.equals("1")) {
                if (brand.type == 2) {
                    Intent intent = new Intent();//启动品牌热词intent
                    intent.setClass(SearchActivity.this, BrandTopicActivity.class);
                    intent.putExtra("topic_id", brand.topic_id);
                    intent.putExtra("title", brand.title);
                    intent.putExtra("share_url", brand.share_url);
                    intent.putExtra("share_vip_url", brand.share_vip_url);
                    intent.putExtra("banner", brand.banner);
                    intent.putExtra("type", "vip");
                    startActivity(intent);
                } else {
                    requestVipSearch(tag);
                }
            } else {
                if (brand.type == 2) {
                    Intent intent = new Intent();//启动品牌热词intent
                    intent.setClass(SearchActivity.this, BrandTopicActivity.class);
                    intent.putExtra("topic_id", brand.topic_id);
                    intent.putExtra("title", brand.title);
                    intent.putExtra("share_url", brand.share_url);
                    intent.putExtra("share_vip_url", brand.share_vip_url);
                    intent.putExtra("banner", brand.banner);
                    intent.putExtra("type", "normal");
                    startActivity(intent);
                } else {
                    requestNormalSearch(tag);
                }
            }
        });
        historyAdapter = new TagAdapter(this, mHistorySearch);
        history_container.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_NONE);
        history_container.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();
        history_container.setOnTagClickListener(((parent, view, position) -> {
            View tagView = parent.getAdapter().getView(position, null, null);
            String tag = (String) tagView.getTag();
            check(tag);
            page = 1;
            InputSoftUtil.hideSoftInput(this, tagView);
//            history_layout.setVisibility(View.VISIBLE);
            if (type.equals("1")) {
                requestVipSearch(tag);
            } else {
                requestNormalSearch(tag);
            }
        }));
    }

    public void initApi() {
        mService = new GoodsService();
        mPresenter = new SearchGoodsPresenter(this, mService);
    }

    public void requestNormalSearch(String keywords) {
        cancel_layout.setVisibility(View.GONE);
        back_layout.setVisibility(View.VISIBLE);
        InputSoftUtil.hideSoftInput(this, searchEditText);
        if (page == 1) {
            no_goods_container.setVisibility(View.GONE);
            bottom_tag_container.setVisibility(View.GONE);
            search_result_layout.setVisibility(View.VISIBLE);
            animLoading.setVisibility(View.VISIBLE);
            goodList.clear();
        }
        goodsHeaderAdapter.removeFooterView();
        search_list_recyclerview.removeOnScrollListener(goodListener);
        mPresenter.getSearchList(GoodsService.gen_goods_search_params(AppContext.getToken(this), "", keywords, "0"));
        page += 1;
    }

    public void requestVipSearch(String keywords) {
        cancel_layout.setVisibility(View.GONE);
        back_layout.setVisibility(View.VISIBLE);
        InputSoftUtil.hideSoftInput(this, searchEditText);
        if (page == 1) {
            no_goods_container.setVisibility(View.GONE);
            bottom_tag_container.setVisibility(View.GONE);
            search_result_layout.setVisibility(View.VISIBLE);
            animLoading.setVisibility(View.VISIBLE);
            goodList.clear();
        }
        goodsHeaderAdapter.removeFooterView();
        search_list_recyclerview.removeOnScrollListener(goodListener);
        mPresenter.getSearchList(GoodsService.gen_goods_search_params(AppContext.getToken(this), "", keywords, "1"));
        page += 1;
    }

    public void goodsResponse(SearchGood searchGood) {
        List<Goods> goodsList = searchGood.data.lists;
        Log.e("List", goodsList.toString());
        animLoading.setVisibility(View.VISIBLE);
        if (goodsList.size() == 0 && goodsList.size() == 0) {
            search_result_layout.setVisibility(View.GONE);
            no_goods_container.setVisibility(View.VISIBLE);
        } else {
            no_goods_container.setVisibility(View.GONE);
        }
        if (goodsList.size() < 20) {
            goodsHeaderAdapter.removeFooterView();
            search_list_recyclerview.removeOnScrollListener(goodListener);
            goodListener = null;
        } else {
            goodsHeaderAdapter.removeFooterView();
            goodsHeaderAdapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.load_more_anim, search_list_recyclerview, false));
            search_list_recyclerview.removeOnScrollListener(goodListener);
            goodListener = new EndlessRecyclerOnScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int currentPage) {
                    if (type.equals("1")) {
                        requestVipSearch(keywords);
                    } else {
                        requestVipSearch(keywords);
                    }
                }
            };
            search_list_recyclerview.addOnScrollListener(goodListener);
        }
        goodList.addAll(goodsList);
        goodsHeaderAdapter.notifyDataSetChanged();
        animLoading.setVisibility(View.GONE);
    }

    //搜索内容监听
    public void bindEvent() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 0) {
                    clearImageView.setVisibility(View.GONE);
                    search_span_list_rv.setVisibility(View.GONE);
                    bottom_tag_container.setVisibility(View.VISIBLE);
                } else {
                    // TODO: 2016/10/12 做正向最长匹配 返回 dataList
                    clearImageView.setVisibility(View.VISIBLE);
//                    search_span_list_rv.setVisibility(View.VISIBLE);
//                    bottom_tag_container.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchEditText.setOnEditorActionListener((view, keyCode, keyEvent) -> {
            if (keyCode == EditorInfo.IME_ACTION_SEARCH) {
                keywords = searchEditText.getText().toString();
                if (!keywords.trim().equals("")) {
                    check(keywords);
                    page = 1;
                    historyAdapter.notifyDataSetChanged();
                    no_search_history.setVisibility(View.GONE);
                    history_layout.setVisibility(View.VISIBLE);
                    searchEditText.setText("");
                    if (type.equals("1")) {
                        requestVipSearch(keywords);
                    } else {
                        requestNormalSearch(keywords);
                    }
                }
                return true;
            } else {
                return false;
            }
        });
    }

    public void refreshGoodsRecycleView(int position) {
        if (goodList.get(position).goods_sale_status == 1) {
            goodList.get(position).goods_sale_status = 0;
        } else {
            goodList.get(position).goods_sale_status = 1;
        }
        normalGoodsHeaderAdapter.notifyDataSetChanged();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void check(String keywords) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyymmddHHmmss");
        String date = sDateFormat.format(new java.util.Date());
        if (AppContext.instance().checkExistInRm(keywords)) {
            for (int i = 0; i < AppContext.instance().getRealmData().size(); i++) {
                if (keywords.trim().equals(mHistorySearch.get(i))) {
                    mHistorySearch.remove(i);
                }
            }
            mHistorySearch.add(0, keywords);
            historyAdapter.notifyDataSetChanged();
            AppContext.instance().updateRealmData(keywords, date);
        } else {
            AppContext.instance().addRealmData(keywords, date);
            mHistorySearch.add(0, keywords);
            historyAdapter.notifyDataSetChanged();
        }
    }
}
