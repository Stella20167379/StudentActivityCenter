package com.example.graduatedesign.hilt;

import android.app.Application;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.HiltAndroidApp;

/**
 * 由Hilt生成基类，作为应用级依赖项容器
 * 生成的这一 Hilt 组件会附加到 Application 对象的生命周期，并为其提供依赖项。
 * 此外，它也是应用的父组件，这意味着，其他组件可以访问它提供的依赖项
 */
@HiltAndroidApp
public class MyApplication extends Application {

}
