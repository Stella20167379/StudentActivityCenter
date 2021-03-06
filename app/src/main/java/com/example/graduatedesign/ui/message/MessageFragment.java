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
        //???????????????????????????????????????
        recyclerView.addOnItemTouchListener(new MessageItemListener());

        viewModel.getOverViewMessages().observe(getViewLifecycleOwner(), messages -> {
            Log.d(TAG, "onViewCreated: ????????????");
            if (messages == null) {
                Log.d(TAG, "onViewCreated:????????????");
                return;
            }
            Log.d(TAG, "onViewCreated:???????????????");
            /* ?????????????????????????????? */
            adapter.submitList(messages);
            adapter.notifyDataSetChanged();
        });
        /* ??????????????????????????????????????????????????? */
        viewModel.getApplyMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages == null)
                return;
            binding.setApplySum(String.valueOf(messages.size()));
        });

        enterApplyView.setOnClickListener(v -> {
            //????????????????????????????????????
            binding.setApplySum(String.valueOf(0));
            Navigation.findNavController(root).navigate(R.id.associationEnterApplyFragment);
        });

        //???????????????
        initData();
        //????????????????????????
        startOnMsgCallBack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    private void initData() {

        /* ??????????????? */
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
                    PromptUtil.snackbarShowTxt(root, "???????????????");
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
        AppCache.getService().addReceiveMsgCallback((code, msg, message) -> {
            if (code == 200) {
                Log.d(TAG, "startOnMsgCallBack: type " + message.getMsgType());
                switch (message.getMsgType()) {
                    case MsgTypes.TextType:
                        //??????????????????
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
                        Log.d(TAG, "?????????????????????????????????... ");
                        break;
                }
                PromptUtil.showAlert(getContext(), "?????????????????????");
                initData();
            } else {
                PromptUtil.showAlert(getContext(), "?????????????????????");
            }
        });
    }

    /**
     * ???viewModel???????????????????????????????????????????????????????????????????????????????????????????????????adapter??????
     * ??????????????????????????????????????????????????????,1-????????????
     *
     * @return
     */
    @Deprecated
    private List<Message> getOverviewMessages(List<Message> dataList) {

        List<Message> result = dataList.stream()
                .filter(message -> message.getMsgType() == 1)
                //???????????????id??????
                .collect(Collectors.groupingBy(Message::getSenderId))
                .values()
                .stream()
                .map(messages1 -> messages1.stream()
                                //??????????????????????????????
//                        .sorted((m1, m2) -> m1.getSendTime().isAfter(m2.getSendTime()) ? -1 : 1)
                                .findFirst()
                                .get()
                )
                .collect(Collectors.toList());
        return result;
    }

    @Deprecated
    public void onRecyclerDataChanged(List<Message> dataList) {
        //????????????,????????????????????????
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
     * socket??????
     */
    @Deprecated
    private void socketLogin(Callback<Void> callback) {
        if (AppCache.getMyInfo() == null || AppCache.getMyInfo().getToken() == null) {
            SharedPreferences sp = requireActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            String token = sp.getString("token", null);

            Completable.create(emitter -> {
                /* socket?????? */
                Log.d(TAG, "storeLoggedInUser: ?????????");
                AppCache.getService().login(token, callback);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .to(RxLifecycleUtils.bindLifecycle(root)).subscribe(() -> {
                    },
                    throwable -> throwable.printStackTrace());
        }
    }

    class MessageItemListener extends RecyclerView.SimpleOnItemTouchListener {
        //????????????????????????????????????????????????????????????implement??????????????????
        private GestureDetector.SimpleOnGestureListener gl = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //???????????????????????????recyclerview?????????
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int position = recyclerView.getChildAdapterPosition(child);
                Message message = adapter.getItem(position);
                Log.d(TAG, "onSingleTapUp????????????: " + message.getSender());

                //??????????????????????????????
                Bundle bundle = new Bundle();
                String title = message.getSender();
                int senderId = message.getSenderId();
                bundle.putString("title", title);
                bundle.putInt("senderId", senderId);

                //????????????
                final NavController navController = Navigation.findNavController(recyclerView);
                navController.navigate(R.id.navigation_messageDetail, bundle);

                return false;
            }
        };

        //??????????????????Compat??????????????????
        private GestureDetectorCompat mGestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), gl);

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            return mGestureDetectorCompat.onTouchEvent(e);
        }
    }

}
