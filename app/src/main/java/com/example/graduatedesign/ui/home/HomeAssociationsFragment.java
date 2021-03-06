package com.example.graduatedesign.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.association_module.adapter.AssociationShowAdapter;
import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.databinding.FragmentWithOneRecyclerviewBinding;
import com.example.graduatedesign.presenter.HomeAssociationsPresenter;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeAssociationsFragment extends Fragment {
    private static final String TAG = "HomeAssociationsFragmen";

    @Inject
    MyRepository repository;

    private FragmentWithOneRecyclerviewBinding binding;
    private View root;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AssociationShowAdapter adapter;
    private HomeAssociationsPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWithOneRecyclerviewBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        presenter = new HomeAssociationsPresenter(this);
        /* ???????????????????????????????????????????????? */
        getLifecycle().addObserver(presenter);

        //??????????????????????????????
        final Toolbar toolbar = binding.toolbar;
        toolbar.setVisibility(View.GONE);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar= binding.progressBar;

        recyclerView= binding.recyclerView;

        adapter = new AssociationShowAdapter();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new AssociationItemListener());

        final MainActivityViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        Integer schoolId = viewModel.getUserInfo().getValue().getSchoolId();
        //???????????????
        presenter.getShowAssociations(repository, schoolId, null);

        /* ???????????? */
        final HomeViewModel homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        homeViewModel.getSearchKey().observe(getViewLifecycleOwner(), key -> {
            if (homeViewModel.getTabOpt() != 1)
                return;
            Log.d(TAG, "onViewCreated: ??????????????????");
            presenter.getShowAssociations(repository, schoolId, key);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    /**
     * ????????????
     * ??????????????????
     */
    public void onSearchFail() {
        progressBar.setVisibility(View.INVISIBLE);
        Log.d(TAG, "onSearchFail: ????????????");
    }

    /**
     * ????????????????????????
     *
     * @param data ??????????????????
     */
    public void onSearchSuccess(List<Association> data) {
        progressBar.setVisibility(View.INVISIBLE);
        adapter.submitList(data);
    }

    class AssociationItemListener extends RecyclerView.SimpleOnItemTouchListener {
        //????????????????????????????????????????????????????????????implement??????????????????
        private GestureDetector.SimpleOnGestureListener gl = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //???????????????????????????recyclerview?????????
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                Log.d(TAG, "onSingleTapUp???????????? " + e.getX());
                int position = (int) recyclerView.getChildAdapterPosition(child);

                /* ????????? */
                if (position < 0)
                    return false;

                Log.d(TAG, "?????????????????????: " + position);
                Association association = adapter.getItem(position);
                //????????????????????????
                Bundle bundle = new Bundle();
                bundle.putInt("id", association.getId());
                final NavController navController = Navigation.findNavController(recyclerView);
                navController.navigate(R.id.associationDetailFragment, bundle);
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
