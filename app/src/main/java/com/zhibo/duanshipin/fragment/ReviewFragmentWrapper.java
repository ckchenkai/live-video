package com.zhibo.duanshipin.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.base.BaseLazyFragment;
import com.zhibo.duanshipin.utils.StatusBarUtil;
import com.zhibo.duanshipin.utils.ULog;


public class ReviewFragmentWrapper extends BaseLazyFragment {
    private static final String FRAGMENT_NAME = "fragment_name";
    private String fragmentName;
    private FragmentActivity mActivity;
    private FragmentManager fragmentManager;
    private Fragment fragment;

    public static ReviewFragmentWrapper newInstance(String fragmentName) {
        ReviewFragmentWrapper fragmentWrapper = new ReviewFragmentWrapper();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_NAME, fragmentName);
        fragmentWrapper.setArguments(args);
        return fragmentWrapper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentName = getArguments().getString(FRAGMENT_NAME);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    @Override
    protected void initStatusBar() {
        super.initStatusBar();
        //如果此时该fragment中回退栈是视频播放的fragment时，改变状态栏颜色为黑色，否则，为白色
        if (mActivity.getSupportFragmentManager() != null) {
            int backStackEntryCount = mActivity.getSupportFragmentManager().getBackStackEntryCount();
            if (backStackEntryCount >= 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    StatusBarUtil.setStatusBarColor(getActivity(), R.color.black);
                    StatusBarUtil.StatusBarDarkMode(getActivity());
                }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    StatusBarUtil.setStatusBarColor(getActivity(), R.color.black);
                }
                return;
            } else if (backStackEntryCount > 0) {
                FragmentManager.BackStackEntry backStack = mActivity.getSupportFragmentManager().getBackStackEntryAt(backStackEntryCount - 1);
                String tag = backStack.getName();
                if (TextUtils.equals(tag, fragmentName)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        StatusBarUtil.setStatusBarColor(getActivity(), R.color.black);
                        StatusBarUtil.StatusBarDarkMode(getActivity());
                    }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        StatusBarUtil.setStatusBarColor(getActivity(), R.color.black);
                    }
                    return;
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getActivity(), R.color.white);
            StatusBarUtil.StatusBarLightMode(getActivity());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarUtil.setStatusBarColor(getActivity(), R.color.black);
            StatusBarUtil.StatusBarDarkMode(getActivity());
            ULog.e("ck","回顾黑色");
        }
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_review_wrapper;
    }

    @Override
    protected void onLazyLoad() {
        fragmentManager = getChildFragmentManager();
        if (fragment != null) {
            if (fragment.isAdded()) {
                return;
            }
        }
        if (fragmentManager != null && fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragment = Fragment.instantiate(mActivity, fragmentName, null);
        transaction.add(R.id.wrapper_review_container, fragment);
        transaction.commit();
    }

    @Override
    protected void initViews(View view) {

    }

    @Override
    protected void initToolbar() {

    }

    @Override
    protected long setLoadInterval() {
        return 0;
    }
}
