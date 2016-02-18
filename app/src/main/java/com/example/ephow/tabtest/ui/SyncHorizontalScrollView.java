package com.example.ephow.tabtest.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.ephow.tabtest.R;

public class SyncHorizontalScrollView extends HorizontalScrollView {
    //private int windowWitdh = 0;
    private Activity mContext;
    private LayoutInflater mInflater;
    //view
    private View view;
    private RadioGroup rg_nav_content;
    private ImageView iv_nav_indicator;
    private ViewPager mViewPager;
    //
    private int count;// 屏幕显示的标签个数
    private int indicatorWidth;// 每个标签所占的宽度
    private int currentIndicatorLeft = 0;// 当前所在标签页面的位移
    private int scrollX;


    public SyncHorizontalScrollView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub  
    }

    public SyncHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub  
    }

    public void setSomeParam(ViewPager mViewPager, Activity context, String[] tabTitle, int count) {
        this.mContext = context;
        this.mViewPager = mViewPager;
        this.count = count;
        //this.view = view;
        initView(tabTitle);
    }

    private void initView(String[] tabTitle) {
        //获取布局填充器
        //mInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        //另一种方式获取
        mInflater = LayoutInflater.from(mContext);
        this.view = mInflater.inflate(R.layout.sync_hsv_item, null);
        this.addView(view);
        //获取标签容器ID
        rg_nav_content = (RadioGroup) view.findViewById(R.id.rg_nav_content);
        //获取滑动下标ID
        iv_nav_indicator = (ImageView) view.findViewById(R.id.iv_nav_indicator);
        //获取屏幕分辨率等信息到dm
        DisplayMetrics dm = new DisplayMetrics();
        this.mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
        //windowWitdh = dm.widthPixels;
        indicatorWidth = dm.widthPixels / count;
        ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
        iv_nav_indicator.setLayoutParams(cursor_Params);
        setListener();
        initNavigationHSV(tabTitle);
    }

    private void initNavigationHSV(String[] tabTitle) {
        rg_nav_content.removeAllViews();
        for(int i=0;i<tabTitle.length;i++){
            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.nav_radiogroup_item, null);
            rb.setId(i);
            rb.setText(tabTitle[i]);
            rb.setLayoutParams(new ViewGroup.LayoutParams(indicatorWidth, ViewGroup.LayoutParams.MATCH_PARENT));
            rg_nav_content.addView(rb);
        }
        //复位ViewGroup.mCheckedId, 否则会引起监听响应问题
        rg_nav_content.check(0);
    }

    private void setListener() {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(rg_nav_content!=null && rg_nav_content.getChildCount()>position){
                    rg_nav_content.getChildAt(position).performClick();
                }
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                //int a = arg0;
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {}
        });

        rg_nav_content.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getChildAt(checkedId) != null) {
                    TranslateAnimation animation = new TranslateAnimation(currentIndicatorLeft, group.getChildAt(checkedId).getLeft(), 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);
                    //执行位移动画
                    iv_nav_indicator.startAnimation(animation);
                    //((RadioButton) rg_nav_content.getChildAt(checkedId)).setTextColor(0xbee65a);
                    mViewPager.setCurrentItem(checkedId);   //ViewPager 跟随一起 切换
                    //记录当前 下标的距最左侧的 距离
                    //currentIndicatorLeft = ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft();
                    currentIndicatorLeft = indicatorWidth * checkedId;
                    scrollX = (checkedId > 1 ? currentIndicatorLeft : 0) - indicatorWidth * 2;
                    smoothScrollTo(scrollX, 0);
                    //checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0) - ((RadioButton) rg_nav_content.getChildAt(2)).getLeft()
                }
            }
        });
    }

    public int getIndicatorWidth(){
        return indicatorWidth;
    }

}  