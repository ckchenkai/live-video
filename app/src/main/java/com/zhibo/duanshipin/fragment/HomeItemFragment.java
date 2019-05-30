package com.zhibo.duanshipin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.activity.WebViewActivity;
import com.zhibo.duanshipin.adapter.HomeItemAdapter;
import com.zhibo.duanshipin.base.BaseLazyFragment;
import com.zhibo.duanshipin.bean.HomeItemCaptionBean;
import com.zhibo.duanshipin.bean.HomeItemCityHorizonBean;
import com.zhibo.duanshipin.bean.HomeItemFineVideoBean;
import com.zhibo.duanshipin.bean.HomeItemLiveBean;
import com.zhibo.duanshipin.httprequest.HttpRequestPresenter;
import com.zhibo.duanshipin.httprequest.HttpRequestView;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.StatusBarUtil;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;
import com.zhibo.duanshipin.utils.UpgradeManager;
import com.zhibo.duanshipin.widget.recyclerview.LRecyclerView;
import com.zhibo.duanshipin.widget.recyclerview.LRecyclerViewAdapter;
import com.zhibo.duanshipin.widget.recyclerview.RecyclerViewFooter;
import com.zhibo.duanshipin.widget.recyclerview.RecyclerViewStateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Created by admin on 2017/8/30.
 */

public class HomeItemFragment extends BaseLazyFragment implements LRecyclerView.OnRefreshListener, LRecyclerView.OnLoadMoreListener, HttpRequestView,
        LRecyclerViewAdapter.OnItemClickListener {
    LRecyclerView recyclerView;
    @BindView(R.id.tv_none)
    TextView tvNone;
    static int micount = 1;
    @BindView(R.id.img_up)
    ImageView imgUp;
    private boolean isRefreshing;
    private boolean isLoadingMore;
    private HttpRequestPresenter requestPresenter;
    private boolean loading = true;
    //直播
    private List<HomeItemLiveBean.ListsBean> mLiveList = new ArrayList<>();
    //短视频
    private List<HomeItemCityHorizonBean.ListsBean> mCityHorizonList = new ArrayList<>();
    //图文
    private List<HomeItemCaptionBean.ListsBean> mCaptionList = new ArrayList<>();
    //精视频
    private List<HomeItemFineVideoBean.ListsBean> mFineVideoList = new ArrayList<>();
    private HomeItemAdapter adapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private String url = "http://newlive.longhoo.net/index.php?g=portal&m=video&a=vlistscom&page=";


    public static final int TYPE_LIVE = 0;     //直播
    public static final int TYPE_CITY_HORIZON = 1;  //城视
    public static final int TYPE_CAPTION = 2;     //2图文
    public static final int TYPE_FINE_VIDEO = 3; //精视频
    private int type = TYPE_LIVE;
    private int mPage = 1;
    private Animation showAnim, hideAnim;
    private FragmentActivity mActivity;


    public static HomeItemFragment newInstance(int type) {
        HomeItemFragment fragment = new HomeItemFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_home_item;
    }

    @Override
    protected void onLazyLoad() {
        recyclerView.setRefreshing(true);
    }

    @Override
    protected void initViews(View view) {

        if (getArguments() != null) {
            type = getArguments().getInt("type");
            //ULog.e("mytype", type + "");
        }
        if (type == TYPE_LIVE) {
            //直播
            url = "http://newlive.longhoo.net/index.php?g=portal&m=video&a=vlistscom";
            adapter = new HomeItemAdapter(mActivity,type,mLiveList);
        } else if (type == TYPE_CITY_HORIZON) {
            //城视
            url = "http://outdata.longhoo.net/?m=home&c=app&a=video";
            adapter = new HomeItemAdapter(mActivity,type,mCityHorizonList);
        } else if (type == TYPE_CAPTION) {
            //图说
            url = "http://outdata.longhoo.net/?m=home&c=app&a=cmslist&catid=401";
            adapter = new HomeItemAdapter(mActivity,type,mCaptionList);
        }else if (type == TYPE_FINE_VIDEO) {
            //精视频
            url = "http://outdata.longhoo.net/?m=home&c=app&a=refined_video";
            adapter = new HomeItemAdapter(mActivity,type,mFineVideoList);
        }


        recyclerView = (LRecyclerView) view.findViewById(R.id.recycler_view);
        requestPresenter = new HttpRequestPresenter(getActivity(), this);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        recyclerView.setAdapter(lRecyclerViewAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setOnLoadMoreListener(this);
        recyclerView.setOnRefreshListener(this);

        showAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_0_to_1);
        hideAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_1_to_0);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int mScrollThreshold;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isSignificantDelta = Math.abs(dy) > mScrollThreshold;
                if (isSignificantDelta) {
                    if (dy > 0) {
                        if (imgUp.isShown()) {
                            imgUp.startAnimation(hideAnim);
                            imgUp.setVisibility(View.GONE);
                        }
                    } else {
                        if (!imgUp.isShown()) {
                            imgUp.startAnimation(showAnim);
                            imgUp.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            public void setScrollThreshold(int scrollThreshold) {
                mScrollThreshold = scrollThreshold;
            }
        });


        lRecyclerViewAdapter.setOnItemClickListener(this);
        imgUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
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
    }

    @Override
    public void onRefreshSuccess(String response) {
        if (!isPrepared) {
            return;
        }
        ULog.e("ck", "home:" + response);
        isRefreshing = false;
        recyclerView.refreshComplete();
        List<HomeItemLiveBean.ListsBean> tempLiveList = null;
        List<HomeItemCaptionBean.ListsBean> tempCaptionList = null;
        List<HomeItemCityHorizonBean.ListsBean> tempCityHorizonList = null;
        List<HomeItemFineVideoBean.ListsBean> tempFineVideoList = null;

        Gson gson = new Gson();
        String code;
        try {
            switch (type) {
                case TYPE_LIVE:
                    HomeItemLiveBean homeItemLiveBean = gson.fromJson(response, HomeItemLiveBean.class);
                    code = homeItemLiveBean.getStatus() + "";
                    if (!TextUtils.equals(code, "0")) {
                        ToastUtils.getInstance().showToast(getActivity(), "刷新失败~");
                        return;
                    }
                    tempLiveList = homeItemLiveBean.getLists();
                    if (tempLiveList == null) {
                        ToastUtils.getInstance().showToast(getActivity(), "刷新失败~");
                        return;
                    }
                    if (tempLiveList.size() > 0) {
                        mLiveList.clear();
                        mLiveList.addAll(tempLiveList);
                        mPage++;
                        lRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        tvNone.setVisibility(View.VISIBLE);
                        tvNone.setText("无更多数据");
                        recyclerView.setVisibility(View.GONE);
                    }
                    break;
                case TYPE_CITY_HORIZON:
                    HomeItemCityHorizonBean cityHorizonBean = gson.fromJson(response, HomeItemCityHorizonBean.class);
                    code = cityHorizonBean.getStatus() + "";
                    if (!TextUtils.equals(code, "0")) {
                        ToastUtils.getInstance().showToast(getActivity(), "刷新失败~");
                        return;
                    }
                    tempCityHorizonList = cityHorizonBean.getLists();
                    if (tempCityHorizonList == null) {
                        ToastUtils.getInstance().showToast(getActivity(), "刷新失败~");
                        return;
                    }
                    if (tempCityHorizonList.size() > 0) {
                        mCityHorizonList.clear();
                        mCityHorizonList.addAll(tempCityHorizonList);
                        mPage++;
                        lRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        tvNone.setVisibility(View.VISIBLE);
                        tvNone.setText("无更多数据");
                        recyclerView.setVisibility(View.GONE);
                    }
                    break;
                case TYPE_CAPTION:
                    HomeItemCaptionBean captionBean = gson.fromJson(response, HomeItemCaptionBean.class);
                    code = captionBean.getStatus() + "";
                    if (!TextUtils.equals(code, "0")) {
                        ToastUtils.getInstance().showToast(getActivity(), "刷新失败~");
                        return;
                    }
                    tempCaptionList = captionBean.getLists();
                    if (tempCaptionList == null) {
                        ToastUtils.getInstance().showToast(getActivity(), "刷新失败~");
                        return;
                    }
                    if (tempCaptionList.size() > 0) {
                        mCaptionList.clear();
                        mCaptionList.addAll(tempCaptionList);
                        mPage++;
                        lRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        tvNone.setVisibility(View.VISIBLE);
                        tvNone.setText("无更多数据");
                        recyclerView.setVisibility(View.GONE);
                    }
                    break;
                case TYPE_FINE_VIDEO:
                    HomeItemFineVideoBean fineVideoBean  = gson.fromJson(response, HomeItemFineVideoBean.class);
                    code = fineVideoBean.getStatus() + "";
                    if (!TextUtils.equals(code, "0")) {
                        ToastUtils.getInstance().showToast(getActivity(), "刷新失败~");
                        return;
                    }
                    tempFineVideoList = fineVideoBean.getLists();
                    if (tempFineVideoList == null) {
                        ToastUtils.getInstance().showToast(getActivity(), "刷新失败~");
                        return;
                    }
                    if (tempFineVideoList.size() > 0) {
                        mFineVideoList.clear();
                        mFineVideoList.addAll(tempFineVideoList);
                        mPage++;
                        lRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        tvNone.setVisibility(View.VISIBLE);
                        tvNone.setText("无更多数据");
                        recyclerView.setVisibility(View.GONE);
                    }
                    break;

            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            ToastUtils.getInstance().showToast(getActivity(), "服务器异常~");
        }

    }

    @Override
    public void onRefreshError() {
        if (!isPrepared) {
            return;
        }
        isRefreshing = false;
        recyclerView.refreshComplete();
        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.Normal, null);
        ToastUtils.getInstance().showToast(getActivity(), "刷新失败~");
    }

    @Override
    public void onLoadMoreSuccess(String response) {
        if (!isPrepared) {
            return;
        }
        ULog.e("ck","home:"+mPage+" "+response);
        isLoadingMore = false;
        //处理数据
        List<HomeItemLiveBean.ListsBean> tempLiveList = null;
        List<HomeItemCaptionBean.ListsBean> tempCaptionList = null;
        List<HomeItemCityHorizonBean.ListsBean> tempCityHorizonList = null;
        List<HomeItemFineVideoBean.ListsBean> tempFineVideoList = null;
        Gson gson = new Gson();
        String code;
        try {
            switch (type) {
                case TYPE_LIVE:
                    HomeItemLiveBean homeItemLiveBean = gson.fromJson(response, HomeItemLiveBean.class);
                    code = homeItemLiveBean.getStatus() + "";
                    if (!TextUtils.equals(code, "0")) {
                        ToastUtils.getInstance().showToast(getActivity(), "获取数据失败~");
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
                        return;
                    }
                    tempLiveList = homeItemLiveBean.getLists();
                    if (tempLiveList == null) {
                        ToastUtils.getInstance().showToast(getActivity(), "获取数据失败~");
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
                        return;
                    }
                    //没有数据或数据<limit，说明无更多
                    if (tempLiveList.size() <= 0) {
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.TheEnd, null);
                    } else {
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.Normal, null);
                        mLiveList.addAll(tempLiveList);
                        mPage++;
                        lRecyclerViewAdapter.notifyDataSetChanged();
                    }
                    break;
                case TYPE_CITY_HORIZON:
                    HomeItemCityHorizonBean cityHorizonBean = gson.fromJson(response, HomeItemCityHorizonBean.class);
                    code = cityHorizonBean.getStatus() + "";
                    if (!TextUtils.equals(code, "0")) {
                        ToastUtils.getInstance().showToast(getActivity(), "获取数据失败~");
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
                        return;
                    }
                    tempCityHorizonList = cityHorizonBean.getLists();
                    if (tempCityHorizonList == null) {
                        ToastUtils.getInstance().showToast(getActivity(), "获取数据失败~");
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
                        return;
                    }
                    //没有数据或数据<limit，说明无更多
                    if (tempCityHorizonList.size() <= 0) {
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.TheEnd, null);
                    } else {
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.Normal, null);
                        mCityHorizonList.addAll(tempCityHorizonList);
                        mPage++;
                        lRecyclerViewAdapter.notifyDataSetChanged();
                    }
                    break;
                case TYPE_CAPTION:
                    HomeItemCaptionBean captionBean = gson.fromJson(response, HomeItemCaptionBean.class);
                    code = captionBean.getStatus() + "";
                    if (!TextUtils.equals(code, "0")) {
                        ToastUtils.getInstance().showToast(getActivity(), "获取数据失败~");
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
                        return;
                    }
                    tempCaptionList = captionBean.getLists();
                    if (tempCaptionList == null) {
                        ToastUtils.getInstance().showToast(getActivity(), "获取数据失败~");
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
                        return;
                    }
                    //没有数据或数据<limit，说明无更多
                    if (tempCaptionList.size() <= 0) {
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.TheEnd, null);
                    } else {
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.Normal, null);
                        mCaptionList.addAll(tempCaptionList);
                        mPage++;
                        lRecyclerViewAdapter.notifyDataSetChanged();
                    }
                    break;
                case TYPE_FINE_VIDEO:
                    HomeItemFineVideoBean fineVideoBean = gson.fromJson(response, HomeItemFineVideoBean.class);
                    code = fineVideoBean.getStatus() + "";
                    if (!TextUtils.equals(code, "0")) {
                        ToastUtils.getInstance().showToast(getActivity(), "获取数据失败~");
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
                        return;
                    }
                    tempFineVideoList = fineVideoBean.getLists();
                    if (tempFineVideoList == null) {
                        ToastUtils.getInstance().showToast(getActivity(), "获取数据失败~");
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
                        return;
                    }
                    //没有数据或数据<limit，说明无更多
                    if (tempFineVideoList.size() <= 0) {
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.TheEnd, null);
                    } else {
                        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.Normal, null);
                        mFineVideoList.addAll(tempFineVideoList);
                        mPage++;
                        lRecyclerViewAdapter.notifyDataSetChanged();
                    }
                    break;

            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            ToastUtils.getInstance().showToast(getActivity(), "服务器异常~");
            RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
        }
    }

    @Override
    public void onLoadMoreError() {
        if (!isPrepared) {
            return;
        }
        isLoadingMore = false;
        RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
        ToastUtils.getInstance().showToast(getActivity(), "获取更多数据失败~");
    }

    @Override
    public void onRefresh() {
        if (isLoadingMore) {
            ToastUtils.getInstance().showToast(getActivity(), "正在加载中，请稍后...");
            return;
        }
        isRefreshing = true;
        mPage = 1;
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(mPage));
        params.put("size","10");
        requestPresenter.doHttpData(url, Consts.REQUEST_METHOD_POST, Consts.REQUEST_REFRESH, params);
    }

    @Override
    public void onLoadMore() {
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
        params.put("size","10");
        requestPresenter.doHttpData(url, Consts.REQUEST_METHOD_POST, Consts.REQUEST_LOADMORE, params);
    }

    /**
     * 重新加载点击
     */
    private View.OnClickListener mFooterClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onLoadMore();
        }
    };


    @Override
    public void onItemClick(View view, int position) {
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.alpha_0_to_1, R.anim.alpha_1_to_0, R.anim.alpha_0_to_1, R.anim.alpha_1_to_0);
        transaction.addToBackStack(HomeFragment.class.getName());

        if (type == TYPE_LIVE) {
            //直播
            String url = mLiveList.get(position).getZburl();
            String thumb = mLiveList.get(position).getMthumb();
            if(TextUtils.isEmpty(thumb)){
                thumb = mLiveList.get(position).getPhonethumb();
            }
            String termId = mLiveList.get(position).getTerm_id();
            String description = mLiveList.get(position).getDescription();
            String termType = mLiveList.get(position).getTerm_type();
            String shareUrl = mLiveList.get(position).getShare_url();
            String title = mLiveList.get(position).getName();
            String isStart = mLiveList.get(position).getIstart();
            String isEnd = mLiveList.get(position).getIsend();
            ULog.e("ck--",url);
            //status:1表示正在直播；2表示未开始；3表示已结束
            if(TextUtils.equals(termType,"0")){  //图文直播
                //VideoPlayActivity.startVideoPlay(getActivity(), url, thumb, termId, description,true, true, false,shareUrl,title);
                transaction.replace(R.id.home_wrapper_container,VideoPlayFragment.newInstance(url, thumb, termId, description,true, true, false,shareUrl,title));
            }else if(TextUtils.equals(termType,"1")){                               //视频直播
                if(TextUtils.equals(isStart,"0")){
                    transaction.replace(R.id.home_wrapper_container,VideoPlayFragment.newInstance(url, thumb, termId, description, true,true, false,shareUrl,title));
                }else{
                    if(TextUtils.equals(isEnd,"0")){
                        transaction.replace(R.id.home_wrapper_container,VideoPlayFragment.newInstance(url, thumb, termId, description, false,true, false,shareUrl,title));
                    }else if(TextUtils.equals(isEnd,"1")){
                        transaction.replace(R.id.home_wrapper_container,VideoPlayFragment.newInstance(url, thumb, termId, description, false,false, true,shareUrl,title));
                    }
                }
            }
            transaction.commit();
            StatusBarUtil.setStatusBarColor(getActivity(),R.color.black);
            StatusBarUtil.StatusBarDarkMode(getActivity());
        } else if (type == TYPE_CITY_HORIZON) {
            //短视频
            String url = mCityHorizonList.get(position).getVodurl();
            String thumb = mCityHorizonList.get(position).getThumb();
            String description = mCityHorizonList.get(position).getDescription();
            String title = mCityHorizonList.get(position).getTitle();
            //VideoPlayActivity.startVideoPlay(getActivity(), url, thumb, null, description,false, false, false,null,title);
            transaction.replace(R.id.home_wrapper_container,VideoPlayFragment.newInstance(url, thumb, null, description,false, false, false,null,title));
            transaction.commit();
            StatusBarUtil.setStatusBarColor(getActivity(),R.color.black);
            StatusBarUtil.StatusBarDarkMode(getActivity());
        } else if (type == TYPE_CAPTION) {
            //图说
            String url = mCaptionList.get(position).getUrl();
            String title = mCaptionList.get(position).getTitle();
            WebViewActivity.goToWebView(getActivity(), url, title, false);
        }else if (type == TYPE_FINE_VIDEO) {
            //精视频
            String url = mFineVideoList.get(position).getVodurl();
            String thumb = mFineVideoList.get(position).getThumb();
            String description = mFineVideoList.get(position).getDescription();
            String title = mFineVideoList.get(position).getTitle();
            transaction.replace(R.id.home_wrapper_container,VideoPlayFragment.newInstance(url, thumb, null, description,false, false, false,null,title));
            transaction.commit();
            StatusBarUtil.setStatusBarColor(getActivity(),R.color.black);
            StatusBarUtil.StatusBarDarkMode(getActivity());
        }
    }
}
