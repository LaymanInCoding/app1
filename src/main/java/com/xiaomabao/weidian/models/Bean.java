package com.xiaomabao.weidian.models;

import java.util.ArrayList;

/**
 * Created by de on 2016/12/21.
 */
public class Bean {
    public String number;

    public ArrayList<BeanRecord> records;

    public class BeanRecord{
        public String id;
        public String user_id;
        public String record_time;
        public String record_desc;
        public String record_val;
        public String record_type;
    }
}
