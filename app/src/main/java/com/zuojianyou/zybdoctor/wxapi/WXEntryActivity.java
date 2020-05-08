package com.zuojianyou.zybdoctor.wxapi;

import android.content.Intent;
import android.os.Bundle;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.activity.BaseActivity;
import com.zuojianyou.zybdoctor.constants.Constants;

/**
 * Created by jc.Ren on 2018/8/14
 * e-mail:renjiangchao1989@sina.com
 * 微信分享
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_entry);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.registerApp(Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
//        String tip="分享结果";
//        switch (resp.errCode) {
//            case 0:
//                tip = "分享成功";
//                break;
//            case -1:
//                tip = "分享失败";
//                break;
//            case -2:
//                tip = "用户取消";
//                break;
//        }
//        ToastBar.show(this,tip);
        finish();
    }
}
