package com.example.graduatedesign.hilt;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.graduatedesign.net.MyTokenInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class ApplicationModule {
    private static final String TAG = "ApplicationModule";

    @NonNull
    @Singleton
    @Provides
    public Retrofit provideRetrofit(OkHttpClient OkHttpClient, GsonConverterFactory gsonConverterFactory) {
        return new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3165/")
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // 支持RxJava
                .validateEagerly(true)//提前检查请求是否合法，开发时用
                .client(OkHttpClient)
                .build();
    }

    @Singleton
    @Provides
    public GsonConverterFactory provideGsonConverterFactory() {
        GsonBuilder builder = new GsonBuilder();
        /*如果不设置serializeNulls,序列化(获取json对象)时默认忽略NULL*/
        builder.serializeNulls();
        /*使打印的json字符串更美观，如果不设置，打印出来的字符串不分行*/
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return GsonConverterFactory.create(gson);
    }

    @Singleton
    @Provides
    public OkHttpClient provideOkhttpClient(MyTokenInterceptor myTokenInterceptor) {
        // 日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        // 新建日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.i("Http请求参数：", message));
        loggingInterceptor.setLevel(level);

        // 开始定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(4, TimeUnit.SECONDS)
                .callTimeout(8,TimeUnit.SECONDS);
        // OkHttp进行添加拦截器
        httpClientBuilder.addInterceptor(myTokenInterceptor);
        httpClientBuilder.addInterceptor(loggingInterceptor);

        return httpClientBuilder.build();
    }

}
