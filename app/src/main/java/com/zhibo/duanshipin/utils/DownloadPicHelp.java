package com.zhibo.duanshipin.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadPicHelp {
    Bitmap bitmap;
    String myimageurl = "";
    Context mContext;
    public MultiFileFormSubmitListener mMultiFileFormSubmitListener;

    public void SaveImageview(Context context, String strimageurl, MultiFileFormSubmitListener mYMultiFileFormSubmitListener) {

        mContext = context;
        myimageurl = strimageurl;
        mMultiFileFormSubmitListener = mYMultiFileFormSubmitListener;
        new Task().execute(myimageurl);

    }


    /**
     * 获取网络图片
     *
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap GetImageInputStream(String imageurl) {
        imageurl = myimageurl;
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imageurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); // 超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); // 设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public interface DownLoadPicSucess {
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                SavaImage(bitmap);

            }
        }

        ;
    };

    /**
     * 异步线程下载图片
     */
    class Task extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            bitmap = GetImageInputStream((String) params[0]);

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Message message = new Message();
            message.what = 0x123;
            handler.sendMessage(message);
        }

    }

    /**
     * 保存位图到本地
     *
     * @param bitmap 本地路径
     * @return void
     */
    public void SavaImage(Bitmap bitmap) {
        if (bitmap == null) {
//            Toast.makeText(mContext.getApplicationContext(), "服务器图片异常！" + myimageurl,
//                    Toast.LENGTH_LONG).show();
            mMultiFileFormSubmitListener.OnDownLoadFaild();
            return;
        }
        // 首先保存图片

        File appDir = new File(mContext.getExternalCacheDir(),
                Consts.HEAD_IMAGE_CACE);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //3是手机号登录2是微信登录1是新浪微博登录0是QQ登录
        String strLoginType = SPTool.getString(mContext, Consts.SP_LOGINTYPE, "6");
        String fileName = "";
        if (strLoginType.equals("2")) {
            fileName = Consts.HEAD_IMAGE_WXNAME + ".jpg";
        } else if (strLoginType.equals("4")) {
            fileName = Consts.HEAD_IMAGE_SINANAME + ".jpg";
        } else {
            fileName = Consts.HEAD_IMAGE_QQNAME + ".jpg";
        }

        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        // 最后通知图库更新
//        mContext.sendBroadcast(new Intent(
//                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
//                + file.getAbsolutePath())));

        mMultiFileFormSubmitListener.OnDownLoadSuccess("图片保存成功！");


    }

    public interface MultiFileFormSubmitListener {

        void OnDownLoadFaild();

        void OnDownLoadSuccess(String strName);


    }


}
