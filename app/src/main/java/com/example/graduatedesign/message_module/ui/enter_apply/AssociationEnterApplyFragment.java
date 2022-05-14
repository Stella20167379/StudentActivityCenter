package com.example.graduatedesign.message_module.ui.enter_apply;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.databinding.FragmentWithOneRecyclerviewBinding;
import com.example.graduatedesign.net.netty.AppCache;
import com.example.graduatedesign.utils.ArrayMapBuilder;
import com.example.graduatedesign.utils.GlideUtils;
import com.example.graduatedesign.utils.GsonConvertTypes;
import com.example.graduatedesign.utils.PromptUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AssociationEnterApplyFragment extends Fragment {
    private static final String TAG = "AssociationEnterApplyFr";
    private FragmentWithOneRecyclerviewBinding binding;
    private View root;
    private AssociationEnterApplyAdapter adapter;
    @Inject
    Gson gson;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWithOneRecyclerviewBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        adapter = new AssociationEnterApplyAdapter();
        recyclerView = binding.recyclerView;
        progressBar = binding.progressBar;
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView title= binding.toolbarTitle;
        final Toolbar toolbar=binding.toolbar;


        toolbar.setNavigationOnClickListener(v->{
            final NavController navController= Navigation.findNavController(root);
            navController.popBackStack();
        });
        title.setText("入会申请");

        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    private void initData() {
        Integer userId = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class).getUserInfo().getValue().getId();
        try {
            String url = "message/get/apply";
            String body = gson.toJson(new ArrayMapBuilder().put("userId", userId).build());
            AppCache.getService().sendActionMsg(url, body, (code, msg, o) -> {
                if (code == 200) {
                    List data = (List) o;

                    JsonElement jsonElement = gson.toJsonTree(data);
                    List<Message> applyList = gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfMessageList);
                    adapter.submitList(applyList);
                    adapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);
                    PromptUtil.snackbarShowTxt(root, "初始化成功");
                } else {
                    progressBar.setVisibility(View.GONE);
                    PromptUtil.showAlert(getContext(), "信息初始化错误： " + msg);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "onViewCreated: 信息初始化失败");
            progressBar.setVisibility(View.GONE);
            PromptUtil.showAlert(getContext(), "socket未登录，信息初始化失败");
        }
    }

    private void updateEnterApplyState(Integer applyId, boolean TOrF) {
        try {
            String url = "message/edit/apply";
            String body = gson.toJson(new ArrayMapBuilder().put("TOrF", TOrF).put("applyId", applyId).build());
            AppCache.getService().sendActionMsg(url, body, (code, msg, o) -> {
                if (code == 200) {
                    Message message = (Message) o;
                    int size = adapter.getItemCount();
                    adapter.getCurrentList().add(message);
                    adapter.notifyItemChanged(size);
                    PromptUtil.snackbarShowTxt(root, "操作成功");
                } else {
                    PromptUtil.snackbarShowTxt(root, "操作失败 " + msg);
                }
            });
        } catch (Exception e) {
            PromptUtil.showAlert(getContext(), "操作失败");
        }
    }

    class AssociationEnterApplyAdapter extends RecyclerView.Adapter<AssociationEnterApplyHolder> {
        private final AsyncListDiffer<Message> mDiffer;
        private final DiffUtil.ItemCallback<Message> diffCallback = new DiffUtil.ItemCallback<Message>() {
            @Override
            public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
                return oldItem.getId() == newItem.getId();
            }
        };

        public AssociationEnterApplyAdapter() {
            mDiffer = new AsyncListDiffer<>(this, diffCallback);
        }

        @NonNull
        @Override
        public AssociationEnterApplyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_association_enter_apply, parent, false);
            return new AssociationEnterApplyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AssociationEnterApplyHolder holder, int position) {
            holder.bind(getItem(position));
        }

        @Override
        public int getItemCount() {
            return mDiffer.getCurrentList().size();
        }

        public List<Message> getCurrentList() {
            return mDiffer.getCurrentList();
        }

        public void submitList(List<Message> data) {
            mDiffer.submitList(data);
        }

        public Message getItem(int position) {
            return mDiffer.getCurrentList().get(position);
        }
    }

    class AssociationEnterApplyHolder extends RecyclerView.ViewHolder {
        private final View itemView;

        private final ImageView portrait;
        private final TextView senderName;
        private final TextView applyContent;
        private Button yesBtn;
        private Button noBtn;

        public AssociationEnterApplyHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;

            portrait = itemView.findViewById(R.id.senderImg);
            senderName = itemView.findViewById(R.id.senderName);
            applyContent = itemView.findViewById(R.id.apply_txt);
            yesBtn = itemView.findViewById(R.id.yes_btn);
            noBtn = itemView.findViewById(R.id.no_btn);

        }

        public void bind(Message message){
            if (message == null)
                return;

            Glide.with(itemView)
                    .load(GlideUtils.getImgDownloadUri(message.getSenderPortrait()))
                    .apply(GlideUtils.OPTIONS)
                    .into(portrait);

            senderName.setText(message.getSender());
            applyContent.setText(message.getContent());
            if (message.isApplyState()) {
                yesBtn.setEnabled(false);
                yesBtn.setText("已通过");
                noBtn.setEnabled(false);
            } else {
                yesBtn.setEnabled(true);
                yesBtn.setText("通过");
                noBtn.setEnabled(true);
                yesBtn.setOnClickListener(v -> {
                    updateEnterApplyState(message.getId(), true);
                });
                noBtn.setOnClickListener(v -> {
                    updateEnterApplyState(message.getId(), false);
                });
            }
        }
    }

}