package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.OrderDetail;
import com.xiaomabao.weidian.presenters.OrderDetailPresenter;
import com.xiaomabao.weidian.services.OrderService;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDetailActivity extends AppCompatActivity {
    @BindString(R.string.order_detail_title) String titleText;
    @BindView(R.id.toolbar_title)TextView toolbarTitleTextView;
    @BindView(R.id.anim_loading)View loadingView;

    @BindView(R.id.order_detail_user)
    TextView order_detail_user;
    @BindView(R.id.order_detail_phone)
    TextView order_detail_phone;
    @BindView(R.id.order_address)
    TextView order_address;
    @BindView(R.id.order_sn)
    TextView order_sn;
    @BindView(R.id.order_add_time)
    TextView order_add_time;
    @BindView(R.id.order_pay_time)
    TextView order_pay_time;
    @BindView(R.id.order_shipping_fee)
    TextView order_shipping_fee;
    @BindView(R.id.order_goods_amount)
    TextView order_goods_amount;
    @BindView(R.id.order_amount)
    TextView order_amount;
    @BindView(R.id.order_shipping_way)
    TextView order_shipping_way;
    @BindView(R.id.order_invoice_no)
    TextView order_invoice_no;
    @BindView(R.id.goods_container)
    LinearLayout goods_container;
    @BindView(R.id.shipping_container)
    View shipping_container;

    String invoice_url = "";

    @OnClick(R.id.invoice_btn) void invoiceDetail() {
        Intent intent = new Intent(OrderDetailActivity.this,InvoiceWebViewActivity.class);
        intent.putExtra("url",invoice_url);
        startActivity(intent);
    }
    @OnClick(R.id.back) void back() {
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        toolbarTitleTextView.setText(titleText);
        initApi();
    }

    OrderService mService;
    OrderDetailPresenter mPresenter;

    protected void initApi(){
        mService = new OrderService();
        mPresenter = new OrderDetailPresenter(this,mService);
        mPresenter.order_detail(OrderService.gen_order_detail_params(AppContext.getToken(this),getIntent().getStringExtra("order_sn")));
    }

    public void setView(OrderDetail.Order order){
        order_detail_user.setText(order.consignee);
        order_detail_phone.setText(order.mobile);
        order_address.setText(order.address);
        order_sn.setText(order.order_sn);
        order_add_time.setText(order.add_time);
        order_pay_time.setText(order.pay_time);
        order_shipping_fee.setText(order.shipping_fee);
        order_invoice_no.setText(order.invoice_no);
        order_goods_amount.setText(order.goods_amount);
        order_amount.setText(order.order_amount);
        order_shipping_way.setText(order.shipping_way);
        invoice_url = order.shipping_url;
        for(int i =0;i<order.order_goods.size();i++){
            LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_order_good,goods_container,false);
            ImageView goodsThumbImageView = (ImageView) layout.findViewById(R.id.goods_thumb);
            Glide.with(this)
                    .load(order.order_goods.get(i).goods_thumb)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(goodsThumbImageView);
            TextView goodsNameTextView = (TextView) layout.findViewById(R.id.goods_name);
            goodsNameTextView.setText(order.order_goods.get(i).goods_name);
            TextView goodsPriceTextView = (TextView) layout.findViewById(R.id.goods_price);
            goodsPriceTextView.setText(order.order_goods.get(i).goods_price);
            TextView goodsNumberTextView = (TextView) layout.findViewById(R.id.goods_number);
            goodsNumberTextView.setText("x " + order.order_goods.get(i).goods_number);
            goods_container.addView(layout);
        }
        if (order.shipping_status.equals("0")){
            shipping_container.setVisibility(View.GONE);
        }
        loadingView.setVisibility(View.GONE);
    }

}
