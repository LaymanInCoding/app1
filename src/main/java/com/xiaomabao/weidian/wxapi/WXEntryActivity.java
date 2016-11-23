package com.xiaomabao.weidian.wxapi;


import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.app.Activity;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{


	@Override
	public void onReq(BaseReq baseReq) {

	}

	@Override
	public void onResp(BaseResp baseResp) {
		finish();
	}
}