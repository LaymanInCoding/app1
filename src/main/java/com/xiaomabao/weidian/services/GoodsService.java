package com.xiaomabao.weidian.services;



import com.xiaomabao.weidian.models.Brand;
import com.xiaomabao.weidian.models.Category;
import com.xiaomabao.weidian.models.Goods;
import com.xiaomabao.weidian.models.HotKey;
import com.xiaomabao.weidian.models.SearchGood;
import com.xiaomabao.weidian.models.ShopGoods;
import com.xiaomabao.weidian.models.Status;
import com.xiaomabao.weidian.util.CommonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public class GoodsService {
    private static final String USER_SERVER_URL = "http://vapi.xiaomabao.com";
//    private static final String USER_SERVER_URL = "http://192.168.11.36/api.php/";
//private static final String USER_SERVER_URL = "http://192.168.10.202";


    private GoodsApi api;

    public GoodsService() {
        RequestInterceptor requestInterceptor = (RequestInterceptor.RequestFacade request) -> request.addHeader("Accept", "application/json");

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(USER_SERVER_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(GoodsApi.class);
    }

    public GoodsApi getApi() {
        return api;
    }

    public interface GoodsApi {
        @FormUrlEncoded
        @POST("/goods/category_list")
        Observable<List<Category>> category_list(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/brand/brand_list_v2")
        Observable<List<Brand>> brand_list(@FieldMap Map<String, String> params);


        @FormUrlEncoded
        @POST("/goods/goods_list")
        Observable<List<Goods>> goods_list(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/search/goods")
        Observable<SearchGood> goods_search(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/goods/on_sale")
        Observable<Status> on_sale(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/goods/off_sale")
        Observable<Status> off_sale(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/goods/shop_goods")
        Observable<List<ShopGoods>> shop_goods(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/goods/vip_goods_list")
        Observable<List<Goods>> vip_goods_list(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/brand/goods_list")
        Observable<List<Goods>> brand_goods_list(@FieldMap Map<String,String> params);

        @FormUrlEncoded
        @POST("/search/index")
        Observable<HotKey> search_hot_words(@FieldMap Map<String,String> params);
    }

    public static HashMap<String,String> gen_brand_list_params(String token,int page){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("page",page+"");
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_category_list_params(String token){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_on_sale_params(String token,String goods_id){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("goods_id",goods_id);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_off_sale_params(String token,String goods_id){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("goods_id",goods_id);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_shop_goods_params(String token,int page){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("page",page+"");
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_goods_list_params(String token,String cat_id,String sort,int page,String keyword){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("cat_id",cat_id);
        hashMap.put("sort",sort);
        hashMap.put("page",page+"");
        hashMap.put("keyword",keyword);
        return CommonUtil.appendParams(hashMap);
    }


    public static HashMap<String,String> gen_vip_goods_list_params(String token,String cat_id,String sort,int page,String keyword){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("cat_id",cat_id);
        hashMap.put("sort",sort);
        hashMap.put("page",page+"");
        hashMap.put("keyword",keyword);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_brand_goods_list_params(String token,String topic_id,String type,String sort,int page,String keyword) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", token);
        hashMap.put("topic_id", topic_id);
        hashMap.put("sort", sort);
        hashMap.put("page", page + "");
        hashMap.put("type", (type.equals("normal") ? 0 : 1) + "");
        hashMap.put("keyword", keyword);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_hot_search_list_params(String token,String share_code){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("share_code",share_code);
        return CommonUtil.appendParams(hashMap);
    }

    public static HashMap<String,String> gen_goods_search_params(String token,String share_code,String keywords,String is_vip ){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token",token);
        hashMap.put("share_code",share_code);
        hashMap.put("is_vip",is_vip);
        hashMap.put("keywords",keywords);
        return CommonUtil.appendParams(hashMap);
    }
}
