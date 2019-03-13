package com.project.capstone.exchangesystem.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.project.capstone.exchangesystem.model.Category;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class CreateItemActivity extends AppCompatActivity {

    private final int GALLERY_REQUEST = 2;
    private final int IMAGE_SIZE = 160;
    private final int IMAGE_MARGIN_TOP_RIGHT = 10;
    private final int ADD_IMAGE_FLAG = 1;
    private final int CHANGE_IMAGE_FLAG = 0;
    private final String PRIVACY_PUBLIC = "Công khai";
    private final String PRIVACY_FRIENDS = "Bạn bè";
    TextView lblToolbar;
    Spinner spCategory;
    RmaAPIService rmaAPIService;
    List<String> categoryList, privacyList, urlList;
    Button btnAdd, btnAddImage;
    ImageView tmpImage;
    EditText edtItemName, edtItemDes, edtItemAddress;
    Spinner spPrivacy;
    Context context;
    ArrayAdapter<String> dataAdapter;
    String imagePath = "aaa", authorization;
    SharedPreferences sharedPreferences;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseAuth fbAuth;
    FirebaseUser fbUser;
    List<Uri> selectedImages;
    int onClickFlag, selectedPosition;

    List<ImageView> imageList;

    GridLayout gridLayout;
    ProgressDialog progressDialog;

    //upload
    Button btnUpload;

    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        context = this;
        getComponents();
        getAllCategory();
        getAllPrivacy();

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
//                checkLoginFirebase();
                createItem();
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
            final int imageCount = i;

            if (selectedImages.get(i) != null) {

                StorageReference reference = storageReference.child("images/" + UUID.randomUUID().toString());
                reference.putFile(selectedImages.get(i)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateItemActivity.this, "Tải thành công", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CreateItemActivity.this, "Không thể đăng được hình " + (imageCount + 1) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.setMessage("Đã tải được " + imageCount + "/" + selectedImages.size() + " hình");
                    }
                });
            }
        }
        progressDialog.dismiss();
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
                    for (int i = 0; i < data.getClipData().getItemCount(); i++){
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

    private void createItem() {
        String itemName = edtItemName.getText().toString();
        String itemDes = edtItemDes.getText().toString();
        String itemAddress = edtItemAddress.getText().toString();
        int privacy = spPrivacy.getSelectedItemPosition();
        int category = spCategory.getSelectedItemPosition();
        final Map<String, Object> jsonBody = new HashMap<String, Object>();

        jsonBody.put("name", itemName);
        jsonBody.put("description", itemDes);
        jsonBody.put("address", itemAddress);
        jsonBody.put("privacy", "" + privacy);
        jsonBody.put("category", "" + (category + 1));
        jsonBody.put("urls", urlList);

        if (authorization != null) {
            rmaAPIService.createItem(jsonBody, authorization).enqueue(new Callback<Item>() {

                @Override
                public void onResponse(Call<Item> call, Response<Item> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_LONG).show();

                            //go to update screen
                            Intent intent = new Intent(context, UpdateItemActivity.class);
                            intent.putExtra("itemId", response.body().getId());
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "" + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Item> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void getAllCategory() {
        Toast.makeText(getApplicationContext(), "getAllCategory", Toast.LENGTH_LONG).show();
        rmaAPIService.getAllCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                Toast.makeText(getApplicationContext(), "Test category!!!!!", Toast.LENGTH_LONG).show();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Category> result = response.body();
                        categoryList = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++) {
                            categoryList.add(result.get(i).getName());
                            System.out.println("Cate 1: " + categoryList.get(i));
                        }
                        Toast.makeText(getApplicationContext(), "list size " + categoryList.size(), Toast.LENGTH_LONG).show();
                        setDataForSpinner(spCategory, categoryList);
                    } else {
                        System.out.println("httpstatus " + response.code());
                        Toast.makeText(getApplicationContext(), "body null", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed category", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                System.out.println("Fail");
                Toast.makeText(getApplicationContext(), "AAAAAAAAAAA", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void getComponents() {
        lblToolbar = findViewById(R.id.lbl_toolbar);
        lblToolbar.setText("Thêm món đồ mới");
        btnAdd = findViewById(R.id.btnAdd);
        btnAddImage = findViewById(R.id.btnAddImage);
        edtItemName = findViewById(R.id.edtItemName);
        edtItemDes = findViewById(R.id.edtItemDes);
        edtItemAddress = findViewById(R.id.edtItemAddress);
        spPrivacy = findViewById(R.id.spPrivacy);
        spPrivacy.setPopupBackgroundResource(R.color.white);
        rmaAPIService = RmaAPIUtils.getAPIService();
        spCategory = findViewById(R.id.spCategory);
        spCategory.setPopupBackgroundResource(R.color.white);
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

    private void getAllPrivacy() {
        privacyList = new ArrayList<>();
        privacyList.add(PRIVACY_PUBLIC);
        privacyList.add(PRIVACY_FRIENDS);
        setDataForSpinner(spPrivacy, privacyList);
    }

    private void setDataForSpinner(Spinner spinner, List<String> dataArray) {
        dataAdapter = new ArrayAdapter<>(context, R.layout.spinner_category_item, dataArray);
        dataAdapter.setDropDownViewResource(R.layout.spinner_category_item);
        spinner.setAdapter(dataAdapter);
    }

    private boolean checkLoginFirebase() {
        final boolean[] result = {true};
        if (fbUser == null) {
            fbAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(CreateItemActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    result[0] = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateItemActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    result[0] = false;
                }
            });
        }
        return result[0];
    }

    public void onBackButton(View view) {
        finish();
    }
}
