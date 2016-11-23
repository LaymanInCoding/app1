package com.xiaomabao.weidian.models;


public class ShopStatistics {

    public int status;
    public String info;
    public ShopStatisticsInfo data;

    public class ShopStatisticsInfo{
        public String visit_total;
        public String profit_total;
        public String day_wait_profit;
        public String day_orders_cnt;
    }

}
