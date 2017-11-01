package com.futureagent.lib.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class FileSystemUtil {

    // 文件缓存路径（外部私有文件）
    public final static String DIRECTORY_PRIVATE_IMAGE_CACHE = "/image";
    public final static String DIRECTORY_PRIVATE_VIDEO_CACHE = "/video";
    public final static String DIRECTORY_PRIVATE_WEB_CACHE = "/web";
    public final static String DIRECTORY_PRIVATE_DETAIL_CACHE = "/detail";
    public static final String DIRECTOR_PRIVATE_LOG = "/log";

    // 用户存储文件路径（外部公共文件）
    public final static String FILE_DIRECTORY_ROOT = "/cover";
    public final static String DIRECTORY_IMAGE = FILE_DIRECTORY_ROOT + "/image";
    public final static String DIRECTORY_VIDEO = FILE_DIRECTORY_ROOT + "/video";
    public final static String DIRECTORY_APK = FILE_DIRECTORY_ROOT + "/apk";

    ////////// 私有文件缓存 //////////

    /**
     * 获取外部私有文件 缓存的根路径
     *
     * @param context
     * @return
     */
    public static final File getPrivateRootCacheDir(Context context) {
        return getPrivateCacheDir(context, null);
    }

    /**
     * 获取外部私有文件 图片缓存路径
     *
     * @param context
     * @return
     */
    public static final File getPrivateImageCacheDir(Context context) {
        return getPrivateCacheDir(context, DIRECTORY_PRIVATE_IMAGE_CACHE);
    }

    /**
     * 获取外部私有文件 视频缓存路径
     *
     * @param context
     * @return
     */
    public static final File getPrivateVideoCacheDir(Context context) {
        return getPrivateCacheDir(context, DIRECTORY_PRIVATE_VIDEO_CACHE);
    }

    /**
     * 获取外部私有文件 web缓存路径
     *
     * @param context
     * @return
     */
    public static final File getPrivateWebCacheDir(Context context) {
        return getPrivateCacheDir(context, DIRECTORY_PRIVATE_WEB_CACHE);
    }

    /**
     * 获取外部私有文件 缓存路径
     *
     * @param context
     * @param type
     * @return
     */
    public static final File getPrivateCacheDir(Context context, String type) {
        File dir;

        if (isExternalStorageWritable()) {
            dir = context.getExternalFilesDir(type);
        }
        else {
            dir = context.getFilesDir();

            if (type != null) {
                dir = new File(context.getFilesDir(), type);
            }
        }

        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    ////////// 共享文件 //////////

    /**
     * 获取外部共享文件 图片存储路径
     *
     * @return
     */
    public static final File getPublicImageStoreDir(Context context) {
        return getPublicFileStoreDir(context, DIRECTORY_IMAGE);
    }

    /**
     * 获取外部共享文件 视频存储路径
     *
     * @return
     */
    public static final File getPublicVideoStoreDir(Context context) {
        return getPublicFileStoreDir(context, DIRECTORY_VIDEO);
    }

    /**
     * 获取外部共享文件 apk安装包存储路径
     *
     * @return
     */
    public static final File getPublicApkStoreDir(Context context) {
        return getPublicFileStoreDir(context, DIRECTORY_APK);
    }

    /**
     * 获取外部共享文件 apk安装包存储路径
     *
     * @return
     */
    public static final File getPrivateApkStoreDir(Context context) {
        return getPrivateCacheDir(context, DIRECTORY_APK);
    }

    /**
     * 获取外部公有文件 图片存储路径
     *
     * @param type
     * @return
     */
    private static final File getPublicFileStoreDir(Context context, String type) {

        File dir;

        if (isExternalStorageWritable()) {
            dir = Environment.getExternalStoragePublicDirectory(type);
        }
        else {
            dir = context.getFilesDir();

            if (type != null) {
                dir = new File(context.getFilesDir(), type);
            }
        }

        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        return (dir != null && dir.exists()) ? dir : getPrivateCacheDir(context, type);
    }

    ////////// 其他 //////////

    /**
     * 检查外部存储是否可以 读写
     *
     * @return
     */
    public final static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public final static String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.0");
        String fileSizeString = "";
        if (size == 0) {
            fileSizeString = "0M";
        } else if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "KB";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static boolean deleteDir(Context context, File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children.length == 0) {
                return true;
            }
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(context, new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();

            fis.close();
        } else {
            file.createNewFile();
        }
        return size;
    }

    public static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null) info = new PackageInfo();
        return info;
    }

    public static String getVersion(Context context) {
        return getPackageInfo(context).versionName;
    }

    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    /**
     * SdCard是否存在
     *
     * @return
     */
    public static boolean isSDCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getFilePathFromUri(Context c, Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.MediaColumns.DATA};
            ContentResolver contentResolver = c.getContentResolver();

            Cursor cursor = contentResolver.query(uri, filePathColumn, null, null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if ("file".equals(uri.getScheme())) {
            filePath = new File(uri.getPath()).getAbsolutePath();
        }
        return filePath;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
