package com.xiaomabao.weidian.models;

import java.util.List;

public class WithdrawRecord {
    public int status;
    public String total;
    public String info;
    public List<Withdraw> data;

    public class Withdraw{
        public String apply_time;
        public String apply_money;
        public int withdraw_status_code;
        public String withdraw_status_desc;
    }
}
