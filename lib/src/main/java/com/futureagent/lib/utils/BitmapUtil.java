package com.futureagent.lib.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class BitmapUtil {

    /**
     * 压缩到sizeRequired尺寸
     *
     * @param filePathStr
     * @param sizeRequired  如720，1024
     * @return
     */
    public final static Bitmap compressImageFile(String filePathStr, int sizeRequired) {
        try {

            File f = new File(filePathStr);

            if (f != null && f.isFile() && sizeRequired > 0) {

                // Decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                FileInputStream fi = new FileInputStream(f);
                BitmapFactory.decodeStream(fi, null, o);
                fi.close();

                // Find the correct scale value. It should be the power of 2.
                int scale = 1;
                while (o.outWidth / scale / 2 >= sizeRequired &&
                        o.outHeight / scale / 2 >= sizeRequired) {
                    scale *= 2;
                }

                // Decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;

                FileInputStream fis = new FileInputStream(f);

                Bitmap bitmap = BitmapFactory.decodeStream(fis, null, o2);
                fis.close();

                // recreate file with compress bitmap
                FileOutputStream fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();

                return bitmap;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转指定图片
     *
     * @param imgPath 图片路径
     * @param degree  角度
     * @return
     */
    public static void turnBitmap(String imgPath, int degree) {
        Bitmap img = BitmapFactory.decodeFile(imgPath);
        Matrix matrix = new Matrix();
        matrix.postRotate(+degree); /*翻转度数*/
        int width = img.getWidth();
        int height = img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);

        saveBitmapFile(imgPath, img);
    }

    /**
     * 图片转文件
     *
     * @param filePath 文件路径
     * @param bitmap   图片
     */
    public static void saveBitmapFile(String filePath, Bitmap bitmap) {
        File file = new File(filePath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 缩放图片大小
     *
     * @param bm        图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    /**
     * 复制图片文件
     *
     * @param oldPath 原文件路径
     * @param newPath 新文件路径
     */
    public static void copyImageFile(String oldPath, String newPath) {
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
