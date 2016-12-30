package com.xiaomabao.weidian.services;

import android.util.Log;

import com.xiaomabao.weidian.models.ShareInfo;
import com.xiaomabao.weidian.models.ShopAvatarStatus;
import com.xiaomabao.weidian.models.ShopBase;
import com.xiaomabao.weidian.models.ShopProfit;
import com.xiaomabao.weidian.models.ShopStatistics;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.util.CommonUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;
import rx.Observable;

public class ShopService {
    private static final String USER_SERVER_URL = "http://vapi.xiaomabao.com";
    public static final String SHARE_QRCODE_URL = "http://vapi.xiaomabao.com/common/show_qrcode";

//    private static final String USER_SERVER_URL = "http://192.168.10.202";
//    public static final String SHARE_QRCODE_URL = "http://192.168.10.202/common/show_qrcode";


    private ShopApi shopApi;

    public ShopService() {
        RequestInterceptor requestInterceptor = (RequestInterceptor.RequestFacade request) -> request.addHeader("Accept", "application/json");

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(USER_SERVER_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();

        shopApi = restAdapter.create(ShopApi.class);
    }

    public ShopApi getApi() {
        return shopApi;
    }

    public interface ShopApi {
        @FormUrlEncoded
        @POST("/shop/base_info")
        Observable<ShopBase> base_info(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/shop/statistics_info")
        Observable<ShopStatistics> statistics_info(@FieldMap Map<String, String> params);

        @Multipart
        @POST("/shop/add_share")
        Observable<ShareInfo> add_shop_share(@Part("token") String token,  @Part("shop_name") String shop_name, @Part("shop_description") String shop_description, @Part("shop_avatar") TypedFile shop_avatar, @Part("shop_background") TypedFile shop_background, @Part("device_id") String device_id, @Part("device_desc") String device_desc);

        @Multipart
        @POST("/shop/update_share")
        Observable<ShareInfo> update_shop_share(@Part("token") String token, @Part("share_id") String share_id, @Part("shop_name") String shop_name, @Part("shop_description") String shop_description, @Part("shop_avatar") TypedFile shop_avatar, @Part("shop_background") TypedFile shop_background, @Part("device_id") String device_id, @Part("device_desc") String device_desc);

        @FormUrlEncoded
        @POST("/shop/delete_share")
        Observable<Status> delete_shop_share(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/shop/set_default")
        Observable<Status> set_default_shop(@FieldMap Map<String, String> params);


        @FormUrlEncoded
        @POST("/shop/profit_info")
        Observable<ShopProfit> profit_info(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/shop/update_name")
        Observable<Status> update_name(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/shop/update_description")
        Observable<Status> update_description(@FieldMap Map<String, String> params);

        @Multipart
        @POST("/shop/update_avatar")
        Observable<ShopAvatarStatus> update_avatar(@Part("token") String token, @Part("shop_avatar") TypedFile shop_avatar, @Part("device_id") String device_id, @Part("device_desc") String device_desc);

        @Multipart
        @POST("/shop/update_background")
        Observable<ShopAvatarStatus> update_background(@Part("token") String token, @Part("shop_background") TypedFile shop_background, @Part("device_id") String device_id,@Part("device_desc") String device_desc);
    }

    public static HashMap<String,String> gen_base_info_params(String token){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_statistics_info_params(String token){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_profit_info_params(String token){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_update_name_params(String token,String shop_name){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("shop_name",shop_name);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_delete_shop_params(String token,String share_id){
        Log.e("token",token);
        Log.e("share_id",share_id);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("share_id",share_id);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_set_default_params(String token,String share_id){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("share_id",share_id);
        return CommonUtil.appendParams(hashMap);
    }


    public static HashMap<String,String> gen_update_description_params(String token,String shop_description){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("shop_description",shop_description);
        return CommonUtil.appendParams(hashMap);
    }
}
