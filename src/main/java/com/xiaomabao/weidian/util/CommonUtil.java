package com.xiaomabao.weidian.util;


import com.xiaomabao.weidian.AppContext;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonUtil {

    //用于匹配手机号码
    public final static String REGEX_MOBILE = "^0?1[34578]\\d{9}$";
    public static final String REGEX_PWD = ".{6,20}";
    public static final String REGEX_CODE = "[0-9A-Za-z]{4,6}";
    public static final String REGEX_INVITE = "[0-9A-Za-z]{6,10}";

    private static Pattern PATTERN_MOBILE;
    private static Pattern PATTERN_PWD;
    private static Pattern PATTERN_CODE;
    private static Pattern INVITE_CODE;

    static {
        PATTERN_MOBILE = Pattern.compile(REGEX_MOBILE);
        PATTERN_PWD = Pattern.compile(REGEX_PWD);
        PATTERN_CODE = Pattern.compile(REGEX_CODE);
        INVITE_CODE = Pattern.compile(REGEX_INVITE);
    }

    private CommonUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    // -------------------------- 输入校验 --------------------------

    /**
     * 判断是否为手机号码
     *
     * @param number 手机号码
     */
    public static boolean isMobilePhone(String number) {
        Matcher match = PATTERN_MOBILE.matcher(number);
        return match.matches();
    }

    /**
     * 判断密码是否正确
     *
     * @param password 密码
     */
    public static boolean validPass(String password) {
        Matcher match = PATTERN_PWD.matcher(password);
        return match.matches();
    }

    /**
     * 判断验证是否正确
     *
     * @param code 验证码
     */
    public static boolean validCode(String code) {
        Matcher match = PATTERN_CODE.matcher(code);
        return match.matches();
    }

    /**
     * 判断验证是否正确
     *
     * @param code 验证码
     */
    public static boolean validInviteCode(String code) {
        Matcher match = INVITE_CODE.matcher(code);
        return match.matches();
    }

    /**
     * 判断字符串是否为空
     *
     * @param string string
     */
    public static boolean nEmptyStringValid(String string) {
        if(string.trim().equals("")){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 判断验证是否正确
     *
     * @param hashMap 验证码
     */
    public static HashMap<String,String> appendParams(HashMap<String,String> hashMap) {
        hashMap.put("device_id", Device.get_deviceId(AppContext.appContext));
        hashMap.put("device_desc",Device.getDeviceName());
        return hashMap;
    }

}
