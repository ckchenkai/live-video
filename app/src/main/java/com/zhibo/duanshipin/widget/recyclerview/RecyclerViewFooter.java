package com.zhibo.duanshipin.widget.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhibo.duanshipin.R;


public class RecyclerViewFooter extends RelativeLayout {

    protected State mState = State.Normal;
    private ImageView mLoadingError;
    private ProgressBar mLoadingProgress;
    private TextView mLoadingText;
    private Animation rotateAnim;

    public RecyclerViewFooter(Context context) {
        super(context);
        init(context);
    }

    public RecyclerViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecyclerViewFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {

        inflate(context, R.layout.layout_recycler_footer, this);
       // mLoadingProgress = (ProgressBar) findViewById(R.id.pb_footer_loading);
        mLoadingProgress = (ProgressBar) findViewById(R.id.iv_footer_loading);
        mLoadingError = (ImageView) findViewById(R.id.iv_footer_error);
        mLoadingText = (TextView) findViewById(R.id.tv_footer_hint);
        rotateAnim = new RotateAnimation(0.0f,360.0f, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(700);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.setFillAfter(true);
        rotateAnim.setInterpolator(new LinearInterpolator());
        setOnClickListener(null);
        setState(State.Normal, true);
    }

    public State getState() {
        return mState;
    }

    public void setState(State status ) {
        setState(status, true);
    }

    /**
     * 设置状态
     *
     * @param status
     * @param showView 是否展示当前View
     */
    public void setState(State status, boolean showView) {
        if (mState == status&&mState!= State.Normal) {
            return;
        }
        mState = status;

        switch (status) {

            case Normal:
                setOnClickListener(null);
                mLoadingProgress.setVisibility(GONE);
//                mLoadingView.clearAnimation();
//                mLoadingView.setVisibility(View.GONE);
                mLoadingText.setVisibility(GONE);
                mLoadingError.setVisibility(GONE);
                break;
            case Loading:
                setOnClickListener(null);
                mLoadingError.setVisibility(GONE);
                mLoadingProgress.setVisibility(showView?VISIBLE:GONE);
//                mLoadingView.setVisibility(showView?VISIBLE:GONE);
//                mLoadingView.startAnimation(rotateAnim);
                mLoadingText.setVisibility(showView?VISIBLE:GONE);
                mLoadingText.setText(R.string.list_footer_loading);
                break;
            case TheEnd:
                setOnClickListener(null);
                mLoadingError.setVisibility(GONE);
                mLoadingProgress.setVisibility(GONE);
//                mLoadingView.clearAnimation();
//                mLoadingView.setVisibility(View.GONE);
                mLoadingText.setVisibility(showView?VISIBLE:GONE);
                mLoadingText.setText(R.string.list_footer_end);
                break;
            case NetWorkError:
                mLoadingError.setVisibility(showView?VISIBLE:GONE);
                mLoadingProgress.setVisibility(GONE);
//                mLoadingView.clearAnimation();
//                mLoadingView.setVisibility(View.GONE);
                mLoadingText.setVisibility(showView?VISIBLE:GONE);
                mLoadingText.setText(R.string.list_footer_network_error);
                break;
            default:

                break;
        }
    }

    public enum State {
        Normal/**正常*/, TheEnd/**加载到最底了*/, Loading/**加载中..*/, NetWorkError/**网络异常*/
    }

    public void setFooterText(String text){
        if(mLoadingText!=null&&text!=null)
            mLoadingText.setText(text);
    }
}