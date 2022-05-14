package com.example.graduatedesign.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.graduatedesign.R;

public class OneInputDialog extends DialogFragment {

    private static final String TAG = "OneInputDialog";
    private String title = "输入信息";
    private OneInputDialogListener listener;
    private String defaultInput = "";

    public void setListener(OneInputDialogListener listener) {
        this.listener = listener;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDefaultInput(String defaultInput) {
        this.defaultInput = defaultInput;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        /* 弹窗包含的视图: 一个输入框 */
        EditText editText = new EditText(this.getContext());
        editText.setText(defaultInput);
        editText.setHeight(150);
        editText.setPadding(20, 10, 20, 10);
        editText.setGravity(Gravity.START);
        editText.setBackgroundResource(R.drawable.shape_rec_background_shadow);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(editText)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(R.string.txt_confirm, (dialog, id) -> {
                    String inputStr = editText.getText().toString();
                    if (listener != null)
                        listener.onDialogPositiveClick(this, inputStr);
                    else
                        Log.d(TAG, "onCreateDialog: " + inputStr);
                })
                .setNegativeButton(R.string.txt_cancel, (dialog, id) -> {
                    if (listener != null)
                        listener.onDialogNegativeClick(this);
                    else
                        this.getDialog().cancel();
                });
        return builder.create();
    }

    public interface OneInputDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String result);

        void onDialogNegativeClick(DialogFragment dialog);
    }

}
