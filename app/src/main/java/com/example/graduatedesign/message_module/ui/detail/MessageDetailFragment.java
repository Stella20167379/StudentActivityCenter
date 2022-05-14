package com.example.graduatedesign.message_module.ui.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.adapter.MessageDetailAdapter;
import com.example.graduatedesign.data.DateTypeConverter;
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.databinding.FragmentMessageDetailBinding;
import com.example.graduatedesign.message_module.MsgTypes;
import com.example.graduatedesign.net.netty.AppCache;
import com.example.graduatedesign.net.netty.model.Callback;
import com.example.graduatedesign.ui.message.MessageViewModel;
import com.example.graduatedesign.utils.ArrayMapBuilder;
import com.example.graduatedesign.utils.GsonConvertTypes;
import com.example.graduatedesign.utils.PromptUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MessageDetailFragment extends Fragment {
    private static final String TAG = "MessageDetailFragment";
    @Inject
    Gson gson;
    private FragmentMessageDetailBinding binding;
    private View root;
    private EditText inputMsgView;
    private Button sendBtn;
    private RecyclerView recyclerView;
    private MessageDetailAdapter adapter;
    private Bundle args;
    private MessageViewModel viewModel;

    /**
     * 当前对话人id
     */
    private int senderId;
    /**
     * 当前用户id
     */
    private int userId;

    private NavController navController;

    /**
     * 新信息回调，请在页面销毁时删除回调
     */
    private Callback<Message> callback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMessageDetailBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        args = getArguments();

        viewModel = new ViewModelProvider(requireActivity()).get(MessageViewModel.class);

        recyclerView = binding.msgDetailShow;
        sendBtn = binding.btnSend;
        inputMsgView = binding.inputMessage;

        adapter = new MessageDetailAdapter();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(root);

        args = getArguments();
        if (args == null) {
            PromptUtil.showAlert(getContext(), "没有传参，错误！");
            navController.popBackStack();
        }

        String senderName = args.getString("title");
        binding.setTitle(senderName);

        this.senderId = args.getInt("senderId");
        if (senderId == 0) {
            PromptUtil.showAlert(getContext(), "不知道发送人的id！");
            navController.popBackStack();
        }
        final MainActivityViewModel mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        userId = mainActivityViewModel.getUserInfo().getValue().getId();

        //为toolbar的导航icon按钮添加监听事件，与navigation的action绑定
        final Toolbar toolbar = binding.toolbar;
        //绑定返回功能
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        /* 发送消息 */
        sendBtn.setOnClickListener(v -> {
            String input = inputMsgView.getText().toString();
            if (input.length() < 1 || input.trim().length() < 1) {
                inputMsgView.setError("请输入内容再发送");
                return;
            }
            sendMsg(input);
            inputMsgView.setText("");
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        viewModel.getDetailMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages == null) {
                Log.d(TAG, "详情: 数据为空");
                return;
            }
            Log.d(TAG, "onViewCreated: 数据长度" + messages.size());
            adapter.submitList(messages);
            //跳转至最底部
            recyclerView.scrollToPosition(messages.size());
        });

        //初始化数据
        initData();

        //添加回调
        startOnMsgCallBack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        removeOnMsgCallBack();

        root = null;
        binding = null;
    }

    public void initData() {
        try {
            String url = "message/get/detail";
            String body = gson.toJson(new ArrayMapBuilder().put("senderId", senderId).put("receiverId", userId).build());
            AppCache.getService().sendActionMsg(url, body, (code, msg, o) -> {
                if (code == 200) {
                    List data = (List) o;

                    JsonElement jsonElement = gson.toJsonTree(data);
                    List<Message> overViewMsgList = gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfMessageList);
                    viewModel.getDetailMessages().setValue(overViewMsgList);
//                    PromptUtil.snackbarShowTxt(root, "初始化成功");
                } else {
                    PromptUtil.showAlert(getContext(), "信息初始化错误： " + msg);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "onViewCreated: 信息初始化失败");
            PromptUtil.showAlert(getContext(), "socket未登录，信息初始化失败");
        }
    }

    private void startOnMsgCallBack() {
        callback = (code, msg, message) -> {
            if (code == 200 && message.getMsgType() == MsgTypes.TextType) {
//                adapter.getCurrentList().add(message);
//                adapter.notifyDataSetChanged();
                initData();
            }
        };
        AppCache.getService().addReceiveMsgCallback(callback);
    }

    private void removeOnMsgCallBack() {
        AppCache.getService().removeReceiveMsgCallback(callback);
    }

    public void sendMsg(String content) {
        Map data = new ArrayMapBuilder().put("content", content).put("to", senderId).put("from", userId).build();
        try {
            String url = "message/send/msg";
            String body = gson.toJson(data);
            AppCache.getService().sendActionMsg(url, body, (code, msg, o) -> {
                if (code == 200) {
                    //服务器返回新信息对象，通知视图修改
                    //防止gson将int转double
                    JsonElement element = gson.toJsonTree(o);
                    Message message = gson.fromJson(element, Message.class);
                    Log.d(TAG, "sendMsg: 确实到达了这里");
//                    adapter.getCurrentList().add(message);

//                    adapter.notifyDataSetChanged();
                    initData();

                    PromptUtil.snackbarShowTxt(root, "发送成功");
                } else {
                    PromptUtil.showAlert(getContext(), "发送失败： " + msg);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "onViewCreated: 信息发送失败");
            PromptUtil.showAlert(getContext(), "信息发送失败");
        }
    }

    /**
     * 用发送人id筛选该发送人发送的消息，同时按时间排列
     * 自己发送的消息也要筛选出来
     */
    @Deprecated
    private List<Message> getDetailMessages(Integer senderId, List<Message> dataList) {
        List<Message> result = dataList.stream()
                .filter(message -> message.getSenderId() == senderId || message.getReceiverId() == senderId)
//                .sorted((m1, m2) -> m1.getSendTime().isAfter(m2.getSendTime()) ? 1 : -1)
                .peek(message -> {
                    //自己收到 - 1 ，自己发送 -2
                    if (message.getSenderId() != userId)
                        message.setType(2);
                    else message.setType(1);
                })
                .collect(Collectors.toList());
        return result;
    }

    private Message createSendMsg() {
        String content = inputMsgView.getText().toString();
        LocalDateTime now = LocalDateTime.now();
        Message message = new Message();
        message.setContent(content);
        message.setSenderId(userId);

        message.setSendTime(DateTypeConverter.dateToString(now, true));

        return message;
    }

}