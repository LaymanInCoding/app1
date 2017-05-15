package com.xiaomabao.weidian.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiaomabao.weidian.R;

/**
 * Created by ming on 2017/4/25.
 */
public class ImageLoaderUtil {

    public static void loadImage(Context context, ImageView imageView) {
        Glide.with(context)
                .load(R.mipmap.mitouxiang)
                .placeholder(R.mipmap.mitouxiang)
                .dontAnimate()
                .error(R.mipmap.mitouxiang)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadImageByUrl(Context context, ImageView imageView, String url) {
        Glide.with(context)
                .load(url)
                .placeholder(R.mipmap.mitouxiang)
                .dontAnimate()
                .error(R.mipmap.mitouxiang)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadBackground(Context context, ImageView imageView) {
        Glide.with(context)
                .load(R.mipmap.index_top_bg)
                .placeholder(R.mipmap.index_top_bg)
                .dontAnimate()
                .error(R.mipmap.index_top_bg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadBackgroundByUrl(Context context, ImageView imageView, String url) {
        Glide.with(context)
                .load(url)
                .placeholder(R.mipmap.index_top_bg)
                .dontAnimate()
                .error(R.mipmap.index_top_bg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}
