package com.zhibo.duanshipin.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.bugtags.library.Bugtags;
import com.umeng.analytics.MobclickAgent;
import com.zhibo.duanshipin.MyApplication;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by Administrator on 2016/12/8.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Unbinder unbinder;
    protected boolean hasFragment; //判断是否是FragmentActivity + Fragmen或是单独的一个activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentId());
        /** 设置是否对日志信息进行加密, 默认false(不加密). */
        MobclickAgent.enableEncrypt(true);
//        PushAgent.getInstance(this).onAppStart();
        unbinder = ButterKnife.bind(this);
        MyApplication App = ((MyApplication) getApplicationContext());
        App.mActivityList.add(this);
        initViews();
        initView(savedInstanceState);
        initToolbar();
        //setStatusTextGrey();
    }

    public void onResume() {
        super.onResume();
        if(!hasFragment){
            MobclickAgent.onPageStart(this.getClass().getSimpleName()); //统计页面
        }
        MobclickAgent.onResume(this);       //统计时长
        Bugtags.onResume(this);
    }

    public void onPause() {
        super.onPause();
        if(!hasFragment){
            MobclickAgent.onPageEnd(this.getClass().getSimpleName()); //统计页面
        }
        MobclickAgent.onPause(this);
        Bugtags.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    protected abstract int getContentId();
    protected abstract void initViews();
    protected abstract void initToolbar();
    protected void initView(Bundle savedInstanceState){

    }

//    private void setStatusTextGrey(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Utils.MIUISetStatusBarLightMode(getWindow(),true);
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.main_divider));
//        }
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }
}
