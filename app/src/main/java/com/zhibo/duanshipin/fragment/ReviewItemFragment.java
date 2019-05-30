package com.zhibo.duanshipin.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.adapter.GridViewAdapter;
import com.zhibo.duanshipin.adapter.ReviewItemAdapter;
import com.zhibo.duanshipin.adapter.ReviewTabAdapter;
import com.zhibo.duanshipin.base.BaseLazyFragment;
import com.zhibo.duanshipin.bean.ReviewItemBean;
import com.zhibo.duanshipin.httprequest.HttpRequestPresenter;
import com.zhibo.duanshipin.httprequest.HttpRequestView;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.StatusBarUtil;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;
import com.zhibo.duanshipin.widget.CustomGridView;
import com.zhibo.duanshipin.widget.recyclerview.LRecyclerView;
import com.zhibo.duanshipin.widget.recyclerview.LRecyclerViewAdapter;
import com.zhibo.duanshipin.widget.recyclerview.RecyclerViewFooter;
import com.zhibo.duanshipin.widget.recyclerview.RecyclerViewStateUtils;
import com.zhibo.duanshipin.widget.recyclerview.RefreshHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by ck on 2018/2/5.
 */

public class ReviewItemFragment extends BaseLazyFragment implements LRecyclerView.OnRefreshListener, LRecyclerView.OnLoadMoreListener, HttpRequestView,
        LRecyclerViewAdapter.OnItemClickListener, GridView.OnItemClickListener {
    LRecyclerView recyclerView;
    @BindView(R.id.img_up)
    ImageView imgUp;
    @BindView(R.id.grid_view)
    CustomGridView tabView;
    private boolean isRefreshing;
    private boolean isLoadingMore;
    private HttpRequestPresenter requestPresenter;
    //直播
    private List<ReviewItemBean.ListsBean> dataList = new ArrayList<>();
    private ReviewItemAdapter adapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private String url = "http://newlive.longhoo.net/index.php?g=portal&m=video&a=newvlists&datatype=3";


    public static final int TYPE_ALL = 0;     //首页
    public static final int TYPE_DISTRICT = 1;  //各区
    public static final int TYPE_DEPARTMENT = 2;  //部门
    public static final int TYPE_ENTERPRICE = 3; //企业
    public static final int TYPE_METROPOLITAN = 4; //都市圈
    private String did = ""; //各区分区号
    private String dmid = ""; //各部门分部门号
    private int type = TYPE_ALL;
    private int mPage = 1;
    private Animation showAnim, hideAnim, upAnim, downAnim;
    private FragmentActivity mActivity;
    private List<ReviewItemBean.DistrictBean> tabDistrictList = new ArrayList<>();
    private List<ReviewItemBean.DepartmentBean> tabDepartmentList = new ArrayList<>();
    private ReviewTabAdapter tabAdapter;
    private boolean isPaddingSet;
    private boolean isTabShow; //tab是否应该显示


    public static ReviewItemFragment newInstance(int type) {
        ReviewItemFragment fragment = new ReviewItemFragment();
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
        return R.layout.fragment_review_item;
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
        if (type == TYPE_DISTRICT) {
            tabAdapter = new ReviewTabAdapter(mActivity, tabDistrictList, null);
            tabView.setAdapter(tabAdapter);
            tabView.setOnItemClickListener(this);
        } else if (type == TYPE_DEPARTMENT) {
            tabAdapter = new ReviewTabAdapter(mActivity, null, tabDepartmentList);
            tabView.setAdapter(tabAdapter);
            tabView.setOnItemClickListener(this);
        }
        recyclerView = (LRecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new ReviewItemAdapter(mActivity, recyclerView, dataList, type);
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
        upAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_middle_to_top);
        downAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_top_to_middle);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //滑到了顶部
                if (recyclerView.getChildAt(0).getY() == 0f||dataList.size()<=1) {
                    if ((type == TYPE_DISTRICT || type == TYPE_DEPARTMENT)) {
                        if (isTabShow && !tabView.isShown()) {
                            tabView.startAnimation(downAnim);
                            tabView.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (dy > 0&&recyclerView.getChildAt(0).getY()<0) {
                        if (imgUp.isShown()) {
                            imgUp.startAnimation(hideAnim);
                            imgUp.setVisibility(View.GONE);
                        }
                        if ((type == TYPE_DISTRICT || type == TYPE_DEPARTMENT) && tabView.isShown() && isTabShow) {
                            tabView.startAnimation(upAnim);
                            tabView.setVisibility(View.GONE);
                        }
                    } else {
                        if (!imgUp.isShown()) {
                            imgUp.startAnimation(showAnim);
                            imgUp.setVisibility(View.VISIBLE);
                        }
                        if ((type == TYPE_DISTRICT || type == TYPE_DEPARTMENT) && !tabView.isShown() && isTabShow) {
                            tabView.startAnimation(downAnim);
                            tabView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        tabView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if ((type == TYPE_DISTRICT || type == TYPE_DEPARTMENT)&&!isPaddingSet) {
                    if (tabView != null) {
                        if (tabView.getHeight() > 0) {
                            ULog.e("ck",isPaddingSet+"");
                            recyclerView.setPadding(0,tabView.getHeight(), 0, 0);
                            recyclerView.scrollToPosition(0);
                            isPaddingSet = true;
                        }
                    }
                }
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
        ULog.e("ck", "review:" + response);
        isRefreshing = false;
        recyclerView.refreshComplete();
        //处理数据
        List<ReviewItemBean.ListsBean> tempReviewList = null;
        Gson gson = new Gson();
        try {
            ReviewItemBean reviewItemBean = gson.fromJson(response, ReviewItemBean.class);
            String code = reviewItemBean.getStatus() + "";
            if (!TextUtils.equals(code, "0")) {
                ToastUtils.getInstance().showToast(getActivity(), "获取数据失败~");
                return;
            }
            tempReviewList = reviewItemBean.getLists();
            //各区、部门二级菜单
            if (type == TYPE_DISTRICT) {
                List<ReviewItemBean.DistrictBean> districtList = reviewItemBean.getDistrict();
                if (districtList != null) {
                    tabDistrictList.clear();
                    tabDistrictList.addAll(districtList);
                    tabAdapter.notifyDataSetChanged();
                    if (!tabView.isShown()) {
                        tabView.startAnimation(downAnim);
                        tabView.setVisibility(View.VISIBLE);
                    }
                    isTabShow = true;
                }
            } else if (type == TYPE_DEPARTMENT) {
                List<ReviewItemBean.DepartmentBean> departmentList = reviewItemBean.getDepartment();
                if (departmentList != null) {
                    tabDepartmentList.clear();
                    tabDepartmentList.addAll(departmentList);
                    tabAdapter.notifyDataSetChanged();
                    if (!tabView.isShown()) {
                        tabView.startAnimation(downAnim);
                        tabView.setVisibility(View.VISIBLE);
                    }
                    isTabShow = true;
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        if (tempReviewList == null) {
            ToastUtils.getInstance().showToast(getActivity(), "服务器异常~");
            return;
        }
        dataList.clear();
        if (tempReviewList.size() > 0) {
            dataList.addAll(tempReviewList);
            mPage++;
        }
        lRecyclerViewAdapter.notifyDataSetChanged();
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
        ULog.e("ck", "review:" + mPage + ":" + response);
        isLoadingMore = false;
        //处理数据
        List<ReviewItemBean.ListsBean> tempList = null;
        Gson gson = new Gson();
        try {
            ReviewItemBean reviewItemBean = gson.fromJson(response, ReviewItemBean.class);
            String code = reviewItemBean.getStatus() + "";
            if (!TextUtils.equals(code, "0")) {
                ToastUtils.getInstance().showToast(getActivity(), "获取数据失败~");
                RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
                return;
            }
            tempList = reviewItemBean.getLists();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            ToastUtils.getInstance().showToast(getActivity(), "解析错误~");
            RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
        }
        if (tempList == null) {
            ToastUtils.getInstance().showToast(getActivity(), "服务器异常！");
            RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.NetWorkError, mFooterClick);
            return;
        }
        //没有数据或数据<limit，说明无更多
        if (tempList.size() <= 0) {
            RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.TheEnd, null);
        } else {
            RecyclerViewStateUtils.setFooterViewState(getActivity(), recyclerView, RecyclerViewFooter.State.Normal, null);
            mPage++;
        }
        dataList.addAll(tempList);
        lRecyclerViewAdapter.notifyDataSetChanged();
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
        if (type == TYPE_ALL) {
            params.put("menu", "1");
        } else if (type == TYPE_DISTRICT) {
            params.put("menu", "2");
            if(!TextUtils.isEmpty(did)){
                params.put("did",did);
            }
        } else if (type == TYPE_DEPARTMENT) {
            params.put("menu", "3");
            if(!TextUtils.isEmpty(dmid)){
                params.put("dmid",dmid);
            }
        } else if (type == TYPE_ENTERPRICE) {
            params.put("enterprise", "1");
        } else if (type == TYPE_METROPOLITAN) {
            params.put("mpl", "1");
        }
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
        if (type == TYPE_ALL) {
            params.put("menu", "1");
        } else if (type == TYPE_DISTRICT) {
            params.put("menu", "2");
            if(!TextUtils.isEmpty(did)){
                params.put("did",did);
            }
        } else if (type == TYPE_DEPARTMENT) {
            params.put("menu", "3");
            if(!TextUtils.isEmpty(dmid)){
                params.put("dmid",dmid);
            }
        } else if (type == TYPE_ENTERPRICE) {
            params.put("enterprise", "1");
        } else if (type == TYPE_METROPOLITAN) {
            params.put("mpl", "1");
        }
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
        //回看
        String url = dataList.get(position).getZburl();
        String thumb = dataList.get(position).getMthumb();
        if (TextUtils.isEmpty(thumb)) {
            thumb = dataList.get(position).getPhonethumb();
        }
        String termId = dataList.get(position).getTerm_id();
        String description = dataList.get(position).getDescription();
        String shareUrl = dataList.get(position).getShare_url();
        String termType = dataList.get(position).getTerm_type();
        String title = dataList.get(position).getName();
        ULog.e("ck", termType + "ck");
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.alpha_0_to_1, R.anim.alpha_1_to_0, R.anim.alpha_0_to_1, R.anim.alpha_1_to_0);
        transaction.addToBackStack(ReviewFragment.class.getName());
        if (TextUtils.isEmpty(url)) {
            //VideoPlayActivity.startVideoPlay(getActivity(), url, thumb, termId, description,true, false, false,shareUrl,title);
            transaction.replace(R.id.wrapper_review_container, VideoPlayFragment.newInstance(url, thumb, termId, description, true, false, false, shareUrl, title));
            //ToastUtils.getInstance().showToast(getActivity(), "当前直播未开始，请稍后...");
        } else {
            if (TextUtils.equals(termType, "0")) {  //图文直播
                // VideoPlayActivity.startVideoPlay(getActivity(), url, thumb, termId, description,true, false, false,shareUrl,title);
                transaction.replace(R.id.wrapper_review_container, VideoPlayFragment.newInstance(url, thumb, termId, description, true, false, false, shareUrl, title));
            } else {                               //视频直播
                //VideoPlayActivity.startVideoPlay(getActivity(), url, thumb, termId, description, false,false, false,shareUrl,title);
                transaction.replace(R.id.wrapper_review_container, VideoPlayFragment.newInstance(url, thumb, termId, description, false, false, false, shareUrl, title));
            }

        }
        transaction.commit();
        StatusBarUtil.setStatusBarColor(getActivity(),R.color.black);
        StatusBarUtil.StatusBarDarkMode(getActivity());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (type == TYPE_DISTRICT) {
            if (position < 0 || position > tabDistrictList.size() - 1) {
                return;
            }
            if (position == tabAdapter.getSelected()) {
                return;
            }
            tabAdapter.setSelected(position);
            if (isLoadingMore) {
                ToastUtils.getInstance().showToast(getActivity(), "正在加载中，请稍后...");
                return;
            }
            isRefreshing = true;
            doChangetTab(position);
        } else if (type == TYPE_DEPARTMENT) {
            if (position < 0 || position > tabDepartmentList.size() - 1) {
                return;
            }
            if (position == tabAdapter.getSelected()) {
                return;
            }
            tabAdapter.setSelected(position);
            if (isLoadingMore) {
                ToastUtils.getInstance().showToast(getActivity(), "正在加载中，请稍后...");
                return;
            }
            isRefreshing = true;
            doChangetTab(position);
        } else {
            ToastUtils.getInstance().showToast(mActivity, "此处是个bug啊哈哈哈");
        }
    }

    /**
     * 点击tab事件
     *
     * @param position
     */
    private void doChangetTab(final int position) {
        final RefreshHeader refreshHeader = recyclerView.getRefreshheader();
        int refreshHeight = 0;
        if (refreshHeader != null) {
            refreshHeader.setState(RefreshHeader.STATE_REFRESHING);
            refreshHeight = refreshHeader.mMeasuredHeight;
        }
        ValueAnimator animator = ValueAnimator.ofInt(0, refreshHeight);
        animator.setDuration(400).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int num = (int) animation.getAnimatedValue();
                if (refreshHeader != null) {
                    refreshHeader.setVisibleHeight(num);
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (type == TYPE_DISTRICT) {
                    mPage = 1;
                    Map<String, String> params = new HashMap<>();
                    params.put("page", String.valueOf(mPage));
                    params.put("menu", "2");
                    params.put("did", tabDistrictList.get(position).getId() + "");
                    did= tabDistrictList.get(position).getId() + "";
                    requestPresenter.doHttpData(url, Consts.REQUEST_METHOD_POST, Consts.REQUEST_REFRESH, params);
                } else if (type == TYPE_DEPARTMENT) {
                    mPage = 1;
                    Map<String, String> params = new HashMap<>();
                    params.put("page", String.valueOf(mPage));
                    params.put("menu", "3");
                    params.put("dmid", tabDepartmentList.get(position).getId() + "");
                    dmid = tabDepartmentList.get(position).getId() + "";
                    requestPresenter.doHttpData(url, Consts.REQUEST_METHOD_POST, Consts.REQUEST_REFRESH, params);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }
}
