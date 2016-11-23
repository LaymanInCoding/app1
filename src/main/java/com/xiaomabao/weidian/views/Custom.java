package com.xiaomabao.weidian.views;

import rx.Subscription;

/**
 * Created by de on 2016/9/5.
 */
public class Custom {
    Subscription subscription;

    public void unSubscribe(){
        subscription.unsubscribe();

    }
}
