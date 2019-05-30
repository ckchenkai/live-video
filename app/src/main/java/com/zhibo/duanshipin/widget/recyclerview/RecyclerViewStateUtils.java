package com.zhibo.duanshipin.widget.recyclerview;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 分页展示数据时，RecyclerView的FooterView State 操作工具类
 * RecyclerView一共有几种State：Normal/Loading/Error/TheEnd
 * @author lizhixian
 * @time 16/9/10 09:56
 */
public class RecyclerViewStateUtils {
    /**
     * 设置LRecyclerViewAdapter的FooterView State
     * @param instance        context
     * @param recyclerView    recyclerView
     * @param state           FooterView State
     * @param showFooterview  是否显示footerview
     * @param errorListener   FooterView处于Error状态时的点击事件
     */
    public static void setFooterViewState(Activity instance, RecyclerView recyclerView, RecyclerViewFooter.State state, boolean showFooterview,View.OnClickListener errorListener) {
        if(instance==null || instance.isFinishing()) {
            return;
        }
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter == null || !(outerAdapter instanceof LRecyclerViewAdapter)) {
            return;
        }
        LRecyclerViewAdapter lRecyclerViewAdapter = (LRecyclerViewAdapter) outerAdapter;

        //只有一页的时候，就别加什么FooterView了
       /* if (lRecyclerViewAdapter.getInnerAdapter().getItemCount() < pageSize) {
            return;
        }*/
        RecyclerViewFooter footerView;
        //已经有footerView了
        if (lRecyclerViewAdapter.getFooterViewsCount() > 0) {
            footerView = (RecyclerViewFooter)lRecyclerViewAdapter.getFooterView();
            footerView.setState(state);
            footerView.setVisibility(showFooterview?View.VISIBLE:View.INVISIBLE);

            if (state == RecyclerViewFooter.State.NetWorkError) {
                if(errorListener!=null)
                    footerView.setOnClickListener(errorListener);
            } else if (state == RecyclerViewFooter.State.TheEnd){
                ((LRecyclerView)recyclerView).setNoMore(true);
            }

        }
        //recyclerView.scrollToPosition(lRecyclerViewAdapter.getItemCount() - 1);
    }

    /**
     *设置LRecyclerViewAdapter的FooterView State
     * @param instance        context
     * @param recyclerView    recyclerView
     * @param state           FooterView State
     * @param errorListener   FooterView处于Error状态时的点击事件
     */
    public static void setFooterViewState(Activity instance, RecyclerView recyclerView, RecyclerViewFooter.State state,View.OnClickListener errorListener) {
        if(instance==null || instance.isFinishing()) {
            return;
        }
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter == null || !(outerAdapter instanceof LRecyclerViewAdapter)) {
            return;
        }
        LRecyclerViewAdapter lRecyclerViewAdapter = (LRecyclerViewAdapter) outerAdapter;

        //只有一页的时候，就别加什么FooterView了
       /* if (lRecyclerViewAdapter.getInnerAdapter().getItemCount() < pageSize) {
            return;
        }*/
        RecyclerViewFooter footerView;
        //已经有footerView了
        if (lRecyclerViewAdapter.getFooterViewsCount() > 0) {
            footerView = (RecyclerViewFooter)lRecyclerViewAdapter.getFooterView();
            footerView.setState(state);
            footerView.setVisibility(View.VISIBLE);

            if (state == RecyclerViewFooter.State.NetWorkError) {
                if(errorListener!=null)
                    footerView.setOnClickListener(errorListener);
            } else if (state == RecyclerViewFooter.State.TheEnd){
                ((LRecyclerView)recyclerView).setNoMore(true);
            }

        }
        //recyclerView.scrollToPosition(lRecyclerViewAdapter.getItemCount() - 1);
    }


    /**
     * 获取当前RecyclerView.FooterView的状态
     *
     * @param recyclerView
     */
    public static RecyclerViewFooter.State getFooterViewState(RecyclerView recyclerView) {

        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof LRecyclerViewAdapter) {
            if (((LRecyclerViewAdapter) outerAdapter).getFooterViewsCount() > 0) {
                RecyclerViewFooter footerView = (RecyclerViewFooter) ((LRecyclerViewAdapter) outerAdapter).getFooterView();
                return footerView.getState();
            }
        }

        return RecyclerViewFooter.State.Normal;
    }

    /**
     * 设置当前RecyclerView.FooterView的状态
     *
     * @param recyclerView
     * @param state
     */
   /* public static void setFooterViewState(RecyclerView recyclerView, RecyclerViewFooter.State state) {
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof LRecyclerViewAdapter) {
            if (((LRecyclerViewAdapter) outerAdapter).getFooterViewsCount() > 0) {
                RecyclerViewFooter footerView = (RecyclerViewFooter) ((LRecyclerViewAdapter) outerAdapter).getFooterView();
                footerView.setState(state);
            }
        }
    }*/
}
