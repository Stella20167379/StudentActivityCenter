package com.example.graduatedesign.message_module.ui.enter_apply;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.databinding.FragmentWithOneRecyclerviewBinding;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AssociationEnterApplyFragment extends Fragment {
    @Inject
    MyRepository repository;

    private FragmentWithOneRecyclerviewBinding binding;
    private View root;
    private AssociationEnterApplyPresenter presenter;
    private AssociationEnterApplyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWithOneRecyclerviewBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        presenter=new AssociationEnterApplyPresenter(this);
        adapter=new AssociationEnterApplyAdapter();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView title= binding.toolbarTitle;
        final Toolbar toolbar=binding.toolbar;
        final RecyclerView recyclerView= binding.recyclerView;
        final MainActivityViewModel activityViewModel=new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        toolbar.setNavigationOnClickListener(v->{
            final NavController navController= Navigation.findNavController(root);
            navController.popBackStack();
        });
        title.setText("入会申请");

        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        presenter.initData(repository,activityViewModel.getUserInfo().getValue().getId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root=null;
        binding=null;
    }

    class AssociationEnterApplyAdapter extends RecyclerView.Adapter<AssociationEnterApplyHolder> {
        private final AsyncListDiffer<Message> mDiffer;
        private final DiffUtil.ItemCallback<Message> diffCallback=new DiffUtil.ItemCallback<Message>() {
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
                    .load(DataUtil.getImgDownloadUri(message.getSenderPortrait()))
                    .apply(GlideUtils.OPTIONS)
                    .into(portrait);
            senderName.setText(message.getSender());
            applyContent.setText(message.getContent());
//todo:完善
            yesBtn.setOnClickListener(v->{
                presenter.passEnterApply();
            });
            noBtn.setOnClickListener(v->{
                presenter.denyEnterApply();
            });
        }
    }

}