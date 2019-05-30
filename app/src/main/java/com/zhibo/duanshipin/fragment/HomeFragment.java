package com.zhibo.duanshipin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.zhibo.duanshipin.AR.ARVideoActivity;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.activity.SearchActivity;
import com.zhibo.duanshipin.adapter.TitlePagerAdapter;
import com.zhibo.duanshipin.base.BaseLazyFragment;
import com.zhibo.duanshipin.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by admin on 2017/8/29.
 */

public class HomeFragment extends BaseLazyFragment {
    @BindView(R.id.tablayout)
    SlidingTabLayout tablayout;
    @BindView(R.id.viewpager)
    NoScrollViewPager viewpager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fl_search_box)
    FrameLayout flSearchBox;
    private TitlePagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initViews(View view) {
//        ImmersionBar.setTitleBar(getActivity(),tablayout );
////        setHasOptionsMenu(true);
        tablayout.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        //

        List<String> titleList = new ArrayList<>();
        titleList.add("直播");
        titleList.add("城视");
        titleList.add("图说");
        titleList.add("精视频");

        List<Integer> typeList = new ArrayList<>();
        typeList.add(0);
        typeList.add(1);
        typeList.add(2);
        typeList.add(3);

        pagerAdapter = new TitlePagerAdapter(getChildFragmentManager(), titleList, typeList);
        viewpager.setAdapter(pagerAdapter);
        viewpager.setOffscreenPageLimit(typeList.size());
        tablayout.setViewPager(viewpager);
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

    @OnClick({R.id.iv_ar,R.id.fl_search_box})
    void onClick(View view){
        Intent intent = null;
        switch (view.getId()){
            case R.id.iv_ar:
                intent = new Intent(getActivity(),ARVideoActivity.class);
                break;
            case R.id.fl_search_box:
                intent = new Intent(getActivity(), SearchActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

}
