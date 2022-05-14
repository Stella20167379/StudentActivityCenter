package com.example.graduatedesign.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.graduatedesign.R;
import com.example.graduatedesign.adapter.CollegeSpinnerAdapter;
import com.example.graduatedesign.data.LoginRepository;
import com.example.graduatedesign.data.model.College;
import com.example.graduatedesign.databinding.FragmentRegisterFirstBinding;
import com.example.graduatedesign.presenter.RegisterFirstContract;
import com.example.graduatedesign.presenter.RegisterFirstPresenter;
import com.example.graduatedesign.utils.AssetsUtil;
import com.example.graduatedesign.utils.GsonConvertTypes;
import com.example.graduatedesign.utils.RxLifecycleUtils;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class RegisterFirstFragment extends Fragment implements AdapterView.OnItemSelectedListener, RegisterFirstContract.IRegisterFirstView {

    @Inject
    LoginRepository repository;

    private static final String TAG = "RegisterFirstFragment";
    private FragmentRegisterFirstBinding binding;
    private View root;
    private RegisterFirstContract.IRegisterFirstPresenter presenter;

    private Integer selectedCollegeId = null;
    private String studentNo = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterFirstBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        presenter = new RegisterFirstPresenter(this);
        /* 别忘了加上观察者，才能起作用啊！ */
        getLifecycle().addObserver(presenter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textView = binding.studentNo;

        Toolbar toolbar = binding.toolbar;
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(root).popBackStack());

        initCollegeInfo();

        final Button btn = binding.continueBtn;
        btn.setOnClickListener(v -> {
            studentNo = textView.getText().toString();
            if (TextUtils.isEmpty(studentNo) || studentNo.trim().length() < 1) {
                textView.setError("学号不能为空");
            } else {
                presenter.validateStudentNo(repository, selectedCollegeId, studentNo);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        repository = null;
        presenter = null;
        root = null;
        binding = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        College college = (College) parent.getSelectedItem();
        selectedCollegeId = college.getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * 初始化下拉框信息
     */
    public void initCollegeInfo() {

        Single.create((SingleOnSubscribe<List<College>>) emitter -> {
            String file = "college.json";
            String listJson = AssetsUtil.getJsonFromAssets(getContext(), file);
            Log.d(TAG, "获取的json内容：" + listJson);

            Gson gson = new Gson();
            List<College> collegeList = gson.fromJson(listJson, GsonConvertTypes.gsonTypeOfCollegeList);
            emitter.onSuccess(collegeList);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(this))
                .subscribe(collegeList -> {
                            CollegeSpinnerAdapter adapter = new CollegeSpinnerAdapter(collegeList);
                            final Spinner spinner = binding.spinner;
                            spinner.setAdapter(adapter);
                            spinner.setOnItemSelectedListener(this);
                        },
                        throwable -> {
                            Log.d(TAG, "发生错误");
                            throwable.printStackTrace();
                        }
                );
    }

    /**
     * 用户输入学号合法时的回调
     */
    @Override
    public void onPassStudentNo(int credentialInfoId) {
        NavController navController = Navigation.findNavController(root);

        Bundle bundle = new Bundle();
        bundle.putInt("collegeId", selectedCollegeId);
        bundle.putInt("credentialInfoId", credentialInfoId);

        navController.navigate(R.id.action_registerFirstFragment_to_registerSecondFragment, bundle);
    }

    /**
     * 检验用户输入是否符合要求时，发生错误或学号非法时的回调
     *
     * @param msg 要展示给用户看的错误信息
     */
    @Override
    public void onDenyStudentNo(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}