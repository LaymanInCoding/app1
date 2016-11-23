package com.xiaomabao.weidian.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class InputSoftUtil {
    public static void hideSoftInput(Context context,View mView){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
    }
}
