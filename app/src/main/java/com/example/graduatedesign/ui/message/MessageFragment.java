package com.example.graduatedesign.ui.message;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.cardview.widget.CardView;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.adapter.MessageAdapter;
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.databinding.FragmentMessageBinding;
import com.example.graduatedesign.message_module.MsgTypes;
import com.example.graduatedesign.net.netty.AppCache;
import com.example.graduatedesign.net.netty.model.Callback;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.utils.ArrayMapBuilder;
import com.example.graduatedesign.utils.GsonConvertTypes;
import com.example.graduatedesign.utils.PromptUtil;
import com.example.graduatedesign.utils.RxLifecycleUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class MessageFragment extends Fragment {

    private static final String TAG = "MessageFragment";
    private FragmentMessageBinding binding;
    private View root;
    private MessageViewModel viewModel;

    private RecyclerView recyclerView;
    @Inject
    Gson gson;
    private MessageAdapter adapter;
    private TextView showPageState;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMessageBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        viewModel = new ViewModelProvider(requireActivity()).get(MessageViewModel.class);

        recyclerView = binding.msgShow;
        showPageState = binding.showPageState;

        user = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class).getUserInfo().getValue();
        if (user.isAssociationAdmin())
            binding.setIsAdmin(true);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final CardView enterApplyView = binding.topClickableApply;

        adapter = new MessageAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        //添加触摸事件，实现点击监听
        recyclerView.addOnItemTouchListener(new MessageItemListener());

        viewModel.getOverViewMessages().observe(getViewLifecycleOwner(), messages -> {
            Log.d(TAG, "onViewCreated: 传入数据");
            if (messages == null) {
                Log.d(TAG, "onViewCreated:数据为空");
                return;
            }
            Log.d(TAG, "onViewCreated:数据不为空");
            /* 通知用户消息列表改变 */
            adapter.submitList(messages);
            adapter.notifyDataSetChanged();
        });
        /* 通知活动管理员有新的入会申请待处理 */
        viewModel.getApplyMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages == null)
                return;
            binding.setApplySum(String.valueOf(messages.size()));
        });

        enterApplyView.setOnClickListener(v -> {
            //点进去就设为全部查看过了
            binding.setApplySum(String.valueOf(0));
            Navigation.findNavController(root).navigate(R.id.associationEnterApplyFragment);
        });

        //初始化信息
        initData();
        //开启消息接收回调
        startOnMsgCallBack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    private void initData() {

        /* 初始化消息 */
        try {
            String url = "message/get/overview";
            String body = gson.toJson(new ArrayMapBuilder().put("userId", user.getId()).put("isAdmin", user.isAssociationAdmin()).build());
            AppCache.getService().sendActionMsg(url, body, (code, msg, o) -> {
                if (code == 200) {
                    Map data = (Map) o;

//                    String countStr = (String) data.get("countStr");
//                    binding.setApplySum(countStr);

                    List list = (List) data.get("list");
                    JsonElement jsonElement = gson.toJsonTree(list);
                    List<Message> overViewMsgList = gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfMessageList);
                    List<Message> msgList = overViewMsgList.stream().filter(message -> message.getSenderId() != user.getId()).collect(Collectors.toList());
                    viewModel.getOverViewMessages().setValue(msgList);
                    PromptUtil.snackbarShowTxt(root, "初始化成功");
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
        AppCache.getService().addReceiveMsgCallback((code, msg, message) -> {
            if (code == 200) {
                Log.d(TAG, "startOnMsgCallBack: type " + message.getMsgType());
                switch (message.getMsgType()) {
                    case MsgTypes.TextType:
                        //更新总览列表
                        List<Message> messagesNow = viewModel.getOverViewMessages().getValue();
                        messagesNow.add(message);
//                        List<Message> messagesNew = messagesNow.stream().filter(var -> var.getSenderId() != message.getSenderId()).collect(Collectors.toList());
                        viewModel.getOverViewMessages().setValue(messagesNow);
                        break;
                    case MsgTypes.ApplyType:
                        Integer count = Integer.parseInt(binding.getApplySum());
                        count++;
                        binding.setApplySum(String.valueOf(count));
                        break;
                    case MsgTypes.SystemNotifyType:
                        Log.d(TAG, "接收到系统通知，待处理... ");
                        break;
                }
                PromptUtil.showAlert(getContext(), "新信息，请查看");
                initData();
            } else {
                PromptUtil.showAlert(getContext(), "接收到无效信息");
            }
        });
    }

    /**
     * 从viewModel获取保存的信息数据后，筛选出发件人不同、时间最新的消息各一条，交由adapter显示
     * 注意此处消息有普通消息和入会申请之分,1-普通消息
     *
     * @return
     */
    @Deprecated
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

    @Deprecated
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
                                showPageState.setText(R.string.title_none_data);
                                showPageState.setVisibility(View.VISIBLE);

                                recyclerView.setVisibility(View.GONE);
                            } else {
                                adapter.submitList(dataList);

                                showPageState.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        },
                        throwable -> throwable.printStackTrace()
                );
    }

    /**
     * socket登录
     */
    @Deprecated
    private void socketLogin(Callback<Void> callback) {
        if (AppCache.getMyInfo() == null || AppCache.getMyInfo().getToken() == null) {
            SharedPreferences sp = requireActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            String token = sp.getString("token", null);

            Completable.create(emitter -> {
                /* socket登录 */
                Log.d(TAG, "storeLoggedInUser: 登录中");
                AppCache.getService().login(token, callback);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .to(RxLifecycleUtils.bindLifecycle(root)).subscribe(() -> {
                    },
                    throwable -> throwable.printStackTrace());
        }
    }

    class MessageItemListener extends RecyclerView.SimpleOnItemTouchListener {
        //对手势交互的监听接口有默认实现的类，避免implement不需要的方法
        private GestureDetector.SimpleOnGestureListener gl = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //获取当前触摸点下，recyclerview的子项
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int position = recyclerView.getChildAdapterPosition(child);
                Message message = adapter.getItem(position);
                Log.d(TAG, "onSingleTapUp，点击了: " + message.getSender());

                //设置跳转时携带的数据
                Bundle bundle = new Bundle();
                String title = message.getSender();
                int senderId = message.getSenderId();
                bundle.putString("title", title);
                bundle.putInt("senderId", senderId);

                //跳转页面
                final NavController navController = Navigation.findNavController(recyclerView);
                navController.navigate(R.id.navigation_messageDetail, bundle);

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

}
