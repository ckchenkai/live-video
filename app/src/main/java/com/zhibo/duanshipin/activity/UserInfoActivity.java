package com.zhibo.duanshipin.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.base.BaseActivity;
import com.zhibo.duanshipin.httprequest.OkHttpCallback;
import com.zhibo.duanshipin.httprequest.OkHttpUtil;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.SPTool;
import com.zhibo.duanshipin.utils.StatusBarUtil;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;
import com.zhibo.duanshipin.utils.Utils;
import com.zhibo.duanshipin.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    private Activity activity;
    private Toolbar toolbar;
    private TextView tvTitle;
    private RelativeLayout headPanel, nickPanel;
    private TextView tvNickname;
    private ImageView ivHead;
    private MyDialog myDialog;
    private ProgressDialog progressDialog;
    private List<LocalMedia> selectList = new ArrayList<>();
    private String avatar, nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarColor(this,R.color.set_bg_clolr);
        StatusBarUtil.StatusBarDarkMode(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initViews() {
        activity = UserInfoActivity.this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        headPanel = (RelativeLayout) findViewById(R.id.head_panel);
        nickPanel = (RelativeLayout) findViewById(R.id.nick_panel);
        tvNickname = (TextView) findViewById(R.id.tv_nickname);
        ivHead = (ImageView) findViewById(R.id.iv_head);
        headPanel.setOnClickListener(this);
        nickPanel.setOnClickListener(this);
        avatar = SPTool.getString(this, Consts.SP_HEADRUL, "");
        nickname = SPTool.getString(this, Consts.SP_NICKNAME, "");
        if (!TextUtils.isEmpty(avatar)) {
            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.default_head).centerCrop();
            Glide.with(this).load(avatar)
                    .apply(options)
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(ivHead);
        }
        tvNickname.setText(nickname);
    }

    @Override
    protected void initToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.left_arrow_white);
        toolbar.setBackgroundColor(getResources().getColor(R.color.set_bg_clolr));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void showEditDialog() {
        myDialog = new MyDialog(this, R.style.myDialog);
        myDialog.setMyEditer(tvNickname.getText().toString().trim());
        myDialog.setMyTitle("修改昵称");
        myDialog.setMyDialogClickListener(new MyDialog.MyDialogClickListener() {
            @Override
            public void onClick(View view, TextView dialogEdit) {
                if (view.getId() == R.id.dialog_ok) {
                    String newNickname = myDialog.dialogEdit.getText().toString();
                    if (!TextUtils.equals(newNickname, nickname) && !TextUtils.isEmpty(newNickname)) {
                        doModifyUserInfo(0, null, myDialog.dialogEdit.getText().toString());
                    }
                }
                tvNickname.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showHideSoftInput(UserInfoActivity.this, tvNickname, false);
                    }
                }, 200);

                myDialog.dismiss();
            }
        });
        myDialog.show();
    }

    /**
     * 修改用户信息
     *
     * @param type     0:昵称 1：头像
     * @param path
     * @param nickname
     */
    private void doModifyUserInfo(final int type, String path, final String nickname) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("数据更新中...");
            progressDialog.show();
        }
        String url = "";
        File headFile = null;
        Map<String, String> params = new HashMap<>();
        params.put("uid", SPTool.getString(this, Consts.SP_UID, ""));
        params.put("ukey", SPTool.getString(this, Consts.SP_UKEY, ""));
        ULog.e("ck", "uid:" + SPTool.getString(this, Consts.SP_UID, "") + " ukey:" + SPTool.getString(this, Consts.SP_UKEY, ""));
        if (type == 0) {
            url = "http://lhzb.longhoo.net/index.php?m=api&c=Index&a=edit_nickname";
            params.put("nickname", nickname);
        } else if (type == 1) {
            url = "http://lhzb.longhoo.net/index.php?m=api&c=Index&a=edit_headpic";
            headFile = new File(path);
        }
        OkHttpUtil.getInstance().doAsyncMultiUpload(url, params, headFile, new OkHttpCallback() {
            @Override
            public void onSuccess(String response) {
                ULog.e("ck", "modify_info:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ToastUtils.getInstance().showToast(UserInfoActivity.this, jsonObject.optString("msg"));
                    if (TextUtils.equals(jsonObject.optString("code"), "0")) {
                        JSONObject data = jsonObject.optJSONObject("data");
                        switch (type) {
                            case 0:
                                tvNickname.setText(nickname);
                                SPTool.putString(UserInfoActivity.this, Consts.SP_NICKNAME, nickname);
                                break;
                            case 1:
                                String headpic = data.optString("headpic");
                                if (!TextUtils.isEmpty(headpic)) {
                                    RequestOptions options = new RequestOptions();
                                    options.diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.default_head).centerCrop();
                                    Glide.with(UserInfoActivity.this)
                                            .load(headpic)
                                            .apply(options)
                                            .transition(new DrawableTransitionOptions().crossFade())
                                            .into(ivHead);
                                    SPTool.putString(UserInfoActivity.this, Consts.SP_HEADRUL, headpic);
                                }
                                break;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToast(UserInfoActivity.this, "更新数据失败~");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToast(UserInfoActivity.this, "更新数据失败~");
                }
                dismissDialog();
            }

            @Override
            public void onFailure(String errorMsg) {
                dismissDialog();
                ToastUtils.getInstance().showToast(UserInfoActivity.this, "上传失败！");
            }
        });
    }

    private void doPickImg() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(0)// 最小选择数量
                .imageSpanCount(3)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                //.enableCrop(true)// 是否裁剪
                .compress(true)// 是否压缩
                .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .openClickSound(false)// 是否开启点击声音
                //.selectionMedia(selectList)// 是否传入已选图片
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList.clear();
                    selectList.addAll(PictureSelector.obtainMultipleResult(data));
                    String imgPath = selectList.get(0).getPath();
                    doModifyUserInfo(1, imgPath, null);
                    break;
            }
        }
    }

    private void dismissDialog() {
        if (myDialog != null) {
            if (myDialog.isShowing())
                myDialog.dismiss();
        }
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_panel:
                doPickImg();
                break;
            case R.id.nick_panel:
                showEditDialog();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }
}
