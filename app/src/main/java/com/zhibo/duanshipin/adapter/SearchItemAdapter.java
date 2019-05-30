package com.zhibo.duanshipin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.activity.SearchActivity;
import com.zhibo.duanshipin.activity.VideoPlayActivity;
import com.zhibo.duanshipin.bean.SearchBean;
import com.zhibo.duanshipin.fragment.VideoPlayFragment;
import com.zhibo.duanshipin.utils.ToastUtils;
import com.zhibo.duanshipin.utils.ULog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/7/12.
 */

public class SearchItemAdapter extends RecyclerView.Adapter {


    private Context context;
    int type;
    //搜索结果
    private List<SearchBean.ListsBean> list;


    public SearchItemAdapter(Context context, List<SearchBean.ListsBean> list, int type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    public void RefeshSearchItemAdapter(Context context, List<SearchBean.ListsBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new LiveViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof LiveViewHolder) {
            if (position < 0 || position > list.size() - 1)
                return;
            final SearchBean.ListsBean bean = list.get(position);
            ((LiveViewHolder) holder).tvName.setText(bean.getName());



                    ((LiveViewHolder) holder).lvAllview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //直播
                            String url = bean.getZburl();
                            String thumb = bean.getPhonethumb();
                            String termId = bean.getTerm_id();
                            String description = bean.getDescription();
                            String termType = bean.getTerm_type();
                            String shareUrl = bean.getShare_url();
                            String title = bean.getName();
                            ULog.e("cc--",url);
                            if (TextUtils.isEmpty(url)) {
                                VideoPlayActivity.startVideoPlay(context, url, thumb, termId, description,true, true, false,shareUrl,title);

                            } else {
                                if(TextUtils.equals(termType,"0")){  //图文直播
                    VideoPlayActivity.startVideoPlay(context, url, thumb, termId, description,true, true, false,shareUrl,title);
                                }else{                               //视频直播
                                    VideoPlayActivity.startVideoPlay(context, url, thumb, termId, description, false,true, false,shareUrl,title);
                                }

                            }
                        }
                    });
            if (type == 1) {
                ((LiveViewHolder) holder).imgTable.setVisibility(View.VISIBLE);
                ((LiveViewHolder) holder).imgTable.setText(position + 1 + "");
                if (position == 0) {
                    ((LiveViewHolder) holder).imgTable.setBackgroundResource(R.drawable.shape_corner_red);
                    ((LiveViewHolder) holder).imgTable.setTextColor(Color.parseColor("#ffffff"));
                } else if (position == 1) {
                    ((LiveViewHolder) holder).imgTable.setBackgroundResource(R.drawable.shape_corner_org);
                    ((LiveViewHolder) holder).imgTable.setTextColor(Color.parseColor("#ffffff"));
                } else if (position == 2) {
                    ((LiveViewHolder) holder).imgTable.setBackgroundResource(R.drawable.shape_corner_yew);
                    ((LiveViewHolder) holder).imgTable.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    ((LiveViewHolder) holder).imgTable.setBackgroundResource(R.drawable.shape_corner_hui);
                    ((LiveViewHolder) holder).imgTable.setTextColor(Color.parseColor("#909090"));
                }

            } else {
                ((LiveViewHolder) holder).imgTable.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {

        return list.size();

    }


    class LiveViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_table)
        TextView imgTable;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.lv_allview)
        LinearLayout lvAllview;
        public LiveViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
