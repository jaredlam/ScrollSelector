package ss.jaredluo.com.stickerselector.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import ss.jaredluo.com.stickerselector.decoration.HorizontalSpaceItemDecoration;

/**
 * Created by admin on 2017/7/4.
 */

public class ScrollSelectorView extends RecyclerView {


    private HorizontalSpaceItemDecoration mDecoration;

    public ScrollSelectorView(Context context) {
        super(context);
        init();
    }

    public ScrollSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollSelectorView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public void setSpace(int space) {
        if (mDecoration == null) {
            mDecoration = new HorizontalSpaceItemDecoration();
        }
        mDecoration.setHorizontalSpaceWidth(space);
        removeItemDecoration(mDecoration);
        addItemDecoration(mDecoration);
    }

    @Override
    public void scrollBy(int x, int y) {
        super.scrollBy(x, y);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX *= 0.4;
        return super.fling(velocityX, velocityY);
    }
}
