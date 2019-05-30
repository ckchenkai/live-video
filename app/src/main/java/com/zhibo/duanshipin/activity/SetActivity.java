package com.zhibo.duanshipin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.tools.PictureFileUtils;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.base.BaseActivity;
import com.zhibo.duanshipin.utils.CacheUtil;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.StatusBarUtil;
import com.zhibo.duanshipin.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

public class SetActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rl_phone_panel)
    RelativeLayout rlPhonePanel;
    @BindView(R.id.rl_feedback_panel)
    RelativeLayout rlFeedbackPanel;
    @BindView(R.id.rl_about_panel)
    RelativeLayout rlAboutPanel;
    @BindView(R.id.rl_cache_panel)
    RelativeLayout rlCachePanel;
    @BindView(R.id.rl_userinfo_panel)
    RelativeLayout rlUserInfoPanel;
    @BindView(R.id.rl_pwd_panel)
    RelativeLayout rlPwdPanel;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.tv_cache)
    TextView tvCache;

    @Override
    protected int getContentId() {
        return R.layout.activity_set;
    }

    @Override
    protected void initViews() {
        StatusBarUtil.setStatusBarColor(this,R.color.set_bg_clolr);
        StatusBarUtil.StatusBarDarkMode(this);
        tvCache.setText(CacheUtil.getInstance().getAllCacheSize(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        String loginType = SPTool.getString(this, Consts.SP_LOGINTYPE, "");
        if (TextUtils.equals(loginType, "1")) {
            rlPhonePanel.setVisibility(View.GONE);
        }
        if (Utils.isLogin(this)) {
            tvLogin.setText("登出");
        } else {
            tvLogin.setText("登录");
        }
    }

    @Override
    protected void initToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setBackgroundColor(getResources().getColor(R.color.set_bg_clolr));
    }


    void BindPhone() {

    }
    private Handler mHandler = new Handler();


    private void launcherTheRocket() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                final View rocket = findViewById(R.id.rocket);
                Animation rocketAnimation = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.rocket);
                rocketAnimation
                        .setAnimationListener(new VisibilityAnimationListener(
                                rocket));
                rocket.startAnimation(rocketAnimation);

                final View cloud = findViewById(R.id.cloud);
                Animation cloudAnimation = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.cloud);
                cloudAnimation
                        .setAnimationListener(new VisibilityAnimationListener(
                                cloud));
                cloud.startAnimation(cloudAnimation);

                final View launcher = findViewById(R.id.launcher);
                Animation launcherAnimation = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.launcher);
                launcherAnimation
                        .setAnimationListener(new VisibilityAnimationListener(
                                launcher));
                launcher.startAnimation(launcherAnimation);

            }
        }, 150);

    }

    public class VisibilityAnimationListener implements Animation.AnimationListener {

        private View mVisibilityView;

        public VisibilityAnimationListener(View view) {
            mVisibilityView = view;
        }

        public void setVisibilityView(View view) {
            mVisibilityView = view;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            if (mVisibilityView != null) {
                mVisibilityView.setVisibility(View.VISIBLE);
                // mVisibilityView.setVisibility(View.GONE);
            }


        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mVisibilityView != null) {
                mVisibilityView.setVisibility(View.GONE);
                PictureFileUtils.deleteCacheDirFile(SetActivity.this);
                CacheUtil.getInstance().clearAllCache(SetActivity.this);
                tvCache.setText("0M");
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }


    @OnClick({R.id.rl_phone_panel, R.id.rl_feedback_panel, R.id.rl_about_panel, R.id.rl_cache_panel,R.id.rl_userinfo_panel, R.id.tv_login,R.id.rl_pwd_panel})

    void onClick(View view) {
        switch (view.getId()) {

            case R.id.rl_phone_panel:

//                绑定账号   http://lhzb.longhoo.net/index.php?m=api&c=Index&a=binding
//                参数：uid（登录的用户id）,phone
                if (Utils.isLogin(SetActivity.this)) {
                    String loginType = SPTool.getString(SetActivity.this, Consts.SP_LOGINTYPE, "");
                    if (loginType.equals("1")) {
                        Toast.makeText(this, "您已经是手机号码登录了，无需绑定手机号", Toast.LENGTH_SHORT).show();
                    } else {
                        if (SPTool.getString(SetActivity.this, Consts.SP_PHONE, "").isEmpty()) {
                            Intent intent = new Intent();
                            intent.setClass(SetActivity.this, BindPhoneActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "您已绑定过手机号了", Toast.LENGTH_SHORT).show();
                        }

                    }

                    return;
                } else {

                }
                break;
            case R.id.rl_feedback_panel:
                Intent feedbackIntent = null;
                if(Utils.isLogin(this)){
                    feedbackIntent = new Intent(this,FeedbackActivity.class);
                }else{
                    feedbackIntent = new Intent(this,LoginActivity.class);
                }
                startActivity(feedbackIntent);
                break;
            case R.id.rl_about_panel:
                WebViewActivity.goToWebView(this,Consts.BASE_URL+"c=Index&a=public_about","关于我们",false);
                break;
            case R.id.rl_cache_panel:
//                launcherTheRocket();
                PictureFileUtils.deleteCacheDirFile(SetActivity.this);
                CacheUtil.getInstance().clearAllCache(SetActivity.this);
                tvCache.setText("0M");
                break;
            case R.id.rl_userinfo_panel:
                Intent infoIntent = null;
                if(Utils.isLogin(this)){
                    infoIntent = new Intent(this,UserInfoActivity.class);
                }else{
                    infoIntent = new Intent(this,LoginActivity.class);
                }
                startActivity(infoIntent);
                break;
            case R.id.tv_login:
                if (TextUtils.equals(tvLogin.getText().toString().trim(), "登录")) {
                    Intent intent = new Intent(SetActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    Activity activity = SetActivity.this;
                    SPTool.remove(activity, Consts.SP_UID);
                    SPTool.remove(activity, Consts.SP_LOGINTYPE);
                    SPTool.remove(activity, Consts.SP_NICKNAME);
                    SPTool.remove(activity, Consts.SP_PWD);
                    SPTool.remove(activity, Consts.SP_PHONE);
                    SPTool.remove(activity, Consts.SP_OUID);
                    SPTool.remove(activity, Consts.SP_UKEY);
                    SPTool.remove(activity, Consts.SP_OPTENID);
                    SPTool.remove(activity, Consts.SP_HEADRUL);
                    SPTool.remove(activity, Consts.SP_SHAREURL);
                    SPTool.remove(activity, Consts.SP_SHAREICON);
                    tvLogin.setText("登录");
                }
                break;
            case R.id.rl_pwd_panel:
                Intent modifyIntent = null;
                if(Utils.isLogin(this)){
                    modifyIntent = new Intent(this,ModifyPwdActivity.class);
                }else{
                    modifyIntent = new Intent(this,LoginActivity.class);
                }
                startActivity(modifyIntent);
                break;
        }
    }
}
