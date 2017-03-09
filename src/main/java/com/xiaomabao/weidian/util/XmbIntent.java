package com.xiaomabao.weidian.util;

import android.content.Context;
import android.content.Intent;

import com.xiaomabao.weidian.views.GoodsPreviewActivity;

public class XmbIntent {
    public static void startIntentGoodsPreview(Context context, String goods_name, String buy_url, String share_url) {
        Intent intent = new Intent(context, GoodsPreviewActivity.class);
        LogUtils.loge(goods_name + "\n" + buy_url + "\n" + share_url + "\n");
        intent.putExtra("buy_url", buy_url);
        intent.putExtra("share_url", share_url);
        intent.putExtra("goods_name", goods_name);
        context.startActivity(intent);
    }
}
