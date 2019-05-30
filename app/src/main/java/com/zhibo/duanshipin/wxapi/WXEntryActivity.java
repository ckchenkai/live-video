package com.zhibo.duanshipin.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhibo.duanshipin.activity.LoginActivity;
import com.zhibo.duanshipin.utils.SharedUtil;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (SharedUtil.wxApi == null) {
                SharedUtil.wxApi = WXAPIFactory.createWXAPI(this, SharedUtil.APP_KEY_WX, false);
            }
            SharedUtil.wxApi.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }


    @Override
    public void onResp(BaseResp resp) {
        if (LoginActivity.isWXLogin) {
            try {
                SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                LoginActivity.WX_CODE = sendResp.code;
                LoginActivity.isWXLogin = true;

//                Toast.makeText(this, "WX_CODE=" + LoginActivity.WX_CODE, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }

        // Toast.makeText(this, "成功!" + LoginActivity.WX_CODE,
        // Toast.LENGTH_LONG)
        // .show();
        switch (resp.errCode) {

            case BaseResp.ErrCode.ERR_OK:
                if (LoginActivity.isWXLogin) {
                    SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                    LoginActivity.WX_CODE = sendResp.code;
                    LoginActivity.isWXLogin = true;
                    finish();
                } else {
                    Toast.makeText(this, "成功!", Toast.LENGTH_LONG).show();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(this, "取消!", Toast.LENGTH_LONG).show();
                LoginActivity.isWXLogin = false;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(this, "被拒绝", Toast.LENGTH_LONG).show();
                LoginActivity.isWXLogin = false;
                break;
            default:
                Toast.makeText(this, "失败!", Toast.LENGTH_LONG).show();
                LoginActivity.isWXLogin = false;
                break;
        }

        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        SharedUtil.wxApi.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }
}
