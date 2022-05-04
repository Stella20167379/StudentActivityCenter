package com.example.graduatedesign.ui.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.adapter.MessageDetailAdapter;
import com.example.graduatedesign.data.DateTypeConverter;
import com.example.graduatedesign.data.MyDatabase;
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.databinding.FragmentMessageDetailBinding;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MessageDetailFragment extends Fragment {
    private static final String TAG = "MessageDetailFragment";
    private FragmentMessageDetailBinding binding;
    private View root;
    private MessageViewModel viewModel;

    private TextView senderTitleView;
    private EditText inputMsgView;
    private Button sendBtn;
    private RecyclerView recyclerView;
    private MessageDetailAdapter adapter;

    private int userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMessageDetailBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        viewModel = new ViewModelProvider(requireActivity()).get(MessageViewModel.class);

        recyclerView = binding.msgDetailShow;
        sendBtn = binding.btnSend;
        inputMsgView = binding.inputMessage;
        senderTitleView = binding.senderNameTitle;

        adapter = new MessageDetailAdapter();

        final MainActivityViewModel mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        userId = mainActivityViewModel.getUserInfo().getValue().getId();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //为toolbar的导航icon按钮添加监听事件，与navigation的action绑定
        final Toolbar toolbar = binding.toolbar;
        //绑定返回功能
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(root)
                .popBackStack());

        /* 发送消息 */
        sendBtn.setOnClickListener(v -> {
            Message newMsg=createSendMsg();
            MyDatabase myDatabase=MyDatabase.getDatabase(getContext());
//            MessageDao messageDao=myDatabase.getMessageDao();
//            messageDao.insertOne(newMsg);
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            Integer senderId = viewModel.getSenderId().getValue();
            List<Message> result = getDetailMessages(senderId, messages);
            adapter.submitList(result);
            //跳转至最底部
            recyclerView.scrollToPosition(result.size());
        });

        viewModel.getToolbarTitle().observe(getViewLifecycleOwner(), senderTitle -> {
            senderTitleView.setText(senderTitle);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    /**
     * 用发送人id筛选该发送人发送的消息，同时按时间排列
     * 自己发送的消息也要筛选出来
     */
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

    private Message createSendMsg(){
        String content=inputMsgView.getText().toString();
        LocalDateTime now=LocalDateTime.now();
        Message message=new Message();
        message.setContent(content);
        message.setSenderId(userId);
        message.setReceiverId(viewModel.getSenderId().getValue());
        message.setSendTime(DateTypeConverter.dateToString(now, true));

        return message;
    }

}