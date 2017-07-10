package ss.jaredluo.com.stickerselector.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Created by admin on 2017/7/10.
 */

public class CircleSelector extends CircleImageView {
    private float mProgressAngle = 0;
    private ValueAnimator mAnimation;

    public CircleSelector(Context context) {
        super(context);
    }

    public CircleSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleSelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        Paint paint = new Paint();
        paint.setColor(0x50000000);
        RectF oval = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        float actualSweepAngle = 360 - mProgressAngle;
        canvas.drawArc(oval, mProgressAngle - 90, actualSweepAngle, true, paint);
    }

    public void setProgress(int progress) {
        float targetAngle = (progress / 100f) * 360f;
        if (mAnimation != null) {
            mAnimation.cancel();
        }
        mAnimation = ValueAnimator.ofFloat(mProgressAngle, targetAngle);
        mAnimation.setDuration(1000);
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
        mProgressAngle = (progress / 100f) * 360f;
        invalidate();
    }
}
