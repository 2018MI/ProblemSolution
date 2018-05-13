package org.chengpx.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * create at 2018/5/12 8:14 by chengpx
 */

///
public class MyGridView extends GridView {

    private boolean mIsMeasure;

    public MyGridView(Context context) {
        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mIsMeasure = true;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mIsMeasure = false;
        super.onLayout(changed, l, t, r, b);
    }

    public boolean isMeasure() {
        return mIsMeasure;
    }
}
