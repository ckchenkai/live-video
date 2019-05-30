package com.zhibo.duanshipin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.bean.ReviewItemBean;
import com.zhibo.duanshipin.fragment.ReviewItemFragment;
import com.zhibo.duanshipin.utils.Utils;
import com.zhibo.duanshipin.widget.recyclerview.LRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CK on 2018/2/2.
 * Email:910663958@qq.com
 */

public class ReviewItemAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ReviewItemBean.ListsBean> list;
    private LRecyclerView recyclerView;
    private int type;

    public ReviewItemAdapter(Context context, LRecyclerView recyclerView, List<ReviewItemBean.ListsBean> list, int type) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.list = list;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReviewViewHolder(LayoutInflater.from(context).inflate(R.layout.item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < 0 || position > list.size() - 1) {
            return;
        }
        ReviewItemBean.ListsBean bean = list.get(position);
        ((ReviewViewHolder) holder).itemTvTitle.setText(bean.getName() + "");
        if (!bean.getEndtime().isEmpty()) {
            (((ReviewViewHolder) holder)).itemTvTime.setText(Utils.getDataTime(bean.getEndtime()));
        }
        if (!TextUtils.isEmpty(bean.getMthumb())) {
            ((ReviewViewHolder) holder).itemIvThumb.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions();
            options.placeholder(R.color.divider).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.color.divider).centerCrop();
            Glide.with(context).load(bean.getMthumb())
                    .apply(options)
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(((ReviewViewHolder) holder).itemIvThumb);
        } else {
            if (!TextUtils.isEmpty(bean.getPhonethumb())) {
                ((ReviewViewHolder) holder).itemIvThumb.setVisibility(View.VISIBLE);
                RequestOptions options = new RequestOptions();
                options.placeholder(R.color.divider).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.color.divider).centerCrop();
                Glide.with(context).load(bean.getPhonethumb())
                        .apply(options)
                        .transition(new DrawableTransitionOptions().crossFade())
                        .into(((ReviewViewHolder) holder).itemIvThumb);
            } else {
                ((ReviewViewHolder) holder).itemIvThumb.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.allview)
        LinearLayout allView;
        @BindView(R.id.item_iv_thumb)
        ImageView itemIvThumb;
        @BindView(R.id.item_iv_type)
        ImageView itemIvType;
        @BindView(R.id.item_tv_title)
        TextView itemTvTitle;
        @BindView(R.id.item_tv_time)
        TextView itemTvTime;
        @BindView(R.id.video_type)
        TextView videoType;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
