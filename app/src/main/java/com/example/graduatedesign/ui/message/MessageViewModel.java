package com.example.graduatedesign.ui.message;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.Message;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 本model的生命周期与activity绑定，实现MessageFragment和MessageDetailViewModel数据共通，同时节省空间
 */
@HiltViewModel
public class MessageViewModel extends ViewModel {
    private final MyRepository myRepository;

    @Inject
    public MessageViewModel(MyRepository myRepository) {
        this.myRepository = myRepository;
    }

    /**
     * 展示的总览消息列表
     */
    private MutableLiveData<List<Message>> overViewMessages = new MutableLiveData<>();
    /**
     * 当前用户查看的详情信息列表
     */
    private MutableLiveData<List<Message>> detailMessages = new MutableLiveData<>();
    /**
     * 用户发送的入会申请
     */
    private MutableLiveData<List<Message>> applyMessages = new MutableLiveData<>();

    public MutableLiveData<List<Message>> getApplyMessages() {
        return applyMessages;
    }

    public MutableLiveData<List<Message>> getDetailMessages() {
        return detailMessages;
    }

    public MutableLiveData<List<Message>> getOverViewMessages() {
        return overViewMessages;
    }

    /**
     * 登录成功后由activity发起
     * 1-首先应该查询数据库，根据发送人分类得到最新的消息id，请求服务器发送该消息发送时间之后的消息
     * 注：room中存储的信息对象包括了发送人昵称和发送人id，但昵称是可变的，暂时做法是从数据库查询出所有消息的发送人id，然后网络请求最新昵称
     * 2-用户点击消息item，在detail页面中向服务器发送请求告知已读
     * 注：可在后续优化中设置分页
     */
    @Deprecated
    public void initMessageData(Integer userId, Context context) {

        Single.create((SingleOnSubscribe<List<Message>>) emitter -> {

        }).subscribeOn(Schedulers.io())
                .subscribe(messages1 -> {
                        }
                        , throwable -> throwable.printStackTrace());

        myRepository.getMessagesFromNet(userId)
                .subscribeOn(Schedulers.io())
                .subscribe(messages -> {
                    /**
                            myDatabase = MyDatabase.getDatabase(context);
                            MessageDao dao = myDatabase.getMessageDao();
                            List<Message> messages = (List<Message>) mapData.get("messages");
                            if (messages != null && messages.size() > 0) {
                                //存储新消息
                                dao.insertAll(messages);
                            }
                            this.messages = (MutableLiveData<List<Message>>) Transformations.distinctUntilChanged(dao.loadAll(userId));
                     */

                        }
                        , throwable -> throwable.printStackTrace()
                );
    }

}