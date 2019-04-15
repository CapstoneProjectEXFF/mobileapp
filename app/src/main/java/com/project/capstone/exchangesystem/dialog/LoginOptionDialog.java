package com.project.capstone.exchangesystem.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.project.capstone.exchangesystem.R;

import static com.project.capstone.exchangesystem.constants.AppStatus.CANCEL_IMAGE_OPTION;
import static com.project.capstone.exchangesystem.constants.AppStatus.LOGIN_REMINDER;

public class LoginOptionDialog extends BottomSheetDialogFragment {
    private LoginOptionListener imgListener;
    private int actionFlag;

    public interface LoginOptionListener {
        void onButtonClicked(int choice);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_option_bottom_sheet, container, false);
        Button btnLoginDialog = v.findViewById(R.id.btnLoginDialog);
        Button btnCancel = v.findViewById(R.id.btnCancel);

//        switch (actionFlag){
//            case LOGIN_REMINDER:
//                btnDelete.setVisibility(View.GONE);
//                break;
//            case DONATE_ACTIVITY_IMAGE_FLAG:
//                btnDelete.setText("Bỏ chọn");
//                btnCapture.setVisibility(View.GONE);
//                btnChoose.setVisibility(View.GONE);
//                break;
//        }

        btnLoginDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgListener.onButtonClicked(LOGIN_REMINDER);
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgListener.onButtonClicked(CANCEL_IMAGE_OPTION);
                dismiss();
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            imgListener = (LoginOptionListener) context;
        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString());
            throw new ClassCastException(e.getMessage());
        }
    }


}
