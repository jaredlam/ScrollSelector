package ss.jaredluo.com.stickerselector.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import ss.jaredluo.com.stickerselector.decoration.HorizontalSpaceItemDecoration;
import ss.jaredluo.com.stickerselector.utils.ScreenUtils;

/**
 * Created by admin on 2017/7/4.
 */

public class ScrollSelectorView extends RecyclerView {


    private int mHorizontalSpace;

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
        mHorizontalSpace = (int) ScreenUtils.convertDpToPx(10);
        addItemDecoration(new HorizontalSpaceItemDecoration(mHorizontalSpace));
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public int getHorizontalSpace() {
        return mHorizontalSpace;
    }

    @Override
    public void scrollBy(int x, int y) {
        super.scrollBy(x, y);
    }

}
