package com.xiaomabao.weidian.ui;

/**
 * Created by ming on 2017/4/25.
 */
public interface IRiseNumber {

    void start();

    void setNumber(float number);

    void setNumber(int number);

    void setDuration(long duration);

    void setOnEndListener(RiseNumberTextView.EndListener callback);

}
