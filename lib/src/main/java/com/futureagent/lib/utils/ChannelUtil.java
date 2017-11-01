package com.futureagent.lib.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.futureagent.lib.config.LibConfigs;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class ChannelUtil {
    private static Boolean DEBUG = LibConfigs.DEBUG_LOG;
    /**
     * 从assets读取lc
     */
    public static String getChannelFromAssets(Context context) {
        BufferedReader readLc = null;
        AssetManager am = context.getAssets();
        try {
            InputStream input = am.open("lc.txt");
            readLc = new BufferedReader(new InputStreamReader(input, "utf-8"));
            return readLc.readLine();
        } catch (IOException e) {
            if (DEBUG)
                e.printStackTrace();
        } finally {
            close(readLc);
        }
        return "";
    }

    private static void close(Closeable target) {
        if (target != null) {
            try {
                target.close();
            } catch (IOException e) {
                target = null;
                if (DEBUG)
                    e.printStackTrace();
            }
        }
    }
}
