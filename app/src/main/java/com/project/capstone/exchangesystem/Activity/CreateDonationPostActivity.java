package com.project.capstone.exchangesystem.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.project.capstone.exchangesystem.fragment.ImageOptionDialog;
import com.project.capstone.exchangesystem.model.FirebaseImg;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.DonationPost;

import java.io.IOException;
import java.util.*;

import static com.project.capstone.exchangesystem.constants.AppStatus.DONATION_CREATE_ACTION;

public class CreateDonationPostActivity extends AppCompatActivity implements ImageOptionDialog.ImageOptionListener {

    private final int GALLERY_REQUEST = 2;
    private final int IMAGE_SIZE = 160;
    private final int IMAGE_MARGIN_TOP_RIGHT = 10;
    private final int ADD_IMAGE_FLAG = 1;
    private final int CHANGE_IMAGE_FLAG = 0;

    TextView txtTitle, btnAdd, txtError;
    List<String> urlList;
    Button btnAddImage;
    ImageView tmpImage;
    EditText edtContent, edtAddress;
    Context context;
    String authorization;
    SharedPreferences sharedPreferences;

    List<Uri> selectedImages;
    int onClickFlag, selectedPosition;

    List<ImageView> imageList;

    GridLayout gridLayout;

    FirebaseImg firebaseImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_donation_post);
        context = this;
        getComponents();

        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);

        imageList = new ArrayList<>();
        urlList = new ArrayList<>();
        //list uri
        selectedImages = new ArrayList<>();

        firebaseImg = new FirebaseImg();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = edtAddress.getText().toString();
                String content = edtContent.getText().toString();
                if (address.trim().length() == 0 || content.trim().length() < 100){
                    notifyError(address.trim().length(), content.trim().length());
                } else {
                    if (firebaseImg.checkLoginFirebase()) {
                        setDonationPostData(address, content);
                    }
                }
            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFlag = ADD_IMAGE_FLAG;
                ImageOptionDialog optionDialog = new ImageOptionDialog();
                optionDialog.show(getSupportFragmentManager(), "optionDialog");
            }
        });
    }

    private void setDonationPostData(String address, String content) {
        DonationPost newPost = new DonationPost();
        newPost.setAddress(address);
        newPost.setContent(content);
        firebaseImg.uploadImagesToFireBase(context, selectedImages, null, newPost, null, authorization, DONATION_CREATE_ACTION, null);
    }

    private void notifyError(int addressLength, int contentLength) {
        if (addressLength == 0){
            edtAddress.setHint("Vui lòng nhập địa chỉ");
            edtAddress.setHintTextColor(Color.RED);
        }
        if (contentLength < 100){
            txtError.setText("Nội dung còn thiếu " + (100 - contentLength) + " ký tự");
            txtError.setVisibility(View.VISIBLE);
        }
    }

    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] imageTypes = {"image/jpeg", "image/png", "image/jpg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, imageTypes);

        if (onClickFlag == ADD_IMAGE_FLAG) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }

        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            if (onClickFlag == ADD_IMAGE_FLAG) {
                if (data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        selectedImages.add(data.getClipData().getItemAt(i).getUri());
                        createImageView();
                    }
                }
            } else if (onClickFlag == CHANGE_IMAGE_FLAG) {
                if (data.getData() != null) {
                    selectedImages.set(selectedPosition, data.getData());
                    try {
                        Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImages.get(selectedPosition));
                        tmpImage.setImageBitmap(bmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void createImageView() {
        gridLayout = (GridLayout) findViewById(R.id.imageGrid);
        final ImageView imageView = new ImageView(this);

        //set image
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImages.get(selectedImages.size() - 1));
            imageView.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFlag = CHANGE_IMAGE_FLAG;
                tmpImage = imageView;
                selectedPosition = imageList.indexOf(imageView);
                ImageOptionDialog optionDialog = new ImageOptionDialog();
                optionDialog.show(getSupportFragmentManager(), "optionDialog");
            }
        });

        imageList.add(imageView);
        gridLayout.addView(imageList.get(imageList.size() - 1));

        ViewGroup.LayoutParams layoutParams = imageList.get(imageList.size() - 1).getLayoutParams();
        layoutParams.height = IMAGE_SIZE;
        layoutParams.width = IMAGE_SIZE;

        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(layoutParams);
        marginLayoutParams.bottomMargin = IMAGE_MARGIN_TOP_RIGHT;
        marginLayoutParams.rightMargin = IMAGE_MARGIN_TOP_RIGHT;
        imageView.setLayoutParams(layoutParams);

        if (imageList.size() == 10) {
            btnAddImage.setEnabled(false);
        }
    }

    private void getComponents() {
        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText("Tạo bài viết mới");
        txtError = findViewById(R.id.txtError);
        edtContent = findViewById(R.id.edtContent);
        edtAddress = findViewById(R.id.edtAddress);
        btnAdd = findViewById(R.id.btnConfirm);
        btnAdd.setText("Đăng bài");
        btnAddImage = findViewById(R.id.btnAddImage);
    }

    @Override
    public void onButtonClicked(int choice) {
        switch (choice) {
            case 0:
                getImageFromGallery();
                break;
            case 1:

                break;
            case 2:
                removeImage();
                break;
            default:
                break;
        }
    }

    private void removeImage() {
        selectedImages.remove(selectedPosition);
        imageList.remove(selectedPosition);
        tmpImage.setVisibility(View.GONE);
    }
}
