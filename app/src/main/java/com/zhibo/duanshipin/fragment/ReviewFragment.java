package com.zhibo.duanshipin.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.zhibo.duanshipin.AR.ARVideoActivity;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.activity.SearchActivity;
import com.zhibo.duanshipin.adapter.TitlePagerAdapter;
import com.zhibo.duanshipin.base.BaseLazyFragment;
import com.zhibo.duanshipin.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 2017/8/29.
 */

public class ReviewFragment extends BaseLazyFragment{

    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tablayout)
    SlidingTabLayout tabLayout;
    @BindView(R.id.viewpager)
    NoScrollViewPager viewPager;
    private FragmentActivity mActivity;
    private TitlePagerAdapter pagerAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_review;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initViews(View view) {
        List<String> titleList = new ArrayList<>();
        titleList.add("首页");
        titleList.add("各区");
        titleList.add("部门");
        titleList.add("企业");
        titleList.add("都市圈");
        List<Fragment> fragmentList = new ArrayList<>();
        for(int i=0;i<5;i++){
            fragmentList.add(ReviewItemFragment.newInstance(i));
        }
        pagerAdapter = new TitlePagerAdapter(fragmentList,getChildFragmentManager(), titleList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(fragmentList.size());
        tabLayout.setViewPager(viewPager);
    }

    @Override
    protected void initToolbar() {
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    protected long setLoadInterval() {
        return 0;
    }

    @OnClick({R.id.iv_ar,R.id.iv_search})
    void onClick(View view){
        Intent intent = null;
        switch (view.getId()){
            case R.id.iv_ar:
                intent = new Intent(getActivity(),ARVideoActivity.class);
                break;
            case R.id.iv_search:
                intent = new Intent(getActivity(), SearchActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
