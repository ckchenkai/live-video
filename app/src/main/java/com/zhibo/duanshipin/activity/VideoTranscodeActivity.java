package com.zhibo.duanshipin.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.qiniu.android.utils.StringUtils;
import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTranscoder;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.utils.Config;
import com.zhibo.duanshipin.utils.GetPathFromUri;
import com.zhibo.duanshipin.utils.RecordSettings;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.view.CustomProgressDialog;

import java.io.File;

import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_LOW_MEMORY;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_NO_VIDEO_TRACK;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_SRC_DST_SAME_FILE_PATH;

public class VideoTranscodeActivity extends AppCompatActivity {
    private static final String TAG = "VideoTranscodeActivity";

    private Spinner mTranscodingBitrateLevelSpinner;
    private Spinner mTranscodingRotationSpinner;
    private EditText mTranscodingWidthEditText;
    private EditText mTranscodingHeightEditText;
    private CustomProgressDialog mProcessingDialog;

    private PLShortVideoTranscoder mShortVideoTranscoder;
    private PLMediaFile mMediaFile;
    private TextView mVideoFilePathText;
    private TextView mVideoSizeText;
    private TextView mVideoBitrateText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("VideoTranscode");
        setContentView(R.layout.activity_transcode);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

        setTitle(R.string.title_transcode);

        mVideoFilePathText = (TextView) findViewById(R.id.SrcVideoPathText);
        mVideoSizeText = (TextView) findViewById(R.id.SrcVideoSizeText);
        mVideoBitrateText = (TextView) findViewById(R.id.SrcVideoBitrateText);

        mTranscodingBitrateLevelSpinner = (Spinner) findViewById(R.id.TranscodingBitrateLevelSpinner);
        mTranscodingRotationSpinner = (Spinner) findViewById(R.id.TranscodingRotationSpinner);
        mTranscodingWidthEditText = (EditText) findViewById(R.id.TranscodingWidth);
        mTranscodingHeightEditText = (EditText) findViewById(R.id.TranscodingHeight);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, RecordSettings.ENCODING_BITRATE_LEVEL_TIPS_ARRAY);
        mTranscodingBitrateLevelSpinner.setAdapter(adapter);
        mTranscodingBitrateLevelSpinner.setSelection(2);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, RecordSettings.ROTATION_LEVEL_TIPS_ARRAY);
        mTranscodingRotationSpinner.setAdapter(adapter);
        mTranscodingRotationSpinner.setSelection(0);

        mProcessingDialog = new CustomProgressDialog(this);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoTranscoder.cancelTranscode();
            }
        });

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
        }
        startActivityForResult(Intent.createChooser(intent, "选择要转码的视频"), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String selectedFilepath = GetPathFromUri.getPath(this, data.getData());
            Log.i(TAG, "Select file: " + selectedFilepath);
            if (!StringUtils.isNullOrEmpty(selectedFilepath)) {
                onVideoFileSelected(selectedFilepath);
                return;
            }
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaFile != null) {
            mMediaFile.release();
        }
    }

    private void onVideoFileSelected(String filepath) {
        mShortVideoTranscoder = new PLShortVideoTranscoder(this, filepath, Config.TRANSCODE_FILE_PATH);
        mMediaFile = new PLMediaFile(filepath);
        mVideoFilePathText.setText(new File(filepath).getName());
        mVideoSizeText.setText(mMediaFile.getVideoWidth() + " x " + mMediaFile.getVideoHeight());
        mTranscodingWidthEditText.setText(String.valueOf(mMediaFile.getVideoWidth()), TextView.BufferType.EDITABLE);
        mTranscodingHeightEditText.setText(String.valueOf(mMediaFile.getVideoHeight()), TextView.BufferType.EDITABLE);
        String bitrate = (mMediaFile.getVideoBitrate() / 1000) + " kbps";
        mVideoBitrateText.setText(bitrate);
    }

    public void onClickTranscode(View v) {
        doTranscode(false);
    }

    public void onClickReverse(View v) {
        doTranscode(true);
    }

    private void doTranscode(boolean isReverse) {
        if (mShortVideoTranscoder == null) {
            ToastUtils.s(this, "请先选择转码文件！");
            return;
        }

        int transcodingBitrateLevel = mTranscodingBitrateLevelSpinner.getSelectedItemPosition();
        int transcodingRotationLevel = mTranscodingRotationSpinner.getSelectedItemPosition();
        int transcodingWidth = Integer.parseInt(mTranscodingWidthEditText.getText().toString());
        int transcodingHeight = Integer.parseInt(mTranscodingHeightEditText.getText().toString());
        String filedirection = "";
        if (transcodingRotationLevel == 0) {
            filedirection = "0";
        } else if (transcodingRotationLevel == 1) {
            filedirection = "90";
        } else if (transcodingRotationLevel == 2) {
            filedirection = "180";
        } else {
            filedirection = "270";
        }
//        ToastUtils.s(this, "视频本地名称：" + mVideoFilePathText.getText().toString().trim() + "\n视频尺寸：" + transcodingHeight + "x" + transcodingWidth + "\n视频码率：" + (RecordSettings.ENCODING_BITRATE_LEVEL_ARRAY[transcodingBitrateLevel] / 1000) + "kbps\n视频旋转角度：" + RecordSettings.ROTATION_LEVEL_ARRAY[transcodingRotationLevel]);
        String strFileNote = "视频本地名称：" + mVideoFilePathText.getText().toString().trim() + "\n视频尺寸：" + transcodingHeight + "x" + transcodingWidth + "\n视频码率：" + (RecordSettings.ENCODING_BITRATE_LEVEL_ARRAY[transcodingBitrateLevel] / 1000) + "kbps\n视频旋转角度：" + RecordSettings.ROTATION_LEVEL_ARRAY[transcodingRotationLevel];
        SPTool.putString(this, Config.TRANSCODE_FILE_NOTE, strFileNote);


        mProcessingDialog.show();

        mShortVideoTranscoder.transcode(
                transcodingWidth, transcodingHeight,
                RecordSettings.ENCODING_BITRATE_LEVEL_ARRAY[transcodingBitrateLevel],
                RecordSettings.ROTATION_LEVEL_ARRAY[transcodingRotationLevel],
                isReverse, new PLVideoSaveListener() {
                    @Override
                    public void onSaveVideoSuccess(final String s) {
                        Log.i(TAG, "save success: " + s);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProcessingDialog.dismiss();
                                showChooseDialog(s);
                            }
                        });
                    }

                    @Override
                    public void onSaveVideoFailed(final int errorCode) {
                        Log.i(TAG, "save failed: " + errorCode);
                        mProcessingDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (errorCode) {
                                    case ERROR_NO_VIDEO_TRACK:
                                        ToastUtils.s(VideoTranscodeActivity.this, "该文件没有视频信息！");
                                        break;
                                    case ERROR_SRC_DST_SAME_FILE_PATH:
                                        ToastUtils.s(VideoTranscodeActivity.this, "源文件路径和目标路径不能相同！");
                                        break;
                                    case ERROR_LOW_MEMORY:
                                        ToastUtils.s(VideoTranscodeActivity.this, "手机内存不足，无法对该视频进行时光倒流！");
                                        break;
                                    default:
                                        ToastUtils.s(VideoTranscodeActivity.this, "transcode failed: " + errorCode);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSaveVideoCanceled() {
                        mProcessingDialog.dismiss();
                    }

                    @Override
                    public void onProgressUpdate(final float percentage) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProcessingDialog.setProgress((int) (100 * percentage));
                            }
                        });
                    }
                });
    }

    private void showChooseDialog(final String filePath) {
        PlaybackActivity.start(VideoTranscodeActivity.this, filePath);
    }

}
