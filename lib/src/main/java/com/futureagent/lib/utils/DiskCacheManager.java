package com.futureagent.lib.utils;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class DiskCacheManager {

    // 单例模式
    private DiskCacheManager() {
    }

    private static final class DiskCacheManagerHolder {
        private final static DiskCacheManager INSTANCE = new DiskCacheManager();
    }

    public static final DiskCacheManager getInstance() {
        return DiskCacheManagerHolder.INSTANCE;
    }

    // todo 缓存文件
}
