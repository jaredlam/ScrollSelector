package ss.jaredluo.com.stickerselector.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by admin on 2017/7/10.
 */

public class CircleSelectorSelector extends CircleSelectorImageView {
    private final int TOTAL_DURATION = 1000;
    private float mProgressAngle = 0;
    private ValueAnimator mAnimation;

    public CircleSelectorSelector(Context context) {
        super(context);
    }

    public CircleSelectorSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleSelectorSelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(0x50000000);
        RectF oval = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        canvas.drawArc(oval, -90, -mProgressAngle, true, paint);
    }

    public void setProgress(int progress) {
        float targetAngle = 360f - (progress / 100f) * 360f;
        if (mAnimation != null) {
            mAnimation.cancel();
        }
        mAnimation = ValueAnimator.ofFloat(mProgressAngle, targetAngle);
        float absDiffAngle = Math.abs(mProgressAngle - targetAngle);
        int duration = (int) (absDiffAngle / 360f * TOTAL_DURATION);
        mAnimation.setDuration(duration);
        mAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgressAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimation.start();
    }

    public void setProgressWithoutAnimation(int progress) {
        mProgressAngle = 360f - (progress / 100f) * 360f;
        invalidate();
    }
}
