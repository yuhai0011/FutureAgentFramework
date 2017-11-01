package com.futureagent.lib.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.TypedValue;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class AttrUtil {

    /**
     * 获取attr预设的颜色
     * @param context
     * @param attrRes
     * @return
     */
    public static int getColor(Context context, int attrRes) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attrRes, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取attr预设的drawable
     * @param context
     * @param attrRes
     * @return
     */
    public static int getDrawableRes(Context context, int attrRes) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attrRes});
        int attributeResourceId = a.getResourceId(0, 0);
        a.recycle();
        return attributeResourceId;
    }
}
