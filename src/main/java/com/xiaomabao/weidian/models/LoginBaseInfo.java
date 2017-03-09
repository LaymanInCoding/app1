package com.xiaomabao.weidian.models;

import java.util.List;

public class LoginBaseInfo {
    public int shop_id;
    public String shop_share_normal_url;
    public String shop_share_url;
    public String shop_share_vip_url;
    public String shop_share_qr;
    public String shop_share_vip_qr;
    public String shop_invite_url;
    public String shop_preview_url;
    public String user_id;
    public String username;
    public String shop_invite_qr;
    public String shop_name;
    public String shop_background;
    public String shop_description;
    public String shop_avatar;
    public String token;
    public String cart_url;
    public String ucenter_url;
    public List<ShopBase.ShopBaseInfo.ShopShareInfo> shop_share_info;
}
