package com.example.graduatedesign.student_activity_module.ui.comment;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.student_activity_module.data.Comment;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActivityCommentPresenter implements DefaultLifecycleObserver {
    private ActivityCommentFragment view;

    public ActivityCommentPresenter(ActivityCommentFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        view = null;
    }

    /**
     * 查询活动相关的评论，提示检查用户是否已评论过，已发布活动评论就要禁止用户输入新评论
     *
     * @param userId     用户id
     * @param activityId 活动id
     */
    public void getCommentsForActivity(int userId, MyRepository repository, int activityId) {
        repository.getCommentsForActivity(activityId, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(data -> {
                            List<Comment> commentList = (List<Comment>) data.get("list");
                            boolean isCommented = (boolean) data.get("isCommented");
                            Comment userComment = null;
                            if (isCommented) {
                                userComment = new Comment();
                                String content = (String) data.get("content");
                                double score = (double) data.get("score");
                                userComment.setContent(content);
                                userComment.setScore(score);
                            }
                            if (commentList.size() < 1)
                                view.onSearchFail();
                            else view.onSearchSuccess(commentList, userComment);
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            view.onSearchFail();
                        });
    }

    public void submitComment(MyRepository repository, Comment comment) {
        repository.addComment(comment.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(comments -> {
                            view.onCommentSuccess(comments);
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            view.onCommentFail("评论失败：" + throwable.getMessage());
                        });
    }

}
