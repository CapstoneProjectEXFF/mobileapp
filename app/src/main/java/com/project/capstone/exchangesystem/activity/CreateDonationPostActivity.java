package com.project.capstone.exchangesystem.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.project.capstone.exchangesystem.adapter.ImageAdapter;
import com.project.capstone.exchangesystem.fragment.ImageOptionDialog;
import com.project.capstone.exchangesystem.model.FirebaseImg;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.model.Image;
//import com.project.capstone.exchangesystem.model.ImageHandler;
import com.project.capstone.exchangesystem.model.PostAction;

import java.io.ByteArrayOutputStream;
import java.util.*;

import static com.project.capstone.exchangesystem.constants.AppStatus.*;

public class CreateDonationPostActivity extends AppCompatActivity implements ImageOptionDialog.ImageOptionListener {

    TextView txtError;
    LinearLayout btnAddImage;
    EditText edtContent, edtAddress, edtTitle;
    Context context;
    String authorization;
    SharedPreferences sharedPreferences;
    int onClickFlag;
    FirebaseImg firebaseImg;

    Toolbar toolbar;
    ImageAdapter imageAdapter;
    ArrayList<Image> imageList, tmpImageList;
    Image tmpImage;
    RecyclerView rvSelectedImages;
    List<Uri> listUri;

//    ImageHandler imageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_donation_post);
        context = this;
        getComponents();
        setToolbar();
        setImageAdapter();

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFlag = ADD_IMAGE_FLAG;
                ImageOptionDialog optionDialog = new ImageOptionDialog();
                optionDialog.setActivityFlag(ADD_IMAGE_FLAG);
                optionDialog.show(getSupportFragmentManager(), "optionDialog");
            }
        });
    }

    private void setImageAdapter() {
        imageAdapter = new ImageAdapter(getApplicationContext(), imageList, new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Image image) {
                tmpImage = image;
                onClickFlag = CHANGE_IMAGE_FLAG;
                ImageOptionDialog optionDialog = new ImageOptionDialog();
                optionDialog.setActivityFlag(CHANGE_IMAGE_FLAG);
                optionDialog.show(getSupportFragmentManager(), "optionDialog");
            }
        });
        rvSelectedImages.setHasFixedSize(true);
        rvSelectedImages.setLayoutManager(new GridLayoutManager(this, 4));
        rvSelectedImages.setAdapter(imageAdapter);
    }

    private void setToolbar() {
        toolbar.setTitle(R.string.title_create_donationPost);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String address = edtAddress.getText().toString();
        String content = edtContent.getText().toString();
        String title = edtTitle.getText().toString();
        if (address.trim().length() == 0 || content.trim().length() == 0 || title.trim().length() == 0){
            notifyError(address.trim().length(), content.trim().length(), title.trim().length());
        } else {
            if (firebaseImg.checkLoginFirebase()) {
                setDonationPostData(address, content, title);
            }
        }
        return true;
    }

    private void setDonationPostData(String address, String content, String title) {
        DonationPost newPost = new DonationPost();
        newPost.setAddress(address);
        newPost.setContent(content);
        newPost.setTitle(title);
        if (imageList.size() != 0){
            listUri = new ArrayList<>();
            for (int i = 0; i < imageList.size(); i++){
                listUri.add(imageList.get(i).getUri());
            }
            firebaseImg.uploadImagesToFireBase(context, listUri, null, newPost, null, authorization, DONATION_CREATE_ACTION, null);
        } else {
            List<String> listUrl = new ArrayList<>();
            new PostAction().manageDonation(newPost, listUrl, authorization, context, DONATION_CREATE_ACTION);
        }
    }

    private void notifyError(int addressLength, int contentLength, int titleLength) {
        if (addressLength == 0){
            edtAddress.setHint(R.string.error_input_address);
            edtAddress.setHintTextColor(Color.RED);
        }
        if (contentLength == 0){
            txtError.setText(R.string.error_input_content);
            txtError.setVisibility(View.VISIBLE);
        }
        if (titleLength == 0){
            edtTitle.setText(R.string.error_input_title);
            edtTitle.setHintTextColor(Color.RED);
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

        if (resultCode == RESULT_OK && data != null){
            if (requestCode == GALLERY_REQUEST) {
                if (onClickFlag == ADD_IMAGE_FLAG) {
                    if (data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            Image newImage = new Image();
                            newImage.setUri(data.getClipData().getItemAt(i).getUri());
                            tmpImageList.add(newImage);
                        }
                        imageAdapter.setfilter(tmpImageList);
                    } else {
                        Image newImage = new Image();
                        newImage.setUri(data.getData());
                        tmpImageList.add(newImage);
                        imageAdapter.setfilter(tmpImageList);
                    }
                } else if (onClickFlag == CHANGE_IMAGE_FLAG) {
                    if (data.getData() != null) {
                        Image newImage = new Image();
                        newImage.setUri(data.getData());
                        tmpImageList.set(tmpImageList.indexOf(tmpImage), newImage);
                        imageAdapter.setfilter(tmpImageList);
                    }
                }
            } else if (requestCode == CAMERA_REQUEST) {
                if (onClickFlag == ADD_IMAGE_FLAG) {
                    if (data.getExtras() != null) {
                        Image newImage = new Image();
                        newImage.setUri(getUriFromCaptureImage(data));
                        tmpImageList.add(newImage);
                        imageAdapter.setfilter(tmpImageList);
                    }
                } else if (onClickFlag == CHANGE_IMAGE_FLAG) {
                    if (data.getExtras() != null) {
                        Image newImage = new Image();
                        newImage.setUri(getUriFromCaptureImage(data));
                        tmpImageList.set(tmpImageList.indexOf(tmpImage), newImage);
                        imageAdapter.setfilter(tmpImageList);
                    }
                }
            }
            rvSelectedImages.setVisibility(View.VISIBLE);
        }
    }

    private Uri getUriFromCaptureImage(Intent data) {
        Bitmap captureImg = (Bitmap)data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        captureImg.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), captureImg, "CaptureIMG", null);
        return Uri.parse(path);
    }

    private void getComponents() {
        txtError = findViewById(R.id.txtError);
        edtContent = findViewById(R.id.edtContent);
        edtAddress = findViewById(R.id.edtAddress);
        btnAddImage = findViewById(R.id.btnAddImage);
        toolbar = findViewById(R.id.tbToolbar);
        rvSelectedImages = findViewById(R.id.rvSelectedImages);
        imageList = new ArrayList<>();
        tmpImageList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        firebaseImg = new FirebaseImg();
        edtTitle = findViewById(R.id.edtTitle);
        //        imageHandler = new ImageHandler();
    }

    @Override
    public void onButtonClicked(int choice) {
        switch (choice) {
            case 0:
                getImageFromGallery();
//                imageHandler.getImageFromGallery(onClickFlag, tmpImageList, imageAdapter, tmpImage);
//                tmpImageList = imageAdapter.getfilter();
//                if (tmpImageList.size() > 0){
//                    rvSelectedImages.setVisibility(View.VISIBLE);
//                }
                break;
            case 1:
                takePhoto();
//                imageHandler.takePhoto(getApplicationContext());
//                tmpImageList = imageAdapter.getfilter();
//                if (tmpImageList.size() > 0){
//                    rvSelectedImages.setVisibility(View.VISIBLE);
//                }
                break;
            case 2:
                removeImage();
                break;
            default:
                break;
        }
    }

    private void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST);
            } else {
                setCameraPermission();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        } else {
            setCameraPermission();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST);
        }
    }

    private void setCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        }
        if (requestCode == EXTERNAL_STORAGE_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setCameraPermission();
            }
        }
    }

    private void removeImage() {
        tmpImageList.remove(tmpImage);
        imageAdapter.setfilter(tmpImageList);
        if (imageList.size() == 0){
            rvSelectedImages.setVisibility(View.GONE);
        }
    }
}