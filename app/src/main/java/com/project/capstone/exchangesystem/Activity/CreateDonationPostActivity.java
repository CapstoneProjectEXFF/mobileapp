package com.project.capstone.exchangesystem.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.Utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;

public class CreateDonationPostActivity extends AppCompatActivity {

    private final int GALLERY_REQUEST = 2;
    private final int IMAGE_SIZE = 160;
    private final int IMAGE_MARGIN_TOP_RIGHT = 10;
    private final int ADD_IMAGE_FLAG = 1;
    private final int CHANGE_IMAGE_FLAG = 0;
    TextView lblToolbar;
    RmaAPIService rmaAPIService;
    List<String> urlList;
    Button btnAdd, btnAddImage;
    ImageView tmpImage;
    EditText edtContent;
    Context context;
    String imagePath = "aaa", authorization;
    SharedPreferences sharedPreferences;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseAuth fbAuth;
    FirebaseUser fbUser;
    List<Uri> selectedImages;
    int onClickFlag, selectedPosition, imageCount = 0;

    List<ImageView> imageList;

    GridLayout gridLayout;
    ProgressDialog progressDialog;

    //upload
    Button btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_donation_post);
        context = this;
        getComponents();

        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();

        imageList = new ArrayList<>();
        urlList = new ArrayList<>();
        //list uri
        selectedImages = new ArrayList<>();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost();
            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFlag = ADD_IMAGE_FLAG;
                getImageFromGallery();
            }
        });

        //upload
        btnUpload = findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLoginFirebase()){
                    uploadImagesToFireBase();
                }
            }
        });
    }

    private void uploadImagesToFireBase() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Đang tải hình ảnh...");
        progressDialog.show();

        for (int i = 0; i < selectedImages.size(); i++) {

            if (selectedImages.get(i) != null) {
                StorageReference reference = storageReference.child("images/" + UUID.randomUUID().toString());
                reference.putFile(selectedImages.get(i)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateDonationPostActivity.this, "Tải thành công", Toast.LENGTH_SHORT).show();
                            task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imagePath = uri.toString(); //get url
                                    urlList.add(imagePath);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CreateDonationPostActivity.this, "Không thể đăng được hình " + (urlList.size() + 1) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.setMessage("Đã tải được " + urlList.size() + "/" + selectedImages.size() + " hình");
                    }
                });
            }
        }
        progressDialog.dismiss();
    }

    private boolean checkLoginFirebase() {
        final boolean[] result = {true};
        if (fbUser == null) {
            fbAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(CreateDonationPostActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    result[0] = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateDonationPostActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    result[0] = false;
                }
            });
        }
        return result[0];
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
                getImageFromGallery();
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

    private void createPost() {
        String content = edtContent.getText().toString();
        final Map<String, Object> jsonBody = new HashMap<String, Object>();

        jsonBody.put("content", content);
        jsonBody.put("urls", urlList);

        if (authorization != null) {
            rmaAPIService.createDonationPost(jsonBody, authorization).enqueue(new Callback<DonationPost>() {

                @Override
                public void onResponse(Call<DonationPost> call, Response<DonationPost> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_LONG).show();

                            //go to update screen
                            Intent intent = new Intent(context, UpdateDonationPostActivity.class);
                            intent.putExtra("donationPostId", response.body().getId());
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "" + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<DonationPost> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void getComponents() {
        lblToolbar = findViewById(R.id.lbl_toolbar);
        lblToolbar.setText("Tạo bài viết mới");
        edtContent = findViewById(R.id.edtContent);
        btnAdd = findViewById(R.id.btnAdd);
        btnAddImage = findViewById(R.id.btnAddImage);
        rmaAPIService = RmaAPIUtils.getAPIService();
    }
}
