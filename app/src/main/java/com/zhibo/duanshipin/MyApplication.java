package com.zhibo.duanshipin;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;


import com.bugtags.library.Bugtags;
import com.facebook.stetho.Stetho;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.zhibo.duanshipin.httprequest.OkHttpUtil;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.ULog;

import java.util.LinkedList;
import java.util.List;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by CK on 2017/7/7.
 */

public class MyApplication extends MultiDexApplication {

    public List<Activity> mActivityList = new LinkedList<Activity>();

    /**
     * 腾讯的ID
     */

    public static final String APPtencent_ID = "1104396284";
    /**
     * 腾讯的key
     */
    public static final String WX_APPtencent_ID = "IR8jmh5dKYyKYLQu";
    /**
     * 微信的id
     */
    public static final String APP_WXID = "wxd32b2fa5ddccd0e6";
    /**
     * 微信开放平台申请到的app_id对应的app_secret
     */

    public static final String APP_WXSECRET = "51a6d8839d7c6f333ee0b443b8c6271a";
    /**
     * 微信用于请求用户信息的作用域
     */
    public static final String WEIXIN_SCOPE = "snsapi_userinfo";
    /**
     * 自定义
     */
    public static final String WEIXIN_STATE = "login_state";
    public static IWXAPI api;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        WbSdk.install(this,new AuthInfo(this, Consts.SinaAPP_KEY, Consts.SINAREDIRECT_URL, Consts.SCOPE));
//        api = WXAPIFactory.createWXAPI(this, LoginActivity.WX_APP_ID.trim(), true);
//        api.registerApp(LoginActivity.WX_APP_ID.trim());
        //禁止默认的页面统计方式，这样将不会再自动统计Activity。
        MobclickAgent.openActivityDurationTrack(false);
        OkHttpUtil.getInstance().init(getApplicationContext());

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                ULog.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
//        LeakCanary.install(this);
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);
        ImageLoader.getInstance().init(configuration);
        //极光推送
        JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        //bugtags
        //在这里初始化
        Bugtags.start("2aeca06e46238e3f8ab93ac0c00aeb76", this, Bugtags.BTGInvocationEventNone);

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    public void RemoveActivity(String strActivityName) {
        if (mActivityList.size() == 0) {

            return;
        }
        for (int i = 0; i < mActivityList.size(); ++i) {
            Activity activity = mActivityList.get(i);
            if (activity.getComponentName().getClassName()
                    .equals(strActivityName)) {
                mActivityList.remove(i);
                activity.finish();
            }
        }
    }

    public void RemoveAllActivity() {
        if (mActivityList.size() == 0) {

            return;
        }
        for (int i = 0; i < mActivityList.size(); ++i) {
            Activity activity = mActivityList.get(i);
            activity.finish();
        }
    }

    public String PrintActivityName() {
        if (mActivityList.size() == 0) {

            return "";
        }
        String strActivityName = "";
        for (int i = 0; i < mActivityList.size(); ++i) {
            Activity activity = mActivityList.get(i);
            strActivityName += String.format("%s\n", activity
                    .getComponentName().getClassName());
        }
        return strActivityName;
    }
}
