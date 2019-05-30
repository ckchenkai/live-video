package com.zhibo.duanshipin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.adapter.TitlePagerAdapter;
import com.zhibo.duanshipin.base.BaseActivity;
import com.zhibo.duanshipin.fragment.IntroductionFragment;
import com.zhibo.duanshipin.fragment.LiveListFragment;
import com.zhibo.duanshipin.fragment.VideoPlayFragment;
import com.zhibo.duanshipin.utils.MessageEvent;
import com.zhibo.duanshipin.utils.SharedUtil;
import com.zhibo.duanshipin.widget.NoScrollViewPager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * This demo shows how to use PLMediaPlayer API playing video stream
 */
public class VideoPlayActivity extends BaseActivity {
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private String mVideoPath = "";
    private String mThumbPath = "";
    private String termId = "";
    private String description = "";
    private boolean isPicLive;
    private boolean liveStreaming;
    private boolean cache;
    private String shareUrl = "";
    private String title = "";


    public static void startVideoPlay(Context context, String videoPath, String thumb, String termId, String description, boolean isPicLive, boolean liveStreaming, boolean cache, String shareUrl, String title) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("thumbPath", thumb);
        intent.putExtra("termId", termId);
        intent.putExtra("description", description);
        intent.putExtra("isPicLive", isPicLive);
        intent.putExtra("isLiveStream", liveStreaming);
        intent.putExtra("isCache", cache);
        intent.putExtra("shareUrl", shareUrl);
        intent.putExtra("title", title);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.anim_none, R.anim.anim_none);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_video_play;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        if (getIntent() != null) {
            mVideoPath = getIntent().getStringExtra("videoPath");
            mThumbPath = getIntent().getStringExtra("thumbPath");
            termId = getIntent().getStringExtra("termId");
            description = getIntent().getStringExtra("description");
            isPicLive = getIntent().getBooleanExtra("isPicLive", false);
            liveStreaming = getIntent().getBooleanExtra("isLiveStream", false);
            cache = getIntent().getBooleanExtra("isCache", false);
            shareUrl = getIntent().getStringExtra("shareUrl");
            title = getIntent().getStringExtra("title");
        }
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null&&fragment!=null) {
            if (fragment.isAdded()) {
                return;
            }
        }
        if (fragmentManager != null && fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragment = VideoPlayFragment.newInstance(mVideoPath, mThumbPath, termId, description, isPicLive, liveStreaming, cache, shareUrl, title);
        transaction.add(R.id.video_paly_container, fragment);
        transaction.commit();
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    public void onPause() {
        super.onPause();
        //videoPlayer.pauseAllVideos();
        JZVideoPlayer.goOnPlayOnPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //videoPlayer.resumeAllVideos();
        JZVideoPlayer.goOnPlayOnResume();
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JZVideoPlayer.releaseAllVideos();
    }
}
