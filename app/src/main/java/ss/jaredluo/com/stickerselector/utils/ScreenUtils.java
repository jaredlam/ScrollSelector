package ss.jaredluo.com.stickerselector.utils;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by admin on 2017/7/4.
 */

public class ScreenUtils {
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }


    public static float convertDpToPx(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

}
