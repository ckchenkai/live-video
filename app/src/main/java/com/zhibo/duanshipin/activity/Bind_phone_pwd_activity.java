package com.zhibo.duanshipin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhibo.duanshipin.MyApplication;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.base.BaseActivity;
import com.zhibo.duanshipin.bean.LoginBean;
import com.zhibo.duanshipin.httprequest.OkHttpCallback;
import com.zhibo.duanshipin.httprequest.OkHttpUtil;
import com.zhibo.duanshipin.utils.AES64;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;
import com.zhibo.duanshipin.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/9/14.
 */

public class Bind_phone_pwd_activity extends BaseActivity {
    @BindView(R.id.topview)
    LinearLayout topview;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.lv_nomore)
    LinearLayout lvNomore;
    @BindView(R.id.edt_yzm)
    EditText edtYzm;
    @BindView(R.id.edt_mm)
    EditText edtMm;
    @BindView(R.id.resend)
    TextView resend;
    @BindView(R.id.email_login_comfirmtip)
    LinearLayout emailLoginComfirmtip;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.lv_time)
    LinearLayout lvTime;
    @BindView(R.id.img_confrimregist)
    ImageView imgConfrimregist;
    @BindView(R.id.email_login_comfirm)
    LinearLayout emailLoginComfirm;
    @BindView(R.id.email_login_form)
    LinearLayout emailLoginForm;
    @BindView(R.id.tv_cancl)
    ImageView tvCancl;
    @BindView(R.id.login_form)
    ScrollView loginForm;
    private int mCountDownTime = 60;
    @Override
    protected int getContentId() {
        return R.layout.activity_bind_phone_pwd;
    }

    @Override
    protected void initViews() {
        Intent intent = getIntent();
        String strPhone = intent.getStringExtra("phone");
        ULog.e("loginmessage=========", strPhone);
        tvPhone.setText(strPhone);

        lvTime.setVisibility(View.VISIBLE);
        emailLoginComfirmtip.setVisibility(View.GONE);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doRequestCode();
            }
        });

        imgConfrimregist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneRegist();
            }
        });

        doRequestCode();
        tvCancl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (--mCountDownTime == 0) {
                    lvTime.setVisibility(View.GONE);
                    emailLoginComfirmtip.setVisibility(View.VISIBLE);
                    mCountDownTime = 60;
                    removeMessages(0);
                } else {
                    tvTime.setText((mCountDownTime) + "s");
                    sendEmptyMessageDelayed(0, 1000);
                }
            }
        }
    };



    /**
     * 手机号登录
     */
    void PhoneRegist() {

//        http://lhzb.longhoo.net/index.php?m=api&c=Index&a=binding
//
//        uid,pwd,phone,vcode
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
        String edtedtYzm = edtYzm.getText().toString().trim();
        if (edtedtYzm.isEmpty()) {
            ToastUtils.getInstance().showToast(this, "验证码不能为空");
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
        map.put("phone", phoneText);
        map.put("pwd", edtMmText);
        map.put("vcode", edtedtYzm);
        map.put("uid", SPTool.getString(this, Consts.SP_UID, ""));
        map.put("ukey", SPTool.getString(this, Consts.SP_UKEY, ""));
        map.put("versioncode", Utils.getVersionName(Bind_phone_pwd_activity.this));
        map.put("versionno", Utils.getLocalVersion(Bind_phone_pwd_activity.this));
        map.put("sys", "1");
        ULog.e("loginmessage=========", "phone="+ phoneText+";pwd="+ edtMmText+";vcode="+ edtedtYzm+";uid="+SPTool.getString(this, Consts.SP_UID, "")+";sys="+ "1");
        OkHttpUtil.getInstance().doAsyncPost(Consts.BASE_URL + "c=Index&a=binding", map, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {

                ULog.e("loginmessage=========", response);

                try {
                    JSONObject obj = new JSONObject(response);


                    if (obj.has("code")) {
                        if (obj.getString("code").equals("0")) {
                            SPTool.putString(Bind_phone_pwd_activity.this, Consts.SP_PHONE, tvPhone.getText().toString().trim());
                            SPTool.putString(Bind_phone_pwd_activity.this, Consts.SP_PWD,edtMm.getText().toString().trim() );
                            MyApplication App=(MyApplication)getApplicationContext();
                            App.RemoveActivity("com.zhibo.duanshipin.activity.BindPhoneActivity");
                            finish();

                        } else {
                            Toast.makeText(Bind_phone_pwd_activity.this, "绑定失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Bind_phone_pwd_activity.this, "绑定失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }





            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtils.getInstance().showToast(Bind_phone_pwd_activity.this, "网络错误~");

            }
        });
    }
    @Override
    protected void initToolbar() {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }



    /**
     * 获取验证码
     */
    private void doRequestCode() {


//        用户发送验证码   http://lhzb.longhoo.net/index.php?m=api&c=Index&a=public_code
//        参数：phone
//        备注：phone使用aes加密
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

        tvTime.setText(mCountDownTime + "s后重试");

        mHandler.sendEmptyMessageDelayed(0, 1000);
        String url = Consts.BASE_URL + "c=Index&a=public_code";
        Map<String, String> params = new HashMap<>();
        params.put("phone", phoneText);
        params.put("versionno", Utils.getLocalVersion(this));
        params.put("versioncode", Utils.getVersionName(this));
        params.put("sys", "1");
        params.put("type", "3");
        OkHttpUtil.getInstance().doAsyncPost(url, params, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {
                ULog.e("ck", "register:code:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ToastUtils.getInstance().showToast(Bind_phone_pwd_activity.this, jsonObject.optString("msg"));
                    if (jsonObject.has("code")) {
                        lvTime.setVisibility(View.VISIBLE);
                        emailLoginComfirmtip.setVisibility(View.GONE);
                        if (!jsonObject.getString("code").equals("0")) {
                            mCountDownTime = 1;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToast(Bind_phone_pwd_activity.this, "服务器异常~");
                    mCountDownTime = 1;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtils.getInstance().showToast(Bind_phone_pwd_activity.this, "网络错误~");
                mCountDownTime = 1;
            }
        });
    }
}
