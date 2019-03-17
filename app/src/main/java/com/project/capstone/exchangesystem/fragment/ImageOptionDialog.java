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

public class ImageOptionDialog extends BottomSheetDialogFragment {
    private ImageOptionListener imgListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.image_option_bottom_sheet, container, false);
        Button btnChoose = v.findViewById(R.id.btnChoose);
        Button btnCapture = v.findViewById(R.id.btnCapture);
        Button btnDelete = v.findViewById(R.id.btnDelete);
        Button btnCancel = v.findViewById(R.id.btnCancel);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgListener.onButtonClicked(0);
                dismiss();
            }
        });

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgListener.onButtonClicked(1);
                dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgListener.onButtonClicked(2);
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgListener.onButtonClicked(3);
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
}
