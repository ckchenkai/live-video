package com.zhibo.duanshipin.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;


import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.qiniu.pili.droid.shortvideo.PLShortVideoUploader;
import com.qiniu.pili.droid.shortvideo.PLUploadProgressListener;
import com.qiniu.pili.droid.shortvideo.PLUploadResultListener;
import com.qiniu.pili.droid.shortvideo.PLUploadSetting;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.httprequest.OkHttpCallback;
import com.zhibo.duanshipin.httprequest.OkHttpUtil;
import com.zhibo.duanshipin.utils.AES64;
import com.zhibo.duanshipin.utils.Config;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;
import com.zhibo.duanshipin.utils.Utils;
import com.zhibo.duanshipin.view.MediaController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PlaybackActivity extends Activity implements
        PLUploadResultListener,
        PLUploadProgressListener {

    private static final String TAG = "PlaybackActivity";
    private static final String MP4_PATH = "MP4_PATH";

    private PLVideoTextureView mVideoView;
    private Button mUploadBtn;
    private PLShortVideoUploader mVideoUploadManager;
    private ProgressBar mProgressBarDeterminate;
    private boolean mIsUpload = false;
    private String mVideoPath;

    public static void start(Activity activity, String mp4Path) {
        Intent intent = new Intent(activity, PlaybackActivity.class);
        intent.putExtra(MP4_PATH, mp4Path);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_playback);

        PLUploadSetting uploadSetting = new PLUploadSetting();

        mVideoUploadManager = new PLShortVideoUploader(getApplicationContext(), uploadSetting);
        mVideoUploadManager.setUploadProgressListener(this);
        mVideoUploadManager.setUploadResultListener(this);

        mUploadBtn = (Button) findViewById(R.id.upload_btn);
        mUploadBtn.setText(R.string.upload);
        mUploadBtn.setOnClickListener(new UploadOnClickListener());
        mProgressBarDeterminate = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBarDeterminate.setMax(100);
        mVideoView = (PLVideoTextureView) findViewById(R.id.video);
        mVideoPath = getIntent().getStringExtra(MP4_PATH);
        mVideoView.setLooping(true);
        mVideoView.setAVOptions(new AVOptions());
        mVideoView.setVideoPath(mVideoPath);
        MediaController mediaController = new MediaController(this, true, false);
        mediaController.setOnClickSpeedAdjustListener(mOnClickSpeedAdjustListener);
        mVideoView.setMediaController(mediaController);

        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        mVideoView.setOnVideoFrameListener(mOnVideoFrameListener);
        mVideoView.setOnAudioFrameListener(mOnAudioFrameListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

    public class UploadOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!mIsUpload) {
                mVideoUploadManager.startUpload(mVideoPath, Config.TOKEN);
                mProgressBarDeterminate.setVisibility(View.VISIBLE);
                mUploadBtn.setText(R.string.cancel_upload);
                mIsUpload = true;
            } else {
                mVideoUploadManager.cancelUpload();
                mProgressBarDeterminate.setVisibility(View.INVISIBLE);
                mUploadBtn.setText(R.string.upload);
                mIsUpload = false;
            }
        }
    }

    @Override
    public void onUploadProgress(String fileName, double percent) {
        mProgressBarDeterminate.setProgress((int) (percent * 100));
        if (1.0 == percent) {
            mProgressBarDeterminate.setVisibility(View.INVISIBLE);
        }
    }

    public void copyToClipboard(String filePath) {
        ClipData clipData = ClipData.newPlainText("VideoFilePath", filePath);
        ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clipData);
    }

    /**
     * 上传视频信息
     */
    private void doRequestUpLoadVideoCenntent(String strUrl, String strContent) {
        String url = Consts.BASE_URL + "c=Index&a=add_video_record";

//        uid，ukey，url，content

        Map<String, String> params = new HashMap<>();

        params.put("uid", SPTool.getString(this, Consts.SP_UID, ""));
        params.put("ukey", SPTool.getString(this, Consts.SP_UKEY, ""));
        params.put("url", strUrl);
        params.put("content", strContent);
        OkHttpUtil.getInstance().doAsyncPost(url, params, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {
                ULog.e("cc", "register:code:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ToastUtils.getInstance().showToast(PlaybackActivity.this, jsonObject.optString("msg"));
                    if (jsonObject.has("code")) {

                        if (jsonObject.getString("code").equals("0")) {
                            Intent intent = new Intent();
                            intent.setClass(PlaybackActivity.this, UploadRecordActivity.class);
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToast(PlaybackActivity.this, "服务器异常~");

                }
            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtils.getInstance().showToast(PlaybackActivity.this, "网络错误~");

            }
        });
    }

    @Override
    public void onUploadVideoSuccess(JSONObject response) {
        try {
            String filePath = "http://" + Config.DOMAIN + "/" + response.getString("key");
            copyToClipboard(filePath);

            doRequestUpLoadVideoCenntent(filePath, SPTool.getString(PlaybackActivity.this, Config.TRANSCODE_FILE_NOTE, "暂无信息"));
            ToastUtils.l(this, "文件上传成功，" + filePath + "已复制到粘贴板");
            mUploadBtn.setVisibility(View.INVISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUploadVideoFailed(int statusCode, String error) {
        ToastUtils.l(this, "Upload failed, statusCode = " + statusCode + " error = " + error);
    }

    private MediaController.OnClickSpeedAdjustListener mOnClickSpeedAdjustListener = new MediaController.OnClickSpeedAdjustListener() {
        @Override
        public void onClickNormal() {
            // 0x0001/0x0001 = 2
            mVideoView.setPlaySpeed(0X00010001);
        }

        @Override
        public void onClickFaster() {
            // 0x0002/0x0001 = 2
            mVideoView.setPlaySpeed(0X00020001);
        }

        @Override
        public void onClickSlower() {
            // 0x0001/0x0002 = 0.5
            mVideoView.setPlaySpeed(0X00010002);
        }
    };

    private PLMediaPlayer.OnVideoFrameListener mOnVideoFrameListener = new PLMediaPlayer.OnVideoFrameListener() {
        @Override
        public void onVideoFrameAvailable(byte[] data, int size, int width, int height, int format, long ts) {
            Log.i(TAG, "onVideoFrameAvailable: " + size + ", " + width + " x " + height + ", " + format + ", " + ts);
        }
    };

    private PLMediaPlayer.OnAudioFrameListener mOnAudioFrameListener = new PLMediaPlayer.OnAudioFrameListener() {
        @Override
        public void onAudioFrameAvailable(byte[] data, int size, int samplerate, int channels, int datawidth, long ts) {
            Log.i(TAG, "onAudioFrameAvailable: " + size + ", " + samplerate + ", " + channels + ", " + datawidth + ", " + ts);
        }
    };

    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            Log.i(TAG, "OnInfo, what = " + what + ", extra = " + extra);
            switch (what) {
                case PLMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    break;
                case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    break;
                case PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    ToastUtils.s(PlaybackActivity.this, "first video render time: " + extra + "ms");
                    break;
                case PLMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                    break;
                case PLMediaPlayer.MEDIA_INFO_VIDEO_FRAME_RENDERING:
                    Log.i(TAG, "video frame rendering, ts = " + extra);
                    break;
                case PLMediaPlayer.MEDIA_INFO_AUDIO_FRAME_RENDERING:
                    Log.i(TAG, "audio frame rendering, ts = " + extra);
                    break;
                case PLMediaPlayer.MEDIA_INFO_VIDEO_GOP_TIME:
                    Log.i(TAG, "Gop Time: " + extra);
                    break;
                case PLMediaPlayer.MEDIA_INFO_SWITCHING_SW_DECODE:
                    Log.i(TAG, "Hardware decoding failure, switching software decoding!");
                    break;
                case PLMediaPlayer.MEDIA_INFO_METADATA:
                    Log.i(TAG, mVideoView.getMetadata().toString());
                    break;
                case PLMediaPlayer.MEDIA_INFO_VIDEO_BITRATE:
                case PLMediaPlayer.MEDIA_INFO_VIDEO_FPS:
                    Log.i(TAG, "FPS: " + extra);
                    break;
                case PLMediaPlayer.MEDIA_INFO_CONNECTED:
                    Log.i(TAG, "Connected !");
                    break;
                case PLMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                    Log.i(TAG, "Rotation Changed: " + extra);
                    mVideoView.setDisplayOrientation(360 - extra);
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    /**
                     * SDK will do reconnecting automatically
                     */
                    Log.e(TAG, "IO Error!");
                    return false;
                case PLMediaPlayer.ERROR_CODE_OPEN_FAILED:
                    ToastUtils.s(PlaybackActivity.this, "failed to open player !");
                    break;
                case PLMediaPlayer.ERROR_CODE_SEEK_FAILED:
                    ToastUtils.s(PlaybackActivity.this, "failed to seek !");
                    break;
                default:
                    ToastUtils.s(PlaybackActivity.this, "unknown error !");
                    break;
            }
            finish();
            return true;
        }
    };

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            Log.i(TAG, "Play Completed !");
            ToastUtils.s(PlaybackActivity.this, "Play Completed !");
            finish();
        }
    };

    private PLMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new PLMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int precent) {
            Log.i(TAG, "onBufferingUpdate: " + precent);
        }
    };

    private PLMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height) {
            Log.i(TAG, "onVideoSizeChanged: width = " + width + ", height = " + height);
        }
    };
}