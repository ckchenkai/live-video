package com.zhibo.duanshipin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.base.BaseActivity;
import com.zhibo.duanshipin.bean.IsLoginBean;
import com.zhibo.duanshipin.httprequest.OkHttpCallback;
import com.zhibo.duanshipin.httprequest.OkHttpUtil;
import com.zhibo.duanshipin.utils.AES64;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/9/14.
 */

public class BindPhoneActivity extends BaseActivity {
    @BindView(R.id.topview)
    LinearLayout topview;
    @BindView(R.id.tv_tipphone)
    TextView tvTipphone;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.email_login_comfirmtip)
    LinearLayout emailLoginComfirmtip;
    @BindView(R.id.img_confrim)
    ImageView imgConfrim;
    @BindView(R.id.email_login_comfirm)
    LinearLayout emailLoginComfirm;
    @BindView(R.id.email_login_form)
    LinearLayout emailLoginForm;
    @BindView(R.id.tv_cancl)
    TextView tvCancl;
    @BindView(R.id.login_form)
    ScrollView loginForm;
    @BindView(R.id.tv_useragreement)
    TextView tvUseragreement;

    @Override
    protected int getContentId() {
        return R.layout.activity_bind_phone;
    }

    @Override
    protected void initViews() {
        Utils.showHideSoftInput(BindPhoneActivity.this, edtPhone, true);
        getWindow().getDecorView().setFitsSystemWindows(true);
        imgConfrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsRegistPhone();
            }
        });
        tvCancl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvUseragreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.goToWebView(BindPhoneActivity.this,Consts.BASE_URL+"c=Index&a=public_regist","龙虎直播用户协议",false);
            }
        });
    }

    @Override
    protected void initToolbar() {

    }

    void IsRegistPhone() {

//        判断用户是否已注册   http://lhzb.longhoo.net/index.php?m=api&c=Index&a=public_check
//
//        参数phone，用aes加密
        Map<String, String> map = new HashMap<>();
        String phoneText = edtPhone.getText().toString().trim();
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
        map.put("phone", phoneText);
        map.put("sys", "1");
        OkHttpUtil.getInstance().doAsyncPost(Consts.BASE_URL + "c=Index&a=public_check", map, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {

                try {
                    Gson gson = new Gson();
                    IsLoginBean mIsLoginBean = gson.fromJson(response, IsLoginBean.class);


                    ToastUtils.getInstance().showToast(BindPhoneActivity.this, mIsLoginBean.getMsg());

                    if (mIsLoginBean.getCode().equals("0")) {
                        //注册
                        if (mIsLoginBean.getData().getStatus() == 0) {
                            Intent intent = new Intent();
                            intent.setClass(BindPhoneActivity.this, Bind_phone_pwd_activity.class);
                            intent.putExtra("phone", edtPhone.getText().toString().trim());
                            startActivity(intent);
                        } else {
                            //登录
                            ToastUtils.getInstance().showToast(BindPhoneActivity.this, "手机号已注册，请更改手机号码");

                        }
                    } else {
                        ToastUtils.getInstance().showToast(BindPhoneActivity.this, "服务器数据异常~");
                    }

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToast(BindPhoneActivity.this, "服务器异常~");

                }
            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtils.getInstance().showToast(BindPhoneActivity.this, "网络错误~");

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
