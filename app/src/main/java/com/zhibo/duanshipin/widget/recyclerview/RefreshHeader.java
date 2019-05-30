package com.zhibo.duanshipin.widget.recyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.ULog;
import com.zhibo.duanshipin.widget.GoogleDotView;

import java.util.Date;


public class RefreshHeader extends LinearLayout {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_RELEASE_TO_REFRESH = 1;
    public static final int STATE_REFRESHING = 2;
    public static final int STATE_DONE = 3;
    private LinearLayout mContainer;
    private ImageView mArrowImageView;
    private TextView mStatusTextView;
    private int mState = STATE_NORMAL;

    private TextView mHeaderTimeView;

    private static final int ROTATE_ANIM_DURATION = 180;
    public int mMeasuredHeight;
    private Context mContext;
    private ImageView rotateView;
    private AnimationDrawable animationDrawable;
    private  String LAST_REFRESH_TIME = "";
    private static int tag = 0;

    public RefreshHeader(Context context) {
        super(context);
        initView();
    }

    /**
     * @param context
     * @param attrs
     */
    public RefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mContext = getContext();
        LAST_REFRESH_TIME = mContext.getClass().getSimpleName()+"_"+(tag++)+"_last_refresh_time";
        Log.e("ck--",LAST_REFRESH_TIME);
        // 初始情况，设置下拉刷新view高度为0
        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.layout_recycler_header, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);

        mArrowImageView = (ImageView) findViewById(R.id.listview_header_arrow);
        mStatusTextView = (TextView) findViewById(R.id.refresh_status_textview);
        rotateView = (ImageView) findViewById(R.id.rotate_header_arrow);


        mHeaderTimeView = (TextView) findViewById(R.id.last_refresh_time);
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();

//        rotateAnim = new RotateAnimation(0.0f,360.0f, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnim.setDuration(700);
//        rotateAnim.setRepeatCount(-1);
//        rotateAnim.setFillAfter(true);
//        rotateAnim.setInterpolator(new LinearInterpolator());
        animationDrawable = (AnimationDrawable) rotateView.getDrawable();
        mHeaderTimeView.setText(friendlyTime());
    }

    public void setProgressStyle(int style) {
      /*  if (style == ProgressStyle.SysProgress) {
            mProgressBar.setView(new ProgressBar(getContext(), null, android.R.attr.progressBarStyle));
        } else {
            AVLoadingIndicatorView progressView = new AVLoadingIndicatorView(this.getContext());
            progressView.setIndicatorColor(0xffB5B5B5);
            progressView.setIndicatorId(style);
            mProgressBar.setView(progressView);
        }*/
    }

    public void setArrowImageView(int resid) {
        mArrowImageView.setImageResource(resid);
    }

    public void setState(int state) {
        if (state == mState) return;
        mHeaderTimeView.setText(friendlyTime());
        switch (state) {
            case STATE_NORMAL:
                mStatusTextView.setText(R.string.listview_header_hint_normal);
                mArrowImageView.setVisibility(View.VISIBLE);
                rotateView.setVisibility(View.INVISIBLE);
                if (mState == STATE_RELEASE_TO_REFRESH) {
                    mArrowImageView.setImageResource(R.drawable.load_down);
                }
                break;
            case STATE_RELEASE_TO_REFRESH:
                mArrowImageView.setVisibility(View.VISIBLE);
                rotateView.setVisibility(View.INVISIBLE);
                if (mState != STATE_RELEASE_TO_REFRESH) {
                    mArrowImageView.setImageResource(R.drawable.load_up);
                    mStatusTextView.setText(R.string.listview_header_hint_release);
                }
                break;
            case STATE_REFRESHING:
                mStatusTextView.setText(R.string.refreshing);
                mArrowImageView.setVisibility(View.INVISIBLE);
                rotateView.setVisibility(View.VISIBLE);
                animationDrawable.start();
                break;
            case STATE_DONE:
                mStatusTextView.setText(R.string.refresh_done);
                mArrowImageView.setVisibility(View.VISIBLE);
                mArrowImageView.setImageResource(R.drawable.load_down);
                if(animationDrawable.isRunning()){
                    animationDrawable.stop();
                }
                rotateView.clearAnimation();
                rotateView.setVisibility(View.INVISIBLE);
                break;
            default:
        }

        mState = state;
    }

    public int getState() {
        return mState;
    }

    public void refreshComplete() {
        SPTool.putLong(getContext(),LAST_REFRESH_TIME,System.currentTimeMillis());
        ULog.e("ck--",LAST_REFRESH_TIME);
        //mHeaderTimeView.setText(friendlyTime());
        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                reset();
            }
        }, 100);
    }

   public void refreshCompletNoDelay(){
        SPTool.putLong(getContext(),LAST_REFRESH_TIME,System.currentTimeMillis());
       Log.e("ck--",LAST_REFRESH_TIME);
        //mHeaderTimeView.setText(friendlyTime());
        setState(STATE_DONE);
        reset();
    }

    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
    }

    public void onMove(float delta) {
        if (getVisibleHeight() > 0 || delta > 0) {
            setVisibleHeight((int) delta + getVisibleHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
                if (getVisibleHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                } else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0) // not visible.
            isOnRefresh = false;

        if (getVisibleHeight() > mMeasuredHeight && mState < STATE_REFRESHING) {
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <= mMeasuredHeight) {
            //return;
        }
        int destHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mState == STATE_REFRESHING) {
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(destHeight);

        return isOnRefresh;
    }

    public void reset() {
        smoothScrollTo(0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                setState(STATE_NORMAL);
            }
        }, 400);
    }

    public void setContainerINVisible() {
        if (mContainer != null)
            mContainer.setVisibility(View.INVISIBLE);
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    public String friendlyTime() {
        long lastRefreshTime = SPTool.getLong(getContext(),LAST_REFRESH_TIME,0);
        int ct = (int) ((System.currentTimeMillis() - lastRefreshTime) / 1000);

        if (ct == 0) {
            return mContext.getResources().getString(R.string.text_just);
        }

        if (ct > 0 && ct < 60) {
            return ct + mContext.getResources().getString(R.string.text_seconds_ago);
        }

        if (ct >= 60 && ct < 3600) {
            return Math.max(ct / 60, 1) + mContext.getResources().getString(R.string.text_minute_ago);
        }
        if (ct >= 3600 && ct < 86400)
            return ct / 3600 + mContext.getResources().getString(R.string.text_hour_ago);
        if (ct >= 86400 && ct < 2592000) { //86400 * 30
            int day = ct / 86400;
            return day + getContext().getResources().getString(R.string.text_day_ago);
        }
        if (ct >= 2592000 && ct < 31104000) { //86400 * 30
            return ct / 2592000 + mContext.getResources().getString(R.string.text_month_ago);
        }
        return ct / 31104000 + mContext.getResources().getString(R.string.text_year_ago);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        tag = 0;
    }
}