package com.xiaomabao.weidian.views;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteFriendActivity extends AppCompatActivity {

    @OnClick(R.id.back)void back(){
        finish();
    }

    @OnClick(R.id.invite_btn)void invite(){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("desc","欢迎成为共享购的新朋友\n" +
                "购买开店大礼包，免费赠您开店资格，超级划算哦");
        hashMap.put("url",AppContext.getShopInviteUrl(this));
        XmbPopubWindow.showShopChooseWindow(this,hashMap,"Show_Share","2");
//        XmbPopubWindow.showShare(this, AppContext.getShopInviteQr(this),AppContext.getShopInviteUrl(this),hashMap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        ButterKnife.bind(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
