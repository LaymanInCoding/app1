package com.xiaomabao.weidian.services;

import android.util.Log;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.BindCard;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.models.WithdrawRecord;
import com.xiaomabao.weidian.util.CommonUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public class ProfitService {
//    private static final String USER_SERVER_URL = "http://vapi.xiaomabao.com";
    private static final String USER_SERVER_URL = Const.BASE_URL;

    private ProfitApi mApi;

    public ProfitService() {
        RequestInterceptor requestInterceptor = (RequestInterceptor.RequestFacade request) -> request.addHeader("Accept", "application/json");

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(USER_SERVER_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();

        mApi = restAdapter.create(ProfitApi.class);
    }

    public ProfitApi getApi() {
        return mApi;
    }

    public interface ProfitApi {
        @FormUrlEncoded
        @POST("/profit/bind_card")
        Observable<BindCard> bind_card(@FieldMap Map<String, String> params);


        @FormUrlEncoded
        @POST("/profit/apply_withdraw")
        Observable<Status> apply_withdraw(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/profit/withdraw_record")
        Observable<WithdrawRecord> withdraw_record(@FieldMap Map<String, String> params);
    }

    public static HashMap<String,String> gen_bind_card_params(String token,String real_name,String province_name,String city_name,String district_name,String deposit_bank,String branch_bank,String card_no,String mobile_phone){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("real_name",real_name);
        hashMap.put("province_name",province_name);
        hashMap.put("city_name",city_name);
        hashMap.put("district_name",district_name);
        hashMap.put("deposit_bank",deposit_bank);
        hashMap.put("branch_bank",branch_bank);
        hashMap.put("card_no",card_no);
        hashMap.put("mobile_phone",mobile_phone);
        Log.d("map",hashMap.toString());
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_apply_withdraw_params(String token,double apply_money){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("apply_money",apply_money + "");
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_withdraw_record_params(String token,int page){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("page",page+"");
        return CommonUtil.appendParams(hashMap);
    }

}
