package com.example.graduatedesign.student_activity_module.ui.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.adapter.MyPagerAdapter;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.databinding.FragmentActivityCommentBinding;
import com.example.graduatedesign.databinding.FragmentActivityDetailBinding;
import com.example.graduatedesign.databinding.FragmentActivityDetailHolderBinding;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.student_activity_module.adapter.ActivityCommentAdapter;
import com.example.graduatedesign.student_activity_module.data.Comment;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;
import com.example.graduatedesign.utils.PromptUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ActivityDetailHolderFragment extends Fragment {
    @Inject
    MyRepository repository;

    private FragmentActivityDetailHolderBinding binding;
    private View root;

    /* 为了将数据传给viewPager子页面，设置了内部类，可直接调用这个数据 */
    Integer activityId = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActivityDetailHolderBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        Bundle args = getArguments();
        if (args == null) {
            PromptUtil.snackbarShowTxt(root, "不知道该展示哪个活动的详情，没有传参！");
            navController.popBackStack();
            return;
        }
        activityId = args.getInt("id");

        final ViewPager2 viewPager = view.findViewById(R.id.viewpager);
        final TabLayout tabLayout = view.findViewById(R.id.tabs);

        /* 要创建的页面的全限定名 */
        List<String> clazzList = new ArrayList<>();
        clazzList.add(ActivityDetailFragment.class.getName());
        clazzList.add(ActivityCommentFragment.class.getName());
        /* 初始化tab栏和viewpager */
        final MyPagerAdapter adapter = new MyPagerAdapter(this, clazzList);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0)
                        tab.setText(R.string.title_activity_detail);
                    else if (position == 1)
                        tab.setText(R.string.title_activity_comment);
                }
        ).attach();

        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    /**
     * 详情页面
     */
    public class ActivityDetailFragment extends Fragment {

        private static final String TAG = "ActivityDetailFragment";

        /**
         * 为区分底部按钮的点击事件而设
         */
        private int btnState = ActivityRelativeStates.BtnSignState;

        private FragmentActivityDetailBinding binding;
        private View root;
        private ActivityDetailPresenter presenter;
        private Button bottomActionBtn;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            binding = FragmentActivityDetailBinding.inflate(inflater, container, false);
            root = binding.getRoot();
            presenter = new ActivityDetailPresenter(this);
            bottomActionBtn = binding.bottomActionBtn;
            return root;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            final MainActivityViewModel activityViewModel=new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
            User user=activityViewModel.getUserInfo().getValue();

            bottomActionBtn.setOnClickListener(v -> {
                switch (btnState) {
                    case ActivityRelativeStates.BtnSignState:
                        presenter.signToActivity(user.getId(),repository,activityId);
                        break;
                    case ActivityRelativeStates.BtnAdminState:
                        //todo:打开相机扫描二维码
                        PromptUtil.snackbarShowTxt(root,"管理员签到签退");
                        break;
                    case ActivityRelativeStates.BtnCheckInState:
                        //todo:生成签到二维码
                        PromptUtil.snackbarShowTxt(root,"用户签到");
                        break;
                    case ActivityRelativeStates.BtnCheckOutState:
                        //todo:生成签退二维码
                        PromptUtil.snackbarShowTxt(root,"用户签退");
                        break;
                }
            });

            presenter.initDetail(repository, activityId);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            root = null;
            binding = null;
        }

        /**
         * 将对象信息显示到控件上
         *
         * @param activity
         */
        private void bindToViews(MyStudentActivity activity) {
            if (activity != null) {
                final ImageView coverView = binding.cover;
                final TextView titleView = binding.activityTitle;
                final TextView associationNameView = binding.associationName;
                final TextView signTimeView = binding.signTime;
                final TextView activityTimeView = binding.activityTime;
                final TextView locationView = binding.location;
                final TextView introductionView = binding.introduction;

                Glide.with(this)
                        .load(DataUtil.getImgDownloadUri(DataUtil.getImgDownloadUri(activity.getCoverImg())))
                        .apply(GlideUtils.OPTIONS)
                        .into(coverView);
                titleView.setText(activity.getTitle());
                associationNameView.setText(activity.getAssociationName());
                signTimeView.setText(activity.getSignStart() + "-" + activity.getSignEnd());
                activityTimeView.setText(activity.getActivityStart() + "-" + activity.getActivityEnd());
                locationView.setText(activity.getLocation());
                introductionView.setText(activity.getIntroduction());

                if (activity.isPayNeed()) {
                    final TextView chargeAmountView = binding.chargeAmount;
                    final TextView fundUseView = binding.fundUse;
                    chargeAmountView.setText(activity.getChargeAmount());
                    fundUseView.setText(activity.getFundUse());
                }

                /* 根据活动各种时间及用户当前与活动的关系来改变底部按钮的文字、状态及点击事件类别 */
                int signState = checkSignState(activity.getSignStart(), activity.getSignEnd());
                int activityState = checkActivityState(activity.getActivityStart(), activity.getActivityEnd());
                checkUserState(signState, activityState, activity.getParticipantState());
            }
        }

        /**
         * 查看活动当前的报名状态
         *
         * @param start 活动报名开始时间
         * @param end   活动报名截止时间
         * @return
         */
        private int checkSignState(String start, String end) {
            int state = ActivityRelativeStates.SignDisableState;
            String nowStr = DataUtil.dateToString(LocalDateTime.now(), true);
            if (DataUtil.compareDatetimeStr(start, nowStr))
                state = ActivityRelativeStates.SignNotStartState;
            else if (DataUtil.compareDatetimeStr(end, nowStr))
                state = ActivityRelativeStates.SignAvailableState;
            return state;
        }

        /**
         * 查看活动当前的举行状态
         *
         * @param start 活动开始举行时间
         * @param end   活动结束时间
         * @return
         */
        private int checkActivityState(String start, String end) {
            int state = ActivityRelativeStates.ActivityEndState;
            String nowStr = DataUtil.dateToString(LocalDateTime.now(), true);
            if (DataUtil.compareDatetimeStr(start, nowStr))
                state = ActivityRelativeStates.ActivityNotStartState;
            else if (DataUtil.compareDatetimeStr(end, nowStr))
                state = ActivityRelativeStates.ActivityStartState;
            return state;
        }

        /**
         * 检查用户和活动的对应状态，以为底部按钮设置响相应状态
         *
         * @param participantState 用户参与的状态
         */
        private void checkUserState(int signState, int activityState, int participantState) {

            if (signState == ActivityRelativeStates.SignNotStartState) {
                bottomActionBtn.setEnabled(false);
                bottomActionBtn.setText(R.string.special_activity_state_1);
                return;
            }

            switch (participantState) {
                case ActivityRelativeStates.RelAlreadyCheckOutState:
                    bottomActionBtn.setEnabled(false);
                    bottomActionBtn.setText(R.string.special_activity_state_8);
                    break;
                case ActivityRelativeStates.RelNoneState:
                    if (signState == ActivityRelativeStates.SignAvailableState) {
                        bottomActionBtn.setText(R.string.special_activity_state_2);
                        this.btnState = ActivityRelativeStates.BtnSignState;
                    } else if (activityState == ActivityRelativeStates.ActivityEndState) {
                        bottomActionBtn.setEnabled(false);
                        bottomActionBtn.setText(R.string.special_activity_state_9);
                    } else {
                        bottomActionBtn.setEnabled(false);
                        bottomActionBtn.setText(R.string.special_activity_state_3);
                    }
                    break;
                case ActivityRelativeStates.RelAdminState:
                    if (activityState == ActivityRelativeStates.ActivityEndState) {
                        bottomActionBtn.setEnabled(false);
                        bottomActionBtn.setText(R.string.special_activity_state_8);
                    } else if (activityState == ActivityRelativeStates.ActivityStartState) {
                        this.btnState = ActivityRelativeStates.BtnAdminState;
                        bottomActionBtn.setText(R.string.special_activity_state_4);
                    } else {
                        bottomActionBtn.setEnabled(false);
                        bottomActionBtn.setText(R.string.special_activity_state_1);
                    }
                    break;
                case ActivityRelativeStates.RelNotCheckInState:
                    /* 如果当前活动未结束，则显示签到按钮，但若已结束，则和未签退状态一样都将被设为未签退文字，故此处未打break */
                    if (activityState!=ActivityRelativeStates.ActivityEndState){
                        this.btnState=ActivityRelativeStates.BtnCheckInState;
                        bottomActionBtn.setText(R.string.special_activity_state_5);
                    }
                case ActivityRelativeStates.RelNotCheckOutState:
                    if (activityState==ActivityRelativeStates.ActivityEndState){
                        bottomActionBtn.setEnabled(false);
                        bottomActionBtn.setText(R.string.special_activity_state_7);
                    }else {
                        this.btnState=ActivityRelativeStates.BtnCheckOutState;
                        bottomActionBtn.setText(R.string.special_activity_state_6);
                    }
                    break;
            }
        }

        /**
         * MVP框架下，查询成功的视图控制回调
         *
         * @param activity 要展示的活动详情对象
         * @param bulletin 该活动最新一条公告
         */
        public void onInitSuccess(MyStudentActivity activity, String bulletin) {
            bindToViews(activity);
            final TextView bulletinView = binding.bulletin;
            bulletinView.setText(bulletin);
        }

        public void onFail(String msg) {
            PromptUtil.snackbarShowTxt(root,msg);
        }

        /**
         * 签到成功的回调操作
         */
        public void onSignSuccess(MyStudentActivity activity){
            PromptUtil.snackbarShowTxt(root,"报名成功！");
            /* 根据活动各种时间及用户当前与活动的关系来改变底部按钮的文字、状态及点击事件类别 */
            int signState = checkSignState(activity.getSignStart(), activity.getSignEnd());
            int activityState = checkActivityState(activity.getActivityStart(), activity.getActivityEnd());
            checkUserState(signState, activityState, activity.getParticipantState());
        }

    }

    /**
     * 评论展示页面
     */
    public class ActivityCommentFragment extends Fragment {

        private FragmentActivityCommentBinding binding;
        private View root;
        private ActivityCommentPresenter presenter;
        private double score = 5.0;

        private ActivityCommentAdapter adapter;
        private TextView scoreView;
        private Button commentBtn;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            binding = FragmentActivityCommentBinding.inflate(inflater, container, false);
            root = binding.getRoot();
            presenter = new ActivityCommentPresenter(this);
            commentBtn= binding.commentBtn;
            return root;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            final RecyclerView recyclerView = binding.recyclerView2;
            final RatingBar ratingBar = binding.ratingBar;
            scoreView = binding.score;
            final EditText newComment = binding.commentInput;

            adapter = new ActivityCommentAdapter();
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
                score = rating;
                PromptUtil.snackbarShowTxt(root, score + "");
            });

            final MainActivityViewModel activityViewModel=new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

            commentBtn.setOnClickListener(v -> {
                Comment comment=new Comment();
                comment.setActivityId(activityId);
                comment.setUserId(activityViewModel.getUserInfo().getValue().getId());
                comment.setContent(newComment.getText().toString());
                comment.setScore(this.score);
                presenter.submitComment(repository,comment);
            });

            //初始化数据
            presenter.getComments(repository, activityId);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            root = null;
            binding = null;
        }

        /**
         * 数据提交recyclerView，计算活动得分并显示
         *
         * @param commentList 当前活动关联的评论
         * @param isCommentAvailable 是否运行当前用户评论
         */
        public void onSearchSuccess(List<Comment> commentList,boolean isCommentAvailable) {
            adapter.submitList(commentList);
            scoreView.setText(String.valueOf(computeScore(commentList)));
            if (!isCommentAvailable)
                disableComment();
        }

        /**
         * 活动评分显示默认的5.0
         * todo:显示暂无评论
         */
        public void onSearchFail() {
            scoreView.setText("5.0");
        }

        /**
         * 结算活动得分
         *
         * @param commentList 要展示的评论列表
         * @return
         */
        private double computeScore(List<Comment> commentList) {
            if (commentList == null || commentList.size() == 0)
                return 5.0;
            double sum = 0;
            for (Comment comment : commentList) {
                sum += comment.getScore();
            }
            return sum / commentList.size();
        }

        /**
         * 评论成功后，刷新评论列表
         * @param commentList 服务器返回的新的评论列表
         */
        public void onCommentSuccess(List<Comment> commentList){
            PromptUtil.snackbarShowTxt(root,"评论成功！");
            adapter.submitList(commentList);
            scoreView.setText(String.valueOf(computeScore(commentList)));
            disableComment();
        }

        /**
         * 禁止用户评论
         */
        private void disableComment(){
            commentBtn.setEnabled(false);
        }

    }
}