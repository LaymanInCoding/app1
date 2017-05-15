package com.xiaomabao.weidian.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by ming on 2017/4/25.
 */
public class RiseNumberTextView extends TextView implements IRiseNumber {

    private static final int STOPPED = 0;
    private static final int RUNNING = 1;
    private int mPlayingState = STOPPED;

    private long mDuration = 1500;
    private float mFromNumber;
    private float mNumber;
    private int numberType = 2;  // 1.int  2 float
    private DecimalFormat fnum = new DecimalFormat("##0.00");
    private EndListener mEndListener = null;

    final static int[] sizeTable = {9, 99, 999, 9999, 99999, 99999, Integer.MAX_VALUE};

    public RiseNumberTextView(Context context) {
        super(context);
    }

    public RiseNumberTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isRunning() {
        return mPlayingState == RUNNING;
    }

    private void runFloat() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mFromNumber, mNumber);
        valueAnimator.setDuration(mDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
//                Logger.d(animator.getAnimatedFraction());
                setText(fnum.format(Float.parseFloat(animator.getAnimatedValue().toString())));
                if (animator.getAnimatedFraction() >= 1) {
                    mPlayingState = STOPPED;
                    if (mEndListener != null) {
                        mEndListener.onEndFinish();
                    }
                }
            }
        });
        valueAnimator.start();
    }

    private void runInt() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) mFromNumber,
                (int) mNumber);
        valueAnimator.setDuration(mDuration);
        valueAnimator
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        //设置瞬时的数据值到界面上
                        setText(valueAnimator.getAnimatedValue().toString());
                        if (valueAnimator.getAnimatedFraction() >= 1) {
                            //设置状态为停止
                            mPlayingState = STOPPED;
                            if (mEndListener != null)
                                //通知监听器，动画结束事件
                                mEndListener.onEndFinish();
                        }
                    }
                });
        valueAnimator.start();
    }

    private int sizeOfInt(int number) {
        for (int i = 0; ; i++) {
            if (number < sizeTable[i]) {
                return i + 1;
            }
        }
    }

    @Override
    public void start() {
        if (!isRunning()) {
            mPlayingState = RUNNING;
            if (numberType == 1) {
                runInt();
            } else {
                runFloat();
            }
        }
    }

    @Override
    public void setNumber(float number) {
        numberType = 2;
        mFromNumber = 0;
        mNumber = number;
    }

    @Override
    public void setNumber(int number) {
        numberType = 1;
        mFromNumber = 0;
        mNumber = number;
    }

    @Override
    public void setDuration(long duration) {
        mDuration = duration;
    }

    @Override
    public void setOnEndListener(EndListener callback) {
        mEndListener = callback;
    }

    public interface EndListener {
        void onEndFinish();
    }
}
