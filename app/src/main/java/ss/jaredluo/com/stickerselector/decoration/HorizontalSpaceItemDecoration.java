package ss.jaredluo.com.stickerselector.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int horizontalSpaceWidth;

    public HorizontalSpaceItemDecoration() {
    }

    public void setHorizontalSpaceWidth(int horizontalSpaceWidth) {
        this.horizontalSpaceWidth = horizontalSpaceWidth;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position != 0 && position != parent.getAdapter().getItemCount() - 1) {
            outRect.right = horizontalSpaceWidth;
            if (position == 1) {
                outRect.left = horizontalSpaceWidth;
            }
        }
    }
}