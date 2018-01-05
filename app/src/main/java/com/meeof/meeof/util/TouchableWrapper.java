package com.meeof.meeof.util;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by ransikadesilva on 1/3/18.
 */

public class TouchableWrapper extends FrameLayout {

    public LockableScrollView mLockableScroll;
    public GoogleMap mGoogleMap;

    public TouchableWrapper(Context context) {
        super(context);
    }

    public void setGoogleMapAndScroll(GoogleMap googleMap, LockableScrollView lockableScroll) {
        mGoogleMap = googleMap;
        mLockableScroll = lockableScroll;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
                // UPDATE: add below line to disable zoom gesture
                mGoogleMap.getUiSettings().setZoomGesturesEnabled(false);
                mLockableScroll.setScrollingEnabled(true);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mLockableScroll.setScrollingEnabled(false);
                mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
                // UPDATE: add below line to enable zoom gesture
                mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);

                break;

            case MotionEvent.ACTION_POINTER_UP:
                // UPDATE: add below line to disable zoom gesture
                mGoogleMap.getUiSettings().setZoomGesturesEnabled(false);
                mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
                mLockableScroll.setScrollingEnabled(true);
                break;

            case MotionEvent.ACTION_UP:
                // UPDATE: add below line to disable zoom gesture
                mGoogleMap.getUiSettings().setZoomGesturesEnabled(false);
                mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
                mLockableScroll.setScrollingEnabled(true);
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
