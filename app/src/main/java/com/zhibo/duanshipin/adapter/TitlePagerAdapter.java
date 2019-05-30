package com.zhibo.duanshipin.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.zhibo.duanshipin.fragment.HomeItemFragment;
import com.zhibo.duanshipin.utils.ULog;

import java.util.List;

/**
 * Created by Administrator on 2016/12/8.
 */

public class TitlePagerAdapter extends FragmentPagerAdapter {
    private List<String> titleList;
    private List<Integer> typeList;
    private List<Fragment> fragmentList;


    public TitlePagerAdapter(FragmentManager fm, List<String> titleList, List<Integer> typeList) {
        super(fm);
        this.titleList = titleList;
        this.typeList = typeList;
    }
    public TitlePagerAdapter(List<Fragment> fragmentList,FragmentManager fm,List<String> titleList){
        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }

    @Override
    public Fragment getItem(int position) {
        if(typeList!=null){
            return HomeItemFragment.newInstance(typeList.get(position));
        } else if (fragmentList != null) {
            return fragmentList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return titleList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
