package com.xiaomabao.weidian.util;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.ViewGroup;

import com.xiaomabao.weidian.AppContext;

/**
 * Created by de on 2017/3/6.
 */
public class MyUtils {
    public static void dynamicSetTabLayoutMode(TabLayout tabLayout) {
        int tabWidth = calculateTabWidth(tabLayout);
        int screenWidth = getScreenWith();

        if (tabWidth <= screenWidth) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }

    private static int calculateTabWidth(TabLayout tabLayout) {
        int tabWidth = 0;
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            final View view = tabLayout.getChildAt(i);
            view.measure(0, 0); // 通知父view测量，以便于能够保证获取到宽高
            tabWidth += view.getMeasuredWidth();
        }
        return tabWidth;
    }

    public static int getScreenWith() {
        return AppContext.instance().getResources().getDisplayMetrics().widthPixels;
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }
    /**
     * 校验银行卡卡号
     *
     * @param bankNumber
     * @return
     */
    public static boolean checkBankCard(String bankNumber) {
        char[] cc = bankNumber.toCharArray();
        int[] n = new int[cc.length + 1];
        int j = 1;
        for (int i = cc.length - 1; i >= 0; i--) {
            n[j++] = cc[i] - '0';
        }
        int even = 0;
        int odd = 0;
        for (int i = 1; i < n.length; i++) {
            if (i % 2 == 0) {
                int temp = n[i] * 2;
                if (temp < 10) {
                    even += temp;
                } else {
                    temp = temp - 9;
                    even += temp;
                }
            } else {
                odd += n[i];
            }
        }

        int total = even + odd;
        if (total % 10 == 0)
            return true;
        return false;

    }
}
