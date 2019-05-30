package com.zhibo.duanshipin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.bean.ReviewItemBean;

import java.util.List;

/**
 * Created by CK on 2018/2/5.
 * Email:910663958@qq.com
 */

public class ReviewTabAdapter extends BaseAdapter {
    private Context context;
    private List<ReviewItemBean.DistrictBean> tabDistrictList;
    private List<ReviewItemBean.DepartmentBean> tabDepartmentList;
    private int selPosition = -1;

    public ReviewTabAdapter(Context context,List<ReviewItemBean.DistrictBean> tabDistrictList,List<ReviewItemBean.DepartmentBean> tabDepartmentList) {
        this.context = context;
        this.tabDistrictList = tabDistrictList;
        this.tabDepartmentList = tabDepartmentList;
    }

    @Override
    public int getCount() {
        if (tabDistrictList != null) {
            return tabDistrictList.size();
        }
        if (tabDepartmentList != null) {
            return tabDepartmentList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (tabDistrictList != null) {
            return tabDistrictList.get(i);
        }
        if (tabDepartmentList != null) {
            return tabDepartmentList.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (tabDistrictList != null) {
            if (i < 0 || i > tabDistrictList.size() - 1) {
                return null;
            }
        }
        if (tabDepartmentList != null) {
            if (i < 0 || i > tabDepartmentList.size() - 1) {
                return null;
            }
        }

        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_review_tab,viewGroup,false);
            holder.textView = (TextView) view.findViewById(R.id.text_view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (tabDistrictList != null) {
            holder.textView.setText(tabDistrictList.get(i).getName()+"");
        } else if (tabDepartmentList != null) {
            holder.textView.setText(tabDepartmentList.get(i).getName()+"");
        }
        if (i == selPosition) {
            holder.textView.setTextColor(Color.parseColor("#fd6f23"));
        }else{
            holder.textView.setTextColor(Color.parseColor("#999999"));
        }
        return view;
    }

    private class ViewHolder {
        TextView textView;
    }

    /**
     * 设定选中
     * @param position
     */
    public void setSelected(int position){
        this.selPosition = position;
        notifyDataSetChanged();
    }

    /**
     * 获取选中的项
     * @return
     */
    public int getSelected(){
        return selPosition;
    }
}
