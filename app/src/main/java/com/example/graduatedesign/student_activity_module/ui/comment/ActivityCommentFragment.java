package com.example.graduatedesign.student_activity_module.ui.comment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.databinding.FragmentActivityCommentBinding;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.student_activity_module.adapter.ActivityCommentAdapter;
import com.example.graduatedesign.student_activity_module.data.Comment;
import com.example.graduatedesign.utils.PromptUtil;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * 评论展示页面
 */
@AndroidEntryPoint
public class ActivityCommentFragment extends Fragment {

    private static final String TAG = "ActivityCommentFragment";
    @Inject
    MyRepository repository;
    private ActivityCommentPresenter presenter;
    private FragmentActivityCommentBinding binding;
    private View root;
    private double score = 5.0;

    private ActivityCommentAdapter adapter;
    private TextView scoreView;
    private Button commentBtn;
    private EditText commentInputView;
    private RatingBar ratingScoreBar;

    private Bundle args;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentActivityCommentBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        presenter = new ActivityCommentPresenter(this);
        /* 别忘了加上观察者，才能起作用啊！ */
        getLifecycle().addObserver(presenter);

        commentBtn = binding.commentBtn;
        commentInputView = binding.commentInput;
        scoreView = binding.score;
        ratingScoreBar = binding.ratingBar;

        args = getArguments();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int activityId = args.getInt("id");

        final RecyclerView recyclerView = binding.recyclerView2;
        final RatingBar ratingBar = binding.ratingBar;

        adapter = new ActivityCommentAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            score = rating;
            Log.d(TAG, "onViewCreated选择评分：" + score);
        });

        final MainActivityViewModel activityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        User user = activityViewModel.getUserInfo().getValue();

        commentBtn.setOnClickListener(v -> {
            Comment comment = new Comment();
            comment.setActivityId(activityId);
            comment.setUserId(user.getId());
            comment.setContent(commentInputView.getText().toString());
            comment.setScore(this.score);
            presenter.submitComment(repository, comment);
        });

        //初始化数据
        presenter.getCommentsForActivity(user.getId(), repository, activityId);
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
     * @param comment     当前用户发布的评论，若无则为null
     */
    public void onSearchSuccess(List<Comment> commentList, Comment comment) {
        adapter.submitList(commentList);
        scoreView.setText(String.valueOf(computeScore(commentList)));
        if (comment != null)
            disableComment(comment);
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
     *
     * @param commentList 服务器返回的新的评论列表，第一条为新发布的评论
     */
    public void onCommentSuccess(List<Comment> commentList) {
        PromptUtil.snackbarShowTxt(root, "评论成功！");
        adapter.submitList(commentList);
        scoreView.setText(String.valueOf(computeScore(commentList)));
        disableComment(commentList.get(0));
    }

    public void onCommentFail(String msg) {
        PromptUtil.snackbarShowTxt(root, msg);
    }

    /**
     * 禁止用户评论
     * 同时显示用户已发布的评论
     */
    private void disableComment(Comment comment) {
        commentBtn.setEnabled(false);
        //显示用户评论
        commentInputView.setEnabled(false);
        commentInputView.setText(comment.getContent());
        //显示用户打分
        ratingScoreBar.setIsIndicator(true);
        ratingScoreBar.setRating((float) comment.getScore());
    }

}
