package com.zhibo.duanshipin.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhibo.duanshipin.MyApplication;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/12/12.
 */

public class Utils {
    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][0-9][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 显示隐藏软键盘
     *
     * @param context
     * @param view
     * @param isShow
     */
    public static void showHideSoftInput(Context context, View view, boolean isShow) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
        }
    }

    /**
     * 获取context宽高
     */
    public static Point getDeviceSize(Context ctx) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        Point size = new Point();
        size.x = dm.widthPixels;
        size.y = dm.heightPixels;
        return size;
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String version = "0.0";
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (Exception e) {
        }
        return version;
    }

    /**
     * 判断sd卡是否存在
     *
     * @return true:存在；false：不存在
     */
    public static boolean isSdcardExisting() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 根据时间戳获取格式时间
     *
     * @param str
     * @return
     */
    public static String getDataTime(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        String dataTime = str;
        //时间戳转化为Sting或Date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Long time = Long.parseLong(str)*1000;
        dataTime = format.format(time);
        return dataTime;
    }

    /**
     * 根据时间戳获取格式时间
     *
     * @param str
     * @return
     */
    public static String getDataTimeWithMinute(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        String dataTime = str;
        //时间戳转化为Sting或Date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd   HH:mm");
        Long time = Long.parseLong(str)*1000;
        dataTime = format.format(time);
        return dataTime;
    }

    /**
     * 将纯数组解析成列表
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);
        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String stringForTime(int timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /*
   * 将时间转换为时间戳
   */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }


    /**
     * 处理URL (防止被360wifi劫持)
     * 从第三方WIFI的URL中提取实际的URL
     * 处理360wifi这样自动显示广告页面的情况（本应用的的URL中不会存在WIFI字样，而第三方WIFI广告一般都会包含WIFI关键字，拦截URL后，截取其中的URL参数就是我们真正要访问的地址）
     *
     * @return
     * @author SHANHY
     * @date 2015-8-8
     */
    public static String processUrl(String url) {
        if (url.contains("wifi") && url.contains("url=")) {
            int urlstartIndex = url.indexOf("url=") + 4;
            url = url.substring(urlstartIndex);
            int urlendIndex = url.length();
            if (url.contains("&")) {
                urlendIndex = url.indexOf("&");
            }
            url = url.substring(0, urlendIndex);
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
        }
        return url;
    }
    // 删除文件夹
    // param folderPath 文件夹完整绝对路径

    public static void delFolder(String strFolder) {
        try {
            delAllFile(strFolder); // 删除完里面所有内容
            String strFilePath = strFolder;
            File FilePath = new File(strFilePath);
            FilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 删除指定文件夹下所有文件
    // param path 文件夹完整绝对路径
    public static boolean delAllFile(String strPath) {
        boolean bFlag = false;
        File file = new File(strPath);
        if (!file.exists()) {
            return bFlag;
        }
        if (!file.isDirectory()) {
            return bFlag;
        }
        String[] strTempList = file.list();
        File temp = null;
        for (int i = 0; i < strTempList.length; i++) {
            if (strPath.endsWith(File.separator)) {
                temp = new File(strPath + strTempList[i]);
            } else {
                temp = new File(strPath + File.separator + strTempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(strPath + "/" + strTempList[i]);// 先删除文件夹里面的文件
                delFolder(strPath + "/" + strTempList[i]);// 再删除空文件夹
                bFlag = true;
            }
        }
        return bFlag;
    }
    public static String generateGUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    public static void creatTempFolder() {
        try {
            File from = new File(Consts.SD_ROOT);
            if (from.exists() && from.isDirectory()) {
                File to = new File(Consts.SD_ROOT_OLD);
                from.renameTo(to);
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }
    /**
     * 应用程序是否已安装
     * @param context
     * @param packageName 应用程序的包名
     * @return
     */
    public static boolean isInstalled(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    /**
     * 获取文件路径
     *
     * @param strRoot
     * @return
     */
    public static String getRoot(String strRoot) {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        } else {
            sdDir = Environment.getRootDirectory();
        }
        String strDir = sdDir.toString() + "/" + strRoot;
        return strDir;
    }
    public static String getLocalVersion(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
            ULog.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return String.valueOf(localVersion);
    }

    /**
     * 获取手机串号
     */
    public static String getToken(Context mContext) {


        TelephonyManager telephonemanage = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);

        try {
            return telephonemanage.getDeviceId();
            // Toast.makeText(this, "MIEI码为：" +mstrtoken,
            // 1000).show();

        } catch (Exception e) {

            ULog.i("error", e.getMessage());
            return "errortoken";
        }

    }
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    public static Boolean isLogin(Context mContext) {
        MyApplication App = (MyApplication) mContext.getApplicationContext();

        String strUid = SPTool.getString(mContext, Consts.SP_UID, "");

        if (strUid.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
