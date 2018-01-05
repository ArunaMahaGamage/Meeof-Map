package com.meeof.meeof.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.meeof.meeof.R;

/**
 * Created by ransikadesilva on 10/2/17.
 */

public class CustomFontTextView extends android.support.v7.widget.AppCompatTextView {

    String ttfName;
    String TAG = getClass().getName();

    public CustomFontTextView(Context context) {
        super(context);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        String customFont = a.getString(R.styleable.CustomFontTextView_fontName);
        setCustomFont(ctx, customFont);
        a.recycle();
    }


    public boolean setCustomFont(Context context, String asset) {
        Typeface tf = null;
        ttfName = asset;
        try {
            tf = Typeface.createFromAsset(context.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: " + e.getMessage());
            return false;
        }

        setTypeface(tf);
        return true;
    }

    public String getTTFName() {
        return ttfName;
    }
}
