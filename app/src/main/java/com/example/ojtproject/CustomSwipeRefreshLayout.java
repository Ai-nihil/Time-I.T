package com.example.ojtproject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {
    private GestureDetector gestureDetector;

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
        init(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float deltaY = e2.getY() - e1.getY();
            if (Math.abs(deltaY) > SWIPE_THRESHOLD && Math.abs(velocityY) > Math.abs(velocityX)) {
                if (deltaY < 0) {
                    // Swipe up (refresh action)
                } else {
                    // Swipe down
                }
            }
            return false;
        }
    }
}
