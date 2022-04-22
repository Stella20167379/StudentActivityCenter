package com.example.graduatedesign.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.data.model.Result;
import com.example.graduatedesign.databinding.FragmentForgetPassSecondBinding;
import com.example.graduatedesign.utils.PromptUtil;

public class ForgetPassSecondFragment extends Fragment {
    private FragmentForgetPassSecondBinding binding;
    private View root;
    private ForgetPassViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentForgetPassSecondBinding.inflate(inflater,container,false);
        root= binding.getRoot();
        viewModel=new ViewModelProvider(this).get(ForgetPassViewModel.class);

        Toolbar toolbar=binding.toolbar;
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(root).popBackStack());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final EditText old1View= binding.oldPassword;
        final EditText new1View= binding.newPassword;
        final EditText confirm1View= binding.confirmPassword;
        final Button resetBtn=binding.btnToResetPass;

        super.onViewCreated(view, savedInstanceState);
        /* -------------------- 实现ViewModel中数据与页面中视图控件双向绑定 --------------------- */
        /* 以下是实现数据改变->视图改变 */
        viewModel.getFormState().observe(getViewLifecycleOwner(),forgetPassFormState -> {
            if (forgetPassFormState==null)
                return;
            resetBtn.setEnabled(forgetPassFormState.isValid());
            if (forgetPassFormState.getOld1Error()!=null)
                old1View.setError(getString(forgetPassFormState.getOld1Error()));
            if (forgetPassFormState.getNew1Error()!=null)
                new1View.setError(getString(forgetPassFormState.getNew1Error()));
            if (forgetPassFormState.getConfirmError()!=null)
                confirm1View.setError(getString(forgetPassFormState.getConfirmError()));
        });

        viewModel.getResetPassResult().observe(getViewLifecycleOwner(),result -> {
            if (result instanceof Result.Success)
                onPassResetSuccess();
            else
                onPassResetFail(result.toString());
        });

        /* 以下是实现视图改变->数据改变 */
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.onFormDataChanged(old1View.getText().toString(),
                        new1View.getText().toString(),confirm1View.getText().toString());
            }
        };
        old1View.addTextChangedListener(afterTextChangedListener);
        new1View.addTextChangedListener(afterTextChangedListener);
        confirm1View.addTextChangedListener(afterTextChangedListener);

        resetBtn.setOnClickListener(v->{
            viewModel.resetPass(old1View.getText().toString(),new1View.getText().toString());
        });

        /* ----------------------------------- END ------------------------------------ */
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root=null;
        binding=null;
    }

    private void onPassResetSuccess(){
        PromptUtil.snackbarShowTxt(root, R.string.prompt_reset_pass_success);
        //若用户在登录状态，重置密码后用户需重新登录，否则退回登录页面
        MainActivityViewModel activityViewModel=new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        if (activityViewModel.getUserInfo().getValue()!=null){
            activityViewModel.setLoginPrompt(getString(R.string.prompt_login_for_reset_pass));
            activityViewModel.getUserInfo().setValue(null);
        }else {
            Navigation.findNavController(root).popBackStack();
        }
    }

    private void onPassResetFail(String msg){
        PromptUtil.snackbarShowTxt(root,msg);
    }

}