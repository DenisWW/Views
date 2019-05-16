package com.runviews.viewslibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class MicrosoftLoadView extends View {

    private int itemCount;
    private Context mContext;
    private Paint paint;
    @ColorInt
    int[] colors = new int[]{
            R.color.color_f1521b,
            R.color.color_00adef,
            R.color.color_80cd29,
            R.color.color_fabc09
    };
    private int scaleIngItem = 0, infactX, infactY;
    private int screenWidth, screenHeight;
    private float scaleValue = 1, halfWidth, quarterWidth;
    private ValueAnimator valueAnimator;
    private boolean isRun = false;
    private boolean isOverOnce = false;
    private float referenceValue;

    //    private Matrix[][] leftTopMatrix = new Matrix[2][2];
//    private RectF[][] leftTopRFs = new RectF[2][2];
//    private RectF[][] allTest = new RectF[2][2];
//    private RectF leftTopRF, rightTopRF, leftBottomRF, rightBottomRF;
//    private Path leftTopPath, rightTopPath, leftBottomPath, rightBottomPath;
    private RectF[][][] all;
    private RectF onceRF;
    private Path oncePath;
    private float addStartH = 0, addStartW = 0;
    private float[] scaleValues;
    private int infactItemCount;
    private int Granule = 1;
    private boolean isLeftStart = false;
    private ValueAnimator value;
    private long beforeTime;

    public MicrosoftLoadView(Context context) {
        this(context, null);
    }

    public MicrosoftLoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
        if (paint == null) paint = new Paint();
        mContext = context;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        valueAnimator = ValueAnimator.ofFloat(1.0f, 0.2f, 1.0f, 1.0f).setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        int loading = 3;
        float average;//一个格子能分多少
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scaleValue = Float.valueOf(animation.getAnimatedValue().toString());
                if (animation.getCurrentPlayTime() - beforeTime > 50) {
                    beforeTime = animation.getCurrentPlayTime();
                    for (int i = scaleValues.length - 1; i > 0; i--) {
                        scaleValues[i] = scaleValues[i - 1];
                    }
                    scaleValues[0] = scaleValue;
                }
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {

            }

            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                scaleIngItem++;
                if (scaleIngItem >= scaleValues.length) {
                    isOverOnce = true;
                    scaleIngItem = 0;
                }
                beforeTime = 0;
                if (!animation.isRunning()) valueAnimator.start();
            }
        });
    }

    private int findMinPosScale(float[] scaleValues) {
        int minPos = 0;
        float min = scaleValues[0];
        for (int m = 1; m < scaleValues.length; m++) {
            if (min > scaleValues[m]) {
                minPos = m;
                min = scaleValues[m];
            }
        }
        return minPos;
    }

    private void initData() {
        itemCount = 8;
        infactItemCount = 2 * itemCount;
        scaleIngItem = 0;
        all = new RectF[4][itemCount][itemCount];
        scaleValues = new float[4 * itemCount - 1];
        for (int i = 0; i < scaleValues.length; i++) {
            scaleValues[i] = 1;
        }
    }

    public void setItemCount(int itemCount) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            initData(itemCount);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initData(int itemCount) {
        this.itemCount = itemCount;
        infactItemCount = 2 * itemCount;
        scaleIngItem = 0;
        quarterWidth = 0;
        beforeTime = 0;
        all = new RectF[4][itemCount][itemCount];
        scaleValues = new float[4 * itemCount - 1];
        for (int i = 0; i < scaleValues.length; i++) {
            scaleValues[i] = 1;
        }
        isRun = false;
        valueAnimator.pause();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (halfWidth == 0) halfWidth = this.getWidth() / 2;
        if (quarterWidth == 0) quarterWidth = this.getWidth() / infactItemCount;
        float scaleValue = 1;
        for (int al = 0; al < all.length; al++) {
            oncePath = new Path();
            addStartW = (al % 2) * halfWidth;
            addStartH = (int) (al / 2) * halfWidth;
            for (int y = 0; y < all[al].length; y++) {
                for (int x = 0; x < all[al][y].length; x++) {
                    onceRF = new RectF(
                            x * quarterWidth + addStartW,
                            y * quarterWidth + addStartH,
                            (x * quarterWidth) + quarterWidth + addStartW,
                            (y * quarterWidth) + quarterWidth + addStartH);
                    Matrix matrix = new Matrix();
                    matrix.mapRect(onceRF);
                    infactX = (x + itemCount * (al % 2));
                    infactY = (y + itemCount * (al / 2));
                    if (isLeftStart) {
                        scaleValue = scaleValues[infactX + infactY];
                    } else {
                        scaleValue = scaleValues[infactX - infactY + (infactItemCount - 1)];
                    }
                    matrix.postScale(scaleValue, scaleValue, (onceRF.width() / 2) + onceRF.left, (onceRF.height() / 2) + onceRF.top);
                    matrix.mapRect(onceRF);
                    oncePath.addRect(onceRF, Path.Direction.CW);
                }
            }
            paint.setColor(mContext.getResources().getColor(colors[al]));
            canvas.drawPath(oncePath, paint);
        }
        if (!isRun) {
            isRun = true;
//            value.start();
            valueAnimator.start();
        }


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
