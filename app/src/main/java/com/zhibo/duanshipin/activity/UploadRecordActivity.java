package com.zhibo.duanshipin.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.adapter.UploadRecordapter;
import com.zhibo.duanshipin.base.BaseActivity;
import com.zhibo.duanshipin.bean.UploadRecordBean;
import com.zhibo.duanshipin.httprequest.HttpRequestPresenter;
import com.zhibo.duanshipin.httprequest.HttpRequestView;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ${CC} on 2018/2/11.
 */

public class UploadRecordActivity extends BaseActivity implements OnRefreshLoadmoreListener, HttpRequestView {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_none)
    TextView tvNone;
    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout smartRefreshLayout;
    private List<UploadRecordBean.DataBean> dataList = new ArrayList<>();
    UploadRecordapter adapter;
    private HttpRequestPresenter requestPresenter;
    private int mPage = 1;


    @Override
    protected int getContentId() {
        return R.layout.activity_uploadrecord;
    }

    @Override
    protected void initViews() {
        requestPresenter = new HttpRequestPresenter(this, this);
        smartRefreshLayout.setRefreshHeader(new ClassicsHeader(this));
        smartRefreshLayout.setRefreshFooter(new ClassicsFooter(this));
        smartRefreshLayout.setOnRefreshLoadmoreListener(this);
        smartRefreshLayout.autoRefresh(0);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new UploadRecordapter(this, dataList);
        recyclerView.setAdapter(adapter);
    }

    private void finishRefreshLoad() {
        if (smartRefreshLayout != null) {
            if (smartRefreshLayout.isRefreshing()) {
                smartRefreshLayout.finishRefresh(0);
            }
            if (smartRefreshLayout.isLoading()) {
                smartRefreshLayout.finishLoadmore(0);
            }
        }
    }

    @Override
    protected void initToolbar() {
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.left_arrow_white);
        toolbar.setBackgroundColor(getResources().getColor(R.color.set_bg_clolr));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onDestroy() {
        finishRefreshLoad();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        mPage++;

        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(mPage + ""));


        params.put("uid", SPTool.getString(this, Consts.SP_UID, ""));
        params.put("ukey", SPTool.getString(this, Consts.SP_UKEY, ""));


//        params.put("token", String.valueOf("mCR2G6JKoWvjOoDfOreYcg=="));


        requestPresenter.doHttpData(Consts.BASE_URL + "c=Index&a=get_video_record", Consts.REQUEST_METHOD_POST, Consts.REQUEST_LOADMORE, params);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        mPage = 1;

        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(mPage + ""));


        params.put("uid", SPTool.getString(this, Consts.SP_UID, ""));
        params.put("ukey", SPTool.getString(this, Consts.SP_UKEY, ""));


//        params.put("token", String.valueOf("mCR2G6JKoWvjOoDfOreYcg=="));


        requestPresenter.doHttpData(Consts.BASE_URL + "c=Index&a=get_video_record", Consts.REQUEST_METHOD_POST, Consts.REQUEST_REFRESH, params);
    }

    @Override
    public void onNetworkError() {
        finishRefreshLoad();
        ToastUtils.getInstance().showToast(this, "网络连接异常，请检查网络设置~");
    }

    @Override
    public void onRefreshSuccess(String response) {
        smartRefreshLayout.finishRefresh(0);

        try {
            JSONObject obj = null;
            obj = new JSONObject(response);
            if (obj.has("code")) {
                if (obj.getString("code").equals("0")) {

                    Gson gson = new Gson();
                    UploadRecordBean searchResultBean = gson.fromJson(response, UploadRecordBean.class);
                    if (searchResultBean.getData().size() > 0) {
                        dataList.clear();
                        dataList.addAll(searchResultBean.getData());
                    }


                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRefreshError() {
        ToastUtils.getInstance().showToast(this, "刷新失败~");
        smartRefreshLayout.finishRefresh(0);
    }

    @Override
    public void onLoadMoreSuccess(String response) {
        smartRefreshLayout.finishLoadmore(0);

        try {
            JSONObject obj = null;
            obj = new JSONObject(response);
            if (obj.has("code")) {
                if (obj.getString("code").equals("0")) {

                    Gson gson = new Gson();
                    UploadRecordBean searchResultBean = gson.fromJson(response, UploadRecordBean.class);

                    if (searchResultBean.getData().size() > 0) {
                        dataList.addAll(searchResultBean.getData());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLoadMoreError() {
        ToastUtils.getInstance().showToast(this, "加载失败~");
        smartRefreshLayout.finishLoadmore(0);
    }
}
