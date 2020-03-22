package com.futureagent.lib.image;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.futureagent.lib.utils.FileUtils;


@GlideModule
public class FaAppGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        String cacheDir = FileUtils.getPrivateImageCacheDir(context).getAbsolutePath();
        long diskCacheSize = 500 * 1024 * 1024L;
        builder.setDiskCache(new DiskLruCacheFactory(cacheDir, diskCacheSize));

        int memoryCacheSize = 30 * 1024 * 1024;

        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(memoryCacheSize));
    }

}
