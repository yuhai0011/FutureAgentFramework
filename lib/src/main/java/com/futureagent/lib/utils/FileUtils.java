
package com.futureagent.lib.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.futureagent.lib.config.LibConfigs;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.zip.ZipFile;

public class FileUtils {
    private static final String TAG = "FileHelper";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private static final int IO_BUF_SIZE = 1024 * 32; // 32KB

    // 文件缓存路径（外部私有文件）
    public final static String DIRECTORY_PRIVATE_IMAGE_CACHE = "/image";
    public final static String DIRECTORY_PRIVATE_VIDEO_CACHE = "/video";
    public final static String DIRECTORY_PRIVATE_WEB_CACHE = "/web";
    public final static String DIRECTORY_PRIVATE_DETAIL_CACHE = "/detail";
    public static final String DIRECTOR_PRIVATE_LOG = "/log";

    // 用户存储文件路径（外部公共文件）
    public final static String FILE_DIRECTORY_ROOT = "/option";
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

        if (isExternalStorageWritable() && context.getExternalFilesDir(type) != null) {
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

    public static long getFolderSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFolderSizes(flist[i]);
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
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Close a {@link Closeable} object and ignore the exception.
     * 
     * @param target The target to close. Can be null.
     */
    public static void close(Closeable target) {
        try {
            if (target != null)
                target.close();
        } catch (IOException e) {
            if (DEBUG)
                LogUtils.w(TAG, "Failed to close the target", e);
        }
    }

    /**
     * Before Android 4.4, ZipFile doesn't implement the interface
     * "java.io.Closeable". So we must provide an overridden "close" method for
     * ZipFile.
     * 
     * @param target The target to close. Can be null.
     */
    public static void close(ZipFile target) {
        try {
            if (target != null)
                target.close();
        } catch (IOException e) {
            if (DEBUG)
                LogUtils.w(TAG, "Failed to close the target", e);
        }
    }

    /**
     * Delete a file or a directory
     */
    public static void deleteFile(String dir, String filename) {
        deleteFile(new File(dir, filename));
    }

    /**
     * Delete a file or a directory
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i]);
                    }
                }
                file.delete();
            }
        } else {
            LogUtils.i(TAG, "Cannot delete " + file.getAbsolutePath() + ", which not found");
        }
    }

    public static void clearFolderFiles(File dir) {
        if (dir != null && dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File item : files) {
                    if (item.isFile()) {
                        item.delete();
                    }
                }
            }
        }
    }

    /**
     * Read all lines of input stream.
     */
    public static String readStreamAsString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder result = new StringBuilder();
        String line = null;

        boolean first = true;
        while ((line = reader.readLine()) != null) {
            if (!first) {
                result.append('\n');
            } else {
                first = false;
            }
            result.append(line);
        }

        return result.toString();
    }

    /**
     * Save the input stream into a file.</br> Note: This method will close the
     * input stream before return.
     */
    public static void saveStreamToFile(InputStream is, File file) throws IOException {
        File dirFile = file.getParentFile();
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        try {
            byte[] buffer = new byte[IO_BUF_SIZE];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } finally {
            FileUtils.close(fos);
            FileUtils.close(is);
        }
    }

    /**
     * Read all lines of input stream.
     */
    public static void readFileToStringBuilder(String filename, StringBuilder result) {
        if (result.length() > 0) {
            result.delete(0, result.length());
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            boolean first = true;
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!first) {
                    result.append('\n');
                } else {
                    first = false;
                }
                result.append(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            LogUtils.w(TAG, "Unexpected excetion: ", e);
        } catch (IOException e) {
            LogUtils.w(TAG, "Unexpected excetion", e);
        } finally {
            FileUtils.close(fis);
        }
    }

    /**
     * Read all lines of text file.
     * 
     * @return null will be returned if any error happens
     */
    public static String readFileAsString(String filename) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            return readStreamAsString(fis);
        } catch (FileNotFoundException e) {
            LogUtils.w(TAG, "Unexpected excetion: ", e);
        } catch (IOException e) {
            LogUtils.w(TAG, "Unexpected excetion", e);
        } finally {
            FileUtils.close(fis);
        }
        return null;
    }

    /**
     * Read all lines of text file and trim the result string. The returned
     * string must be non-empty if not null.
     */
    public static String readFileAsStringTrim(String filename) {
        String result = readFileAsString(filename);
        if (result != null) {
            result = result.trim();
            if (result.length() == 0) {
                result = null;
            }
        }
        return result;
    }

    public static File createExternalPath(String path) {
        if (StorageUtils.externalStorageAvailable()) {
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
            return f;
        }
        return null;
    }

    public static File getSafeExternalFile(String path, String name) {
        createExternalPath(path);
        return new File(path, name);
    }

    /**
     * null may be returned if no extension found
     * 
     * @return
     */
    public static String replaceFileExtensionName(String filename, String newExt) {
        int index = filename.lastIndexOf('.');
        if (index > 0) {
            return filename.substring(0, index + 1) + newExt;
        }
        return null;
    }

    public static byte[] computeFileMd5(File file) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            MessageDigest md5;
            md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                md5.update(buffer, 0, len);
            }
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("MD5 algorithm not found");
        } finally {
            FileUtils.close(fis);
        }
    }

    public static String fileNameWrapper(String fileName) {
        if (fileName == null) {
            return null;
        }
        if (fileName.equals("")) {
            return fileName;
        }
        StringBuilder sb = new StringBuilder("\"");
        // replace " with \" and use "" wrap the file name.
        return sb.append(fileName.replace("\"", "\\\"")).append("\"").toString();
    }

    /**
     * 复制文件
     *
     * @param oldPath 原文件路径
     * @param newPath 新文件路径
     */
    public static void copyFile(String oldPath, String newPath) {
        if (TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newPath)) {
            return;
        }
        if (oldPath.equals(newPath)) {
            return;
        }
        try {
            int byteRead = 0;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                fs.flush();
                fs.close();
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
