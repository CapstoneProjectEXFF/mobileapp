package com.project.capstone.exchangesystem.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.project.capstone.exchangesystem.R;

import static com.project.capstone.exchangesystem.constants.AppStatus.ADD_IMAGE_FLAG;
import static com.project.capstone.exchangesystem.constants.AppStatus.CANCEL_IMAGE_OPTION;
import static com.project.capstone.exchangesystem.constants.AppStatus.CAPTURE_IMAGE_OPTION;
import static com.project.capstone.exchangesystem.constants.AppStatus.CHANGE_IMAGE_FLAG;
import static com.project.capstone.exchangesystem.constants.AppStatus.CHOOSE_IMAGE_OPTION;
import static com.project.capstone.exchangesystem.constants.AppStatus.DELETE_IMAGE_OPTION;
import static com.project.capstone.exchangesystem.constants.AppStatus.DONATE_ACTIVITY_IMAGE_FLAG;

public class ImageOptionDialog extends BottomSheetDialogFragment {
    private ImageOptionListener imgListener;
    private int actionFlag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.image_option_bottom_sheet, container, false);
        Button btnChoose = v.findViewById(R.id.btnChoose);
        Button btnCapture = v.findViewById(R.id.btnCapture);
        Button btnDelete = v.findViewById(R.id.btnDelete);
        Button btnCancel = v.findViewById(R.id.btnCancel);

        switch (actionFlag){
            case ADD_IMAGE_FLAG:
                btnDelete.setVisibility(View.GONE);
                break;
            case DONATE_ACTIVITY_IMAGE_FLAG:
                btnDelete.setText("Bỏ chọn");
                btnCapture.setVisibility(View.GONE);
                btnChoose.setVisibility(View.GONE);
                break;
        }

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgListener.onButtonClicked(CHOOSE_IMAGE_OPTION);
                dismiss();
            }
        });

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgListener.onButtonClicked(CAPTURE_IMAGE_OPTION);
                dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgListener.onButtonClicked(DELETE_IMAGE_OPTION);
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

    public interface ImageOptionListener{
        void onButtonClicked(int choice);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            imgListener = (ImageOptionListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString());
        }
    }

    public void setActivityFlag(int flag){
        actionFlag = flag;
    }
}
