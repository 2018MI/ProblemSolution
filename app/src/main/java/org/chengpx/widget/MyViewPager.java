package org.chengpx.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {

    private boolean mIsNoScroll;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getCurrentItem() != 0) {
            requestDisallowInterceptTouchEvent(true);
        }
        return mIsNoScroll && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mIsNoScroll && super.onTouchEvent(ev);
    }

    public boolean isNoScroll() {
        return mIsNoScroll;
    }

    public void setNoScroll(boolean noScroll) {
        mIsNoScroll = noScroll;
    }

}
