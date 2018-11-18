package com.kwony.mdpreview.Tabs.Pager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MainViewPager extends ViewPager {
    public MainViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /* Scrolling into ViewPages in MainActivity is disabled */
        return false;
    }
}
