package com.example.graduatedesign.ui.message;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.R;
import com.example.graduatedesign.adapter.MessageAdapter;
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.databinding.FragmentMessageBinding;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MessageFragment extends Fragment {

    private static final String TAG = "MessageFragment";
    private FragmentMessageBinding binding;
    private View root;
    private MessageViewModel viewModel;

    private RecyclerView recyclerView;
    private TextView txtShowNoneData;
    private MessageAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMessageBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        recyclerView = binding.msgShow;
        txtShowNoneData = binding.showNoneData;

        adapter = new MessageAdapter();
        viewModel = new ViewModelProvider(requireActivity()).get(MessageViewModel.class);
        viewModel.getMessageList().observe(getViewLifecycleOwner(), messages -> {
            onRecyclerDataChanged(messages);
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        //添加触摸事件，实现点击监听
        recyclerView.addOnItemTouchListener(new MessageItemListener());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    public void onRecyclerDataChanged(List<Message> dataList) {
        if (dataList == null || dataList.size() < 1) {
            txtShowNoneData.setText(R.string.title_none_data);
            txtShowNoneData.setVisibility(View.VISIBLE);

            recyclerView.setVisibility(View.GONE);
        } else {
            /* 待改-传入的是所有的消息数据，需要从中筛选 */
            adapter.submitList(dataList);

            txtShowNoneData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    class MessageItemListener extends RecyclerView.SimpleOnItemTouchListener {
        //对手势交互的监听接口有默认实现的类，避免implement不需要的方法
        private GestureDetector.SimpleOnGestureListener gl = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: 总之确实是点到了");
                //获取当前触摸点下，recyclerview的子项
//                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                return false;
            }
        };

        //识别手势类的Compat（兼容）版本
        private GestureDetectorCompat mGestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), gl);

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            return mGestureDetectorCompat.onTouchEvent(e);
        }
    }

    /**
     * 从viewModel获取保存的信息数据后，筛选出发件人不同、时间最新的消息各一条，交由adapter显示
     *
     * @return
     */
    private void getOverviewMessages() {
        viewModel.getMessageList().observe(getViewLifecycleOwner(), messages -> {
            List<Message> result=messages.stream()
                    //按照发送人id分组
                    .collect(Collectors.groupingBy(Message::getSenderId))
                    .values()
                    .stream()
                    .map((Function<List<Message>, Message>) messages1 -> messages1.stream()
                            //时间最新的排在最前面
                            .sorted((m1, m2) -> m1.getSendTime().isAfter(m2.getSendTime()) ? -1 : 1)
                            .findFirst()
                            .get()
                    )
                    .collect(Collectors.toList());
            adapter.submitList(result);
        });
    }

}
