package com.xiaomabao.weidian.services;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.HKUpdateInfo;
import com.xiaomabao.weidian.models.UpdateInfo;
import com.xiaomabao.weidian.util.CommonUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by de on 2016/9/8.
 */
public class CommonService {
//    private static final String Common_Base_Url = "http://vapi.xiaomabao.com/";
    public static final String Common_Base_Url = Const.BASE_URL;


    private CommonApi commonApi;

    public CommonService() {
        RequestInterceptor requestInterceptor = (RequestInterceptor.RequestFacade request) -> request.addHeader("Accept", "application/json");
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Common_Base_Url)
                .setRequestInterceptor(requestInterceptor)
                .build();

        commonApi = restAdapter.create(CommonApi.class);
    }

    public CommonApi getApi() {
        return commonApi;
    }

    public interface CommonApi {

        @FormUrlEncoded
        @POST("/common/update")
        Observable<UpdateInfo> getUpdateInfo(@FieldMap Map<String, String> params);

        @POST("/download/check_searchlib_version")
        Observable<HKUpdateInfo> getHotSearchInfo();

    }

    public static HashMap<String, String> gen_update_params(String device) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("device", device);
        return CommonUtil.appendParams(hashMap);
    }
}
