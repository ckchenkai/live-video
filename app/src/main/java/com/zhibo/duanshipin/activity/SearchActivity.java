package com.zhibo.duanshipin.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.adapter.SearchItemAdapter;
import com.zhibo.duanshipin.base.BaseActivity;
import com.zhibo.duanshipin.bean.LoginBean;
import com.zhibo.duanshipin.bean.SearchBean;
import com.zhibo.duanshipin.httprequest.HttpRequestView;
import com.zhibo.duanshipin.httprequest.OkHttpCallback;
import com.zhibo.duanshipin.httprequest.OkHttpUtil;
import com.zhibo.duanshipin.utils.AES64;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;
import com.zhibo.duanshipin.utils.Utils;
import com.zhibo.duanshipin.widget.recyclerview.LRecyclerView;
import com.zhibo.duanshipin.widget.recyclerview.LRecyclerViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ${CC} on 2018/2/2.
 */

public class SearchActivity extends BaseActivity {
    @BindView(R.id.topview)
    LinearLayout topview;
    @BindView(R.id.img_search)
    ImageView imgSearch;
    @BindView(R.id.edit_searchinfo)
    EditText editSearchinfo;
    @BindView(R.id.tv_cancl)
    TextView tvCancl;
    @BindView(R.id.recycler_view)
    android.support.v7.widget.RecyclerView recyclerView;
    @BindView(R.id.tv_none)
    TextView tvNone;
    @BindView(R.id.login_form)
    LinearLayout loginForm;
    @BindView(R.id.recycler_viewresult)
    android.support.v7.widget.RecyclerView recyclerViewresult;
    @BindView(R.id.tv_noneresult)
    TextView tvNoneresult;
    @BindView(R.id.lv_searchresult)
    LinearLayout lvSearchresult;
    @BindView(R.id.lv_hot)
    LinearLayout lvHot;
    private String url = "http://newlive.longhoo.net/index.php?g=portal&m=video&a=search";
    SearchItemAdapter mHotSearchItemAdapter;
    SearchItemAdapter mSearchtSearchItemAdapter;
    List<SearchBean.ListsBean> hotList = null;
    List<SearchBean.ListsBean> searchList = null;

    @Override
    protected int getContentId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initViews() {
        lvHot.setVisibility(View.VISIBLE);
        lvSearchresult.setVisibility(View.GONE);
        Utils.showHideSoftInput(SearchActivity.this, editSearchinfo, true);
        getWindow().getDecorView().setFitsSystemWindows(true);
        mHotSearchItemAdapter = new SearchItemAdapter(this, hotList,1);

        mSearchtSearchItemAdapter = new SearchItemAdapter(this, searchList,2);
        hotList();
        editSearchinfo.addTextChangedListener(editclick);
        tvCancl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initToolbar() {

    }

    private TextWatcher editclick = new TextWatcher() {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        //一般我们都是在这个里面进行我们文本框的输入的判断，上面两个方法用到的很少
        @Override
        public void afterTextChanged(Editable s) {
            String money = editSearchinfo.getText().toString().trim();
            if (money.equals("")) {
                searchList=null;
                lvHot.setVisibility(View.VISIBLE);
                lvSearchresult.setVisibility(View.GONE);
            } else {
                SearchList(money);
            }
        }
    };

    void SearchList(String strNmae) {
        lvHot.setVisibility(View.GONE);
        lvSearchresult.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("name", strNmae);

        OkHttpUtil.getInstance().doAsyncPost(url, map, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {
                ULog.e("searchmessage=========", response);
                try {
                    JSONObject obj = new JSONObject(response);


                    Gson gson = new Gson();
                    try {
                        SearchBean homeItemLiveBean = gson.fromJson(response, SearchBean.class);

                        searchList = homeItemLiveBean.getLists();
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    if (searchList == null) {
                        ToastUtils.getInstance().showToast(SearchActivity.this, "服务器异常~");
                        return;
                    }


                    if (searchList.size() != 0) {
                        tvNoneresult.setVisibility(View.GONE);
                        LinearLayoutManager manager = new LinearLayoutManager(SearchActivity.this);
                        manager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerViewresult.setLayoutManager(manager);
                        mSearchtSearchItemAdapter.RefeshSearchItemAdapter(SearchActivity.this, searchList);
                        recyclerViewresult.setAdapter(mSearchtSearchItemAdapter);
                        mSearchtSearchItemAdapter.notifyDataSetChanged();
                    } else {
                        LinearLayoutManager manager = new LinearLayoutManager(SearchActivity.this);
                        manager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerViewresult.setLayoutManager(manager);
                        mSearchtSearchItemAdapter.RefeshSearchItemAdapter(SearchActivity.this, searchList);
                        recyclerViewresult.setAdapter(mSearchtSearchItemAdapter);
                        mSearchtSearchItemAdapter.notifyDataSetChanged();
                        tvNoneresult.setVisibility(View.VISIBLE);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtils.getInstance().showToast(SearchActivity.this, "网络错误~");

            }
        });


    }

    void hotList() {
        lvHot.setVisibility(View.VISIBLE);
        lvSearchresult.setVisibility(View.GONE);
        Map<String, String> map = new HashMap<>();
        map.put("name", "");
        OkHttpUtil.getInstance().doAsyncPost(url, map, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {
                ULog.e("hotmessage=========", response);
                try {
                    JSONObject obj = new JSONObject(response);


                    Gson gson = new Gson();
                    try {
                        SearchBean homeItemLiveBean = gson.fromJson(response, SearchBean.class);

                        hotList = homeItemLiveBean.getLists();
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    if (hotList == null) {
                        ToastUtils.getInstance().showToast(SearchActivity.this, "服务器异常~");
                        return;
                    }

                    LinearLayoutManager manager = new LinearLayoutManager(SearchActivity.this);
                    manager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(manager);
                    mHotSearchItemAdapter.RefeshSearchItemAdapter(SearchActivity.this, hotList);
                    recyclerView.setAdapter(mHotSearchItemAdapter);

                    mHotSearchItemAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtils.getInstance().showToast(SearchActivity.this, "网络错误~");

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


}
