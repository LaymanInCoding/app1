package com.xiaomabao.weidian.models;

import java.util.List;

public class OrderDetail {
    public int status;
    public Order data;
    public String info;

    public class Order{
        public String order_sn;
        public String add_time;
        public String pay_time;
        public String shipping_way;
        public String invoice_no;
        public String shipping_status;
        public String order_amount;
        public String goods_amount;
        public String shipping_fee;
        public String address;
        public String consignee;
        public String mobile;
        public String shipping_url;
        public List<OrderList.Goods> order_goods;
    }
}
