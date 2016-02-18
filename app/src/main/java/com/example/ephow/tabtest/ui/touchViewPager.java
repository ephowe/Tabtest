package com.example.ephow.tabtest.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ephow on 2015/10/4.
 * 自定义VIEWPAGER，可设置是否禁止左右滑动换页
 */
public class touchViewPager extends ViewPager {
    //左右换页开关,默认可滑动
    private boolean isCanScroll = true;

    public touchViewPager(Context context) {
        super(context);
    }
    public touchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setScanScroll(boolean isCanScroll){
        this.isCanScroll = isCanScroll;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return isCanScroll ? super.onInterceptTouchEvent(arg0) : false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return isCanScroll ? super.onTouchEvent(arg0) : false;
    }

}
