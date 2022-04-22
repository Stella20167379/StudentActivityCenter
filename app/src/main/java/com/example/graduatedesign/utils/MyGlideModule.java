package com.example.graduatedesign.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.request.RequestOptions;


public class MyGlideModule {

    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        int defaultArrayPoolSize = calculator.getArrayPoolSizeInBytes();
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565));
        builder.setMemoryCache(new LruResourceCache(defaultMemoryCacheSize/2));
        builder.setBitmapPool(new LruBitmapPool(defaultBitmapPoolSize/2));
        builder.setArrayPool(new LruArrayPool(defaultArrayPoolSize/2));
    }
}
