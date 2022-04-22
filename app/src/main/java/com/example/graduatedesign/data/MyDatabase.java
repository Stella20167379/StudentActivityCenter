package com.example.graduatedesign.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.graduatedesign.data.dao.CollegeDao;
import com.example.graduatedesign.data.dao.MessageDao;
import com.example.graduatedesign.data.model.College;
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.utils.AssetsUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Database(entities = {College.class, Message.class}, version = 1, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class MyDatabase extends RoomDatabase {
    private static final String TAG = "MyDatabase";

    public abstract CollegeDao getCollegeDao();
    public abstract MessageDao getMessageDao();

    /* 变量声明 */
    private static volatile MyDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    /**
     * 使用单例模式返回数据库对象，同时设有创建回调操作用以预填充数据库
     * 对数据库的操作必须开辟子线程，UI线程中操作会报异常
     *
     * @param context 上下文环境
     * @return 数据库对象
     */
    public static MyDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            MyDatabase.class, "my_database")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Log.d(TAG, "onCreate: 创建回调中");
                                    Completable.create(emitter -> {
                                        List<College> collegeList = AssetsUtil.getColleges(context);
                                        if (collegeList != null){
                                            Log.d(TAG, "onCreate: 读取到数据，另开线程异步插入数据库");
                                            INSTANCE.getCollegeDao().insertAll(collegeList);
                                        }
                                    })
                                    .subscribeOn(Schedulers.computation())
                                    .subscribe();
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
