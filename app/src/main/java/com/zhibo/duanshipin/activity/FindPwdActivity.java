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
import com.google.gson.JsonSyntaxException;
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

public class FindPwdActivity extends BaseActivity {

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
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected int getContentId() {
        return R.layout.activity_find_pwd;
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
                Find_PWD();
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
     * 找回密码
     */
    void Find_PWD() {

//        用户注册  http://lhzb.longhoo.net/index.php?m=api&c=Index&a=public_register
//        参数：phone,pwd,nickname,vcode
//        备注：phone，pwd使用aes加密
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
        map.put("npwd", edtMmText);
        map.put("vcode", edtedtYzm);
        map.put("sys", "1");
        OkHttpUtil.getInstance().doAsyncPost(Consts.BASE_URL + "c=Index&a=public_find_pwd", map, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.has("code")) {
                        if (obj.getString("code").equals("0")) {
                            finish();
                        }
                        String msg = obj.optString("msg");
                        ToastUtils.getInstance().showToast(FindPwdActivity.this, msg);
                    } else {
                        ToastUtils.getInstance().showToast(FindPwdActivity.this, "数据异常~");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToast(FindPwdActivity.this, "服务器异常~");
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToast(FindPwdActivity.this, "服务器异常~");
                }

            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtils.getInstance().showToast(FindPwdActivity.this, "网络错误~");
            }
        });
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
        params.put("type", "2");
        OkHttpUtil.getInstance().doAsyncPost(url, params, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {
                ULog.e("ck", "register:code:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ToastUtils.getInstance().showToast(FindPwdActivity.this, jsonObject.optString("msg"));
                    if (jsonObject.has("code")) {
                        lvTime.setVisibility(View.VISIBLE);
                        emailLoginComfirmtip.setVisibility(View.GONE);
                        if (!jsonObject.getString("code").equals("0")) {
                            mCountDownTime = 1;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToast(FindPwdActivity.this, "服务器异常~");
                    mCountDownTime = 1;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtils.getInstance().showToast(FindPwdActivity.this, "网络错误~");
                mCountDownTime = 1;
            }
        });
    }

    @Override
    protected void initToolbar() {

    }

}
