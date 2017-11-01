package com.futureagent.lib.utils;

import android.content.Context;
import android.provider.Settings.Secure;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by yuhai on 16-2-27.
 */
public class TokenManager {
    private static String uuid;
    private static final String COMMON_ANDROID_ID = "9774d56d682e549c";

    public static String getToken(Context ctx) {
        if (uuid == null) {
            synchronized (TokenManager.class) {
                if (uuid == null) {
                    final String id = TokenSharedPref.getToken(ctx);
                    if (!TextUtils.isEmpty(id)) {
                        uuid = id;
                    } else {
                        final String androidId = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
                        try {
                            if (!COMMON_ANDROID_ID.equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                            } else {
                                uuid = UUID.randomUUID().toString();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        uuid = uuid.replace("-", "");
                        TokenSharedPref.setToken(ctx, uuid);
                    }
                }
            }
        }
        return uuid;
    }

    private static class TokenSharedPref {
        protected static final String PREFS_DEVICE_ID = "TokenSharedPref_device_id";

        public static String getToken(Context ctx) {
            return (String) SharedPrefUtils.get(ctx, PREFS_DEVICE_ID, "");
        }

        public static void setToken(Context ctx, String value) {
            SharedPrefUtils.put(ctx, PREFS_DEVICE_ID, value);
        }
    }
}
