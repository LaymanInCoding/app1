package com.xiaomabao.weidian.models;

public class ApplyShopCode {
    public int status;
    public String info;
    public Order data;

    public class Order{
        public String order_amount;
        public String order_amount_formatted;
        public String ali_sign;
        public String order_sn;
    }
}
