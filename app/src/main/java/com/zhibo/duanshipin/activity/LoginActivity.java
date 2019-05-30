package com.zhibo.duanshipin.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhibo.duanshipin.MyApplication;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.base.BaseActivity;
import com.zhibo.duanshipin.bean.IsLoginBean;
import com.zhibo.duanshipin.bean.LoginBean;
import com.zhibo.duanshipin.httprequest.OkHttpCallback;
import com.zhibo.duanshipin.httprequest.OkHttpUtil;
import com.zhibo.duanshipin.utils.AES64;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.DownloadPicHelp;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.SharedUtil;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;
import com.zhibo.duanshipin.utils.Util;
import com.zhibo.duanshipin.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements DownloadPicHelp.MultiFileFormSubmitListener {


    public static String WX_CODE = "";


    public static boolean isWXLogin = false;

    public static Tencent mTencent;
    /**
     * 腾讯
     */

    private static boolean isServerSideLogin = false;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.img_qq_login)
    ImageView imgQqLogin;
    @BindView(R.id.img_wx_login)
    ImageView imgWxLogin;
    @BindView(R.id.img_sina_login)
    ImageView imgSinaLogin;
    @BindView(R.id.email_ohterlogin)
    LinearLayout emailOhterlogin;
    @BindView(R.id.email_login_comfirm)
    LinearLayout emailLoginComfirm;
    @BindView(R.id.tv_cancl)
    TextView tvCancl;
    @BindView(R.id.img_confrim)
    ImageView imgConfrim;
    @BindView(R.id.login_form)
    ScrollView loginForm;
    @BindView(R.id.tv_useragreement)
    TextView tvUseragreement;
    private UserInfo mInfo;

    /**
     * sina
     */
    private AuthInfo mAuthInfo;

    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;

    String strOuid;// 第三方的ID
    String strType;// 登录的类型
    String strGender;// 性别
    String strOpenid;// openid
    String strProfile;// 头像
    String strNickname;// 昵称
    DownloadPicHelp mDownloadPicHelp;

    @Override
    protected int getContentId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {
        Utils.showHideSoftInput(LoginActivity.this, edtPhone, true);
        getWindow().getDecorView().setFitsSystemWindows(true);
        edtPhone.addTextChangedListener(new EditChangedListener());
        mDownloadPicHelp = new DownloadPicHelp();
        if (SharedUtil.wxApi == null) {
            SharedUtil.wxApi = WXAPIFactory.createWXAPI(this, SharedUtil.APP_KEY_WX.trim(), true);
            SharedUtil.wxApi.registerApp(SharedUtil.APP_KEY_WX.trim());
        }
        imgWxLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedUtil.wxApi.isWXAppInstalled()) {
                    // 提醒用户没有按照微信
                    Toast.makeText(LoginActivity.this,
                            "没有安装微信,请先安装微信!", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }


                WXLogion();
            }
        });
        initSinaContorller();

        if (mTencent == null) {
            mTencent = Tencent
                    .createInstance(SharedUtil.APP_KEY_TECENT, LoginActivity.this);
        }
        imgQqLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogin();
            }
        });
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
                WebViewActivity.goToWebView(LoginActivity.this, Consts.BASE_URL + "c=Index&a=public_regist ", "龙虎直播用户协议", false);
            }
        });
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
//        Log.e("phone",phoneText);
        map.put("phone", phoneText);
        map.put("sys", "1");
        OkHttpUtil.getInstance().doAsyncPost(Consts.BASE_URL + "c=Index&a=public_check", map, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {

                try {
                    Gson gson = new Gson();
                    IsLoginBean mIsLoginBean = gson.fromJson(response, IsLoginBean.class);


//                    ToastUtils.getInstance().showToast(LoginActivity.this, mIsLoginBean.getMsg());

                    if (mIsLoginBean.getCode().equals("0")) {
                        //注册
                        if (mIsLoginBean.getData().getStatus() == 0) {
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, RegistActivity.class);
                            intent.putExtra("phone", edtPhone.getText().toString().trim());
                            startActivity(intent);
                        } else {
                            //登录
                            ToastUtils.getInstance().showToast(LoginActivity.this, "已注册，请登录");
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, PhoneLoginActivity.class);
                            intent.putExtra("phone", edtPhone.getText().toString().trim());
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        ToastUtils.getInstance().showToast(LoginActivity.this, "服务器数据异常~");
                    }

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToast(LoginActivity.this, "服务器异常~");

                }
            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtils.getInstance().showToast(LoginActivity.this, "网络错误~");

            }
        });


    }


    class EditChangedListener implements TextWatcher {
        private CharSequence temp; // 监听前的文本
        private int editStart; // 光标开始位置
        private int editEnd; // 光标结束位置

        // 输入文本之前的状态
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        // 输入文字中的状态，count是一次性输入字符数
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
//          if (charMaxNum - s.length() <= 5) {
//              tip.setText("还能输入" + (charMaxNum - s.length()) + "字符");
//          }
            if (s.length() > 0) {
                emailLoginComfirm.setVisibility(View.VISIBLE);
                emailOhterlogin.setVisibility(View.GONE);
            } else {
                emailLoginComfirm.setVisibility(View.GONE);
                emailOhterlogin.setVisibility(View.VISIBLE);
            }
        }

        // 输入文字后的状态
        @Override
        public void afterTextChanged(Editable s) {
            /** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
            editStart = edtPhone.getSelectionStart();
            editEnd = edtPhone.getSelectionEnd();
            if (s.length() > 0) {
                emailLoginComfirm.setVisibility(View.VISIBLE);
                emailOhterlogin.setVisibility(View.GONE);
            } else {
                emailLoginComfirm.setVisibility(View.GONE);
                emailOhterlogin.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected void initToolbar() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    //微信登录
    void WXLogion() {
        strType = "2";
        isWXLogin = true;
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        SharedUtil.wxApi.sendReq(req);
    }


    @Override
    public void onResume() {
        super.onResume();

        // if (isHomeactivity) {
        // Intent intent = new Intent();
        // intent.setClass(LoginActivity.this, HomeActivity.class);
        // startActivity(intent);
        // finish();
        // }
        // isHomeactivity = false;
        if (isWXLogin) {

            String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                    + SharedUtil.APP_KEY_WX.trim()
                    + "&secret="
                    + SharedUtil.WX_SECRET.trim()
                    + "&code="
                    + WX_CODE
                    + "&grant_type=authorization_code";
            //登录成功
//            WXReturnNode.Request(LoginActivity.this, accessTokenUrl,
//                    LoginActivity.this, WXLOGININFO);


            OkHttpUtil.getInstance().doAsyncGet(accessTokenUrl, new OkHttpCallback() {
                @Override
                public void onSuccess(String response) {
                    if (null != response) {
//                        Toast.makeText(LoginActivity.this,
//                                "微信登录成功!" + response, Toast.LENGTH_SHORT)
//                                .show();
                        String accessToken;
                        try {
                            JSONObject MyJSONObject = new JSONObject(response);
                            accessToken = MyJSONObject.getString("access_token");
                            String openId = MyJSONObject.getString("openid");
                            String userUrl = "https://api.weixin.qq.com/sns/userinfo?access_token="
                                    + accessToken + "&openid=" + openId;
//                            WXReturnNode.Request(LoginActivity.this, userUrl,
//                                    LoginActivity.this, WXLOGININFOtwo);

                            OkHttpUtil.getInstance().doAsyncGet(userUrl, new OkHttpCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    try {
                                        ULog.e("weixinresponse", response);
                                        JSONObject MySecondJSONObject = new JSONObject(response);
                                        strOpenid = MySecondJSONObject.getString("openid");
                                        strNickname = MySecondJSONObject.getString("nickname");
                                        strGender = MySecondJSONObject.getString("sex");
                                        strProfile = MySecondJSONObject.getString("headimgurl");
                                        strOuid = MySecondJSONObject.getString("unionid");
                                        SPTool.putString(LoginActivity.this, Consts.SP_LOGINTYPE, "2");
                                        SPTool.putString(LoginActivity.this, Consts.SP_NICKNAME, strNickname);
                                        SPTool.putString(LoginActivity.this, Consts.SP_OUID, strOuid);
                                        SPTool.putString(LoginActivity.this, Consts.SP_OPTENID, strOpenid);
                                        mDownloadPicHelp.SaveImageview(LoginActivity.this, strProfile, LoginActivity.this);
                                        ULog.e("weixinimageview", strProfile);
//                                        Toast.makeText(LoginActivity.this, "微信头像"+strProfile, Toast.LENGTH_SHORT).show();
                                        Toast.makeText(LoginActivity.this,
                                                "微信登录成功!", Toast.LENGTH_SHORT)
                                                .show();
                                        // Toast.makeText(LoginActivity.this, "结果为openid：" + strOpenid,
                                        // 5000).show();
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(String errorMsg) {

                                }
                            });
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    isWXLogin = false;
                }

                @Override
                public void onFailure(String errorMsg) {

                }
            });


        }
    }
//QQ登录

    //QQ登录
    private void onClickLogin() {
        isWXLogin = false;
        strType = "3";
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", loginListener);
            isServerSideLogin = false;
            ULog.d("SDKQQAgentPref",
                    "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
        } else {
            if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
                mTencent.logout(this);
                mTencent.login(this, "all", loginListener);
                isServerSideLogin = false;
                ULog.d("SDKQQAgentPref",
                        "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
                return;
            }
            mTencent.logout(this);
            updateUserInfo();
        }
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                JSONObject response = (JSONObject) msg.obj;

                // {"is_yellow_year_vip":"0","ret":0,"figureurl_qq_1":"http:\/\/q.qlogo.cn\/qqapp\/1105381453\/B75232A051002D9BC100C8FB5800EB60\/40",
                // "figureurl_qq_2":"http:\/\/q.qlogo.cn\/qqapp\/1105381453\/B75232A051002D9BC100C8FB5800EB60\/100",
                // "nickname":"静静是谁","yellow_vip_level":"0","is_lost":0,"msg":"","city":"苏州",
                // "figureurl_1":"http:\/\/qzapp.qlogo.cn\/qzapp\/1105381453\/B75232A051002D9BC100C8FB5800EB60\/50","vip":"0",
                // "level":"0","figureurl_2":"http:\/\/qzapp.qlogo.cn\/qzapp\/1105381453\/B75232A051002D9BC100C8FB5800EB60\/100",
                // "province":"江苏","is_yellow_vip":"0","gender":"男","" +
                // "figureurl":"http:\/\/qzapp.qlogo.cn\/qzapp\/1105381453\/B75232A051002D9BC100C8FB5800EB60\/30"}
                try {
                    if (response.has("nickname")) {
                        strNickname = response.getString("nickname");
                    }
                    if (response.has("gender")) {
                        strGender = response.getString("gender");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Toast.makeText(LoginActivity.this,
                        "QQ登陆成功!", Toast.LENGTH_SHORT)
                        .show();
                SPTool.putString(LoginActivity.this, Consts.SP_LOGINTYPE, "3");
                SPTool.putString(LoginActivity.this, Consts.SP_NICKNAME, strNickname);
                SPTool.putString(LoginActivity.this, Consts.SP_OUID, strOuid);
                SPTool.putString(LoginActivity.this, Consts.SP_OPTENID, strOpenid);

                mDownloadPicHelp.SaveImageview(LoginActivity.this, strProfile, LoginActivity.this);
            } else if (msg.what == 1) {
                // 头像
                Bitmap bitmap = (Bitmap) msg.obj;

            }
        }

    };

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {

                @Override
                public void onError(UiError e) {

                }

                @Override
                public void onComplete(final Object response) {
                    Message msg = new Message();
                    msg.obj = response;
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                    new Thread() {

                        @Override
                        public void run() {
                            JSONObject json = (JSONObject) response;
                            // {"ret":0,"pay_token":"23554DA2D8CAA32A4ADE8269DEF9BA02",
                            // "pf":"desktop_m_qq-10000144-android-2002-","query_authority_cost":274,"authority_cost":-50626799,
                            // "openid":"B75232A051002D9BC100C8FB5800EB60","expires_in":7776000,
                            // "pfkey":"fa9b298e788fc1031cdda78ba3467ba4","msg":"",
                            // "access_token":"B32DA901FABF8E6854B55B848CA340D7","login_cost":241}

                            // {"is_yellow_year_vip":"0","ret":0,
                            // "figureurl_qq_1":"http:\/\/q.qlogo.cn\/qqapp\/1105381453\/B75232A051002D9BC100C8FB5800EB60\/40",
                            // "figureurl_qq_2":"http:\/\/q.qlogo.cn\/qqapp\/1105381453\/B75232A051002D9BC100C8FB5800EB60\/100","nickname":"静静是谁",
                            // "yellow_vip_level":"0","is_lost":0,"msg":"","city":"苏州",
                            // "figureurl_1":"http:\/\/qzapp.qlogo.cn\/qzapp\/1105381453\/B75232A051002D9BC100C8FB5800EB60\/50",
                            // "vip":"0","level":"0","figureurl_2":"http:\/\/qzapp.qlogo.cn\/qzapp\/1105381453\/B75232A051002D9BC100C8FB5800EB60\/100",
                            // "province":"江苏","is_yellow_vip":"0",
                            // "gender":"男","figureurl":"http:\/\/qzapp.qlogo.cn\/qzapp\/1105381453\/B75232A051002D9BC100C8FB5800EB60\/30"}

                            if (json.has("figureurl_qq_1")) {
                                Bitmap bitmap = null;
                                try {
                                    // bitmap = Util.getbitmap(json
                                    // .getString("figureurl_qq_2"));
                                    if (json.has("figureurl_qq_1")) {
                                        strProfile = json
                                                .getString("figureurl_qq_1");
                                    }

                                    if (json.has("gender")) {
                                        strGender = json.getString("gender");
                                    }
                                    if (strGender == null) {
                                        Toast.makeText(LoginActivity.this,
                                                "性别获取失败，请重试", Toast.LENGTH_LONG)
                                                .show();
                                        return;
                                    }
                                    if (json.has("openid")) {
                                        strOuid = json.getString("openid");
                                    }

                                    // strGender
//                                    LogionNode.Request(LoginActivity.this,
//                                            LoginActivity.this, LOGIONNODE,
//                                            strOuid, strType, strGender,
//                                            strOpenid, strProfile, strNickname);
                                    //QQ登陆成功

                                } catch (JSONException e) {

                                }
                                Message msg = new Message();
                                msg.obj = bitmap;
                                msg.what = 1;
                                mHandler.sendMessage(msg);
                            }
                        }

                    }.start();
                }

                @Override
                public void onCancel() {

                }
            };
            mInfo = new UserInfo(this, mTencent.getQQToken());
            mInfo.getUserInfo(listener);

        } else {
            // mUserInfo.setText("");
            // mUserInfo.setVisibility(android.view.View.GONE);
            // mUserLogo.setVisibility(android.view.View.GONE);
        }
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Util.showResultDialog(LoginActivity.this, "返回为空", "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Util.showResultDialog(LoginActivity.this, "返回为空", "登录失败");
                return;
            }
            // Util.showResultDialog(LoginActivity.this, response.toString(),
            // "登录成功");

            try {
                if (jsonResponse.has("openid")) {
                    strOpenid = jsonResponse.getString("openid");
                }
                if (jsonResponse.has("openid")) {
                    strOuid = jsonResponse.getString("openid");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            Util.toastMessage(LoginActivity.this, "onError: " + e.errorDetail);
            Util.dismissDialog();
        }

        @Override
        public void onCancel() {
            Util.toastMessage(LoginActivity.this, "登录取消");
            Util.dismissDialog();
            if (isServerSideLogin) {
                isServerSideLogin = false;
            }
        }
    }

    public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }

    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            ULog.d("SDKQQAgentPref",
                    "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
            initOpenidAndToken(values);
            updateUserInfo();
        }
    };

    //新浪登录

    void initSinaContorller() {

        // 创建微博实例


        mSsoHandler = new SsoHandler(LoginActivity.this);
        imgSinaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isWXLogin = false;
                strType = "4";
                mSsoHandler.authorize(new SelfWbAuthListener());
            }
        });
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()) {
            updateTokenView(true);
        }

    }

    private class SelfWbAuthListener implements WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            LoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAccessToken = token;
                    if (mAccessToken.isSessionValid()) {
                        // 显示 Token
                        updateTokenView(false);
                        // 保存 Token 到 SharedPreferences
                        AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);
//                        Toast.makeText(LoginActivity.this,
//                                "登陆成功token=" + token, Toast.LENGTH_SHORT).show();


                        String userUrl = "https://api.weibo.com/2/users/show.json?"
                                + "access_token=" + mAccessToken.getToken() + "&uid=" + mAccessToken.getUid();
//                            WXReturnNode.Request(LoginActivity.this, userUrl,
//                                    LoginActivity.this, WXLOGININFOtwo);

                        OkHttpUtil.getInstance().doAsyncGet(userUrl, new OkHttpCallback() {
                            @Override
                            public void onSuccess(String response) {
                                try {
                                    JSONObject MySecondJSONObject = new JSONObject(response);
                                    ULog.d("sina=", response);
//
//                                    Toast.makeText(LoginActivity.this,
//                                            response + strGender, Toast.LENGTH_SHORT)
//                                            .show();

                                    strOpenid = mAccessToken.getToken();
                                    strNickname = MySecondJSONObject.getString("name");
                                    strGender = MySecondJSONObject.getString("gender");
                                    if (strGender.equals("m")) {
                                        strGender = "男";
                                    } else if (strGender.equals("f")) {
                                        strGender = "女";
                                    } else {
                                        strGender = "未知";
                                    }
                                    strProfile = MySecondJSONObject.getString("profile_image_url");
                                    strOuid = mAccessToken.getUid();
                                    SPTool.putString(LoginActivity.this, Consts.SP_LOGINTYPE, "4");
                                    SPTool.putString(LoginActivity.this, Consts.SP_NICKNAME, strNickname);
                                    SPTool.putString(LoginActivity.this, Consts.SP_OUID, strOuid);
                                    SPTool.putString(LoginActivity.this, Consts.SP_OPTENID, strOpenid);
                                    mDownloadPicHelp.SaveImageview(LoginActivity.this, strProfile, LoginActivity.this);

                                    // Toast.makeText(LoginActivity.this, "结果为openid：" + strOpenid,
                                    // 5000).show();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(String errorMsg) {

                            }
                        });


                    }
                }
            });
        }

        @Override
        public void cancel() {
            Toast.makeText(LoginActivity.this,
                    "登录取消", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            Toast.makeText(LoginActivity.this, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void getUser() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * 显示当前 Token 信息。
     *
     * @param hasExisted 配置文件中是否已存在 token 信息并且合法
     */
    private void updateTokenView(boolean hasExisted) {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new Date(mAccessToken.getExpiresTime()));
        String format = "Token：%1$s \\n有效期：%2$s";
//        mTokenText.setText(String.format(format, mAccessToken.getToken(), date));


//        Toast.makeText(LoginActivity.this,
//                (String.format(format, mAccessToken.getToken(), date)), Toast.LENGTH_LONG).show();
        String message = String.format(format, mAccessToken.getToken(), date);
        if (hasExisted) {
            message = "Token 仍在有效期内，无需再次登录。" + "\n" + message;
        }
//        Toast.makeText(LoginActivity.this,
//                message, Toast.LENGTH_LONG).show();
    }


    @Override
    public void OnDownLoadFaild() {
        ThridLogin(strNickname, strOuid, strType, false);
    }

    @Override
    public void OnDownLoadSuccess(String strName) {
//        Toast.makeText(LoginActivity.this, strName,
//                Toast.LENGTH_LONG).show();
        //3是手机号登录2是微信登录1是新浪微博登录0是QQ登录
        Bitmap bitmap = null;
        File appDir = new File(LoginActivity.this.getExternalCacheDir(),
                Consts.HEAD_IMAGE_CACE);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        if (SPTool.getString(LoginActivity.this, Consts.SP_LOGINTYPE, "3").equals("3")) {


            File file = new File(appDir, Consts.HEAD_IMAGE_QQNAME + ".jpg");

            if (file.exists())

            {
//                bitmap = BitmapFactory.decodeFile(String.valueOf(file));
//                ((ImageView) findViewById(R.id.test_img)).setImageBitmap(bitmap);
            }
        } else if (SPTool.getString(LoginActivity.this, Consts.SP_LOGINTYPE, "4").equals("4")) {
            File file = new File(appDir, Consts.HEAD_IMAGE_SINANAME + ".jpg");

            if (file.exists())

            {
//                bitmap = BitmapFactory.decodeFile(String.valueOf(file));
//                ((ImageView) findViewById(R.id.test_img)).setImageBitmap(bitmap);
            }
        } else if (SPTool.getString(LoginActivity.this, Consts.SP_LOGINTYPE, "2").equals("2")) {
            File file = new File(appDir, Consts.HEAD_IMAGE_WXNAME + ".jpg");

            if (file.exists())

            {
//                bitmap = BitmapFactory.decodeFile(String.valueOf(file));
//                ((ImageView) findViewById(R.id.test_img)).setImageBitmap(bitmap);
            }
        }


//        public void ThridLogin(String strUrl, String strNickname, String strOutid, String strOutType)
        ThridLogin(strNickname, strOuid, strType, true);

    }


    public void ThridLogin(String strNickname, String strOutid, String strOutType, boolean isFile) {
        File file = null;
        Map<String, String> map = new HashMap<>();

      /*  用户登陆   http://lhzb.longhoo.net/index.php?m=api&c=Index&a=public_login
        参数：类型type，//1手机号码 2微信 3qq 4新浪微博
        type=1时，参数：phone，pwd
                type=2、3、4时，参数：ouid（第三方id号）nickname，headpic*/
//        try {
//            AES64 aes64 = new AES64();
//            strOutid = aes64.encrypt(strOutid);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        map.put("nickname", strNickname);
        map.put("ouid", strOutid);
        map.put("type", strOutType);
        map.put("token", Utils.getToken(LoginActivity.this));
        map.put("versioncode", Utils.getVersionName(LoginActivity.this));
        map.put("versionno", Utils.getLocalVersion(LoginActivity.this));
        map.put("sys", "1");
        if (isFile) {
            File appDir = new File(LoginActivity.this.getExternalCacheDir(),
                    Consts.HEAD_IMAGE_CACE);
            if (!appDir.exists()) {
                appDir.mkdir();
            }


            if (SPTool.getString(LoginActivity.this, Consts.SP_LOGINTYPE, "3").equals("3")) {


                file = new File(appDir, Consts.HEAD_IMAGE_QQNAME + ".jpg");

                if (file.exists())

                {

                } else {

                    isFile = false;
                }
            } else if (SPTool.getString(LoginActivity.this, Consts.SP_LOGINTYPE, "4").equals("4")) {
                file = new File(appDir, Consts.HEAD_IMAGE_SINANAME + ".jpg");

                if (file.exists())

                {

                } else {
                    isFile = false;

                }
            } else if (SPTool.getString(LoginActivity.this, Consts.SP_LOGINTYPE, "2").equals("2")) {
                file = new File(appDir, Consts.HEAD_IMAGE_WXNAME + ".jpg");

                if (file.exists())

                {

                } else {
                    isFile = false;
                }
            }


        }
        if (isFile) {
            OkHttpUtil.getInstance().doAsyncMultiUpload(Consts.BASE_URL + "c=Index&a=public_login", map, file, new OkHttpCallback() {
                @Override
                public void onSuccess(String response) {

                    try {
                        JSONObject obj = new JSONObject(response);


                        if (obj.has("code")) {
                            if (obj.getString("code").equals("0")) {
                                Gson mGson = new Gson();
                                LoginBean mLoginBean = mGson.fromJson(response, LoginBean.class);
                                MyApplication App = (MyApplication) getApplicationContext();
                                SPTool.putString(LoginActivity.this, Consts.SP_UID, mLoginBean.getData().getUid());
                                ULog.e("ck", "三方：" + mLoginBean.getData().getUid());
                                SPTool.putString(LoginActivity.this, Consts.SP_OUID, strOuid);
                                SPTool.putString(LoginActivity.this, Consts.SP_PHONE, mLoginBean.getData().getPhone());
                                SPTool.putString(LoginActivity.this, Consts.SP_NICKNAME, mLoginBean.getData().getNickname());
                                SPTool.putString(LoginActivity.this, Consts.SP_HEADRUL, mLoginBean.getData().getHeadpic());
                                SPTool.putString(LoginActivity.this, Consts.SP_UKEY, mLoginBean.getData().getUkey());
                                SPTool.putString(LoginActivity.this, Consts.SP_LOGINTYPE, strType);
                                SPTool.putString(LoginActivity.this, Consts.SP_HEADRUL, mLoginBean.getData().getHeadpic());
                                SPTool.putString(LoginActivity.this, Consts.SP_SHAREURL, mLoginBean.getData().getShareurl());
                                SPTool.putString(LoginActivity.this, Consts.SP_SHAREICON, mLoginBean.getData().getSharepic());
                                ULog.e("headurl1", mLoginBean.getData().getHeadpic());
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(String errorMsg) {

                }
            });
        } else {
            OkHttpUtil.getInstance().doAsyncPost(Consts.BASE_URL + "c=Index&a=public_login", map, new OkHttpCallback() {
                @Override
                public void onSuccess(String response) {

                    try {
                        JSONObject obj = new JSONObject(response);


                        if (obj.has("code")) {
                            if (obj.getString("code").equals("0")) {
                                Gson mGson = new Gson();
                                LoginBean mLoginBean = mGson.fromJson(response, LoginBean.class);
                                MyApplication App = (MyApplication) getApplicationContext();
                                SPTool.putString(LoginActivity.this, Consts.SP_UID, mLoginBean.getData().getUid());
                                ULog.e("ck", "三方：" + mLoginBean.getData().getUid());
                                SPTool.putString(LoginActivity.this, Consts.SP_OUID, strOuid);
                                SPTool.putString(LoginActivity.this, Consts.SP_PHONE, mLoginBean.getData().getPhone());
                                SPTool.putString(LoginActivity.this, Consts.SP_NICKNAME, mLoginBean.getData().getNickname());
                                SPTool.putString(LoginActivity.this, Consts.SP_HEADRUL, mLoginBean.getData().getHeadpic());
                                SPTool.putString(LoginActivity.this, Consts.SP_UKEY, mLoginBean.getData().getUkey());
                                SPTool.putString(LoginActivity.this, Consts.SP_LOGINTYPE, strType);
                                SPTool.putString(LoginActivity.this, Consts.SP_HEADRUL, mLoginBean.getData().getHeadpic());
                                SPTool.putString(LoginActivity.this, Consts.SP_SHAREURL, mLoginBean.getData().getShareurl());
                                SPTool.putString(LoginActivity.this, Consts.SP_SHAREICON, mLoginBean.getData().getSharepic());
                                ULog.e("headurl1", mLoginBean.getData().getHeadpic());
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(String errorMsg) {

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.REQUEST_LOGIN
                || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data,
                    loginListener);
        }

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

