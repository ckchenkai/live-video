package com.zhibo.duanshipin.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.base.BaseActivity;
import com.zhibo.duanshipin.httprequest.OkHttpCallback;
import com.zhibo.duanshipin.httprequest.OkHttpUtil;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyPwdActivity extends BaseActivity {

    @BindView(R.id.et_old_pwd)
    EditText etOldPwd;
    @BindView(R.id.et_new_pwd)
    EditText etNewPwd;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_commit)
    ImageView ivCommit;
    private ProgressDialog progressDialog;

    @Override
    protected int getContentId() {
        return R.layout.activity_modify_pwd;
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected void initToolbar() {

    }

    @OnClick({R.id.iv_commit,R.id.iv_back})
    void onClick(View view){
        switch (view.getId()){
            case R.id.iv_commit:
                doCommit();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    private void doCommit(){
        String oldPwd = etOldPwd.getText().toString().trim();
        String newPwd = etNewPwd.getText().toString().trim();
        if(TextUtils.isEmpty(oldPwd)){
            ToastUtils.getInstance().showToast(this,"请输入原密码！");
            return;
        }
        if(TextUtils.isEmpty(newPwd)){
            ToastUtils.getInstance().showToast(this,"请输入新密码！");
            return;
        }
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在修改中...");
            progressDialog.show();
        }
        String url = "http://lhzb.longhoo.net/index.php?m=api&c=Index&a=edit_pwd";
        Map<String,String> params = new HashMap<>();
        params.put("uid", SPTool.getString(this, Consts.SP_UID,""));
        params.put("ukey", SPTool.getString(this, Consts.SP_UKEY, ""));
        params.put("pwd",oldPwd);
        params.put("npwd",newPwd);
        OkHttpUtil.getInstance().doAsyncPost(url, params, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {
                ULog.e("ck","修改密码："+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(TextUtils.equals(jsonObject.optString("code"),"0")){
                        finish();
                    }
                    ToastUtils.getInstance().showToast(ModifyPwdActivity.this,jsonObject.optString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToast(ModifyPwdActivity.this,"服务器异常~");
                }
                dismissDialog();
            }

            @Override
            public void onFailure(String errorMsg) {
                dismissDialog();
                ToastUtils.getInstance().showToast(ModifyPwdActivity.this,"网络异常~");
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
