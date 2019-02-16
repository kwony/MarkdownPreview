package com.kwony.mdpreview.Utilities;

import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;

public class ImgTouchListener implements View.OnTouchListener {
    private int clickedColor;

    public ImgTouchListener() {
        clickedColor = 0x93939393;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                v.getBackground().setColorFilter(clickedColor, PorterDuff.Mode.SRC_ATOP);
                v.invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                v.getBackground().clearColorFilter();
                v.invalidate();
                break;
            }
        }
        return false;
    }
}