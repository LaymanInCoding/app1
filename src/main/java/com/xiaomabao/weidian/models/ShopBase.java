package com.xiaomabao.weidian.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ShopBase {

    public int status;
    public String info;
    public ShopBaseInfo data;

    public class ShopBaseInfo {
        public String shop_share_code;
        public String shop_name;
        public String shop_background;
        public String shop_description;
        public String shop_avatar;
        public String real_name;
        public String deposit_bank;
        public String branch_bank;
        public String card_no;
        public int card_bind_status;
        public ArrayList<ShopShareInfo> shop_share_info;

        public class ShopShareInfo  {
            public String id;
            public String shop_name;
            public String shop_description;
            public String shop_avatar;
            public String shop_background;
            public String is_default;

        }
    }
}
