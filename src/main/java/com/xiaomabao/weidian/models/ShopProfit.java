package com.xiaomabao.weidian.models;

public class ShopProfit {
    public int status;
    public String info;
    public Profit data;

    public class Profit{
        public String available_balance;
        public String presenting_withdraw;
        public String waiting_profit;
        public String presented_withdraw;
    }
}
