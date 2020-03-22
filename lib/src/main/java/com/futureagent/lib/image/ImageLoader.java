package com.futureagent.lib.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.futureagent.lib.utils.PixelUtil;
import com.futureagent.lib.utils.ThreadUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

/**
 * Created by skywalker on 2016/12/21.
 * Email: skywalker@thecover.cn
 * Description:图片加载工具封装
 */

public class ImageLoader {
    /**
     * ImageLoader初始化，开启app时调用
     *
     * @param context
     */
    public static void init(@NonNull final Context context) {
        // 新版本使用FmAppGlideModule初始化
    }

    public static void load(Context context, File file, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false);
        try {
            Glide.with(context).load(file).apply(options).into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(Context context, File file, ImageView imageView, RequestListener listener) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false);
        try {
            Glide.with(context).load(file).listener(listener).apply(options).into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RequestOptions getRequestOptions(int error, int place) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .centerCrop()
                .error(error)
                .placeholder(place);
        return options;
    }

    public static void load(Context context, String url) {
        try {
            Glide.with(context).load(getUrl(url)).submit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(Context context, int resId, ImageView imageView) {
        try {
            Glide.with(context)
                    .load(resId)
                    .apply(new RequestOptions().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL))
                    .into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(Context context, int resId, ImageView imageView, int errId, int holderId) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .centerCrop()
                .error(errId)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .placeholder(holderId);
        try {
            Glide.with(context)
                    .load(resId)
                    .apply(options)
                    .into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <Y extends Target> void load(Context context, String url, Y target) {
        try {
            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .diskCacheStrategy(DiskCacheStrategy.DATA))
                    .into(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <Y extends Target> void load(Context context, String url, RequestOptions options, Y target) {
        try {
            Glide.with(context)
                    .load(url)
                    .apply(options)
                    .into(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadAsBitmap(Context context, String url, ImageView imageView) {
        try {
            RequestOptions options = new RequestOptions()
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context).asBitmap().load(getUrl(url)).apply(options).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(Context context, String url, ImageView imageView) {
        try {
            RequestOptions options = new RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
            Glide.with(context).load(getUrl(url)).apply(options).into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(Context context, String url, ImageView imageView, int error, int place) {
        RequestOptions options = getRequestOptions(error, place);
        try {
            Glide.with(context).load(getUrl(url)).apply(options).into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(View context, String url, ImageView imageView, int error, int place) {
        RequestOptions options = getRequestOptions(error, place);
        try {
            Glide.with(context).load(getUrl(url)).apply(options).into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(Context context, String url, ImageView imageView, RequestOptions options) {
        try {
            Glide.with(context).load(getUrl(url)).apply(options).into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(Context context, String url, ImageView imageView, RequestOptions options, RequestListener requestListener) {
        try {
            Glide.with(context).load(getUrl(url)).listener(requestListener).apply(options).into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadRound(Context context, String url, ImageView imageView, RequestOptions options) {
        try {
            Glide.with(context).load(getUrl(url)).apply(options).into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadRound(Context context, String url, ImageView imageView, int error, int place) {
        loadRound(context, url, imageView, error, place, (int) PixelUtil.dp2px(3, context));
    }

    public static void loadRound(View view, String url, ImageView imageView, int error, int place) {
        loadRound(view, url, imageView, error, place, (int) PixelUtil.dp2px(3, view.getContext()));
    }

    public static void loadRound(Context context, String url, ImageView imageView) {
        loadRound(context, url, imageView, 0, 0, (int) PixelUtil.dp2px(3, context));
    }

    public static void loadRound(Context context, String url, ImageView imageView, int error, int place, int round) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .centerCrop()
                .error(error)
                .transform(new RoundedCorners(round))
                .placeholder(place);
        try {
            Glide.with(context).load(url).apply(options).into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadRound(View view, String url, ImageView imageView, int error, int place, int round) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .centerCrop()
                .error(error)
                .transform(new RoundedCorners(round))
                .placeholder(place);
        try {
            Glide.with(view).load(url).apply(options).into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCircle(Context context, String url, ImageView imageView, int error, int place) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .error(error)
                .placeholder(place);
        try {
            Glide.with(context).load(getUrl(url)).apply(options).into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCircle(View view, String url, ImageView imageView, int error, int place) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .error(error)
                .placeholder(place);
        try {
            Glide.with(view).load(getUrl(url)).apply(options).into(new GifDrawableImageViewTarget(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 高斯模糊
     *
     * @param context
     * @param url
     * @param imageView
     */
    public static void loadBlur(Context context, String url, ImageView imageView, int errId, int holderId) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .centerCrop()
                .transform(new BlurTransformation())
                .error(errId)
                .placeholder(holderId);
        try {
            Glide.with(context).load(getUrl(url)).apply(options).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get on a background thread.
     *
     * @param context
     * @param url
     * @return
     */
    public static String downloadImg(Context context, String url) {
        try {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.DATA);
            File file = Glide.with(context).download(getUrl(url)).apply(options).submit().get();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param context
     * @param url
     * @param callBack
     */
    public static void downImage(final Context context, final String url, final FileCallBack callBack) {
        ThreadUtils.getInstance().runInThread(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestOptions options = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.DATA);
                    File file = Glide.with(context).download(getUrl(url)).apply(options).submit().get();
                    if (callBack != null && file != null) {
                        callBack.onLoad(file);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 缓存到glide缓存中，返回图片路径
     *
     * @param context
     * @param url
     * @return
     */
    public static String downImage(Context context, String url) {
        FutureTarget<File> fileTarget = Glide.with(context).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        try {
            File file = fileTarget.get();
            return file.getAbsolutePath();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void saveFileToSdCard(String filePath, byte[] bytes) throws Exception {
        //如果手机已插入sd卡,且app具有读写sd卡的权限
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        //这里就不要用openFileOutput了,那个是往手机内存中写数据的
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(filePath);
            output.write(bytes);
            //将bytes写入到输出流中

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                //关闭输出流
                output.close();
            }
        }
    }

    public interface FileCallBack {
        void onLoad(File file);
    }

    public static Object getUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return new GlideUrl("null");
        }
        if (url.startsWith("https://") || url.startsWith("http://")) {
            GlideUrl glideUrl = new GlideUrl(url,
                    new LazyHeaders
                            .Builder()
                            .addHeader("User-Agent", "fa-android")
                            .build());
            return glideUrl;
        }
        return url;
    }

    public static class GifDrawableImageViewTarget extends DrawableImageViewTarget {

        public GifDrawableImageViewTarget(ImageView view) {
            super(view);
        }

        @Override
        protected void setResource(@Nullable Drawable resource) {
            if (resource != null) {
                if (resource instanceof GifDrawable) {
                    GifDrawable gifDrawable = (GifDrawable) resource;
                    gifDrawable.setLoopCount(GifDrawable.LOOP_INTRINSIC);
                    view.setImageDrawable(gifDrawable);
                } else {
                    view.setImageDrawable(resource);
                }
            }
        }
    }
}
