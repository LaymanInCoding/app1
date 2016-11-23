package com.xiaomabao.weidian.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.yxp.permission.util.lib.PermissionUtil;

import java.util.UUID;

public class Device {

    public static String get_deviceId(Context context){
        if (!AppContext.getDeviceId(context).equals("")){
            return AppContext.getDeviceId(context);
        }
        try{
            int permission = PermissionUtil.getInstance().checkSinglePermission(Manifest.permission.READ_PHONE_STATE);
            if(permission != 1){
                if (AppContext.getDeviceId(context).equals("")){
                    String uuid = UUID.randomUUID().toString();
                    AppContext.setDeviceId(context,uuid);
                    return uuid;
                }else{
                    return AppContext.getDeviceId(context);
                }
            }
        }catch (Exception e){

        }

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        AppContext.setDeviceId(context,deviceId);
        return deviceId;
    }

    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }
}
