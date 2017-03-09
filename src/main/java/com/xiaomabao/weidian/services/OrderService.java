package com.xiaomabao.weidian.services;

import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.OrderDetail;
import com.xiaomabao.weidian.models.OrderList;
import com.xiaomabao.weidian.models.OrderType;
import com.xiaomabao.weidian.util.CommonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public class OrderService {

//    private static final String ORDER_SERVER_URL = "http://vapi.xiaomabao.com";
    private static final String ORDER_SERVER_URL = Const.BASE_URL;

    private OrderApi api;

    public OrderService() {
        RequestInterceptor requestInterceptor = (RequestInterceptor.RequestFacade request) -> request.addHeader("Accept", "application/json");

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ORDER_SERVER_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(OrderApi.class);
    }

    public OrderApi getApi() {
        return api;
    }

    public interface OrderApi {
        @FormUrlEncoded
        @POST("/order/order_type")
        Observable<List<OrderType>> order_type(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/order/order_list")
        Observable<OrderList> order_list(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/order/order_detail")
        Observable<OrderDetail> order_detail(@FieldMap Map<String, String> params);
    }

    public static HashMap<String,String> gen_order_type_params(String token){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_order_list_params(String token,String order_sn,String order_type,int page){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("order_sn",order_sn);
        hashMap.put("order_type",order_type + "");
        hashMap.put("page",page + "");
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_order_detail_params(String token,String order_sn){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("order_sn",order_sn);
        return CommonUtil.appendParams(hashMap);
    }

}
