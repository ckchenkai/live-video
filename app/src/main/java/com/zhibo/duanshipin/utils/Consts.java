package com.zhibo.duanshipin.utils;

/**
 * Created by CK on 2017/7/6.
 */

public class Consts {
    public static final String BASE_URL = "http://lhzb.longhoo.net/index.php?m=api&";
    public static final String TAG = "ck";
    public static final int REQUEST_REFRESH = 0;
    public static final int REQUEST_LOADMORE = 1;
    public static final int REQUEST_METHOD_GET = 100;
    public static final int REQUEST_METHOD_POST = 101;
    public static final String IS_FIRST_IN_APP = "is_first_in_app";
    public static final String SD_ROOT = Utils.getRoot("/longhuduanshipin/");
    public static final String SD_ROOT_OLD = Utils.getRoot("/longhuduanshipin/");
    // 第三方平台类型  //1手机号码 2微信 3qq 4新浪微博
    public static String SP_LOGINTYPE = "sp_login_type";
    public static String SP_NICKNAME = "sp_nickname";
    public static String SP_PWD = "sp_pwd";
    public static String SP_PHONE = "sp_phone";
    public static String SP_UID = "sp_uid";
    public static String SP_OUID = "sp_ouid";
    public static String SP_UKEY = "sp_ukey";
    public static String SP_OPTENID = "sp_opid";
    public static String SP_HEADRUL = "sp_headurl";
    public static String SP_SHAREURL = "sp_share_url";
    public static String SP_SHAREICON = "sp_share_icon";

    public static final String HEAD_IMAGE_CACE = "headimageviewcace";
    public static final String HEAD_IMAGE_QQNAME = "headimageqqcace";
    public static final String HEAD_IMAGE_SINANAME = "headimagesinacace";
    public static final String HEAD_IMAGE_WXNAME = "headimageweixincace";
    public static final String TEMP = "temp";
    public static final String SD_TEMPAPK = SD_ROOT + "temp.apk";

    /**
     * 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY
     */

    public static final String SinaAPP_KEY = "2058435861";
    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * <p>
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响， 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String SINAREDIRECT_URL = "http://open.weibo.com";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利 选择赋予应用的功能。
     * <p>
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的 使用权限，高级权限需要进行申请。
     * <p>
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * <p>
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
//    public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
//            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
//            + "follow_app_official_microblog," + "invitation_write";
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

//           SPTool.putString(LoginActivity.this, Consts.SP_LOGINTYPE, "2");
//                                        SPTool.putString(LoginActivity.this, Consts.SP_NICKNAME, strNickname);
//                                        SPTool.putString(LoginActivity.this, Consts.SP_UID, strOuid);
//                                        SPTool.putString(LoginActivity.this, Consts.SP_ATOKEN, strOpenid);
}
