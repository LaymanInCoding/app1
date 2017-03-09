package com.xiaomabao.weidian.views.fragment;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.rx.RxBus;
import com.xiaomabao.weidian.rx.RxManager;

/**
 * Created by de on 2017/3/6.
 */
public class ScrollAwareFabBehavior extends FloatingActionButton.Behavior {
    RxManager rxManager;

    public ScrollAwareFabBehavior(Context context, AttributeSet attrs) {
        super();
        if (rxManager == null) {
            rxManager = new RxManager();
        }
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }


    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
                               int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hide();
            RxBus.getInstance().post(Const.MENU_SHOW_HIDE, false);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            RxBus.getInstance().post(Const.MENU_SHOW_HIDE, true);
            child.show();
        }
    }
}
