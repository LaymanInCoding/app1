package com.xiaomabao.weidian.services;

import com.xiaomabao.weidian.models.ApplyShopCode;
import com.xiaomabao.weidian.models.ShopCode;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.models.UserLogin;
import com.xiaomabao.weidian.util.CommonUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public class UserService {
    private static final String USER_SERVER_URL = "http://vapi.xiaomabao.com";
//    private static final String USER_SERVER_URL = "http://192.168.11.36/api.php/";
//    private static final String USER_SERVER_URL = "http://192.168.22.222";


    private UserApi userApi;

    public UserService() {
        RequestInterceptor requestInterceptor = (RequestInterceptor.RequestFacade request) -> request.addHeader("Accept", "application/json");
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(USER_SERVER_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();

        userApi = restAdapter.create(UserApi.class);
    }

    public UserApi getApi() {
        return userApi;
    }

    public interface UserApi {
        @FormUrlEncoded
        @POST("/user/login")
        Observable<UserLogin> login(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/user/login_by_code")
        Observable<UserLogin> login_by_code(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/user/send_login_code")
        Observable<Status> send_login_code(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/user/send_code")
        Observable<Status> send_code(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/user/valid_code")
        Observable<Status> valid_code(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/user/reset_password")
        Observable<Status> reset_password(@FieldMap Map<String, String> params);

//        @FormUrlEncoded
//        @POST("/user/register")
//        Observable<Status> register(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/user/register_without_shopcode")
        Observable<Status> register(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/user/refresh_token")
        Observable<Status> refresh_token(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/user/apply_shop_code")
        Observable<ApplyShopCode> apply_shop_code(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/user/get_shop_code")
        Observable<ShopCode> get_shop_code(@FieldMap Map<String, String> params);
    }

    public static HashMap<String,String> gen_account_login_params(String username,String password){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("username",username);
        hashMap.put("password",password);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_phone_login_params(String phone,String phone_code){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("username",phone);
        hashMap.put("phone_code",phone_code);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_send_code_params(String phone){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("phone",phone);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_valid_code_params(String phone,String phone_code){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("phone",phone);
        hashMap.put("phone_code",phone_code);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_reset_password_params(String phone,String phone_code,String password){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("phone",phone);
        hashMap.put("phone_code",phone_code);
        hashMap.put("password",password);
        return CommonUtil.appendParams(hashMap);
    }

//    public static HashMap<String,String> gen_register_params(String phone,String phone_code,String password,String shop_code,String invite_code){
//        HashMap<String,String> hashMap = new HashMap<>();
//        hashMap.put("username",phone);
//        hashMap.put("phone_code",phone_code);
//        hashMap.put("password",password);
//        hashMap.put("shop_code",shop_code);
//        hashMap.put("invite_code",invite_code);
//        return CommonUtil.appendParams(hashMap);
//    }

    public static HashMap<String,String> gen_register_params(String phone,String phone_code,String password){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("username",phone);
        hashMap.put("phone_code",phone_code);
        hashMap.put("password",password);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_refresh_token_params(String token){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_apply_shop_code_params(String receipt_user,String receipt_phone,String phone_code,String province,String city,String district,String receipt_address){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("receipt_user",receipt_user);
        hashMap.put("receipt_phone",receipt_phone);
        hashMap.put("receipt_code",phone_code);
        hashMap.put("province_name",province);
        hashMap.put("city_name",city);
        hashMap.put("district_name",district);
        hashMap.put("receipt_address",receipt_address);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_get_shop_code_params(String phone,String order_sn){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("mobile",phone);
        hashMap.put("order_sn",order_sn);
        return CommonUtil.appendParams(hashMap);
    }

}
