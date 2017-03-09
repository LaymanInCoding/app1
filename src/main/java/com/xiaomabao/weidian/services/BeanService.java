package com.xiaomabao.weidian.services;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.Bean;
import com.xiaomabao.weidian.models.BeanStatus;
import com.xiaomabao.weidian.util.CommonUtil;
import java.util.HashMap;
import java.util.Map;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by de on 2016/12/21.
 */
public class BeanService {
//        public static final String Common_Base_Url = "http://vapi.xiaomabao.com/";
    public static final String Common_Base_Url = Const.BASE_URL;


    private BeanApi beanApi;

    public BeanService() {
        RequestInterceptor requestInterceptor = (RequestInterceptor.RequestFacade request) -> request.addHeader("Accept", "application/json");
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Common_Base_Url)
                .setRequestInterceptor(requestInterceptor)
                .build();

        beanApi = restAdapter.create(BeanApi.class);
    }

    public BeanApi getApi() {
        return beanApi;
    }

    public interface BeanApi {

        @FormUrlEncoded
        @POST("/bean/info/")
        Observable<Bean> getMyBean(@FieldMap Map<String, String> params);


        @FormUrlEncoded
        @POST("/bean/send")
        Observable<BeanStatus> sendBean(@FieldMap Map<String, String> params);

    }

    public static HashMap<String, String> gen_sendBean_param(String token, String send_number, String friend_account) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", token);
        hashMap.put("send_number", send_number);
        hashMap.put("friend_account", friend_account);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String, String> gen_getBean_param(String token, int page) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", token);
        hashMap.put("page", page + "");
        return CommonUtil.appendParams(hashMap);
    }
}
