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

import com.example.graduatedesign.adapter.MessageDetailAdapter;
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.databinding.FragmentMessageDetailBinding;

import java.util.List;
import java.util.function.Function;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMessageDetailBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        viewModel=new ViewModelProvider(requireActivity()).get(MessageViewModel.class);

        recyclerView=binding.msgDetailShow;
        sendBtn= binding.btnSend;
        inputMsgView= binding.inputMessage;
        senderTitleView=binding.senderNameTitle;

        adapter=new MessageDetailAdapter();
        //为toolbar的导航icon按钮添加监听事件，与navigation的action绑定
        Toolbar toolbar = binding.toolbar;
        //绑定返回功能
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(root)
                .popBackStack());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getMessageList().observe(getViewLifecycleOwner(),messages -> {
            adapter.submitList(messages);
        });
        viewModel.getToolbarTitle().observe(getViewLifecycleOwner(),senderTitle->{
            senderTitleView.setText(senderTitle);
        });

        sendBtn.setOnClickListener(v->{

        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        //由MessageFragment跳转进来时传递的关键数据（如id），用来查询数据库/服务器
        try {
            int senderId=getArguments().getInt("sender");
//            viewModel.initData(senderId);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root=null;
        binding=null;
    }

    /**
     * 从viewModel中获取消息列表后，用MessageFragment传来的id搜索该发送人发送的消息，同时按时间排列
     * @return
     */
    private void getDetailMessages(Integer senderId){
        viewModel.getMessageList().observe(getViewLifecycleOwner(), messages -> {
            List<Message> result=messages.stream()
                    .filter(message -> message.getSenderId()==senderId)
                    .sorted((m1, m2) -> m1.getSendTime().isAfter(m2.getSendTime()) ? -1 : 1)
                    .collect(Collectors.toList());

            adapter.submitList(result);
        });
    }

}