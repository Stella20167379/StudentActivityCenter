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
     * ???????????????id
     */
    private int senderId;
    /**
     * ????????????id
     */
    private int userId;

    private NavController navController;

    /**
     * ???????????????????????????????????????????????????
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
            PromptUtil.showAlert(getContext(), "????????????????????????");
            navController.popBackStack();
        }

        String senderName = args.getString("title");
        binding.setTitle(senderName);

        this.senderId = args.getInt("senderId");
        if (senderId == 0) {
            PromptUtil.showAlert(getContext(), "?????????????????????id???");
            navController.popBackStack();
        }
        final MainActivityViewModel mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        userId = mainActivityViewModel.getUserInfo().getValue().getId();

        //???toolbar?????????icon??????????????????????????????navigation???action??????
        final Toolbar toolbar = binding.toolbar;
        //??????????????????
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        /* ???????????? */
        sendBtn.setOnClickListener(v -> {
            String input = inputMsgView.getText().toString();
            if (input.length() < 1 || input.trim().length() < 1) {
                inputMsgView.setError("????????????????????????");
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
                Log.d(TAG, "??????: ????????????");
                return;
            }
            Log.d(TAG, "onViewCreated: ????????????" + messages.size());
            adapter.submitList(messages);
            //??????????????????
            recyclerView.scrollToPosition(messages.size());
        });

        //???????????????
        initData();

        //????????????
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
//                    PromptUtil.snackbarShowTxt(root, "???????????????");
                } else {
                    PromptUtil.showAlert(getContext(), "???????????????????????? " + msg);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "onViewCreated: ?????????????????????");
            PromptUtil.showAlert(getContext(), "socket?????????????????????????????????");
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
                    //???????????????????????????????????????????????????
                    //??????gson???int???double
                    JsonElement element = gson.toJsonTree(o);
                    Message message = gson.fromJson(element, Message.class);
                    Log.d(TAG, "sendMsg: ?????????????????????");
//                    adapter.getCurrentList().add(message);

//                    adapter.notifyDataSetChanged();
                    initData();

                    PromptUtil.snackbarShowTxt(root, "????????????");
                } else {
                    PromptUtil.showAlert(getContext(), "??????????????? " + msg);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "onViewCreated: ??????????????????");
            PromptUtil.showAlert(getContext(), "??????????????????");
        }
    }

    /**
     * ????????????id?????????????????????????????????????????????????????????
     * ???????????????????????????????????????
     */
    @Deprecated
    private List<Message> getDetailMessages(Integer senderId, List<Message> dataList) {
        List<Message> result = dataList.stream()
                .filter(message -> message.getSenderId() == senderId || message.getReceiverId() == senderId)
//                .sorted((m1, m2) -> m1.getSendTime().isAfter(m2.getSendTime()) ? 1 : -1)
                .peek(message -> {
                    //???????????? - 1 ??????????????? -2
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