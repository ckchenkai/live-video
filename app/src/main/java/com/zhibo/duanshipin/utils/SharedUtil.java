package com.zhibo.duanshipin.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;


import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.open.t.Weibo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhibo.duanshipin.R;

import org.json.JSONObject;

import java.util.ArrayList;


public class SharedUtil implements WbShareCallback {


    public static String APP_KEY_WX = "wx9c2172e412b896e9";// 新的


    public static String WX_SECRET = "04c8d63d4b72cc9df46f65a0ae57f443";
    public static String APP_KEY_SINA = "2058435861";
    public static String APP_KEY_SINA_REDIRECT_URL = Consts.SINAREDIRECT_URL;
    public static final String APP_KEY_SINA_REDIRECT_SCOPE = Consts.SCOPE;

    public static String APP_KEY_TECENT = "1106395976";
    private static WbShareHandler shareHandler;

    static Activity mConext;
    public static IWXAPI wxApi;

    /**
     * 分享微信朋友圈
     */
    public static boolean SharePengYouQuan(String strUrl, String strTitle,
                                           Activity parent, Bitmap mBImg) {
        if (strUrl == null) {
            parent.finish();
            return false;
        }
        if (strTitle.length() == 0) {
            Toast.makeText(parent, "微信朋友圈分享内容为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strUrl.length() == 0) {
            Toast.makeText(parent, "微信朋友圈分享正文为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        IWXAPI WeixApi = WXAPIFactory.createWXAPI(parent, APP_KEY_WX, true);
        if (false == CheckWXVersion(WeixApi, parent)) {
            return false;
        }
        WeixApi.registerApp(APP_KEY_WX);
        WeixApi.handleIntent(parent.getIntent(), (IWXAPIEventHandler) parent);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = strUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = strTitle;
        msg.setThumbImage(mBImg);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        WeixApi.sendReq(req);
        mBImg.recycle();
        return true;
    }

    /**
     * 分享微信朋友
     */
    public static boolean SharePengYou(String strUrl, String strTitle,
                                       Activity parent, Bitmap mBImg) {
        if (strTitle == null) {
            parent.finish();
            return false;
        }
        if (strTitle.length() == 0) {
            Toast.makeText(parent, "微信朋友分享内容为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strUrl.length() == 0) {
            Toast.makeText(parent, "微信朋友分享正文为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        IWXAPI WeixApi = WXAPIFactory.createWXAPI(parent, APP_KEY_WX, true);
        if (false == CheckWXVersion(WeixApi, parent)) {
            return false;
        }
        WeixApi.registerApp(APP_KEY_WX);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = strUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        // msg.title = strTitle;
        msg.description = strTitle;

        msg.setThumbImage(mBImg);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        WeixApi.sendReq(req);
        if (mBImg != null && !mBImg.isRecycled()) {
            // 回收并且置为null
            mBImg.recycle();
            mBImg = null;
        }
        return true;
    }

    /**
     * 分享微信朋友圈
     */
    public static boolean SharePengYouQuan(String strUrl, String strTitle,
                                           Activity parent) {
        if (strUrl == null) {
            parent.finish();
            return false;
        }
        // if (strTitle.length() == 0) {
        // Toast.makeText(parent, "微信朋友圈分享内容为空", Toast.LENGTH_SHORT).show();
        // return false;
        // }

        // if (strUrl.length() == 0) {
        // Toast.makeText(parent, "微信朋友圈分享正文为空", Toast.LENGTH_SHORT).show();
        // return false;
        // }
        final int THUMB_SIZE = 150;
        IWXAPI WeixApi = WXAPIFactory.createWXAPI(parent, APP_KEY_WX, true);
        if (false == CheckWXVersion(WeixApi, parent)) {
            return false;
        }
        WeixApi.registerApp(APP_KEY_WX);
//        WeixApi.handleIntent(parent.getIntent(), (IWXAPIEventHandler) parent);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = strUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = strTitle;
        Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(),
                R.mipmap.ic_launcher);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE,
                THUMB_SIZE, true);
        bitmap.recycle();
        msg.setThumbImage(thumbBmp);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        WeixApi.sendReq(req);
        return true;
    }

    /**
     * 分享微信朋友
     */
    public static boolean SharePengYou(String strUrl, String strTitle, String strContent,
                                       Activity parent) {
        if (strTitle == null) {
            parent.finish();
            return false;
        }
        // if (strTitle.length() == 0) {
        // Toast.makeText(parent, "微信朋友分享内容为空", Toast.LENGTH_SHORT).show();
        // return false;
        // }

        // if (strUrl.length() == 0) {
        // Toast.makeText(parent, "微信朋友分享正文为空", Toast.LENGTH_SHORT).show();
        // return false;
        // }
        final int THUMB_SIZE = 150;
        IWXAPI WeixApi = WXAPIFactory.createWXAPI(parent, APP_KEY_WX, true);
        if (false == CheckWXVersion(WeixApi, parent)) {
            return false;
        }
        WeixApi.registerApp(APP_KEY_WX);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = strUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = strTitle;
        msg.description = strContent;
        Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(),
                R.mipmap.ic_launcher);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE,
                THUMB_SIZE, true);
        bitmap.recycle();
        msg.setThumbImage(thumbBmp);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        WeixApi.sendReq(req);
        return true;
    }

    static boolean CheckWXVersion(IWXAPI WeixApi, Activity parent) {
        int wxSdkVersion = WeixApi.getWXAppSupportAPI();
        if (wxSdkVersion >= 0x21020001) {
            return true;
        } else if (wxSdkVersion == 0) {
            Toast.makeText(parent, "没有安装微信，请先安装微信继续分享", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else {
            Toast.makeText(parent, "微信版本不支持分享", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private static TextObject getTextObj(String text) {
        TextObject textObject = new TextObject();
        textObject.text = text;
        return textObject;
    }

    /**
     * 分享短信
     */
    public static void ShareMessage(String strUrl, Activity parent) {
        if (strUrl == null) {
            parent.finish();
            return;
        }
        if (strUrl.length() == 0) {
            Toast.makeText(parent, "短信分享内容为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", strUrl);
        parent.startActivity(intent);
    }

    /**
     * 分享邮件
     */
    public static boolean ShareEmail(String strText, String strUrl,
                                     Activity parent) {
        if (strUrl == null) {
            parent.finish();
            return false;
        }
        if (strText.length() == 0) {
            Toast.makeText(parent, "邮件分享内容为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Intent data = new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse("mailto:qq10000@qq.com"));
            data.putExtra(Intent.EXTRA_SUBJECT, strText);
            data.putExtra(Intent.EXTRA_TEXT, strUrl);
            parent.startActivity(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * QQ分享
     */
    public static void ShareQQHaoYou(String strTitle, String strContent,
                                     String strUrl, String strImgUrl, Activity parent) {
        mConext = parent;
        if (strUrl == null) {
            parent.finish();
            return;
        }
        if (strTitle.length() == 0) {
            // Toast.makeText(parent, "QQ分享标题为空", Toast.LENGTH_SHORT).show();
            strTitle = "龙虎直播";
            // return;
        }

        // if (strContent.length() == 0) {
        // Toast.makeText(parent, "QQ分享正文为空", Toast.LENGTH_SHORT).show();
        // return;
        // }

        if (strUrl.length() == 0) {
            Toast.makeText(parent, "QQ分享跳转路径为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (strImgUrl.length() == 0) {
            Toast.makeText(parent, "QQ分享图片跳转路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mTencent = Tencent.createInstance(APP_KEY_TECENT, parent);
//        mTencent.login(parent, "all", qZoneShareListener);

//        mTencent.login(parent, "all", mBaseUiListener);

        ULog.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TITLE, strTitle);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, strUrl);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, strContent);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, strImgUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "龙虎直播");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, 1);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0);
        mTencent.shareToQQ(parent, params, qZoneShareListener);
    }

    /**
     * QQ空间分享
     */
    public static void ShareQQKongJian(String strTitle, String strContent,
                                       String strUrl, String strImgUrl, final Activity parent) {
        mConext = parent;

        if (strTitle.length() == 0) {
            // Toast.makeText(parent, "QQ空间分享标题为空", Toast.LENGTH_SHORT).show();
            // return;
            strTitle = "龙虎直播";
        }

        // if (strContent.length() == 0) {
        // Toast.makeText(parent, "QQ空间分享正文为空", Toast.LENGTH_SHORT).show();
        // return;
        // }

        if (strUrl.length() == 0) {
            Toast.makeText(parent, "QQ空间分享跳转路径为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (strImgUrl.length() == 0) {
            Toast.makeText(parent, "QQ空间分享图片跳转路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
//        final Tencent    myTencent = Tencent.createInstance(APP_KEY_TECENT, parent);
        mTencent = Tencent.createInstance(APP_KEY_TECENT, parent);
//        mTencent.login(parent, "all", qZoneShareListener);

        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, 1);
//        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, strTitle);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, strContent);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, strUrl);
        ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls.add(strImgUrl);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
//        ThreadManager.getMainHandler().post(new Runnable() {
//
//            @Override
//            public void run() {
//
//
////                mTencent.publishToQzone(parent, params, qZoneShareListener);
//
//            }
//        });
        mTencent.shareToQzone(parent, params, qZoneShareListener);
//        tencent.publishToQzone(parent, params, mBaseUiListener);
    }

    /**
     * QQ空间登陆
     */
    public static void QQLogin(Activity parent, IUiListener listerner) {
        Tencent tencent = Tencent.createInstance(APP_KEY_TECENT, parent);
        tencent.login(parent, "all", listerner);
    }

    /**
     * 腾讯微博
     */
    static Activity mParent;
    public static Tencent mTencent;
    String mstrContent;
    String mstrUrlSrc;

    public void ShareQQWeiBo(String strContent, String strUrl, Activity parent) {
        if (strUrl == null) {
            parent.finish();
            return;
        }
        // if (strContent.length() == 0) {
        // Toast.makeText(parent, "腾讯微博分享正文为空", Toast.LENGTH_SHORT).show();
        // return;
        // }
        if (strUrl.length() == 0) {
            Toast.makeText(parent, "腾讯微博分享跳转路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mParent = parent;
        mstrContent = strContent;
        mstrUrlSrc = strUrl;
        mTencent = Tencent.createInstance(APP_KEY_TECENT, parent);
        mTencent.login(parent, "all", loginListener);
    }

    /**
     * 腾讯微博带图片分享
     */
    String mstrPic; // 这个是本地图片

    public void ShareQQWeiBo(String strContent, String strPic, String strUrl,
                             Activity parent) {
        if (strUrl == null) {
            parent.finish();
            return;
        }
        if (mstrPic.length() == 0) {
            Toast.makeText(parent, "腾讯微博分享正文为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (strUrl.length() == 0) {
            Toast.makeText(parent, "腾讯微博分享跳转路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mstrPic = strPic;

        // if (strContent.length() == 0) {
        // Toast.makeText(parent, "腾讯微博分享正文为空", Toast.LENGTH_SHORT).show();
        // return;
        // }
        ShareQQWeiBo(strContent, strUrl, parent);
    }

    // QQ微博登陆监听
    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            ULog.d("SDKQQAgentPref",
                    "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
            initOpenidAndToken(values);
        }
    };

    @Override
    public void onWbShareSuccess() {
        Toast.makeText(mParent, "分享成功", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onWbShareCancel() {
        Toast.makeText(mParent, "分享取消", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onWbShareFail() {
        Toast.makeText(mParent, "分享失败", Toast.LENGTH_SHORT)
                .show();
    }


    public class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Toast.makeText(mParent, "返回为空, 登录失败", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Toast.makeText(mParent, "返回为空, 登录失败", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            // Toast.makeText(mParent, "分享成功", Toast.LENGTH_SHORT).show();
            // 有奖分享处理
            // handlePrizeShare();
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            // Toast.makeText(mParent, "onError:" + e.errorDetail,
            // Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancel() {
            // Toast.makeText(mParent, "onCancel: ", Toast.LENGTH_SHORT).show();

        }
    }

    public final static String PARAM_ACCESS_TOKEN = "access_token";
    public final static String PARAM_EXPIRES_IN = "expires_in";
    public final static String PARAM_OPEN_ID = "openid";

    public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String strToken = jsonObject.getString(PARAM_ACCESS_TOKEN);
            String strExpires = jsonObject.getString(PARAM_EXPIRES_IN);
            String strOpenID = jsonObject.getString(PARAM_OPEN_ID);
            if (strToken.length() > 0 && strExpires.length() > 0
                    && strOpenID.length() > 0) {
                mTencent.setAccessToken(strToken, strExpires);
                mTencent.setOpenId(strOpenID);
                Weibo weibo = new Weibo(mParent, mTencent.getQQToken());
                if ((null == mstrPic) || (mstrPic.length() == 0)) {
                    weibo.sendText(mstrContent + mstrUrlSrc,
                            (IUiListener) mParent);
                } else {
                    weibo.sendPicText(mstrContent + mstrUrlSrc, mstrPic,
                            (IUiListener) mParent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void shareWB(String strTitle, String strContent,
                               String strUrl, Activity parent) {


        shareHandler = new WbShareHandler(parent);
        shareHandler.registerApp();


        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        TextObject textObject = new TextObject();
        textObject.text = strContent;
        textObject.title = strTitle;
        textObject.actionUrl = strUrl;
        weiboMessage.textObject = textObject;

        ImageObject imageObject = new ImageObject();
        Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(), R.mipmap.ic_launcher);
        imageObject.setImageObject(bitmap);
        weiboMessage.imageObject = imageObject;
//        weiboMessage.

        weiboMessage.mediaObject = getWebpageObj(parent, strTitle, strContent, strUrl);

        if (Utils.isInstalled(parent, "com.sina.weibo")) {
            shareHandler.shareMessage(weiboMessage, true);
        } else {
            shareHandler.shareMessage(weiboMessage, false);
        }

    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private static WebpageObject getWebpageObj(Activity mParent, String strTitle, String strContent, String strUrl) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utils.generateGUID();
        mediaObject.title = strTitle;
        mediaObject.description = strContent;

        Bitmap bitmap = BitmapFactory.decodeResource(mParent.getResources(), R.mipmap.ic_launcher);
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = strUrl;

        mediaObject.defaultText = "龙虎直播 默认文案";
        return mediaObject;
    }

    public static IUiListener qZoneShareListener = new IUiListener() {

        @Override
        public void onCancel() {
//            Toast.makeText(mConext, "取消分享", Toast.LENGTH_SHORT)
//                    .show();
        }

        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
//            Toast.makeText(mConext, "分享失败", Toast.LENGTH_SHORT)
//                    .show();
        }

        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
//            Toast.makeText(mConext, "分享成功", Toast.LENGTH_SHORT)
//                    .show();
        }

    };
}
