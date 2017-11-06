package com.futureagent.lib.network.handler;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;
import com.futureagent.lib.utils.FileUtils;


/**
 * Created by tom on 17/5/18.
 */

public class OptionGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDiskCache(new DiskLruCacheFactory(FileUtils.getPrivateImageCacheDir(context).getAbsolutePath(), 500 * 1024 * 1024));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
