package com.zhibo.duanshipin.fragment;;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.base.BaseLazyFragment;

import butterknife.BindView;

public class IntroductionFragment extends BaseLazyFragment {
    private static final String CONTENT = "content";
    @BindView(R.id.tv_content)
    TextView tvContent;
    private String mContent="";

    public static IntroductionFragment newInstance(String content) {
        IntroductionFragment fragment = new IntroductionFragment();
        Bundle args = new Bundle();
        args.putString(CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContent = getArguments().getString(CONTENT);
        }
    }


    @Override
    protected int getContentId() {
        return R.layout.fragment_introduction;
    }

    @Override
    protected void onLazyLoad() {
    }

    @Override
    protected void initViews(View view) {
        tvContent.setText(mContent);
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    protected long setLoadInterval() {
        return 0;
    }

    public void refreshData(String text){
        if(tvContent!=null&&!getActivity().isFinishing()){
            tvContent.setText(mContent);
        }
    }
}
