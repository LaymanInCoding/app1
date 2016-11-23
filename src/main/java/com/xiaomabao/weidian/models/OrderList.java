package com.xiaomabao.weidian.models;

import java.util.List;

public class OrderList {
    public int status;
    public String info;
    public List<Order> data;

    public class Order{
        public String add_time;
        public String order_status;
        public String order_sn;
        public List<Goods> order_goods;
    }

    public class Goods{
        public String goods_id;
        public String goods_name;
        public String goods_thumb;
        public String goods_price;
        public String goods_number;
    }
}
