package com.example.graduatedesign.ui.message;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.R;
import com.example.graduatedesign.adapter.CollegeSpinnerAdapter;
import com.example.graduatedesign.adapter.MessageAdapter;
import com.example.graduatedesign.adapter.viewholder.MessageHolder;
import com.example.graduatedesign.data.MyDatabase;
import com.example.graduatedesign.data.model.College;
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.databinding.FragmentMessageBinding;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;

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

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.msgShow;
        txtShowNoneData = binding.showNoneData;

        adapter = new MessageAdapter();
        viewModel = new ViewModelProvider(requireActivity()).get(MessageViewModel.class);
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            onRecyclerDataChanged(messages);
        });

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
        //筛选信息,此处建议异步进行
        Single.create((SingleOnSubscribe<List<Message>>) emitter -> {
            List<Message> usefulMsg = getOverviewMessages(dataList);
            emitter.onSuccess(usefulMsg);
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(this))
                .subscribe(usefulMsg -> {
                            if (usefulMsg == null || usefulMsg.size() < 1) {
                                txtShowNoneData.setText(R.string.title_none_data);
                                txtShowNoneData.setVisibility(View.VISIBLE);

                                recyclerView.setVisibility(View.GONE);
                            } else {
                                adapter.submitList(dataList);

                                txtShowNoneData.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        },
                        throwable -> throwable.printStackTrace()
                );
    }

    class MessageItemListener extends RecyclerView.SimpleOnItemTouchListener {
        //对手势交互的监听接口有默认实现的类，避免implement不需要的方法
        private GestureDetector.SimpleOnGestureListener gl = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //获取当前触摸点下，recyclerview的子项
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int position= (int) recyclerView.getChildItemId(child);
                Message message=adapter.getItem(position);
                //设置全局数据
                viewModel.getToolbarTitle().setValue(message.getSender());
                viewModel.getSenderId().setValue(message.getSenderId());
                //跳转页面
                final NavController navController= Navigation.findNavController(recyclerView);
                navController.navigate(R.id.navigation_messageDetail);
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
     *  注意此处消息有普通消息和入会申请之分,1-普通消息
     * @return
     */
    private List<Message> getOverviewMessages(List<Message> dataList) {

        List<Message> result = dataList.stream()
                .filter(message -> message.getMsgType() == 1)
                //按照发送人id分组
                .collect(Collectors.groupingBy(Message::getSenderId))
                .values()
                .stream()
                .map(messages1 -> messages1.stream()
                        //时间最新的排在最前面
//                        .sorted((m1, m2) -> m1.getSendTime().isAfter(m2.getSendTime()) ? -1 : 1)
                        .findFirst()
                        .get()
                )
                .collect(Collectors.toList());
        return result;
    }

}
