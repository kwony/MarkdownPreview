package com.kwony.mdpreview.Tabs.Pager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MainViewPager extends ViewPager {
    public MainViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /* Dragging between ViewPages in MainActivity is disabled */
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        /* Parent class ViewPager uses onTouchEvent function only to trap the status
         * mIsBeingDragged and performs dragging. As this app does not support dragging
         * between ViewPages, it is emptied and returns false as default */
        return false;
    }
}
