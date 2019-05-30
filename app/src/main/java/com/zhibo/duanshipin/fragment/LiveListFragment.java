package com.zhibo.duanshipin.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.adapter.LiveListAdapter;
import com.zhibo.duanshipin.base.BaseLazyFragment;
import com.zhibo.duanshipin.bean.LiveListBean;
import com.zhibo.duanshipin.httprequest.HttpRequestPresenter;
import com.zhibo.duanshipin.httprequest.HttpRequestView;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;
import com.zhibo.duanshipin.widget.recyclerview.RecyclerViewFooter;
import com.zhibo.duanshipin.widget.recyclerview.RecyclerViewStateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;


public class LiveListFragment extends BaseLazyFragment implements HttpRequestView, LiveListAdapter.OnGridItemClickListener,OnRefreshLoadmoreListener{
    private static final String TERM_ID = "term_id";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private String mTermId;
    private int mPage = 1;
    private HttpRequestPresenter requestPresenter;
    private String url = "http://newlive.longhoo.net/index.php?g=portal&m=video&a=picinfo";
    private LiveListAdapter adapter;
    private List<LiveListBean.ListsBean> dataList = new ArrayList<>();
    //private LRecyclerViewAdapter lRecyclerViewAdapter;
    private boolean isRefreshing;
    private boolean isLoadingMore;
    private LinearLayoutManager manager;
    boolean mFull = false;

    public static LiveListFragment newInstance(String mTermId) {
        LiveListFragment fragment = new LiveListFragment();
        Bundle args = new Bundle();
        args.putString(TERM_ID, mTermId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTermId = getArguments().getString(TERM_ID);
        }
    }


    @Override
    protected int getContentId() {
        return R.layout.fragment_live_list;
    }

    @Override
    protected void onLazyLoad() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //recyclerView.setRefreshing(true);
                if(recyclerView!=null&&!getActivity().isFinishing()){
                    refreshLayout.finishRefresh();
                    onRefresh(refreshLayout);
                }
            }
        }, 200);
    }

    @Override
    protected void initViews(View view) {
        requestPresenter = new HttpRequestPresenter(getActivity(), this);
        adapter = new LiveListAdapter(getActivity(), dataList);
        adapter.setOnGridItemClickListener(this);
//        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
//        lRecyclerViewAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        manager = new LinearLayoutManager(this.getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
//        recyclerView.setPullRefreshEnabled(true);
//        recyclerView.setOnRefreshListener(this);
//        recyclerView.setOnLoadMoreListener(this);
        refreshLayout.setOnRefreshLoadmoreListener(this);
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            int firstVisibleItem, lastVisibleItem;
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                firstVisibleItem = manager.findFirstVisibleItemPosition();
//                lastVisibleItem = manager.findLastVisibleItemPosition();
//                //大于0说明有播放
//                if (GSYVideoManager.instance().getPlayPosition() >= 0) {
//                    //当前播放的位置
//                    int position = GSYVideoManager.instance().getPlayPosition();
//                    //对应的播放列表TAG
//                    if (GSYVideoManager.instance().getPlayTag().equals(LiveListAdapter.TAG)
//                            && (position < firstVisibleItem || position > lastVisibleItem)) {
//                        //如果滑出去了上面和下面就是否，和今日头条一样
//                        if(!mFull) {
//                            GSYVideoPlayer.releaseAllVideos();
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            }
//        });
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                JZVideoPlayer jzvd = (JZVideoPlayer) view.findViewById(R.id.video_item_player);
                if (jzvd != null && JZUtils.dataSourceObjectsContainsUri(jzvd.dataSourceObjects, JZMediaManager.getCurrentDataSource())) {
                    JZVideoPlayer.releaseAllVideos();
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
    public void onNetworkError() {
        ToastUtils.getInstance().showToast(getActivity(), "网络异常！");
    }

    @Override
    public void onRefreshSuccess(String response) {
        if (!isPrepared) {
            return;
        }
        ULog.e(Consts.TAG, "播放图文:" + response);
        isRefreshing = false;
        //recyclerView.refreshComplete();
        refreshLayout.finishRefresh();
        //处理数据
        List<LiveListBean.ListsBean> tempList = null;
        Gson gson = new Gson();
        try {
            LiveListBean liveListBean = gson.fromJson(response, LiveListBean.class);
            int code = liveListBean.getStatus();
            if (code != 0) {
                ToastUtils.getInstance().showToast(getActivity(), "获取数据失败~");
                return;
            }
            tempList = liveListBean.getLists();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        if (tempList == null) {
            ToastUtils.getInstance().showToast(getActivity(), "服务器异常~");
            return;
        }
        dataList.clear();
        if (tempList.size() > 0) {
            dataList.addAll(tempList);
            mPage++;
        }
        adapter.notifyDataSetChanged();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(recyclerView!=null&&!getActivity().isFinishing()){
                    recyclerView.scrollToPosition(0);
                }
            }
        },500);
    }

    @Override
    public void onRefreshError() {
        if (!isPrepared) {
            return;
        }
        isRefreshing = false;
        //recyclerView.refreshComplete();
        refreshLayout.finishRefresh();
        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.Normal, null);
        ToastUtils.getInstance().showToast(getActivity(), "刷新失败~");
    }

    @Override
    public void onLoadMoreSuccess(String response) {
        if (!isPrepared) {
            return;
        }
        ULog.e(Consts.TAG, "播放图文:" + mPage + ":" + response);
        isLoadingMore = false;
        //RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.Normal, null);
        refreshLayout.finishLoadmore();
        //处理数据
        List<LiveListBean.ListsBean> tempList = null;
        Gson gson = new Gson();
        try {
            LiveListBean liveListBean = gson.fromJson(response, LiveListBean.class);
            int code = liveListBean.getStatus();
            if (code != 0) {
                ToastUtils.getInstance().showToast(getActivity(), "获取数据失败~");
                return;
            }
            tempList = liveListBean.getLists();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        if (tempList == null) {
            ToastUtils.getInstance().showToast(getActivity(), "解析错误！");
            return;
        }
        //没有数据或数据<limit，说明无更多
        if (tempList.size() <= 0) {
            RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.TheEnd, null);
        } else {
            dataList.addAll(tempList);
            mPage++;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadMoreError() {
        if (!isPrepared) {
            return;
        }
        isLoadingMore = false;
        //RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
        ToastUtils.getInstance().showToast(getActivity(), "获取更多数据失败~");
    }



    @Override
    public void onGridItemClick(int listPosition, int gridPosition) {
        if (listPosition >= 0 && listPosition <= dataList.size() - 1) {
            List<LocalMedia> photoList = new ArrayList<>();
            for (String photo : dataList.get(listPosition).getPhotos()) {
                photoList.add(new LocalMedia(photo, 0, PictureMimeType.ofImage(), ""));
            }
            PictureSelector.create(this).externalPicturePreview(gridPosition, photoList);
        }
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        if (isRefreshing) {
            ToastUtils.getInstance().showToast(getActivity(), "正在刷新中，请稍后...");
            return;
        }
        isLoadingMore = true;
        RecyclerViewFooter.State state = RecyclerViewStateUtils.getFooterViewState(recyclerView);
        if (state == RecyclerViewFooter.State.Loading) {
            return;
        }
        //开始加载
        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.Loading, null);
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(mPage));
        params.put("size", "10");
        params.put("term_id", mTermId);
        requestPresenter.doHttpData(url, Consts.REQUEST_METHOD_POST, Consts.REQUEST_LOADMORE, params);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (isLoadingMore) {
            ToastUtils.getInstance().showToast(getActivity(), "正在加载中，请稍后...");
            return;
        }
        isRefreshing = true;
        mPage = 1;
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(mPage));
        params.put("size", "10");
        params.put("term_id", mTermId);
        requestPresenter.doHttpData(url, Consts.REQUEST_METHOD_POST, Consts.REQUEST_REFRESH, params);
    }

    public void refreshData(String mTermId){
        if(refreshLayout!=null&&!getActivity().isFinishing()){
            this.mTermId = mTermId;
            onRefresh(refreshLayout);
        }
    }
}
