package com.zhibo.duanshipin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.activity.VideoPlayActivity;
import com.zhibo.duanshipin.bean.UploadRecordBean;
import com.zhibo.duanshipin.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ${CC} on 2017/12/4.
 */

public class UploadRecordapter extends RecyclerView.Adapter {


    Context mContext;

    private List<UploadRecordBean.DataBean> list;

    public UploadRecordapter(Context context, List<UploadRecordBean.DataBean> list) {
        this.mContext = context;
        this.list = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UploadRecordapterHolder(LayoutInflater.from(mContext).inflate(R.layout.item_uploadrecord, parent, false));
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int pos = position;
        if (pos < 0 || pos > list.size() - 1)
            return;
        if (holder instanceof UploadRecordapterHolder) {
            final UploadRecordBean.DataBean bean = list.get(position);
            ((UploadRecordapterHolder) holder).tvName.setText("视频七牛地址："+bean.getUrl()+"\n"+"\n"+bean.getContent()
                    .replace("\n","\n" )
                    .replace("\\n","\n" )
                    .replace("\\\n","\n" )
                    .replace("\n","\n" )
                    .replace("\r","\n")
                    .replace("\\r","\n")
                    .replace("\\\r","\n")
            );
            ((UploadRecordapterHolder) holder).tvTime.setText(Utils.getDataTimeWithMinute(bean.getTime()));
            ((UploadRecordapterHolder) holder).lvAllview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoPlayActivity.startVideoPlay(mContext, bean.getUrl(), "", null, "视频七牛地址："+bean.getUrl()+"\n"+"\n"+bean.getContent()
                            .replace("\n","\n" )
                            .replace("\\n","\n" )
                            .replace("\\\n","\n" )
                            .replace("\n","\n" )
                            .replace("\r","\n")
                            .replace("\\r","\n")
                            .replace("\\\r","\n"), false,true, false,"","视频七牛地址："+bean.getUrl()+"\n"+"\n"+bean.getContent()
                            .replace("\n","\n" )
                            .replace("\\n","\n" )
                            .replace("\\\n","\n" )
                            .replace("\n","\n" )
                            .replace("\r","\n")
                            .replace("\\r","\n")
                            .replace("\\\r","\n"));
                }
            });


        }
    }

    class UploadRecordapterHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_time)
        TextView tvTime;

        @BindView(R.id.lv_allview)
        LinearLayout lvAllview;

        public UploadRecordapterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
