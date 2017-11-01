
package com.futureagent.lib.utils;

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
import java.util.zip.ZipFile;

public class FileUtils {
    private static final String TAG = "FileHelper";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private static final int IO_BUF_SIZE = 1024 * 32; // 32KB

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
                LogHelper.w(TAG, "Failed to close the target", e);
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
                LogHelper.w(TAG, "Failed to close the target", e);
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
            LogHelper.i(TAG, "Cannot delete " + file.getAbsolutePath() + ", which not found");
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
            LogHelper.w(TAG, "Unexpected excetion: ", e);
        } catch (IOException e) {
            LogHelper.w(TAG, "Unexpected excetion", e);
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
            LogHelper.w(TAG, "Unexpected excetion: ", e);
        } catch (IOException e) {
            LogHelper.w(TAG, "Unexpected excetion", e);
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

    public static long getFileSize(File file) {
        return getFileSize(file, 0);
    }

    private static long getFileSize(File file, int level) {
        if (file == null || !file.exists()) {
            return 0;
        }
        long size = 0;
        if (file.isDirectory()) {
            if (level >= 10) {
                return 0;
            }
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (int i = 0; i < subFiles.length; i++) {
                    size += getFileSize(subFiles[i], level + 1);
                }
            }
        } else if (file.isFile()) {
            size = file.length();
        }
        return size;
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
