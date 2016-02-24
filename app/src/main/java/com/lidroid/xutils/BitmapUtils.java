/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lidroid.xutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.pageview.GalleryFlow;
import com.lidroid.xutils.bitmap.BitmapCacheListener;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.BitmapGlobalConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.bitmap.core.AsyncDrawable;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.lidroid.xutils.bitmap.download.Downloader;
import com.lidroid.xutils.cache.FileNameGenerator;
import com.lidroid.xutils.task.PriorityAsyncTask;
import com.lidroid.xutils.task.PriorityExecutor;
import com.lidroid.xutils.task.TaskHandler;

import java.io.File;
import java.lang.ref.WeakReference;

public class BitmapUtils implements TaskHandler {

    private boolean pauseTask = false;
    private boolean cancelAllTask = false;
    private final Object pauseTaskLock = new Object();

    private Context context;
    private BitmapGlobalConfig globalConfig;
    private BitmapDisplayConfig defaultDisplayConfig;

    /////////////////////////////////////////////// create ///////////////////////////////////////////////////
    public BitmapUtils(Context context) {
        this(context, null);
    }

    public BitmapUtils(Context context, String diskCachePath) {
        if (context == null) {
            throw new IllegalArgumentException("context may not be null");
        }

        this.context = context.getApplicationContext();
        globalConfig = BitmapGlobalConfig.getInstance(this.context, diskCachePath);
        defaultDisplayConfig = new BitmapDisplayConfig();
    }

    public BitmapUtils(Context context, String diskCachePath, int memoryCacheSize) {
        this(context, diskCachePath);
        globalConfig.setMemoryCacheSize(memoryCacheSize);
    }

    public BitmapUtils(Context context, String diskCachePath, int memoryCacheSize, int diskCacheSize) {
        this(context, diskCachePath);
        globalConfig.setMemoryCacheSize(memoryCacheSize);
        globalConfig.setDiskCacheSize(diskCacheSize);
    }

    public BitmapUtils(Context context, String diskCachePath, float memoryCachePercent) {
        this(context, diskCachePath);
        globalConfig.setMemCacheSizePercent(memoryCachePercent);
    }

    public BitmapUtils(Context context, String diskCachePath, float memoryCachePercent, int diskCacheSize) {
        this(context, diskCachePath);
        globalConfig.setMemCacheSizePercent(memoryCachePercent);
        globalConfig.setDiskCacheSize(diskCacheSize);
    }

    //////////////////////////////////////// config ////////////////////////////////////////////////////////////////////

    public BitmapUtils configDefaultLoadingImage(Drawable drawable) {
        defaultDisplayConfig.setLoadingDrawable(drawable);
        return this;
    }

    public BitmapUtils configDefaultLoadingImage(int resId) {
        defaultDisplayConfig.setLoadingDrawable(context.getResources().getDrawable(resId));
        return this;
    }

    public BitmapUtils configDefaultLoadingImage(Bitmap bitmap) {
        defaultDisplayConfig.setLoadingDrawable(new BitmapDrawable(context.getResources(), bitmap));
        return this;
    }

    public BitmapUtils configDefaultLoadFailedImage(Drawable drawable) {
        defaultDisplayConfig.setLoadFailedDrawable(drawable);
        return this;
    }

    public BitmapUtils configDefaultLoadFailedImage(int resId) {
        defaultDisplayConfig.setLoadFailedDrawable(context.getResources().getDrawable(resId));
        return this;
    }

    public BitmapUtils configDefaultLoadFailedImage(Bitmap bitmap) {
        defaultDisplayConfig.setLoadFailedDrawable(new BitmapDrawable(context.getResources(), bitmap));
        return this;
    }

    public BitmapUtils configDefaultBitmapMaxSize(int maxWidth, int maxHeight) {
        defaultDisplayConfig.setBitmapMaxSize(new BitmapSize(maxWidth, maxHeight));
        return this;
    }

    public BitmapUtils configDefaultBitmapMaxSize(BitmapSize maxSize) {
        defaultDisplayConfig.setBitmapMaxSize(maxSize);
        return this;
    }

    public BitmapUtils configDefaultImageLoadAnimation(Animation animation) {
        defaultDisplayConfig.setAnimation(animation);
        return this;
    }

    public BitmapUtils configDefaultAutoRotation(boolean autoRotation) {
        defaultDisplayConfig.setAutoRotation(autoRotation);
        return this;
    }

    public BitmapUtils configDefaultShowOriginal(boolean showOriginal) {
        defaultDisplayConfig.setShowOriginal(showOriginal);
        return this;
    }

    public BitmapUtils configDefaultBitmapConfig(Bitmap.Config config) {
        defaultDisplayConfig.setBitmapConfig(config);
        return this;
    }

    public BitmapUtils configDefaultDisplayConfig(BitmapDisplayConfig displayConfig) {
        defaultDisplayConfig = displayConfig;
        return this;
    }

    public BitmapUtils configDownloader(Downloader downloader) {
        globalConfig.setDownloader(downloader);
        return this;
    }

    public BitmapUtils configDefaultCacheExpiry(long defaultExpiry) {
        globalConfig.setDefaultCacheExpiry(defaultExpiry);
        return this;
    }

    public BitmapUtils configDefaultConnectTimeout(int connectTimeout) {
        globalConfig.setDefaultConnectTimeout(connectTimeout);
        return this;
    }

    public BitmapUtils configDefaultReadTimeout(int readTimeout) {
        globalConfig.setDefaultReadTimeout(readTimeout);
        return this;
    }

    public BitmapUtils configThreadPoolSize(int threadPoolSize) {
        globalConfig.setThreadPoolSize(threadPoolSize);
        return this;
    }

    public BitmapUtils configMemoryCacheEnabled(boolean enabled) {
        globalConfig.setMemoryCacheEnabled(enabled);
        return this;
    }

    public BitmapUtils configDiskCacheEnabled(boolean enabled) {
        globalConfig.setDiskCacheEnabled(enabled);
        return this;
    }

    public BitmapUtils configDiskCacheFileNameGenerator(FileNameGenerator fileNameGenerator) {
        globalConfig.setFileNameGenerator(fileNameGenerator);
        return this;
    }

    public BitmapUtils configBitmapCacheListener(BitmapCacheListener listener) {
        globalConfig.setBitmapCacheListener(listener);
        return this;
    }

    ////////////////////////// display ////////////////////////////////////

    public <T extends View> void display(T container, String uri) {
        display(container, uri, null, null);
    }

    public <T extends View> void display(T container, String uri, BitmapDisplayConfig displayConfig) {
        display(container, uri, displayConfig, null);
    }

    public <T extends View> void display(T container, String uri, BitmapLoadCallBack<T> callBack) {
        display(container, uri, null, callBack);
    }

    public <T extends View> void display(T container, String uri, BitmapDisplayConfig displayConfig, BitmapLoadCallBack<T> callBack) {
        if (container == null) {
            return;
        }

        if (callBack == null) {
            callBack = new DefaultBitmapLoadCallBack<T>();
        }

        if (displayConfig == null || displayConfig == defaultDisplayConfig) {
            displayConfig = defaultDisplayConfig.cloneNew();
        }

        // Optimize Max Size
        BitmapSize size = displayConfig.getBitmapMaxSize();
        displayConfig.setBitmapMaxSize(BitmapCommonUtils.optimizeMaxSizeByView(container, size.getWidth(), size.getHeight()));

        container.clearAnimation();

        if (TextUtils.isEmpty(uri)) {
            callBack.onLoadFailed(container, uri, displayConfig.getLoadFailedDrawable());
            return;
        }

        // start loading
        callBack.onPreLoad(container, uri, displayConfig);

        // find bitmap from mem cache.
        Bitmap bitmap = globalConfig.getBitmapCache().getBitmapFromMemCache(uri, displayConfig);

        if (bitmap != null) {
            callBack.onLoadStarted(container, uri, displayConfig);
            callBack.onLoadCompleted(
                    container,
                    uri,
                    bitmap,
                    displayConfig,
                    BitmapLoadFrom.MEMORY_CACHE);
        } else if (!bitmapLoadTaskExist(container, uri, callBack)) {

            final BitmapLoadTask<T> loadTask = new BitmapLoadTask<T>(container, uri, displayConfig, callBack);

            // get executor
            PriorityExecutor executor = globalConfig.getBitmapLoadExecutor();
            File diskCacheFile = this.getBitmapFileFromDiskCache(uri);
            boolean diskCacheExist = diskCacheFile != null && diskCacheFile.exists();
            if (diskCacheExist && executor.isBusy()) {
                executor = globalConfig.getDiskCacheExecutor();
            }
            // set loading image
            Drawable loadingDrawable = displayConfig.getLoadingDrawable();
            callBack.setDrawable(container, new AsyncDrawable<T>(loadingDrawable, loadTask));

            loadTask.setPriority(displayConfig.getPriority());
            loadTask.executeOnExecutor(executor);
        }
    }

    /////////////////////////////////////////////// cache /////////////////////////////////////////////////////////////////

    public void clearCache() {
        globalConfig.clearCache();
    }

    public void clearMemoryCache() {
        globalConfig.clearMemoryCache();
    }

    public void clearDiskCache() {
        globalConfig.clearDiskCache();
    }

    public void clearCache(String uri) {
        globalConfig.clearCache(uri);
    }

    public void clearMemoryCache(String uri) {
        globalConfig.clearMemoryCache(uri);
    }

    public void clearDiskCache(String uri) {
        globalConfig.clearDiskCache(uri);
    }

    public void flushCache() {
        globalConfig.flushCache();
    }

    public void closeCache() {
        globalConfig.closeCache();
    }

    public File getBitmapFileFromDiskCache(String uri) {
        return globalConfig.getBitmapCache().getBitmapFileFromDiskCache(uri);
    }

    public Bitmap getBitmapFromMemCache(String uri, BitmapDisplayConfig config) {
        if (config == null) {
            config = defaultDisplayConfig;
        }
        return globalConfig.getBitmapCache().getBitmapFromMemCache(uri, config);
    }

    ////////////////////////////////////////// tasks //////////////////////////////////////////////////////////////////////

    @Override
    public boolean supportPause() {
        return true;
    }

    @Override
    public boolean supportResume() {
        return true;
    }

    @Override
    public boolean supportCancel() {
        return true;
    }

    @Override
    public void pause() {
        pauseTask = true;
        flushCache();
    }

    @Override
    public void resume() {
        pauseTask = false;
        synchronized (pauseTaskLock) {
            pauseTaskLock.notifyAll();
        }
    }

    @Override
    public void cancel() {
        pauseTask = true;
        cancelAllTask = true;
        synchronized (pauseTaskLock) {
            pauseTaskLock.notifyAll();
        }
    }

    @Override
    public boolean isPaused() {
        return pauseTask;
    }

    @Override
    public boolean isCancelled() {
        return cancelAllTask;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    private static <T extends View> BitmapLoadTask<T> getBitmapTaskFromContainer(T container, BitmapLoadCallBack<T> callBack) {
        if (container != null) {
            final Drawable drawable = callBack.getDrawable(container);
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable<T> asyncDrawable = (AsyncDrawable<T>) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private static <T extends View> boolean bitmapLoadTaskExist(T container, String uri, BitmapLoadCallBack<T> callBack) {
        final BitmapLoadTask<T> oldLoadTask = getBitmapTaskFromContainer(container, callBack);

        if (oldLoadTask != null) {
            final String oldUrl = oldLoadTask.uri;
            if (TextUtils.isEmpty(oldUrl) || !oldUrl.equals(uri)) {
                oldLoadTask.cancel(true);
            } else {
                return true;
            }
        }
        return false;
    }

    public class BitmapLoadTask<T extends View> extends PriorityAsyncTask<Object, Object, Bitmap> {
        private final String uri;
        private final WeakReference<T> containerReference;
        private final BitmapLoadCallBack<T> callBack;
        private final BitmapDisplayConfig displayConfig;

        private BitmapLoadFrom from = BitmapLoadFrom.DISK_CACHE;

        public BitmapLoadTask(T container, String uri, BitmapDisplayConfig config, BitmapLoadCallBack<T> callBack) {
            if (container == null || uri == null || config == null || callBack == null) {
                throw new IllegalArgumentException("args may not be null");
            }

            this.containerReference = new WeakReference<T>(container);
            this.callBack = callBack;
            this.uri = uri;
            this.displayConfig = config;
        }

        @Override
        protected Bitmap doInBackground(Object... params) {

            synchronized (pauseTaskLock) {
                while (pauseTask && !this.isCancelled()) {
                    try {
                        pauseTaskLock.wait();
                        if (cancelAllTask) {
                            return null;
                        }
                    } catch (Throwable e) {
                    }
                }
            }

            Bitmap bitmap = null;

            // get cache from disk cache
            if (!this.isCancelled() && this.getTargetContainer() != null) {
                this.publishProgress(PROGRESS_LOAD_STARTED);
                bitmap = globalConfig.getBitmapCache().getBitmapFromDiskCache(uri, displayConfig);
            }

            // download image
            if (bitmap == null && !this.isCancelled() && this.getTargetContainer() != null) {
                bitmap = globalConfig.getBitmapCache().downloadBitmap(uri, displayConfig, this);
                from = BitmapLoadFrom.URI;
            }
            if (BaseApplication.is3Dbitmap) {
                return createReflectedImages(bitmap);
            }else{
                return bitmap;
            }
        }

        public void updateProgress(long total, long current) {
            this.publishProgress(PROGRESS_LOADING, total, current);
        }

        private static final int PROGRESS_LOAD_STARTED = 0;
        private static final int PROGRESS_LOADING = 1;

        @Override
        protected void onProgressUpdate(Object... values) {
            if (values == null || values.length == 0) return;

            final T container = this.getTargetContainer();
            if (container == null) return;

            switch ((Integer) values[0]) {
                case PROGRESS_LOAD_STARTED:
                    callBack.onLoadStarted(container, uri, displayConfig);
                    break;
                case PROGRESS_LOADING:
                    if (values.length != 3) return;
                    callBack.onLoading(container, uri, displayConfig, (Long) values[1], (Long) values[2]);
                    break;
                default:
                    break;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            final T container = this.getTargetContainer();
            if (container != null) {
                if (bitmap != null) {
                    callBack.onLoadCompleted(
                            container,
                            this.uri,
                            bitmap,
                            displayConfig,
                            from);
                } else {
                    callBack.onLoadFailed(
                            container,
                            this.uri,
                            displayConfig.getLoadFailedDrawable());
                }
            }
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            synchronized (pauseTaskLock) {
                pauseTaskLock.notifyAll();
            }
        }

        public T getTargetContainer() {
            final T container = containerReference.get();
            final BitmapLoadTask<T> bitmapWorkerTask = getBitmapTaskFromContainer(container, callBack);

            if (this == bitmapWorkerTask) {
                return container;
            }

            return null;
        }
    }

    public Bitmap createReflectedImages(Bitmap bitmap) {
        // The gap we want between the reflection and the original image
        final int reflectionGap = 4;


        Bitmap originalImage = bitmap;
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                height / 2, width, height / 2, matrix, false);

        // Create a new bitmap with same width but taller to fit
        // reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 2), Bitmap.Config.ARGB_8888);

        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        // Draw in the gap
        Paint deafaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap,
                deafaultPaint);
        // Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        // Create a shader that is a linear gradient that covers the
        // reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage
                .getHeight(), 0, bitmapWithReflection.getHeight()
                + reflectionGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);
        //���ͼƬ�ľ������
//        BitmapDrawable bd = new BitmapDrawable(bitmapWithReflection);
//        bd.setAntiAlias(true);

//        ImageView imageView = new ImageView(mContext);
//        //imageView.setImageBitmap(bitmapWithReflection);
//        imageView.setImageDrawable(bd);
//        imageView.setLayoutParams(new GalleryFlow.LayoutParams(160, 240));
//        // imageView.setScaleType(ScaleType.MATRIX);
//            mImages[index++] = imageView;

        return bitmapWithReflection;
    }
}
