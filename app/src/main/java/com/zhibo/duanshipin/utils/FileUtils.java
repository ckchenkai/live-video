package com.zhibo.duanshipin.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/5.
 */

public class FileUtils {

    /**
     * 创建缓存文件
     * @param context
     * @param dir
     * @param fileName
     * @return
     */
    public static String createCacheFile(Context context,String dir,String fileName){
        File cachDir = new File(context.getExternalCacheDir(), dir);
        if (!cachDir.exists())
            cachDir.mkdir();
        File file = new File(cachDir,fileName);
        return file.getAbsolutePath();
    }
    /**
     * 存储list对象数据
     * @param context
     * @param tArrayList
     * @param fileName
     */
    public static void saveList(Context context, ArrayList tArrayList, String fileName) {

        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileName);  //新建一个内容为空的文件
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(tArrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (objectOutputStream != null) {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取list对象数据
     * @param fileName
     * @return
     */
    public static ArrayList<?> readList(String fileName) {
        ObjectInputStream objectInputStream = null;
        FileInputStream fileInputStream = null;
        ArrayList<?> savedArrayList = new ArrayList<>();
        try {
            fileInputStream = new FileInputStream(fileName);
            objectInputStream = new ObjectInputStream(fileInputStream);
            savedArrayList = (ArrayList<?>) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedArrayList;
    }

    /**
     * 删除指定路径下文件
     * @param filepath
     */
    public static boolean deleteFile(String filepath){
        File file = new File(filepath);
        if(file.isFile()&&file.exists()){
            return file.delete();
        }
        return false;
    }

}
