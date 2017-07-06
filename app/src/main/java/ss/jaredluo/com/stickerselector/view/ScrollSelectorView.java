package ss.jaredluo.com.stickerselector.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import ss.jaredluo.com.stickerselector.decoration.HorizontalSpaceItemDecoration;

/**
 * Created by admin on 2017/7/4.
 */

public class ScrollSelectorView extends RecyclerView {


    public ScrollSelectorView(Context context) {
        super(context);
        initSpaceDecoration();
    }

    public ScrollSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initSpaceDecoration();
    }

    public ScrollSelectorView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initSpaceDecoration();
    }

    private void initSpaceDecoration(){
        addItemDecoration(new HorizontalSpaceItemDecoration(0));
    }

    @Override
    public void scrollBy(int x, int y) {
        super.scrollBy(x, y);
    }


}
