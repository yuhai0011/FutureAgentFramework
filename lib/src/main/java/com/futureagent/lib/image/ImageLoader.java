package com.futureagent.lib.image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.request.RequestListener;
import com.futureagent.lib.utils.FileUtils;

import java.io.File;

/**
 * Created by skywalker on 2016/12/21.
 * Email: skywalker@thecover.cn
 * Description:图片加载工具封装
 */

public class ImageLoader {
    /**
     * 图片淡入时间
     */
    private static final int IMAGE_FADE_DURATION = 500;

    /**
     * ImageLoader初始化，开启app时调用
     *
     * @param context
     */
    public static void init(@NonNull final Context context) {
        GlideBuilder builder = new GlideBuilder(context);

        // disk cache
        // 最大缓存500M
        builder.setDiskCache(new DiskLruCacheFactory(FileUtils.getPrivateImageCacheDir(context).getAbsolutePath(), 500 * 1024 * 1024));
    }

    public static void load(Context context, String url, ImageView imageView, int errId, int holderId) {
        Glide.with(context).load(url).placeholder(holderId).error(errId).crossFade(IMAGE_FADE_DURATION).into(imageView);
    }

    public static void load(Context context, int resId, ImageView imageView, int errId, int holderId) {
        Glide.with(context).load(resId).placeholder(holderId).error(errId).crossFade(IMAGE_FADE_DURATION).into(imageView);
    }

    public static void loadCircle(Context context, String url, ImageView imageView, int errId, int holderId) {
        Glide.with(context).load(url).placeholder(holderId).error(errId).crossFade(IMAGE_FADE_DURATION).bitmapTransform(new GlideCircleTransform(context)).into(imageView);
    }

    public static void loadRound(Context context, String url, ImageView imageView, int errId, int holderId, int dp) {
        Glide.with(context).load(url).placeholder(holderId).error(errId).crossFade(IMAGE_FADE_DURATION).bitmapTransform(new GlideRoundTransform(context, dp)).into(imageView);
    }

    public static void loadBlur(Context context, String url, ImageView imageView, int errId, int holderId) {
        Glide.with(context).load(url).placeholder(holderId).error(errId).crossFade(IMAGE_FADE_DURATION).bitmapTransform(new GlideBlurTransform(context)).into(imageView);
    }

    public static void loadFile(Context context, File file, ImageView imageView) {
        Glide.with(context).load(file).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
    }

    public static void loadFile(Context context, File file, ImageView imageView, RequestListener listener) {
        Glide.with(context).load(file).skipMemoryCache(true).crossFade()
                .listener(listener).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
    }
}
