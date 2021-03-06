package com.example.graduatedesign.association_module.ui.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.association_module.ui.AssociationViewModel;
import com.example.graduatedesign.custom.OneInputDialog;
import com.example.graduatedesign.databinding.FragmentAssociationDetailBinding;
import com.example.graduatedesign.net.netty.AppCache;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.student_activity_module.adapter.ActivitySimpleAdapter;
import com.example.graduatedesign.student_activity_module.ui.RelativeStates;
import com.example.graduatedesign.utils.ArrayMapBuilder;
import com.example.graduatedesign.utils.GlideUtils;
import com.example.graduatedesign.utils.PromptUtil;
import com.google.gson.Gson;

import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AssociationDetailFragment extends Fragment {

    private static final String TAG = "AssociationDetailFragment";
    private FragmentAssociationDetailBinding binding;
    private View root;
    private Bundle args;
    private AssociationViewModel associationViewModel;
    @Inject
    Gson gson;
    private ImageView portraitView;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAssociationDetailBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        associationViewModel = new ViewModelProvider(requireActivity()).get(AssociationViewModel.class);
        portraitView = binding.portrait;
        user = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class).getUserInfo().getValue();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        args = getArguments();
        if (args == null) {
            PromptUtil.snackbarShowTxt(root, "?????????????????????????????????????????????????????????");
            navController.popBackStack();
            return;
        }

        final Toolbar toolbar = binding.toolbar;
        final ImageView moreBulletinBtn = binding.moreBulletin;
        final RecyclerView recyclerView = binding.recyclerView;
        final TextView joinBtn = binding.joinBtn;
        final TextView latestBulletinView = binding.bulletin;

        //??????????????????
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        ActivitySimpleAdapter adapter = new ActivitySimpleAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        associationViewModel.getState().setValue(null);
        /* ??????livedata???????????????????????????????????????????????? */
        associationViewModel.getState().observe(getViewLifecycleOwner(), state -> {
            if (state == null)
                return;
            switch (state) {
                case 0:
                    //????????????????????????
                    joinBtn.setVisibility(View.VISIBLE);
                    joinBtn.setOnClickListener(v -> {
                        OneInputDialog dialog = new OneInputDialog();
                        dialog.setTitle("????????????");
                        dialog.setListener(new OneInputDialog.OneInputDialogListener() {
                            @Override
                            public void onDialogPositiveClick(DialogFragment dialog, String result) {
                                sendAssociationJoinApply(user.getId(), result, args.getInt("id"));
                            }

                            @Override
                            public void onDialogNegativeClick(DialogFragment dialog) {
                                dialog.getDialog().cancel();
                            }
                        });
                        dialog.show(getParentFragmentManager(), "joinBtn");
                    });
                    break;
                case 1:
                    joinBtn.setVisibility(View.GONE);
                    break;
                case 2:
                    //??????????????????
                    joinBtn.setVisibility(View.GONE);
                    renderAdminMenu();
                    break;
            }
        });
        associationViewModel.getAssociationToShowDetail().observe(getViewLifecycleOwner(), associationToShowDetail -> {
            if (associationToShowDetail == null) {
                PromptUtil.snackbarShowTxt(root, "?????????????????????");
                navController.popBackStack();
                return;
            }
            bindToViews(associationToShowDetail);
        });
        associationViewModel.getActivities().observe(getViewLifecycleOwner(), activities -> {
            adapter.submitList(activities);
        });
        associationViewModel.getLatestBulletin().observe(getViewLifecycleOwner(), latestBulletin -> {
            latestBulletinView.setText(latestBulletin);
        });
        /* ????????????????????????????????????????????????????????????/???????????????????????????Glide??????????????? */
        associationViewModel.getNewImgPath().observe(getViewLifecycleOwner(), imgPath -> {
            Log.d(TAG, "onViewCreated ????????????: " + imgPath);
//            Glide.with(requireActivity())
//                    .load(DataUtil.getImgDownloadUri(DataUtil.getImgDownloadUri(imgPath)))
//                    .apply(GlideUtils.OPTIONS)
//                    .into(portraitView);
        });

        moreBulletinBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("type", 2);
            bundle.putInt("ownerId", associationViewModel.getAssociationToShowDetail().getValue().getId());
            navController.navigate(R.id.bulletinDetailFragment, bundle);
        });

        associationViewModel.initData(user.getId(), root, args.getInt("id"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    private void bindToViews(Association associationToShow) {
        final TextView associationName = binding.name;
        final TextView establishTime = binding.establishTime;
        final TextView introduction = binding.introduction;

        if (associationToShow != null) {
            Glide.with(requireActivity())
                    .load(GlideUtils.getImgDownloadUri(associationToShow.getCoverImg()))
                    .apply(GlideUtils.OPTIONS)
                    .into(portraitView);
            establishTime.setText(associationToShow.getEstablishTime());
            introduction.setText(associationToShow.getIntroduction());
            associationName.setText(associationToShow.getAssociationName());
        }
    }

    private void sendAssociationJoinApply(Integer userId, String content, Integer associationId) {
        Map data = new ArrayMapBuilder().put("content", content).put("associationId", associationId).put("userId", userId).build();
        try {
            String url = "message/join/association";
            String body = gson.toJson(data);
            AppCache.getService().sendActionMsg(url, body, (code, msg, o) -> {
                if (code == 200) {
                    PromptUtil.snackbarShowTxt(root, "????????????????????????");
                } else {
                    PromptUtil.showAlert(getContext(), "????????????????????? " + msg);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "onViewCreated: ??????????????????");
            PromptUtil.showAlert(getContext(), "??????????????????");
        }
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    private void renderAdminMenu() {
        final Toolbar toolbar = binding.toolbar;
        final NavController navController = Navigation.findNavController(root);
        Association association = associationViewModel.getAssociationToShowDetail().getValue();

        assert association != null;

        Bundle bundle = new Bundle();
        bundle.putInt("associationId", association.getId());

        toolbar.inflateMenu(R.menu.association_admin_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.edit_information:
                    msg += "????????????";
                    navController.navigate(R.id.associationEditFragment);
                    break;
                case R.id.add_activity:
                    msg += "????????????";
                    bundle.putInt("type", RelativeStates.FromAssociationFragment);
                    bundle.putString("name", association.getAssociationName());
                    navController.navigate(R.id.activityAddFragment, bundle);
                    break;
                case R.id.send_msg:
                    msg += "????????????";
                    OneInputDialog dialog = new OneInputDialog();
                    dialog.setTitle("??????");
                    dialog.setListener(new OneInputDialog.OneInputDialogListener() {
                        @Override
                        public void onDialogPositiveClick(DialogFragment dialog, String result) {
                            sendNotifyMsg(association.getId(), result, user.getId());
                        }

                        @Override
                        public void onDialogNegativeClick(DialogFragment dialog) {
                            dialog.getDialog().cancel();
                        }
                    });
                    dialog.show(getParentFragmentManager(), "send_msg");
                    break;
                case R.id.show_members:
                    msg += "????????????";
                    navController.navigate(R.id.showMembersFragment, bundle);
                    break;
            }
            if (!msg.equals("")) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
            return true;
        });

    }

    /**
     * ??????????????????????????????????????????
     *
     * @param associationId ??????id
     * @param content       ??????
     * @param from          ????????????????????????id
     */
    private void sendNotifyMsg(Integer associationId, String content, Integer from) {
        Map data = new ArrayMapBuilder().put("content", content).put("associationId", associationId).put("from", from).build();
        try {
            String url = "message/send/notify";
            String body = gson.toJson(data);
            AppCache.getService().sendActionMsg(url, body, (code, msg, o) -> {
                if (code == 200) {
                    PromptUtil.snackbarShowTxt(root, "????????????");
                } else {
                    PromptUtil.showAlert(getContext(), "??????????????? " + msg);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "onViewCreated: ??????????????????");
            PromptUtil.showAlert(getContext(), "??????????????????");
        }
    }

}
