package org.chengpx.widget;


import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DragScaleImageView extends android.support.v7.widget.AppCompatImageView implements View.OnTouchListener {

    private static final Integer DRAG = 10;// 拖拉模式
    private static final Integer SCALE = 11;// 缩放模式
    private static Integer mMode = null;
    private Matrix mCurrentMatrix = new Matrix();
    private Matrix mMatrix = new Matrix();
    private PointF mStartPointF;
    private float mStartDistance;
    private PointF mMidPointF;


    public DragScaleImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(this);
    }

    public DragScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {// 取低八位
            case MotionEvent.ACTION_DOWN:// 手指按下
                mMode = DRAG;
                mStartPointF = new PointF(event.getX(0), event.getY(0));
                mCurrentMatrix.set(getImageMatrix());// 记录当前图片按下的矩阵位置
                break;
            case MotionEvent.ACTION_MOVE:
                if (DRAG.equals(mMode)) {
                    float dx = event.getX(0) - mStartPointF.x;
                    float dy = event.getY(0) - mStartPointF.y;
                    // 在手指按下的矩阵位置的基础之上进行移动
                    mMatrix.set(mCurrentMatrix);
                    mMatrix.postTranslate(dx, dy);
                } else if (SCALE.equals(mMode)) {
                    float varDistance = calcDistance(new PointF(event.getX(0), event.getY(0)),
                            new PointF(event.getX(1), event.getY(1)));
                    float scale = varDistance / mStartDistance;
                    // 基于当前矩阵进行缩放
                    mMatrix.set(mCurrentMatrix);
                    mMatrix.postScale(scale, scale, mMidPointF.x, mMidPointF.y);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:// 屏幕上已经存在一根手指, 但继续按下另一根手指
                System.out.println("屏幕上已经存在一根手指, 但继续按下另一根手指");
                mMode = SCALE;
                float endX = event.getX(1);
                float endY = event.getY(1);
                // 得到中心点的坐标
                mMidPointF = new PointF((endX + mStartPointF.x) / 2, (endY + mStartPointF.y) / 2);
                PointF endPointF = new PointF(endX, endY);
                mStartDistance = calcDistance(mStartPointF, endPointF);// 得到开始距离
                mCurrentMatrix.set(getImageMatrix());// 记录当前 imageView 的缩放倍数
                break;
            case MotionEvent.ACTION_POINTER_UP:// 有手指离开屏幕, 但屏幕上还有手指
                System.out.println("有手指离开屏幕, 但屏幕上还有手指");
                mMode = null;
                break;
            case MotionEvent.ACTION_UP:// 手指抬起
                System.out.println("手指抬起");
                mMode = null;
                break;
        }
        setImageMatrix(mMatrix);// 将矩阵设置给图片
        return true;// 消费掉该事件
    }

    private float calcDistance(PointF startPointF, PointF endPointF) {
        float a = Math.abs(endPointF.x - startPointF.x);
        float b = Math.abs(endPointF.y - startPointF.y);
        return (float) Math.pow(Math.pow(a, 2) + Math.pow(b, 2), 0.5);
    }

}