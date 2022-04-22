package com.example.graduatedesign.ui.message;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.graduatedesign.data.MyDatabase;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.dao.MessageDao;
import com.example.graduatedesign.data.model.Message;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 本model的生命周期与activity绑定，实现MessageFragment和MessageDetailViewModel数据共通，同时节省空间
 */
@HiltViewModel
public class MessageViewModel extends ViewModel {
    private MutableLiveData<List<Message>> messageList = new MutableLiveData<>();
    private MutableLiveData<String> toolbarTitle = new MutableLiveData<>("消息");
    private MutableLiveData<Integer> senderId = new MutableLiveData<>();
    private MyDatabase myDatabase;
    private final MyRepository myRepository;

    @Inject
    public MessageViewModel(MyRepository myRepository) {
        this.myRepository = myRepository;
    }

    public MutableLiveData<List<Message>> getMessageList() {
        return messageList;
    }

    public MutableLiveData<String> getToolbarTitle() {
        return toolbarTitle;
    }

    public MutableLiveData<Integer> getSenderId() {
        return senderId;
    }

    /**
     * 登录成功后由activity发起
     * 1-首先发起网络请求，查询是否有未读消息，将服务器发来的消息存入room
     * 注：room中存储的信息对象包括了发送人昵称和发送人id，但昵称是可变的，暂时做法是在MessageFragment中筛选信息的同时发起网络请求获取最新昵称
     * 2-服务器发送完消息的同时，将消息id列表放入redis缓存，并生成随机验证码交给前端
     * 3-前端使用验证码再次请求服务器确认收到，服务器根据验证码改变消息状态
     * 注：可在后续优化中设置分页
     */
    public void initMessageData(Integer userId, Context context) {
        myRepository.getMessageFromNet(userId)
                .subscribeOn(Schedulers.io())
                .subscribe(mapData -> {
                            myDatabase = MyDatabase.getDatabase(context);
                            MessageDao dao = myDatabase.getMessageDao();
                            List<Message> messages = (List<Message>) mapData.get("messages");
                            if (messages != null && messages.size() > 0) {
                                //存储新消息
                                dao.insertAll(messages);
                                //告知服务器已收到
                                String code = (String) mapData.get("code");
                                myRepository.notifyMsgRead(code)
                                        .subscribe(
                                                ()->{}
                                                ,throwable -> throwable.printStackTrace()
                                        );
                            }
                            messageList = (MutableLiveData<List<Message>>) Transformations.distinctUntilChanged(dao.loadAll(userId));
                        }
                        , throwable -> throwable.printStackTrace()
                );
    }

}