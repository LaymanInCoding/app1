package com.xiaomabao.weidian.models;

import java.util.List;

/**
 * Created by de on 2016/10/12.
 */
public class SearchGood {
    public GoodList data;
    public String info;
    public int status ;

    public class GoodList{
        public List<Goods> lists;
    }
}
