package com.zhibo.duanshipin.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zhibo.duanshipin.R;

/**
 * Created by CK on 2018/3/2.
 * Email:910663958@qq.com
 */

public class LoadingDialog extends ProgressDialog {
    private ImageView rotateView;
    private AnimationDrawable animationDrawable;
    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }

    private void init(Context context) {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.layout_loading_dialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
        rotateView = (ImageView) findViewById(R.id.rotate_header_arrow);
        animationDrawable = (AnimationDrawable) rotateView.getDrawable();
    }

    @Override
    public void show() {
        super.show();
        animationDrawable.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(animationDrawable.isRunning()){
            animationDrawable.stop();
        }
    }
}
