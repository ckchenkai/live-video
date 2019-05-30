package com.zhibo.duanshipin.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.adapter.MainPagerAdapter;
import com.zhibo.duanshipin.base.BaseActivity;
import com.zhibo.duanshipin.fragment.HomeFragment;
import com.zhibo.duanshipin.fragment.HomeFragmentWrapper;
import com.zhibo.duanshipin.fragment.MeFragment;
import com.zhibo.duanshipin.fragment.ReviewFragment;
import com.zhibo.duanshipin.fragment.ReviewFragmentWrapper;
import com.zhibo.duanshipin.fragment.SceneLiveFragment;
import com.zhibo.duanshipin.fragment.VideoPlayFragment;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.StatusBarUtil;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.UpgradeManager;
import com.zhibo.duanshipin.utils.Utils;
import com.zhibo.duanshipin.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;

/**
 * Created by admin on 2017/8/29.
 */

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener , UpgradeManager.UpGradeListener {

    @BindView(R.id.tv_msg_1)
    TextView tvMsg1;
    @BindView(R.id.iv_bottom_1)
    ImageView ivBottom1;
    @BindView(R.id.tv_bottom_1)
    TextView tvBottom1;
    @BindView(R.id.main_panel_1)
    FrameLayout mainPanel1;
    @BindView(R.id.tv_msg_2)
    TextView tvMsg2;
    @BindView(R.id.iv_bottom_2)
    ImageView ivBottom2;
    @BindView(R.id.tv_bottom_2)
    TextView tvBottom2;
    @BindView(R.id.main_panel_2)
    FrameLayout mainPanel2;
    @BindView(R.id.tv_msg_3)
    TextView tvMsg3;
    @BindView(R.id.iv_bottom_3)
    ImageView ivBottom3;
    @BindView(R.id.tv_bottom_3)
    TextView tvBottom3;
    @BindView(R.id.main_panel_3)
    FrameLayout mainPanel3;

    @BindView(R.id.bottom_container)
    LinearLayout bottomContainer;
    @BindView(R.id.viewpager)
    NoScrollViewPager viewPager;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.tv_msg_4)
    TextView tvMsg4;
    @BindView(R.id.iv_bottom_4)
    ImageView ivBottom4;
    @BindView(R.id.tv_bottom_4)
    TextView tvBottom4;
    @BindView(R.id.main_panel_4)
    FrameLayout mainPanel4;
    private MainPagerAdapter adapter;
    private ImageView[] imageViews;
    private TextView[] textViews;
    private TextView[] msgViews;
    private int[] inactRes = {R.drawable.main_tab_1_1, R.drawable.main_tab_2_1, R.drawable.main_tab_3_1, R.drawable.main_tab_4_1};
    private int[] actRes = {R.drawable.main_tab_1_2, R.drawable.main_tab_2_2, R.drawable.main_tab_3_2, R.drawable.main_tab_4_2};
    private List<Fragment> list;
    static int micount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EventBus.getDefault().register(this);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        Utils.creatTempFolder();
        Utils.delFolder(Consts.SD_ROOT_OLD);
        hasFragment = true;
        imageViews = new ImageView[]{ivBottom1, ivBottom2, ivBottom3,ivBottom4};
        textViews = new TextView[]{tvBottom1, tvBottom2, tvBottom3,tvBottom4};
        msgViews = new TextView[]{tvMsg1, tvMsg2, tvMsg3,tvMsg4};
        list = new ArrayList<>();
        list.add(new SceneLiveFragment());
        list.add(HomeFragmentWrapper.newInstance(HomeFragment.class.getName()));
        list.add(ReviewFragmentWrapper.newInstance(ReviewFragment.class.getName()));
        list.add(new MeFragment());

        adapter = new MainPagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(list.size());
        viewPager.addOnPageChangeListener(this);
        viewPager.setNoScroll(true);
        viewPager.setCurrentItem(0);
        setUI(0);
        if (micount == 1) {
            new UpgradeManager(this,this, true);
            micount++;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean autoRefresh = intent.getBooleanExtra("auto_refresh", false);
        if (autoRefresh) {
            recreate();
            if (viewPager != null) {
                viewPager.setCurrentItem(0);
            }
        }
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setUI(position);
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setUI(int position) {
        int size = textViews.length;
        for (int i = 0; i < size; i++) {
            if (i == position) {
                textViews[i].setTextColor(getResources().getColor(R.color.main_bottombar_active));
                imageViews[i].setImageResource(actRes[i]);
                if (msgViews[i].isShown())
                    msgViews[i].setVisibility(View.GONE);
            } else {
                textViews[i].setTextColor(getResources().getColor(R.color.main_bottombar_inactive));
                imageViews[i].setImageResource(inactRes[i]);
            }
        }
    }

    @OnClick({R.id.main_panel_1, R.id.main_panel_2, R.id.main_panel_3,R.id.main_panel_4})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_panel_1:
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.main_panel_2:
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.main_panel_3:
                viewPager.setCurrentItem(2, false);
                break;
            case R.id.main_panel_4:
                viewPager.setCurrentItem(3,false);
                break;
        }
    }

    long lastTime;
    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_VISIBLE
//            );
//        } else getWindow().getDecorView().setSystemUiVisibility(0);
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportFragmentManager() != null) {
            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackEntryCount > 0) {
                //使viewpager定位在当前fragment栈
                FragmentManager.BackStackEntry backStack = getSupportFragmentManager().getBackStackEntryAt(backStackEntryCount - 1);
                String tag = backStack.getName();
                if (tag.equals(HomeFragment.class.getName())) {
                    viewPager.setCurrentItem(1, false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        StatusBarUtil.setStatusBarColor(this, R.color.white);
                        StatusBarUtil.StatusBarLightMode(this);
                    }
                } else if (tag.equals(ReviewFragment.class.getName())) {
                    viewPager.setCurrentItem(2, false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        StatusBarUtil.setStatusBarColor(this, R.color.white);
                        StatusBarUtil.StatusBarLightMode(this);
                    }
                }
                super.onBackPressed();
                return;
            }
        }

        lastTime = SPTool.getLong(this, "last_back_time", 0);
        long curTime = System.currentTimeMillis();
        if (curTime - lastTime > 2 * 1000) {
            ToastUtils.getInstance().showToast(this, "再按一次退出");
            SPTool.putLong(this, "last_back_time", curTime);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        micount = 1;
        //EventBus.getDefault().unregister(this);
    }

    @Override
    public void OnUpgradeResult(int iRetCode, int iUpgradeType) {
        if (UpgradeManager.UPGRADEMANAGER_UNCONNECTED == iUpgradeType) {
            return;
        }
        if (UpgradeManager.UPGRADEMANAGER_FORCEUPDATE == iUpgradeType
                || UpgradeManager.FINISH == iRetCode) {
            System.exit(0);
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(MessageEvent event) {
//        Log.e("ck", "获取" + event.index);
//        //从播放器过来，设置当前页面
//        int index = event.index;
//        if (index >= 0 && index < list.size()) {
//            viewPager.setCurrentItem(index);
//        }
//    }
}
