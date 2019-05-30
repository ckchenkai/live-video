package com.zhibo.duanshipin.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.base.BaseActivity;
import com.zhibo.duanshipin.httprequest.OkHttpCallback;
import com.zhibo.duanshipin.httprequest.OkHttpUtil;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.StatusBarUtil;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.et_feedback)
    EditText etFeedback;
    @BindView(R.id.tv_commit)
    TextView tvCommit;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_text_num)
    TextView tvTextNum;
    private int maxCount = 200;
    private ProgressDialog progressDialog;

    @Override
    protected int getContentId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initViews() {
        etFeedback.addTextChangedListener(this);
        StatusBarUtil.setStatusBarColor(this,R.color.set_bg_clolr);
        StatusBarUtil.StatusBarDarkMode(this);
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() >= maxCount + 1) {
            etFeedback.setText(s.toString().subSequence(0, maxCount));
            etFeedback.setSelection(maxCount);
            ToastUtils.getInstance().showToast(this, "最多输入" + maxCount + "个字！");
        }
        tvTextNum.setText(String.valueOf(maxCount>=s.length()?(maxCount - s.length()):0));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @OnClick(R.id.tv_commit)
    void onClick(View view){
        switch (view.getId()){
            case R.id.tv_commit:
                    doCommit();
                break;
        }
    }

    private void doCommit(){
        String content = etFeedback.getText().toString().trim();
        if(TextUtils.isEmpty(content)){
            ToastUtils.getInstance().showToast(this,"请输入内容！");
            return;
        }
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("提交反馈中...");
            progressDialog.show();
        }
        String url = "http://lhzb.longhoo.net/index.php?m=api&c=Index&a=add_question";
        Map<String,String> params = new HashMap<>();
        params.put("uid", SPTool.getString(this, Consts.SP_UID,""));
        params.put("ukey", SPTool.getString(this, Consts.SP_UKEY, ""));
        params.put("content",etFeedback.getText().toString().trim());
        OkHttpUtil.getInstance().doAsyncPost(url, params, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {
                ULog.e("ck","反馈："+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(TextUtils.equals(jsonObject.optString("code"),"0")){
                        ULog.e("ck","feedback:success");
                        finish();
                    }
                    ToastUtils.getInstance().showToast(FeedbackActivity.this,jsonObject.optString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToast(FeedbackActivity.this,"服务器异常~");
                }
                dismissDialog();
            }

            @Override
            public void onFailure(String errorMsg) {
                dismissDialog();
                ToastUtils.getInstance().showToast(FeedbackActivity.this,"网络异常~");
            }
        });
    }

    private void dismissDialog(){
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }
}
