package com.example.graduatedesign.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.adapter.MyPagerAdapter;
import com.example.graduatedesign.databinding.FragmentHomeBinding;
import com.example.graduatedesign.personal_module.data.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private View root;
    private HomeViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView schoolNameTitle= binding.schoolNameTitle;
        final TextView nicknameTitle= binding.nickname;
        final ImageButton filterBtn=binding.filterBtn;
        final ImageButton searchBtn=binding.searchBtn;
        final EditText keyInput= binding.editTextWithDel;
        final TabLayout tabLayout = view.findViewById(R.id.tabs);
        final ViewPager2 viewPager= binding.viewpager;

        final MainActivityViewModel mainActivityViewModel=new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        viewModel=new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        /* 顶部用户信息展示 */
        User user=mainActivityViewModel.getUserInfo().getValue();
        nicknameTitle.setText(user.getNickname());
        schoolNameTitle.setText(user.getSchoolName());
        binding.setIsAnyAssociationAdmin(user.isAssociationAdmin());

        /* 初始化tab栏和viewpager */
        List<String> clazzList=new ArrayList<>();
        clazzList.add(HomeActivitiesFragment.class.getName());
        clazzList.add(HomeAssociationsFragment.class.getName());
        final MyPagerAdapter adapter = new MyPagerAdapter(this,clazzList);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position==0)
                        tab.setText(R.string.txt_activity);
                    else if (position==1)
                        tab.setText(R.string.txt_association);
                }
        ).attach();

        searchBtn.setOnClickListener(v->{
            String key=keyInput.getText().toString();
            viewModel.setTabOpt(tabLayout.getSelectedTabPosition());
            viewModel.getKey().setValue(key);
//            adapter.notifyDataSetChanged();
        });

        filterBtn.setOnClickListener(v->{
            final NavController navController= Navigation.findNavController(view);
            navController.navigate(R.id.navigation_activity_search);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root=null;
        binding=null;
    }
}

