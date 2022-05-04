package com.example.graduatedesign.ui.personal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.databinding.FragmentPersonalBinding;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.student_activity_module.adapter.ActivitySimpleAdapter;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;

public class PersonalFragment extends Fragment {

    private FragmentPersonalBinding binding;
    private View root;
    private ActivitySimpleAdapter adapter;
    private PersonalViewModel personalViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPersonalBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        personalViewModel=new ViewModelProvider(requireActivity()).get(PersonalViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ImageView imageView= binding.imageView;
        final TextView nicknameView= binding.userName;
        final TextView schoolView= binding.schoolView;
        final TextView moreBtn= binding.moreBtn;
        final RecyclerView recyclerView=binding.recyclerView;

        final ImageButton myAssociationBtn= binding.myAssociationBtn;
        final ImageButton myOrderBtn= binding.myOrderBtn;
        final ImageButton myCommentBtn= binding.myCommentBtn;
        final NavController navController=Navigation.findNavController(root);

        final MainActivityViewModel mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        User user=mainActivityViewModel.getUserInfo().getValue();

        Glide.with(this)
                .load(DataUtil.getImgDownloadUri(DataUtil.getImgDownloadUri(user.getPortrait())))
                .apply(GlideUtils.OPTIONS)
                .into(imageView);
        nicknameView.setText(user.getNickname());
        schoolView.setText(user.getSchoolName());

        adapter=new ActivitySimpleAdapter();
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        personalViewModel.getMyActivities().observe(requireActivity(),activities->{
            adapter.submitList(activities);
        });

        Bundle bundle=new Bundle();
        bundle.putInt("id",user.getId());

        moreBtn.setOnClickListener(v->{
            navController.navigate(R.id.personalDetailFragment,bundle);
        });

        myAssociationBtn.setOnClickListener(v->{
            navController.navigate(R.id.myAssociationFragment,bundle);
        });
        myOrderBtn.setOnClickListener(v->{
            navController.navigate(R.id.myRecordFragment,bundle);
        });
        myCommentBtn.setOnClickListener(v->{
            navController.navigate(R.id.myCommentFragment,bundle);
        });

        personalViewModel.initMyActivities(user.getId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root=null;
        binding=null;
    }
}