package com.zhibo.duanshipin.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.bean.LiveListBean;
import com.zhibo.duanshipin.utils.DisplayUtil;
import com.zhibo.duanshipin.utils.Utils;
import com.zhibo.duanshipin.widget.CustomGridView;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * Created by CK on 2017/9/5.
 * Email:910663958@qq.com
 */

public class LiveListAdapter extends RecyclerView.Adapter {
    public static final String TAG = "LiveListAdapter";
    private Context context;
    private List<LiveListBean.ListsBean> dateList;

    public LiveListAdapter(Context context, List<LiveListBean.ListsBean> dateList) {
        this.context = context;
        this.dateList = dateList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_live, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position < 0 || position > dateList.size() - 1) {
            return;
        }
        LiveListBean.ListsBean bean = dateList.get(position);
        if (holder instanceof MyViewHolder) {
            if(position%2==0){
                ((MyViewHolder) holder).tvHost.setText("主持人 小龙");
            }else{
                ((MyViewHolder) holder).tvHost.setText("主持人 小虎");
            }
            ((MyViewHolder) holder).itemTime.setText(bean.getPost_date() + "");
            ((MyViewHolder) holder).itemContent.setText(bean.getPost_content() + "");
            //判断时间是否显示
            if (position > 0) {
                String lastTime = dateList.get(position - 1).getPost_date();
                String curTime = dateList.get(position).getPost_date();
                try {
                    if (Long.parseLong(Utils.dateToStamp(lastTime)) - Long.parseLong(Utils.dateToStamp(curTime)) > 2 * 60 * 1000) {
                        ((MyViewHolder) holder).itemTimeContainer.setVisibility(View.VISIBLE);
                    } else {
                        ((MyViewHolder) holder).itemTimeContainer.setVisibility(View.GONE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    ((MyViewHolder) holder).itemTimeContainer.setVisibility(View.VISIBLE);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    ((MyViewHolder) holder).itemTimeContainer.setVisibility(View.VISIBLE);
                }
            } else {
                ((MyViewHolder) holder).itemTimeContainer.setVisibility(View.VISIBLE);
            }
            //图片
            //加载网格图片
            List<String> picList = (List<String>) bean.getPhotos();
            GridViewAdapter adapter = new GridViewAdapter(context, picList);
            int width = 0;
            int height = 0;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ((MyViewHolder) holder).itemGridView.getLayoutParams();
            if (picList.size() <= 0) {
                ((MyViewHolder) holder).itemGridView.setVisibility(View.GONE);
            } else if (picList.size() == 1) {
                ((MyViewHolder) holder).itemGridView.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).itemGridView.setNumColumns(1);
                width = Utils.getDeviceSize(context).x - DisplayUtil.dp2px(context, 40);
                lp.width = RecyclerView.LayoutParams.MATCH_PARENT;
            } else if (picList.size() == 2 || picList.size() == 4) {
                ((MyViewHolder) holder).itemGridView.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).itemGridView.setNumColumns(2);
                width = (Utils.getDeviceSize(context).x - DisplayUtil.dp2px(context, 40)) / 3;
                lp.width = width * 2 + DisplayUtil.dp2px(context, 4);
            } else {
                ((MyViewHolder) holder).itemGridView.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).itemGridView.setNumColumns(3);
                width = (Utils.getDeviceSize(context).x - DisplayUtil.dp2px(context, 40)) / 3;
                lp.width = RecyclerView.LayoutParams.MATCH_PARENT;
            }
            ((MyViewHolder) holder).itemGridView.setLayoutParams(lp);
            height = width * 3 / 4;
            adapter.setItemHeight(height);
            ((MyViewHolder) holder).itemGridView.setAdapter(adapter);
            ((MyViewHolder) holder).itemGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    if (onGridItemClickListener != null) {
                        onGridItemClickListener.onGridItemClick(position, pos);
                    }
                }
            });
            //视频设置
            String url = bean.getQn_video();
            String thumbUrl = bean.getQn_video_img();
            if(TextUtils.isEmpty(url)){
                ((MyViewHolder) holder).videoPlayer.setVisibility(View.GONE);
            }else{
                ((MyViewHolder) holder).videoPlayer.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).videoPlayer.setUp(url, JZVideoPlayer.SCREEN_WINDOW_LIST,"");
                if (!TextUtils.isEmpty(thumbUrl)) {
                    RequestOptions options = new RequestOptions();
                    options.placeholder(R.color.black).error(R.color.black).centerCrop();
                    Glide.with(context).load(thumbUrl)
                            .apply(options)
                            .transition(new DrawableTransitionOptions().crossFade())
                            .into(((MyViewHolder) holder).videoPlayer.thumbImageView);
                }
                ((MyViewHolder) holder).videoPlayer.positionInList = position;
            }
        }
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_host)
        TextView tvHost;
        @BindView(R.id.item_time)
        TextView itemTime;
        @BindView(R.id.item_content)
        TextView itemContent;
        @BindView(R.id.item_grid_view)
        CustomGridView itemGridView;
        @BindView(R.id.item_time_container)
        LinearLayout itemTimeContainer;
        @BindView(R.id.video_item_player)
        JZVideoPlayerStandard videoPlayer;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnGridItemClickListener onGridItemClickListener;

    public void setOnGridItemClickListener(OnGridItemClickListener onGridItemClickListener) {
        this.onGridItemClickListener = onGridItemClickListener;
    }

    public interface OnGridItemClickListener {
        void onGridItemClick(int listPosition, int gridPosition);
    }
}
