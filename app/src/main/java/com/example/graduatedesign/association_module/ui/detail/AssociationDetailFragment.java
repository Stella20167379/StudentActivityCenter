package com.example.graduatedesign.association_module.ui.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.R;
import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.association_module.ui.AssociationViewModel;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.databinding.FragmentAssociationDetailBinding;
import com.example.graduatedesign.student_activity_module.adapter.ActivitySimpleAdapter;
import com.example.graduatedesign.ui.bulletin.BulletinDetailFragment;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;
import com.example.graduatedesign.utils.PromptUtil;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AssociationDetailFragment extends Fragment {

    private static final String TAG = "AssociationDetailFragme";
    private FragmentAssociationDetailBinding binding;
    private View root;
    private Bundle args;
    private AssociationViewModel associationViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAssociationDetailBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        associationViewModel=new ViewModelProvider(requireActivity()).get(AssociationViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        args = getArguments();
        if (args == null) {
            PromptUtil.snackbarShowTxt(root, "不知道该展示哪个社团的详情，没有传参！");
            navController.popBackStack();
            return;
        }

        final Toolbar toolbar = binding.toolbar;
        final ImageView moreBulletinBtn = binding.moreBulletin;
        final RecyclerView recyclerView = binding.recyclerView;
        final TextView joinBtn = binding.joinBtn;
        final TextView latestBulletinView= binding.bulletin;

        //绑定返回功能
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        ActivitySimpleAdapter adapter = new ActivitySimpleAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        associationViewModel.getState().observe(getViewLifecycleOwner(),state ->{
            switch (state){
                case 0:
                    //加入按钮监听事件
                    joinBtn.setVisibility(View.VISIBLE);
                    joinBtn.setOnClickListener(v -> {
                    });
                    break;
                case 1:
                    joinBtn.setVisibility(View.GONE);
                    break;
                case 2:
                    //渲染更多菜单
                    renderAdminMenu();
                    joinBtn.setVisibility(View.GONE);
                    break;
            }

        });
        associationViewModel.getAssociationToShowDetail().observe(getViewLifecycleOwner(),associationToShowDetail->{
            bindToViews(associationToShowDetail);
        });
        associationViewModel.getActivities().observe(getViewLifecycleOwner(),activities->{
            adapter.submitList(activities);
        });
        associationViewModel.getLatestBulletin().observe(getViewLifecycleOwner(),latestBulletin->{
            latestBulletinView.setText(latestBulletin);
        });

        moreBulletinBtn.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putInt("type",2);
            bundle.putInt("ownerId",associationViewModel.getAssociationToShowDetail().getValue().getId());
            navController.navigate(R.id.bulletinDetailFragment,bundle);
        });

        associationViewModel.initData(args.getInt("id"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    private void bindToViews(Association associationToShow) {
        final ImageView portraitView = binding.portrait;
        final TextView associationName = binding.name;
        final TextView establishTime = binding.establishTime;
        final TextView introduction = binding.introduction;

        if (associationToShow != null) {
            Glide.with(requireActivity())
                    .load(DataUtil.getImgDownloadUri(DataUtil.getImgDownloadUri(associationToShow.getCover())))
                    .apply(GlideUtils.OPTIONS)
                    .into(portraitView);
            establishTime.setText(associationToShow.getEstablishTime());
            introduction.setText(associationToShow.getIntroduction());
            associationName.setText(associationToShow.getAssociationName());
        }
    }

    /**
     * 对该社团的管理员，需要渲染更多下拉菜单
     */
    private void renderAdminMenu(){
        final Toolbar toolbar = binding.toolbar;
        final NavController navController = Navigation.findNavController(root);
        Bundle bundle=new Bundle();
        bundle.putInt("associationId",args.getInt("id"));

        toolbar.inflateMenu(R.menu.association_admin_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.edit_information:
                    msg += "修改资料";
                    navController.navigate(R.id.associationEditFragment);
                    break;
                case R.id.add_activity:
                    msg += "新建活动";
                    navController.navigate(R.id.activityAddFragment,bundle);
                    break;
                case R.id.add_bulletin:
                    msg += "新建公告";
                    break;
                case R.id.show_members:
                    msg += "查看成员";
                    navController.navigate(R.id.showMembersFragment,bundle);
                    break;
            }
            if (!msg.equals("")) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        });

    }

}
