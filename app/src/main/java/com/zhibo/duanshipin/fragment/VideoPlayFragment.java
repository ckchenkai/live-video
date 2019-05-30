package com.zhibo.duanshipin.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.flyco.tablayout.SlidingTabLayout;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.adapter.TitlePagerAdapter;
import com.zhibo.duanshipin.base.BaseLazyFragment;
import com.zhibo.duanshipin.httprequest.OkHttpCallback;
import com.zhibo.duanshipin.httprequest.OkHttpUtil;
import com.zhibo.duanshipin.utils.SharedUtil;
import com.zhibo.duanshipin.utils.StatusBarUtil;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;
import com.zhibo.duanshipin.widget.NoScrollViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jzvd.JZUserAction;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;


/**
 * 视频播放
 */
public class VideoPlayFragment extends BaseLazyFragment implements View.OnClickListener {
    private static final String TAG = VideoPlayFragment.class.getSimpleName();
    private JZVideoPlayerStandard videoPlayer;
    private ImageView ivPicLive;
    private ImageView ivShare;
    private String mVideoPath = "";
    private String mThumbPath = "";

    //顶部标题栏
    private ImageView ivBack;
    private TextView tvTitle;
    private List<Fragment> fragmentList = new ArrayList<>();
    private TitlePagerAdapter pagerAdapter;
    private NoScrollViewPager viewPager;
    private SlidingTabLayout tabLayout;
    private String termId;
    private String description;
    private boolean isPicLive;
    private PopupWindow popupWindow;
    private String shareUrl = "";
    private String title = "";
    private boolean isPlay;
    private boolean isPause;
    private boolean isLiveStream;
    private ProgressBar progressBar;
    private LinearLayout mainLayout;
    /**
     * 是否直播流
     */
    private boolean isCache;
    /**
     * 是否缓存视频
     */
    private LiveListFragment mLiveListFragment;
    private Activity mActivity;
    private boolean isNeedGetData;//是否需要获取直播数据，如是，则progressbar显示，则请求接口获取数据；如不是，则使用传过来的数据

    public static VideoPlayFragment newInstance(boolean isNeedGetData) {
        VideoPlayFragment fragment = new VideoPlayFragment();
        Bundle args = new Bundle();
        args.putBoolean("isNeedGetData", isNeedGetData);
        fragment.setArguments(args);
        return fragment;
    }
    public static VideoPlayFragment newInstance(String videoPath, String thumb, String termId, String description, boolean isPicLive, boolean liveStreaming, boolean cache, String shareUrl, String title) {
        VideoPlayFragment fragment = new VideoPlayFragment();
        Bundle args = new Bundle();
        args.putString("videoPath", videoPath);
        args.putString("thumbPath", thumb);
        args.putString("termId", termId);
        args.putString("description", description);
        args.putBoolean("isPicLive", isPicLive);
        args.putBoolean("isLiveStream", liveStreaming);
        args.putBoolean("isCache", cache);
        args.putString("shareUrl", shareUrl);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_video_play;
    }

    @Override
    protected void onLazyLoad() {
        if (isNeedGetData) {
            doGetData();
            ivBack.setVisibility(View.INVISIBLE);
        } else {
            initData();
            ivBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initStatusBar() {
        super.initStatusBar();
        StatusBarUtil.setStatusBarColor(getActivity(),R.color.black);
        StatusBarUtil.StatusBarDarkMode(getActivity());
    }

    @Override
    protected void initViews(View view) {
        mActivity = getActivity();
        //沉浸式状态栏和透明导航栏
//        if (android.os.Build.VERSION.SDK_INT >= 16) {
//            int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//            mActivity.getWindow().getDecorView().setSystemUiVisibility(visibility);
//        }
//        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getArguments() != null) {
            isNeedGetData = getArguments().getBoolean("isNeedGetData");
            mVideoPath = getArguments().getString("videoPath");
            mThumbPath = getArguments().getString("thumbPath");
            isLiveStream = getArguments().getBoolean("isLiveStream", false);
            isCache = getArguments().getBoolean("isCache", false);
            termId = getArguments().getString("termId");
            description = getArguments().getString("description");
            isPicLive = getArguments().getBoolean("isPicLive", false);
            shareUrl = getArguments().getString("shareUrl");
            title = getArguments().getString("title");
        }
        mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        ivBack = (ImageView) view.findViewById(R.id.iv_back);
        ivShare = (ImageView) view.findViewById(R.id.iv_share);
        videoPlayer = (JZVideoPlayerStandard) view.findViewById(R.id.video_player);
        ivBack.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        ivPicLive = (ImageView) view.findViewById(R.id.iv_pic_live);
        viewPager = (NoScrollViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (SlidingTabLayout) view.findViewById(R.id.tablayout);
    }

    /**
     * 获取直播数据
     */
    private void doGetData() {
        String url = "http://newlive.longhoo.net/index.php?g=portal&m=video&a=newVlistscom";
        OkHttpUtil.getInstance().doAsyncPost(url, new HashMap<String, String>(), new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {
                ULog.e("ck","直播数据："+response);
                if (!isPrepared) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.optString("status");
                    String msg = jsonObject.optString("info");
                    JSONObject lists = jsonObject.optJSONObject("lists");
                    if (lists == null) {
                        ToastUtils.getInstance().showToast(getActivity(), msg);
                        return;
                    }
                    mVideoPath = lists.getString("zburl");
                    mThumbPath = lists.optString("mthumb");
                    if(TextUtils.isEmpty(mThumbPath)){
                        mThumbPath = lists.optString("phonethumb");
                    }
                    if (TextUtils.equals(status, "1")) { //:1表示正在直播；2表示未开始；3表示已结束
                        isLiveStream = true;
                        isCache = false;
                    } else {
//                        if (TextUtils.equals(status, "2")) {
//                            ToastUtils.getInstance().showToast(getActivity(), "当前直播未开始，请稍后...");
//                        }
                        isLiveStream = false;
                        isCache = true;
                    }
                    termId = lists.optString("term_id");
                    String termType = lists.optString("term_type");
                    description = lists.optString("description");
                    if (TextUtils.equals(termType, "0")) {  //图文直播
                        isPicLive = true;
                    } else {                               //视频直播
                        isPicLive = false;
                    }
                    shareUrl = lists.optString("share_url");
                    title = lists.optString("name");
                    initData();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToast(getActivity(), "服务器异常~");
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                if(!isPrepared){
                    return;
                }
                ToastUtils.getInstance().showToast(getActivity(), "网络异常~");
            }
        });
    }

    private void initData() {
        tvTitle.setText(title);
        List<String> titleList = new ArrayList<>();
        if (termId != null) {
            titleList.add("图文直播");
            mLiveListFragment = LiveListFragment.newInstance(termId);
            fragmentList.add(mLiveListFragment);
        }
        titleList.add("简介");
        fragmentList.add(IntroductionFragment.newInstance(description));
        pagerAdapter = new TitlePagerAdapter(fragmentList, getChildFragmentManager(), titleList);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setViewPager(viewPager);
        if (TextUtils.isEmpty(shareUrl)) {
            ivShare.setVisibility(View.GONE);
        }
        //判断是否图文直播
        if (isPicLive) {
            ivPicLive.setVisibility(View.VISIBLE);
            videoPlayer.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(mThumbPath)) {
                RequestOptions options = new RequestOptions();
                options.placeholder(R.color.black).error(R.color.black).centerCrop();
                Glide.with(this).load(mThumbPath)
                        .apply(options)
                        .transition(new DrawableTransitionOptions().crossFade())
                        .into(ivPicLive);
            }
        } else {
            ivPicLive.setVisibility(View.GONE);
            videoPlayer.setVisibility(View.VISIBLE);
            videoPlayer.setUp(mVideoPath, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
            videoPlayer.setIsLiveStream(isLiveStream);
            if (!TextUtils.isEmpty(mThumbPath)) {
                RequestOptions options = new RequestOptions();
                options.placeholder(R.color.black).error(R.color.black).centerCrop();
                Glide.with(this).load(mThumbPath)
                        .apply(options)
                        .transition(new DrawableTransitionOptions().crossFade())
                        .into(videoPlayer.thumbImageView);
            }
            videoPlayer.startButton.performClick();
        }
        videoPlayer.setJzUserAction(new JZUserAction() {
            @Override
            public void onEvent(int type, Object url, int screen, Object... objects) {
                if (type == JZUserAction.ON_AUTO_COMPLETE) {
                    ToastUtils.getInstance().showToast(mActivity,"播放结束");
                }
            }
        });
    }

    @Override
    protected void initToolbar() {
    }

    @Override
    protected long setLoadInterval() {
        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closePopWindow();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.goOnPlayOnPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //JZVideoPlayer.goOnPlayOnResume();
    }

    //    @Override
//    public void onBackPressed() {
//        if (JZVideoPlayer.backPress()) {
//            return;
//        }
//        super.onBackPressed();
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                mActivity.onBackPressed();
                break;
            case R.id.iv_share:
                if (!TextUtils.isEmpty(shareUrl)) {
                    showPopWindow();
                }
                break;
        }
    }


    private void showPopWindow() {
        TextView tvWxFriend, tvWxPyq, tvQQFriend, tvQQZone, tvWeibo;
        ImageView ivClose;
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(mActivity).inflate(R.layout.layout_share_pop, null);
            tvWxFriend = (TextView) contentView.findViewById(R.id.tv_wx_friend);
            tvWxPyq = (TextView) contentView.findViewById(R.id.tv_wx_pyq);
            tvQQFriend = (TextView) contentView.findViewById(R.id.tv_qq_friend);
            tvQQZone = (TextView) contentView.findViewById(R.id.tv_qq_zone);
            tvWeibo = (TextView) contentView.findViewById(R.id.tv_weibo);
            ivClose = (ImageView) contentView.findViewById(R.id.iv_close);
            tvWxFriend.setOnClickListener(new ShareItemClick(tvWxFriend));
            tvWxPyq.setOnClickListener(new ShareItemClick(tvWxPyq));
            tvQQFriend.setOnClickListener(new ShareItemClick(tvQQFriend));
            tvQQZone.setOnClickListener(new ShareItemClick(tvQQZone));
            tvWeibo.setOnClickListener(new ShareItemClick(tvWeibo));
            ivClose.setOnClickListener(new ShareItemClick(ivClose));
            popupWindow = new PopupWindow(mActivity);
            popupWindow.setContentView(contentView);
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setAnimationStyle(-1);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    closePopWindow();
                }
            });
        }
        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = 0.5f;
        mActivity.getWindow().setAttributes(params);
        popupWindow.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);

    }

    private void closePopWindow() {
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            popupWindow = null;
            WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
            params.alpha = 1f;
            mActivity.getWindow().setAttributes(params);
        }
    }

    private class ShareItemClick implements View.OnClickListener {
        private View view;

        public ShareItemClick(View view) {
            this.view = view;
        }

        @Override
        public void onClick(View v) {
            String url = TextUtils.isEmpty(shareUrl) ? "" : shareUrl;
            String content = description;
            switch (view.getId()) {
                case R.id.tv_wx_friend:
                    SharedUtil.SharePengYou(url, title, content, mActivity);
                    break;
                case R.id.tv_wx_pyq:
                    SharedUtil.SharePengYouQuan(url, title, mActivity);
                    break;
                case R.id.tv_qq_friend:
                    SharedUtil.ShareQQHaoYou(title, content, url, "http://newlive.longhoo.net/ic_launcher.png", mActivity);
                    break;
                case R.id.tv_qq_zone:
                    SharedUtil.ShareQQKongJian(title, "", url, "http://newlive.longhoo.net/ic_launcher.png", mActivity);
                    break;
                case R.id.tv_weibo:
                    SharedUtil.shareWB(title, "", url, mActivity);
                    break;
                case R.id.iv_close:
                    break;
            }
            closePopWindow();
        }
    }

    /**
     * 分享后回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != SharedUtil.mTencent) {
            SharedUtil.mTencent.onActivityResultData(requestCode, resultCode, data, SharedUtil.qZoneShareListener);
        }
    }

}
