package com.xiaomabao.weidian.models;

import java.util.List;

/**
 * Created by de on 2016/10/12.
 */
public class HotKey {
    public String info;
    public int status;
    public HotList data;

    public class HotList {

        public List<GoodInfo> hot;

        public class GoodInfo {
            public String topic_id;
            public String banner;
            public String title;
            public int type;
            public String share_url;
            public String share_vip_url;
        }
    }


}
