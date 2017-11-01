package com.futureagent.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.futureagent.lib.R;


/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class ShortcutUtil {

    public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    public static final String ACTION_REMOVE_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";

    /**
     * 创建快捷方式
     *
     * @param context
     * @param name
     */
    public static void addShortcut(Context context, String name, Activity activity) {
        Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);

        // 不允许重复创建
        addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
        // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
        // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
        // 屏幕上没有空间时会提示
        // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式

        // 名字
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

        // 图标
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context,
                        R.mipmap.ic_launcher));

        addShortcutIntent.putExtra("duplicate", false);

        // 设置关联程序
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.setClass(context, activity.getClass());
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        addShortcutIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

        // 发送广播
        context.sendBroadcast(addShortcutIntent);
    }

    /**
     * 删除快捷方式
     *
     * @param context
     * @param name
     */
    public static void removeShortcut(Context context, String name, Activity activity) {
        // remove shortcut的方法在小米系统上不管用，在三星上可以移除
        Intent intent = new Intent(ACTION_REMOVE_SHORTCUT);

        // 名字
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

        // 设置关联程序
        Intent launcherIntent = new Intent(context,
                activity.getClass()).setAction(Intent.ACTION_MAIN);

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

        // 发送广播
        context.sendBroadcast(intent);
    }

    /**
     * 判断当前应用在桌面是否有桌面快捷方式
     *
     * @param cx
     */
    public static boolean hasShortcut(Context cx) {
        boolean result = false;
        String title = null;
        final PackageManager pm = cx.getPackageManager();
        try {
            title = pm.getApplicationLabel(
                    pm.getApplicationInfo(cx.getPackageName(),
                            PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final String uriStr;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            uriStr = "content://com.android.launcher.settings/favorites?notify=true";
        } else {
            uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
        }
        final Uri CONTENT_URI = Uri.parse(uriStr);
        final Cursor c = cx.getContentResolver().query(CONTENT_URI, null,
                "title=?", new String[]{title}, null);
        if (c != null && c.getCount() > 0) {
            result = true;
        }
        if (c != null) {
            c.close();
        }
        return result;
    }

    /**
     * 是否已经创建了快捷方式
     *
     * @param context
     * @return
     */
    public static boolean isShortcutCreated(Context context) {
        return PrefManager.getBoolean(context, context.getString(R.string.preference_short_cut_created), false);
    }

    /**
     * 设置是否创建了快捷方式
     *
     * @param context
     * @param value
     */
    public static void setShortcutCreated(Context context, boolean value) {
        PrefManager.putBoolean(context, context.getString(R.string.preference_short_cut_created), value);
    }
}
