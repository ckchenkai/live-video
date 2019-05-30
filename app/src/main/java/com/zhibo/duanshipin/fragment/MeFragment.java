package com.zhibo.duanshipin.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhibo.duanshipin.AR.ARVideoActivity;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.activity.BindPhoneActivity;
import com.zhibo.duanshipin.activity.FeedbackActivity;
import com.zhibo.duanshipin.activity.LoginActivity;
import com.zhibo.duanshipin.activity.ModifyPwdActivity;
import com.zhibo.duanshipin.activity.PhoneLoginActivity;
import com.zhibo.duanshipin.activity.UploadRecordActivity;
import com.zhibo.duanshipin.activity.UserInfoActivity;
import com.zhibo.duanshipin.activity.VideoTranscodeActivity;
import com.zhibo.duanshipin.activity.WebViewActivity;
import com.zhibo.duanshipin.base.BaseLazyFragment;
import com.zhibo.duanshipin.bean.JurisdictionBean;
import com.zhibo.duanshipin.bean.LoginBean;
import com.zhibo.duanshipin.httprequest.OkHttpCallback;
import com.zhibo.duanshipin.httprequest.OkHttpUtil;
import com.zhibo.duanshipin.utils.AES64;
import com.zhibo.duanshipin.utils.CacheUtil;
import com.zhibo.duanshipin.utils.Config;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.DisplayUtil;
import com.zhibo.duanshipin.utils.PermissionChecker;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.StatusBarUtil;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;
import com.zhibo.duanshipin.utils.Utils;
import com.zhibo.duanshipin.widget.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeFragment extends BaseLazyFragment {
    @BindView(R.id.iv_head)
    CircleImageView ivHead;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.ll_login_panel)
    LinearLayout llLoginPanel;
    @BindView(R.id.rl_phone_panel)
    RelativeLayout rlPhonePanel;
    @BindView(R.id.rl_feedback_panel)
    RelativeLayout rlFeedbackPanel;
    @BindView(R.id.rl_about_panel)
    RelativeLayout rlAboutPanel;
    @BindView(R.id.rl_cache_panel)
    RelativeLayout rlCachePanel;
    @BindView(R.id.rl_pwd_panel)
    RelativeLayout rlPwdPanel;
    @BindView(R.id.tv_login_out)
    TextView tvLoginOut;
    @BindView(R.id.tv_cache)
    TextView tvCache;
    @BindView(R.id.iv_ar)
    ImageView ivAr;
    @BindView(R.id.rl_transcode)
    RelativeLayout rlTranscode;
    @BindView(R.id.rl_transcodelist)
    RelativeLayout rlTranscodelist;
    @BindView(R.id.ll_set_panel)
    LinearLayout llSetPanel;

    Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void onLazyLoad() {
        getDataAsync();
    }

    private void getDataAsync() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://newlive.longhoo.net/qiniu_upload/android.php")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d("kwwl", "获取数据成功了");
                    Log.d("kwwl", "response.code()==" + response.code());
                    String str = response.body().string() + "";
                    Log.d("kwwl", "response.body().string()==" + str);
                    Config.TOKEN = str;
                    Log.d("kwwl1", "response.body().string()==" + Config.TOKEN);
//                        try {
//                            JSONObject obj = new JSONObject(response.body().string().toString());
//                            if (obj.has("token")) {
//                                Config.TOKEN = obj.getString("token");
//                                Log.d("kwwlTOKEN", "response.body().string()==" + Config.TOKEN);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }


                }
            }
        });
    }

    @Override
    protected void initViews(View view) {
        tvCache.setText(CacheUtil.getInstance().getAllCacheSize(getActivity()));
    }

    @Override
    protected void initStatusBar() {
        super.initStatusBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getActivity(), R.color.main_me_bg);
            StatusBarUtil.StatusBarDarkMode(getActivity());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarUtil.setStatusBarColor(getActivity(), R.color.main_me_bg);
        }
    }

    void GetJurisdiction() {

        Map<String, String> map = new HashMap<>();

        map.put("uid", SPTool.getString(getActivity(), Consts.SP_UID, ""));
        map.put("ukey", SPTool.getString(getActivity(), Consts.SP_UKEY, ""));
        OkHttpUtil.getInstance().doAsyncPost(Consts.BASE_URL + "c=Index&a=check_user ", map, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);


                    if (obj.has("code")) {
                        if (obj.getString("code").equals("0")) {
                            Gson mGson = new Gson();
                            JurisdictionBean mJurisdictionBean = mGson.fromJson(response, JurisdictionBean.class);

//                            是否拥有视频编码权限：0没有，1拥有

                            if (mJurisdictionBean.getData().getIs_ok().equals("1")) {
                                rlTranscode.setVisibility(View.VISIBLE);

                                rlTranscodelist.setVisibility(View.VISIBLE);
                            } else {
                                rlTranscode.setVisibility(View.GONE);
                                rlTranscodelist.setVisibility(View.GONE);
                            }

                        } else {
                            rlTranscode.setVisibility(View.GONE);
                            rlTranscodelist.setVisibility(View.GONE);
//                            Toast.makeText(getActivity(), "权限获取失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        rlTranscode.setVisibility(View.GONE);
                        rlTranscodelist.setVisibility(View.GONE);
//                        Toast.makeText(getActivity(), "权限获取失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtils.getInstance().showToast(getActivity(), "网络错误~");

            }
        });


    }

    private void setLayoutMargin(View view,int marginTop){
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
        lp.topMargin = DisplayUtil.dp2px(getActivity(),marginTop);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.isLogin(getActivity())) {
            llLoginPanel.setVisibility(View.GONE);
            setLayoutMargin(llSetPanel,12);
            tvNickname.setVisibility(View.VISIBLE);
            tvNickname.setText(SPTool.getString(getActivity(), Consts.SP_NICKNAME, ""));
            String headPath = SPTool.getString(getActivity(), Consts.SP_HEADRUL, "");
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .showImageForEmptyUri(R.drawable.default_head)
                    .showImageOnFail(R.drawable.default_head)
                    .showImageOnLoading(R.drawable.default_head)
                    .build();
            ImageLoader.getInstance().displayImage(headPath, ivHead, options);
            tvLoginOut.setVisibility(View.VISIBLE);
            GetJurisdiction();
        } else {
            llLoginPanel.setVisibility(View.VISIBLE);
            setLayoutMargin(llSetPanel,50);
            tvNickname.setVisibility(View.GONE);
            tvLoginOut.setVisibility(View.GONE);
            rlTranscode.setVisibility(View.GONE);
            rlTranscodelist.setVisibility(View.GONE);

        }

        String loginType = SPTool.getString(getActivity(), Consts.SP_LOGINTYPE, "");
        if (TextUtils.equals(loginType, "1")) {
            rlPhonePanel.setVisibility(View.GONE);
        } else {
            rlPhonePanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initToolbar() {

    }

    private boolean isPermissionOK() {
        PermissionChecker checker = new PermissionChecker(getActivity());
        boolean isPermissionOK = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checker.checkPermission();
        if (!isPermissionOK) {
            ToastUtils.s(getActivity(), "Some permissions is not approved !!!");
        }
        return isPermissionOK;
    }

    @Override
    protected long setLoadInterval() {
        return 0;
    }

    @OnClick({R.id.rl_transcodelist, R.id.rl_transcode, R.id.tv_login, R.id.tv_register, R.id.iv_head, R.id.tv_nickname, R.id.rl_phone_panel, R.id.rl_feedback_panel, R.id.rl_about_panel, R.id.rl_cache_panel, R.id.tv_login_out, R.id.rl_pwd_panel, R.id.iv_ar})
    void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.tv_login:
                intent = new Intent(getActivity(), LoginActivity.class);
                break;
            case R.id.rl_transcode:

                if (isPermissionOK()) {

                    intent = new Intent(getActivity(), VideoTranscodeActivity.class);
                }


                break;
            case R.id.rl_transcodelist:


                intent = new Intent(getActivity(), UploadRecordActivity.class);

                break;
            case R.id.tv_register:
                intent = new Intent(getActivity(), LoginActivity.class);
                break;
            case R.id.iv_head:
                if (Utils.isLogin(getActivity())) {
                    intent = new Intent(getActivity(), UserInfoActivity.class);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.tv_nickname:
                if (Utils.isLogin(getActivity())) {
                    intent = new Intent(getActivity(), UserInfoActivity.class);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.rl_phone_panel:
                if (Utils.isLogin(getActivity())) {
                    String loginType = SPTool.getString(getActivity(), Consts.SP_LOGINTYPE, "");
                    if (loginType.equals("1")) {
                        Toast.makeText(getActivity(), "您已经是手机号码登录了，无需绑定手机号", Toast.LENGTH_SHORT).show();
                    } else {
                        if (SPTool.getString(getActivity(), Consts.SP_PHONE, "").isEmpty()) {
                            intent = new Intent();
                            intent.setClass(getActivity(), BindPhoneActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "您已绑定过手机号了", Toast.LENGTH_SHORT).show();
                        }

                    }
                    return;
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.rl_feedback_panel:
                if (Utils.isLogin(getActivity())) {
                    intent = new Intent(getActivity(), FeedbackActivity.class);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.rl_about_panel:
                WebViewActivity.goToWebView(getActivity(), Consts.BASE_URL + "c=Index&a=public_about", "关于我们", false);
                break;
            case R.id.rl_cache_panel:
                PictureFileUtils.deleteCacheDirFile(getActivity());
                CacheUtil.getInstance().clearAllCache(getActivity());
                tvCache.setText("0M");
                break;
            case R.id.tv_login_out:

                Activity activity = getActivity();
                SPTool.remove(activity, Consts.SP_UID);
                SPTool.remove(activity, Consts.SP_LOGINTYPE);
                SPTool.remove(activity, Consts.SP_NICKNAME);
                SPTool.remove(activity, Consts.SP_PWD);
                SPTool.remove(activity, Consts.SP_PHONE);
                SPTool.remove(activity, Consts.SP_OUID);
                SPTool.remove(activity, Consts.SP_UKEY);
                SPTool.remove(activity, Consts.SP_OPTENID);
                SPTool.remove(activity, Consts.SP_HEADRUL);
                SPTool.remove(activity, Consts.SP_SHAREURL);
                SPTool.remove(activity, Consts.SP_SHAREICON);
                tvLoginOut.setVisibility(View.GONE);
                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_1_to_0);
                tvLoginOut.startAnimation(anim);
                //更新UI
                ivHead.setImageResource(R.drawable.default_head);
                llLoginPanel.setVisibility(View.VISIBLE);
                setLayoutMargin(llSetPanel,50);
                tvNickname.setVisibility(View.GONE);
                tvLoginOut.setVisibility(View.GONE);
                String loginType = SPTool.getString(getActivity(), Consts.SP_LOGINTYPE, "");
                if (TextUtils.equals(loginType, "1")) {
                    rlPhonePanel.setVisibility(View.GONE);
                } else {
                    rlPhonePanel.setVisibility(View.VISIBLE);
                }
                GetJurisdiction();
                Utils.showHideSoftInput(getActivity(), rlPhonePanel, false);
                break;
            case R.id.rl_pwd_panel:
                if (Utils.isLogin(getActivity())) {
                    if (SPTool.getString(getActivity(), Consts.SP_PHONE, "").isEmpty()) {
                        intent = new Intent();
                        intent.setClass(getActivity(), BindPhoneActivity.class);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "请您先绑定手机号码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent = new Intent(getActivity(), ModifyPwdActivity.class);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.iv_ar:
                intent = new Intent(getActivity(), ARVideoActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
