package com.example.photoeditor.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class NonSwipeableViewpager extends ViewPager {
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    public NonSwipeableViewpager(@NonNull Context context) {
        super(context);
        setmyscroller();
    }

    private void setmyscroller() {
        try{
            Class<?> viewpager=ViewPager.class;
            Field scroller=viewpager.getDeclaredField("MyScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public NonSwipeableViewpager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setmyscroller();
    }

    private class MyScroller extends Scroller {
        public MyScroller(Context context) {
            super(context,new DecelerateInterpolator());
        }
    }
}
