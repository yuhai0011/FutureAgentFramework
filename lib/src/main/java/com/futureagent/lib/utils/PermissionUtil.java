package com.futureagent.lib.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class PermissionUtil{
    private static final int REQUEST_PERMISION = 1;

    /**
     * 最需要的权限是否已经允许
     *
     * @param context
     * @return
     */
    public static boolean isAllowed(Context context){
        return context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取全部需要用户允许的权限
     * @param act
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void getPermision(Activity act, Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        ArrayList<String> pers = new ArrayList<String>();
        String permissions[] = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        for (String permission : permissions)
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.shouldShowRequestPermissionRationale(act, permission);//显示请求权限
                pers.add(permission);
            }

        ActivityCompat.requestPermissions(act, pers.toArray(new String[pers.size()]), REQUEST_PERMISION);//请求权限
    }
}
