package com.xiaomabao.weidian.models;

public class BindCard {
    public int status;
    public String info;
    public Card data;

    public class Card{
        public String real_name;
        public String deposit_bank;
        public String branch_bank;
        public String card_no;
        public String card_bind_status;
    }
}
