package ss.jaredluo.com.stickerselector.utils;

import android.content.res.Resources;

/**
 * Created by admin on 2017/7/4.
 */

public class ScreenUtils {
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
}
