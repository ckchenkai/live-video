package com.zhibo.duanshipin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.bean.HomeItemCaptionBean;
import com.zhibo.duanshipin.bean.HomeItemCityHorizonBean;
import com.zhibo.duanshipin.bean.HomeItemFineVideoBean;
import com.zhibo.duanshipin.bean.HomeItemLiveBean;
import com.zhibo.duanshipin.fragment.HomeItemFragment;
import com.zhibo.duanshipin.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by ck on 2018/2/2.
 */

public class HomeItemAdapter extends RecyclerView.Adapter {
    private Context context;
    private int type = 0;//0直播 1短视频 2图说 3.精视频
    private List list;

    public HomeItemAdapter(Context context, int type, List list) {
        this.context = context;
        this.type = type;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (type == HomeItemFragment.TYPE_LIVE) {
            return new LiveViewHolder(LayoutInflater.from(context).inflate(R.layout.item_zhibo, parent, false));
        } else if (type == HomeItemFragment.TYPE_CITY_HORIZON) {
            return new CityHorizonHolder(LayoutInflater.from(context).inflate(R.layout.item_city_horizon, parent, false));
        } else if (type == HomeItemFragment.TYPE_CAPTION) {
            return new CaptionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_caption, parent, false));
        } else if (type == HomeItemFragment.TYPE_FINE_VIDEO) {
            return new FineVideoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fine_video, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (type) {
            case HomeItemFragment.TYPE_LIVE:
                if (position < 0 || position > list.size() - 1)
                    return;
                if (holder instanceof LiveViewHolder) {
                    HomeItemLiveBean.ListsBean bean = (HomeItemLiveBean.ListsBean) list.get(position);
                    ((LiveViewHolder) holder).itemContent.setText(bean.getName() + "");
                    String isStart = bean.getIstart();
                    String isEnd = bean.getIsend();
                    if(TextUtils.equals(isStart,"0")){
                        ((LiveViewHolder) holder).videotype.setBackgroundResource(R.drawable.bg_type_yugao);
                        ((LiveViewHolder) holder).videotype.setText("预告");
                    }else{
                        if(TextUtils.equals(isEnd,"0")){
                            ((LiveViewHolder) holder).videotype.setBackgroundResource(R.drawable.bg_type_zhibo);
                            ((LiveViewHolder) holder).videotype.setText("直播");
                        }else if(TextUtils.equals(isEnd,"1")){
                            ((LiveViewHolder) holder).videotype.setBackgroundResource(R.drawable.bg_type_zhibo);
                            ((LiveViewHolder) holder).videotype.setText("直播");
                        }
                    }
                    if (!TextUtils.isEmpty(bean.getMthumb())) {
                        ((LiveViewHolder) holder).itemIvThumb.setVisibility(View.VISIBLE);
                        RequestOptions options = new RequestOptions();
                        options.placeholder(R.color.divider).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.color.divider).centerCrop();
                        Glide.with(context).load(bean.getMthumb())
                                .apply(options)
                                .transition(new DrawableTransitionOptions().crossFade())
                                .into(((LiveViewHolder) holder).itemIvThumb);
                    } else {
                        if (!TextUtils.isEmpty(bean.getPhonethumb())) {
                            ((LiveViewHolder) holder).itemIvThumb.setVisibility(View.VISIBLE);
                            RequestOptions options = new RequestOptions();
                            options.placeholder(R.color.divider).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.color.divider).centerCrop();
                            Glide.with(context).load(bean.getPhonethumb())
                                    .apply(options)
                                    .transition(new DrawableTransitionOptions().crossFade())
                                    .into(((LiveViewHolder) holder).itemIvThumb);
                        } else {
                            ((LiveViewHolder) holder).itemIvThumb.setVisibility(View.GONE);
                        }
                    }
                }
                break;
            case HomeItemFragment.TYPE_CITY_HORIZON:
                if (holder instanceof CityHorizonHolder) {
                    if (position < 0 || position > list.size() - 1)
                        return;
                    HomeItemCityHorizonBean.ListsBean bean = (HomeItemCityHorizonBean.ListsBean) list.get(position);
                    ((CityHorizonHolder) holder).tvTitle.setText(bean.getTitle() + "");
                    if (!bean.getInputtime().isEmpty()) {
                        ((CityHorizonHolder) holder).tvTime.setText(Utils.getDataTime(bean.getInputtime()));
                    }

                    if (!TextUtils.isEmpty(bean.getThumb())) {
                        ((CityHorizonHolder) holder).ivThumb.setVisibility(View.VISIBLE);
                        RequestOptions options = new RequestOptions();
                        options.placeholder(R.color.divider).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.color.divider).centerCrop();
                        Glide.with(context).load(bean.getThumb())
                                .apply(options)
                                .transition(new DrawableTransitionOptions().crossFade())
                                .into(((CityHorizonHolder) holder).ivThumb);
                    } else {
                        ((CityHorizonHolder) holder).ivThumb.setVisibility(View.GONE);
                    }
                }
                break;
            case HomeItemFragment.TYPE_CAPTION:
                if (holder instanceof CaptionViewHolder) {
                    if (position < 0 || position > list.size() - 1)
                        return;
                    HomeItemCaptionBean.ListsBean bean = (HomeItemCaptionBean.ListsBean) list.get(position);
                    ((CaptionViewHolder) holder).itemTvTitle.setText(bean.getTitle() + "");
                    if (!bean.getInputtime().isEmpty()) {
                        ((CaptionViewHolder) holder).itemTvTime.setText(Utils.getDataTime(bean.getInputtime()));
                    }
                    if (!TextUtils.isEmpty(bean.getThumb())) {
                        ((CaptionViewHolder) holder).itemIvThumb.setVisibility(View.VISIBLE);
                        RequestOptions options = new RequestOptions();
                        options.placeholder(R.color.divider).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.color.divider).centerCrop();
                        Glide.with(context).load(bean.getThumb())
                                .apply(options)
                                .transition(new DrawableTransitionOptions().crossFade())
                                .into(((CaptionViewHolder) holder).itemIvThumb);

                    } else {
                        ((CaptionViewHolder) holder).itemIvThumb.setVisibility(View.GONE);
                    }
                }
                break;
            case HomeItemFragment.TYPE_FINE_VIDEO:
                if (holder instanceof FineVideoViewHolder) {
                    if (position < 0 || position > list.size() - 1)
                        return;
                    HomeItemFineVideoBean.ListsBean bean = (HomeItemFineVideoBean.ListsBean) list.get(position);
                    ((FineVideoViewHolder) holder).itemTvTitle.setText(bean.getTitle() + "");
                    if (!bean.getInputtime().isEmpty()) {
                        ((FineVideoViewHolder) holder).itemTvTime.setText(Utils.getDataTime(bean.getInputtime()));
                    }
                    if (!TextUtils.isEmpty(bean.getThumb())) {
                        ((FineVideoViewHolder) holder).itemIvThumb.setVisibility(View.VISIBLE);
                        RequestOptions options = new RequestOptions();
                        options.placeholder(R.color.divider).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.color.divider).centerCrop();
                        Glide.with(context).load(bean.getThumb())
                                .apply(options)
                                .transition(new DrawableTransitionOptions().crossFade())
                                .into(((FineVideoViewHolder) holder).itemIvThumb);

                    } else {
                        ((FineVideoViewHolder) holder).itemIvThumb.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class LiveViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_iv_thumb)
        ImageView itemIvThumb;
        @BindView(R.id.item_tv_content)
        TextView itemContent;
        @BindView(R.id.video_type)
        TextView videotype;

        public LiveViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class CityHorizonHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_iv_thumb)
        ImageView ivThumb;
        @BindView(R.id.item_tv_title)
        TextView tvTitle;
        @BindView(R.id.item_tv_time)
        TextView tvTime;

        public CityHorizonHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class CaptionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_iv_thumb)
        ImageView itemIvThumb;
        @BindView(R.id.item_tv_title)
        TextView itemTvTitle;
        @BindView(R.id.item_iv_type)
        ImageView itemIvType;
        @BindView(R.id.item_tv_time)
        TextView itemTvTime;

        public CaptionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FineVideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_iv_thumb)
        ImageView itemIvThumb;
        @BindView(R.id.item_tv_title)
        TextView itemTvTitle;
        @BindView(R.id.item_iv_type)
        ImageView itemIvType;
        @BindView(R.id.item_tv_time)
        TextView itemTvTime;

        public FineVideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
