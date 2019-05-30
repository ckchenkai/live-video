package com.zhibo.duanshipin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhibo.duanshipin.MyApplication;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.base.BaseActivity;
import com.zhibo.duanshipin.bean.IsLoginBean;
import com.zhibo.duanshipin.bean.LoginBean;
import com.zhibo.duanshipin.httprequest.OkHttpCallback;
import com.zhibo.duanshipin.httprequest.OkHttpUtil;
import com.zhibo.duanshipin.utils.AES64;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;
import com.zhibo.duanshipin.utils.Util;
import com.zhibo.duanshipin.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class PhoneLoginActivity extends BaseActivity {

    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.edt_mm)
    EditText edtMm;
    @BindView(R.id.findpwd)
    TextView findpwd;
    @BindView(R.id.email_login_comfirmtip)
    LinearLayout emailLoginComfirmtip;
    @BindView(R.id.img_confrimregist)
    ImageView imgConfrimregist;
    @BindView(R.id.tv_cancl)
    ImageView tvCancl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getContentId() {
        return R.layout.activity_phone_login;
    }

    @Override
    protected void initViews() {
        Intent intent = getIntent();
        String strPhone = intent.getStringExtra("phone");
        ULog.e("loginmessage=========", strPhone);
        tvPhone.setText(strPhone);
        tvCancl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //找回密码
                Intent intent = new Intent();
                intent.setClass(PhoneLoginActivity.this, FindPwdActivity.class);
                intent.putExtra("phone", tvPhone.getText().toString().trim());
                startActivity(intent);
            }
        });
        imgConfrimregist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneLogin();
            }
        });
    }

    void PhoneLogin() {
        //////     /  判断用户是否已注册   http://lhzb.longhoo.net/index.php?m=api&c=Index&a=public_check
//
//        参数phone，用aes加密
        Map<String, String> map = new HashMap<>();
        String phoneText = tvPhone.getText().toString().trim();
        if (!Utils.isMobile(phoneText)) {
            ToastUtils.getInstance().showToast(this, "手机格式不正确！");
            return;
        }
        try {
            phoneText = new AES64().encrypt(phoneText);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.getInstance().showToast(this, "AES加密失败！");
            return;
        }
        String edtMmText = edtMm.getText().toString().trim();
        if (edtMmText.isEmpty()) {
            ToastUtils.getInstance().showToast(this, "密码不能为空");
            return;
        }
        try {
            edtMmText = new AES64().encrypt(edtMmText);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.getInstance().showToast(this, "AES加密失败！");
            return;
        }
        map.put("type", "1");
        map.put("phone", phoneText);
        map.put("pwd", edtMmText);
        map.put("sys", "1");
        OkHttpUtil.getInstance().doAsyncPost(Consts.BASE_URL + "c=Index&a=public_login", map, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {
                ULog.e("loginmessage=========", response);
                try {
                    JSONObject obj = new JSONObject(response);


                    if (obj.has("code")) {
                        if (obj.getString("code").equals("0")) {
                            Gson mGson = new Gson();
                            LoginBean mLoginBean = mGson.fromJson(response, LoginBean.class);
                            SPTool.putString(PhoneLoginActivity.this, Consts.SP_UID, mLoginBean.getData().getUid());
                            ULog.e("ck", "三方：" + mLoginBean.getData().getUid());
                            SPTool.putString(PhoneLoginActivity.this, Consts.SP_PHONE, mLoginBean.getData().getPhone());
                            SPTool.putString(PhoneLoginActivity.this, Consts.SP_PWD,edtMm.getText().toString().trim() );
                            SPTool.putString(PhoneLoginActivity.this, Consts.SP_NICKNAME, mLoginBean.getData().getNickname());
                            SPTool.putString(PhoneLoginActivity.this, Consts.SP_HEADRUL, mLoginBean.getData().getHeadpic());
                            SPTool.putString(PhoneLoginActivity.this, Consts.SP_UKEY, mLoginBean.getData().getUkey());
                            SPTool.putString(PhoneLoginActivity.this, Consts.SP_LOGINTYPE, "1");
                            SPTool.putString(PhoneLoginActivity.this, Consts.SP_HEADRUL, mLoginBean.getData().getHeadpic());
                            SPTool.putString(PhoneLoginActivity.this, Consts.SP_SHAREURL, mLoginBean.getData().getShareurl());
                            SPTool.putString(PhoneLoginActivity.this, Consts.SP_SHAREICON, mLoginBean.getData().getSharepic());
                            ULog.e("headurl1", mLoginBean.getData().getHeadpic());
                            MyApplication App = (MyApplication) getApplicationContext();
                            App.RemoveActivity("com.zhibo.duanshipin.activity.LoginActivity");
                            Utils.showHideSoftInput(PhoneLoginActivity.this,edtMm,false);
                            finish();
                        } else {
                            Toast.makeText(PhoneLoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PhoneLoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtils.getInstance().showToast(PhoneLoginActivity.this, "网络错误~");

            }
        });


    }

    @Override
    protected void initToolbar() {

    }

}
