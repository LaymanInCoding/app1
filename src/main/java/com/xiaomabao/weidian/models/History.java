package com.xiaomabao.weidian.models;

import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by de on 2016/10/11.
 */
public class History extends RealmObject {

    String keyWords;

    String time;

    public History(){
    }
    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
