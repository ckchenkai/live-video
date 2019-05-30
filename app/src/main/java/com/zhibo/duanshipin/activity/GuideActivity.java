package com.zhibo.duanshipin.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.adapter.GuideAdapter;
import com.zhibo.duanshipin.base.BaseActivity;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.widget.CustomVideoView;
import com.zhibo.duanshipin.widget.NoScrollViewPager;
import com.zhibo.duanshipin.widget.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener, OnClickListener, NoScrollViewPager.OnItemTouchListener {
    private NoScrollViewPager viewPager;
    private CircleIndicator indicatorPanel;
    private Button btEnter;
    private GuideAdapter adapter;
    private int imgArray[] = {R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
    private List<ImageView> imageViewsList = new ArrayList<>();
    private List<View> viewList = new ArrayList<>();
    private static final int FLING_MIN_DISTANCE = 180;// 移动最小距离
    private static final int FLING_MIN_VELOCITY = 300;// 移动最小速度
    GestureDetector mygesture;
    //创建播放视频的控件对象
    private CustomVideoView videoview;
    ImageView btinenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_guide;
    }

    //返回重启加载
    @Override
    protected void onRestart() {
        initVideo();
        super.onRestart();
    }

    //防止锁屏或者切出的时候，音乐在播放
    @Override
    protected void onStop() {
        videoview.stopPlayback();
        super.onStop();
    }


    void initVideo() {
        //加载视频资源控件
        videoview = (CustomVideoView) findViewById(R.id.videoview);
        //设置播放加载路径
        videoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));

        //播放
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0f, 0f);
                mp.start();
                videoview.start();
            }
        });
        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        });
    }

    @Override
    protected void initViews() {
        btinenter = ((ImageView) findViewById(R.id.bt_inenter));
        btinenter.setVisibility(View.GONE);
        initVideo();
        hasFragment = true;
        mygesture = new GestureDetector(this, new GuideViewTouch());
        int size = imgArray.length;
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imgArray[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lp);
            imageViewsList.add(imageView);

        }
        viewPager = (NoScrollViewPager) findViewById(R.id.viewpager);
        adapter = new GuideAdapter(this, imageViewsList);
        viewPager.setAdapter(adapter);
        viewPager.setOnItemTouchListener(this);
        indicatorPanel = (CircleIndicator) findViewById(R.id.indicator_panel);
        viewPager.addOnPageChangeListener(this);
        indicatorPanel.setViewPager(viewPager);
        btinenter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SPTool.putBoolean(GuideActivity.this, Consts.IS_FIRST_IN_APP, false);
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 2) {
            btinenter.setVisibility(View.VISIBLE);
        } else {
            btinenter.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_enter) {
//            SPTool.putBoolean(this, Consts.IS_FIRST_IN_APP,false);
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mygesture.onTouchEvent(ev)) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onItemTouch(int position) {

        if (position == 3) {
            SPTool.putBoolean(this, Consts.IS_FIRST_IN_APP, false);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private class GuideViewTouch extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (viewPager.getCurrentItem() == imgArray.length - 1 && e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(e1.getY() - e2.getY()) < 200 && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                onItemTouch(3);
            }
            return false;
        }
    }
}
