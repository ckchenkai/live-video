package com.zhibo.duanshipin.widget.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.utils.DisplayUtil;
import com.zhibo.duanshipin.widget.NoScrollViewPager;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ViewPager实现的轮播图广告自定义视图，如京东首页的广告轮播图效果； 既支持自动轮播页面也支持手势滑动切换页面
 */
public class SlideBannerView extends FrameLayout {
    // 自定义轮播图的资源
    //private List<String> imageUrls =new ArrayList<>();
    private List<BannerEntity> bannerList = new ArrayList<>();
    // 放轮播图片的ImageView 的list
    private List<FrameLayout> imageViewsList;
    // 放圆点的View的list
    private List<View> dotViewsList;

    private NoScrollViewPager viewPager;
    private TextView tvType, tvTitle;
    // 当前轮播页
    private int currentItem = 0;

    private Context context;

    private BannerItemCLickListener itemClickListener;

    private Timer time;
    private LinearLayout dotLayout;
    private int defaultImage = R.drawable.blank;

    public SlideBannerView(Context context) {
        this(context, null);
    }

    public SlideBannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideBannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_custome_banner, this, true);
        dotLayout = (LinearLayout) findViewById(R.id.layout_banner_points_group);
        viewPager = (NoScrollViewPager) findViewById(R.id.layout_banner_viewpager);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        initData();
        startPlay();
    }

    /**
     * 开始轮播图切换
     */
    public void startPlay() {
        autogallery();
    }

    /**
     * 初始化相关Data
     */
    private void initData() {
        imageViewsList = new ArrayList<>();
        dotViewsList = new ArrayList<>();
    }

    /**
     * 初始化Views等UI
     */
    private void initUI() {
        if (bannerList.size() == 0) {
            bannerList.add(new BannerEntity("", "", "", ""));
        }
        currentItem = 0;
        dotLayout.removeAllViews();
        imageViewsList.clear();
        dotViewsList.clear();
        currentItem = 0;
        for (int i = 0; i < bannerList.size(); i++) {
            ImageView dotView = new ImageView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DisplayUtil.dp2px(context, 8), DisplayUtil.dp2px(context, 8));
            lp.setMargins(DisplayUtil.dp2px(context, 3), DisplayUtil.dp2px(context, 0), DisplayUtil.dp2px(context, 3), DisplayUtil.dp2px(context, 0));
            dotView.setLayoutParams(lp);
            dotLayout.addView(dotView);
            dotViewsList.add(dotView);
            if (i == currentItem) {
                dotView.setBackgroundResource(R.drawable.shape_dots_select);
            } else {
                dotView.setBackgroundResource(R.drawable.shape_dots_default);
            }
        }
        viewPager.setFocusable(true);
        viewPager.setAdapter(new MyPagerAdapter(defaultImage));
        viewPager.addOnPageChangeListener(new MyPageChangeListener());
        viewPager.setCurrentItem(currentItem);
        setTitle(currentItem);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                startPlay();
            }
        },300);
    }

    /**
     * 填充ViewPager的页面适配器
     */
    private class MyPagerAdapter extends PagerAdapter {
        private int defaultPic;

        MyPagerAdapter(int defaultPic) {
            this.defaultPic = defaultPic;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            if (position >= imageViewsList.size()) {
                return;
            }
            ((ViewPager) container).removeView(imageViewsList.get(position));
        }

        @Override
        public Object instantiateItem(View container, final int position) {
            final int pos = position % bannerList.size();

            FrameLayout frameLayout = new FrameLayout(context);

            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER);
            LayoutParams lp2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.RIGHT);

            ImageView view = new ImageView(context);
            view.setBackgroundResource(defaultPic);
            view.setScaleType(ScaleType.FIT_XY);
            if (!TextUtils.isEmpty(bannerList.get(pos).img)) {
                RequestOptions options = new RequestOptions();
                options.placeholder(R.drawable.blank).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.blank).centerCrop().dontAnimate();
                Glide.with(getContext())
                        .load(bannerList.get(pos).img)
                        .apply(options)
                        .into(view);
            }
            view.setLayoutParams(lp);
            frameLayout.addView(view);
            imageViewsList.add(frameLayout);
            ((ViewPager) container).addView(frameLayout);
            return frameLayout;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

        @Override
        public void finishUpdate(View arg0) {
        }
    }

    private void autogallery() {
        if (time != null) {
            time.cancel();
            time = null;
        }
        time = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (bannerList.size() > 1) {
                    Message m = handler.obtainMessage();
                    handler.sendMessage(m);
                }
            }
        };
        time.schedule(task, 5000, 5000);
    }

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            viewPager.setCurrentItem(currentItem + 1);
        }
    };

    /**
     * ViewPager的监听器 当ViewPager中页面的状态发生改变时调用
     */
    private class MyPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:// 手势滑动，空闲中
                    if (time != null) {
                        time.cancel();
                        time = null;
                    }
                    break;
                case 2:// 界面切换中
                    if (time == null) {
                        autogallery();
                    }
                    break;
                case 0:
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            int pos = position % bannerList.size();
            currentItem = position;
            for (int i = 0; i < dotViewsList.size(); i++) {
                if (i == pos) {
                    (dotViewsList.get(pos)).setBackgroundResource(R.drawable.shape_dots_select);
                } else {
                    (dotViewsList.get(i)).setBackgroundResource(R.drawable.shape_dots_default);
                }
            }
            setTitle(pos);
        }
    }

    /**
     * 必须在setImagePath(List<String>)调用之后调用
     */
    public void setOnBannerItemClickListener(BannerItemCLickListener bannerItemCLickListener) {
        itemClickListener = bannerItemCLickListener;
        viewPager.setOnItemTouchListener(new NoScrollViewPager.OnItemTouchListener() {

            @Override
            public void onItemTouch(int position) {
                if (itemClickListener != null) {
                    itemClickListener.onBannerItemClick(position % bannerList.size());
                }
            }
        });
    }

    public void setImagePath(List<BannerEntity> bannerEntityList) {
        bannerList.clear();
        bannerList.addAll(bannerEntityList);
        initUI();
    }

    public void setImagePath(List<BannerEntity> bannerEntityList,int defaultImage) {
        this.defaultImage = defaultImage;
        bannerList.clear();
        bannerList.addAll(bannerEntityList);
        initUI();
    }

    public interface BannerItemCLickListener {
        void onBannerItemClick(int position);
    }

    /**
     * 设置标题
     *
     * @param position
     */
    private void setTitle(int position) {
        if (bannerList == null)
            return;
        if (position < 0 || position > bannerList.size() - 1)
            return;
        tvTitle.setText(bannerList.get(position).title+"");
        String type = bannerList.get(position).type;
        if (TextUtils.equals(type, "0")) {
            tvType.setText("头条");
        } else if (TextUtils.equals(type, "1")) {
            tvType.setText("文化");
        } else if (TextUtils.equals(type, "2")) {
            tvType.setText("公益");
        } else if (TextUtils.equals(type, "3")) {
            tvType.setText("视频");
        } else if (TextUtils.equals(type, "4")) {
            tvType.setText("投票");
        } else if (TextUtils.equals(type, "5")) {
            tvType.setText("文明");
        } else if (TextUtils.equals(type, "6")) {
            tvType.setText("专题");
        } else if (TextUtils.equals(type, "7")) {
            tvType.setText("投票结果");
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (time != null) {
            time.cancel();
            time = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}