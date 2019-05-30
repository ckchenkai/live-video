package com.zhibo.duanshipin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhibo.duanshipin.activity.GuideActivity;
import com.zhibo.duanshipin.activity.MainActivity;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends BaseActivity implements View.OnClickListener {
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};
    private static final int PERMISSION_REQUESTCODE = 0;
    private ImageView ivAdv;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏、全屏、透明虚拟按键
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        super.onCreate(savedInstanceState);
    }


    void PhoneLogin(String strPhone, String strPWD) {
        //////     /  判断用户是否已注册   http://lhzb.longhoo.net/index.php?m=api&c=Index&a=public_check
//
//        参数phone，用aes加密

        ULog.e("loginmessage=========", strPhone + "|||" + strPWD);
        Map<String, String> map = new HashMap<>();

        if (!Utils.isMobile(strPhone)) {

            return;
        }
        try {
            strPhone = new AES64().encrypt(strPhone);
        } catch (Exception e) {
            e.printStackTrace();

            return;
        }

        try {
            strPWD = new AES64().encrypt(strPWD);
        } catch (Exception e) {
            e.printStackTrace();

            return;
        }
        map.put("type", "1");
        map.put("phone", strPhone);
        map.put("pwd", strPWD);
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
                            SPTool.putString(SplashActivity.this, Consts.SP_UID, mLoginBean.getData().getUid());
                            ULog.e("ck", "三方：" + mLoginBean.getData().getUid());
                            SPTool.putString(SplashActivity.this, Consts.SP_PHONE, mLoginBean.getData().getPhone());
                            SPTool.putString(SplashActivity.this, Consts.SP_NICKNAME, mLoginBean.getData().getNickname());
                            SPTool.putString(SplashActivity.this, Consts.SP_HEADRUL, mLoginBean.getData().getHeadpic());
                            SPTool.putString(SplashActivity.this, Consts.SP_UKEY, mLoginBean.getData().getUkey());
                            SPTool.putString(SplashActivity.this, Consts.SP_LOGINTYPE, "1");
                            SPTool.putString(SplashActivity.this, Consts.SP_HEADRUL, mLoginBean.getData().getHeadpic());
                            SPTool.putString(SplashActivity.this, Consts.SP_SHAREURL, mLoginBean.getData().getShareurl());
                            SPTool.putString(SplashActivity.this, Consts.SP_SHAREICON, mLoginBean.getData().getSharepic());
                            ULog.e("headurl1", mLoginBean.getData().getHeadpic());
                            MyApplication App = (MyApplication) getApplicationContext();
                            Toast.makeText(SplashActivity.this, "登录成功", Toast.LENGTH_SHORT).show();


                        } else {

                        }
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtils.getInstance().showToast(SplashActivity.this, "网络错误~");

            }
        });


    }

    @Override
    protected int getContentId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews() {
        File file = new File(Consts.SD_ROOT);
//判断文件夹是否存在,如果如果存在则删除
        if (file.exists()) {
            Utils.delFolder(Consts.SD_ROOT);
        }
        File file1 = new File(Consts.SD_ROOT_OLD);
        if (file1.exists()) {
            Utils.delFolder(Consts.SD_ROOT_OLD);
        }
        //初始化view
        ivAdv = (ImageView) findViewById(R.id.iv_adv);
        ivAdv.setVisibility(View.VISIBLE);
        ivAdv.setBackgroundResource(R.drawable.splash);
        webView = (WebView) findViewById(R.id.agent_view);
        //检查权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions(permissions);
        } else {
            doRealAction();
        }


        String loginType = SPTool.getString(SplashActivity.this, Consts.SP_LOGINTYPE, "");
        String phone = SPTool.getString(SplashActivity.this, Consts.SP_PHONE, "");
        String pwd = SPTool.getString(SplashActivity.this, Consts.SP_PWD, "");
        String nickname = SPTool.getString(SplashActivity.this, Consts.SP_NICKNAME, "");
        String outId = SPTool.getString(SplashActivity.this, Consts.SP_OUID, "");
        ULog.e("outId", outId);
        if (TextUtils.equals(loginType, "2") || TextUtils.equals(loginType, "3") || TextUtils.equals(loginType, "4")) {
            ThridLogin(nickname, outId, loginType);
        } else if (TextUtils.equals(loginType, "1")) {
            PhoneLogin(phone, pwd);
        }
    }

    public void ThridLogin(String strNickname, String strOutid, String strOutType) {
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
        map.put("token", Utils.getToken(SplashActivity.this));
        map.put("versioncode", Utils.getVersionName(SplashActivity.this));
        map.put("versionno", Utils.getLocalVersion(SplashActivity.this));
        map.put("sys", "1");

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
                            SPTool.putString(SplashActivity.this, Consts.SP_UID, mLoginBean.getData().getUid());
                            ULog.e("ck", "三方：" + mLoginBean.getData().getUid());
                            SPTool.putString(SplashActivity.this, Consts.SP_PHONE, mLoginBean.getData().getPhone());
                            SPTool.putString(SplashActivity.this, Consts.SP_NICKNAME, mLoginBean.getData().getNickname());
                            SPTool.putString(SplashActivity.this, Consts.SP_HEADRUL, mLoginBean.getData().getHeadpic());
                            SPTool.putString(SplashActivity.this, Consts.SP_UKEY, mLoginBean.getData().getUkey());
                            SPTool.putString(SplashActivity.this, Consts.SP_HEADRUL, mLoginBean.getData().getHeadpic());
                            SPTool.putString(SplashActivity.this, Consts.SP_SHAREURL, mLoginBean.getData().getShareurl());
                            SPTool.putString(SplashActivity.this, Consts.SP_SHAREICON, mLoginBean.getData().getSharepic());
                            ULog.e("headurl1", mLoginBean.getData().getHeadpic());
                            Toast.makeText(SplashActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    } else {

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

    @Override
    protected void initToolbar() {

    }

    private void checkPermissions(String[] permissions) {
        List<String> needRequestPermissionsList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                needRequestPermissionsList.add(permission);
            }
        }
        if (needRequestPermissionsList != null && needRequestPermissionsList.size() > 0) {
            ActivityCompat.requestPermissions(this, needRequestPermissionsList.toArray(new String[needRequestPermissionsList.size()]), PERMISSION_REQUESTCODE);
        } else {
            //权限都有了
            doRealAction();
        }
    }

    /**
     * 检测是否已授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUESTCODE) {
            if (!verifyPermissions(grantResults)) {
                ToastUtils.getInstance().showToast(this, "请先授予部分权限！");
                finish();
            } else {
                //权限都有了
                doRealAction();
            }

        }
    }

    private void doRealAction() {
        ivAdv.setOnClickListener(this);
        //showAdvImg(ivAdv,"ll");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing())
                    return;
                //检查是否第一次进入应用
                boolean isFirst = SPTool.getBoolean(SplashActivity.this, Consts.IS_FIRST_IN_APP, true);
                if (isFirst) {
                    Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 1500);
    }

    private void showAdvImg(ImageView imageView, String path) {
        if (!TextUtils.isEmpty(path)) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.blank).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.blank).centerCrop();
            Glide.with(this).load(path)
                    .apply(options)
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(imageView);
        }
        imageView.setVisibility(View.VISIBLE);
        Animation animation = new AlphaAnimation(0, 1.0f);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        imageView.setAnimation(animation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_adv:

                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
}
