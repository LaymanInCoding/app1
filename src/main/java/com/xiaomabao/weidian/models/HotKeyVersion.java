package com.xiaomabao.weidian.models;

import io.realm.RealmObject;

/**
 * Created by de on 2016/10/12.
 */
public class HotKeyVersion extends RealmObject{
    public String version;

    public HotKeyVersion(){
    }
    public void setVersion(String version){
        this.version = version;
    }
    public String getVersion(){
        return  this.version;
    }
}
