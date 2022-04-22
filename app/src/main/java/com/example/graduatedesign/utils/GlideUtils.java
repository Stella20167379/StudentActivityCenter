package com.example.graduatedesign.utils;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.graduatedesign.R;

public class GlideUtils {
    //Glide加载图片的具体设置
    public static final RequestOptions OPTIONS = new RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存策略
            .placeholder(R.drawable.ic_img_loading_holder)//加载时的占位图
            .error(R.drawable.ic_img_error_holder)//加载失败的展示图
            .centerCrop()//图片填充方式
            .dontAnimate()//没有动画
            ;


}
