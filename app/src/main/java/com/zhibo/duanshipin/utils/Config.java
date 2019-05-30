package com.zhibo.duanshipin.utils;

import android.os.Environment;

public class Config {
    public static String TOKEN = "";
    public static final String ak = "UzhoY56RXwqdlkF9CUTERmxAPyAZxiSzWdHGGBKV";
    public static final String sk = "pujH97sPu9Jhqr6MONL6O4rJC-RsneYZ0TJ2s2Dv";
    //    public static final String DOMAIN = "shortvideo.pdex-service.com";
    public static final String DOMAIN = "vod.025nj.com";
    public static final String VIDEO_STORAGE_DIR = Environment.getExternalStorageDirectory() + "/ShortVideo/";
    public static final String DUB_FILE_PATH = VIDEO_STORAGE_DIR + "dub.mp4";
    public static final String AUDIO_RECORD_FILE_PATH = VIDEO_STORAGE_DIR + "audio_record.m4a";
    public static final String EDITED_FILE_PATH = VIDEO_STORAGE_DIR + "edited.mp4";
    public static final String TRANSCODE_FILE_PATH = VIDEO_STORAGE_DIR + "transcoded.mp4";
    public static final String TRANSCODE_FILE_NOTE = VIDEO_STORAGE_DIR + "filenote";




}