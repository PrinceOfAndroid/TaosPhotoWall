package com.taos.up.photowalllib.utils;

import android.content.Context;

/**
 * Created by PrinceOfAndroid on 2018/4/20 0020.
 */

public class SizeUtils {
    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(float dpValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
