package com.xiaomabao.weidian.models;


import java.util.List;

public class Category {
    public String cat_id;
    public String cat_name;
    public String icon;
    public String type;
    public int selected;
    public List<ChildCategory> child;


    public class ChildCategory{
        public String cat_id;
        public String icon;
    }
}
