package com.project.capstone.exchangesystem.activity;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.project.capstone.exchangesystem.adapter.ImageAdapter;
import com.project.capstone.exchangesystem.fragment.ImageOptionDialog;
import com.project.capstone.exchangesystem.model.Category;
import com.project.capstone.exchangesystem.model.DonationPostTarget;
import com.project.capstone.exchangesystem.model.DonationPostWrapper;
import com.project.capstone.exchangesystem.model.FirebaseImg;
import com.project.capstone.exchangesystem.model.Image;
import com.project.capstone.exchangesystem.model.PostAction;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static com.project.capstone.exchangesystem.constants.AppStatus.*;

public class UpdateDonationPostActivity extends AppCompatActivity implements ImageOptionDialog.ImageOptionListener {

    TextView txtError, txtCategory;
    RmaAPIService rmaAPIService;
    List<Integer> removedImageIds;
    LinearLayout btnAddImage;
    EditText edtContent, edtAddress, edtTitle;
    Context context;
    String authorization, donationPostTitle;
    int donationPostId, onClickFlag = -1;
    SharedPreferences sharedPreferences;
    FirebaseImg firebaseImg;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    ImageAdapter imageAdapter;
    ArrayList<Image> imageList, tmpImageList;
    Image tmpImage;
    RecyclerView rvSelectedImages;
    DonationPost donationPost;
    ArrayList<Category> selectedCategoryList;
    List<DonationPostTarget> targets;
    ArrayList<DonationPostTarget> donationPostTargetList;
    DonationPostWrapper donationPostWrapper;
    ArrayList<Integer> removedTargetIds;
    boolean checkSelectedCategory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_donation_post);
        context = this;
        loadProgressDialog();
        getComponents();
        setToolbar();
        setImageAdapter();
        loadDonationPost();
        getCategoryData();

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFlag = ADD_IMAGE_FLAG;
                ImageOptionDialog optionDialog = new ImageOptionDialog();
                optionDialog.setActivityFlag(ADD_IMAGE_FLAG);
                optionDialog.show(getSupportFragmentManager(), "optionDialog");
            }
        });

        txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChooseCategoryActivity.class);
                intent.putExtra("selectedCategory", selectedCategoryList);
                startActivityForResult(intent, CATEGORY_REQUEST);
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
        toolbar.setTitle(R.string.title_edit_donationPost);
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
        setDonationPostTargetList();

        if (!checkSelectedCategory){
            Toast.makeText(context, getString(R.string.error_category), Toast.LENGTH_SHORT).show();
        } else if (address.trim().length() == 0 || content.trim().length() == 0) {
            notifyError(address.trim().length(), content.trim().length());
        } else {
            if (firebaseImg.checkLoginFirebase()) {
                setDonationPostData(address, content);
            }
        }
        return true;
    }

    private void setDonationPostData(String address, String content) {

        DonationPost tmpDonationPost = new DonationPost();
        tmpDonationPost.setId(donationPost.getId());
        tmpDonationPost.setTitle(donationPostTitle);
        tmpDonationPost.setContent(content);
        tmpDonationPost.setAddress(address);

        donationPostWrapper.setDonationPost(tmpDonationPost);
        donationPostWrapper.setRemovedUrlIds(removedImageIds);
        donationPostWrapper.setTargets(donationPostTargetList);
        donationPostWrapper.setRemoveTargets(removedTargetIds);

        List<Uri> listUri = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            if (imageList.get(i).getUri() != null) {
                listUri.add(imageList.get(i).getUri());
            }
        }
        if (listUri.size() != 0) {
            firebaseImg.uploadImagesToFireBase(context, listUri, null, donationPostWrapper, null, authorization, DONATION_UPDATE_ACTION, null);
        } else {
            List<String> listUrl = new ArrayList<>();
            new PostAction().manageDonation(donationPostWrapper, listUrl, authorization, context, DONATION_UPDATE_ACTION);
        }
    }

    private void setDonationPostTargetList() {
        for (int i = 0; i < selectedCategoryList.size(); i++) {
            Category tmpCategory = selectedCategoryList.get(i);
            if (tmpCategory.isCheckSelectedCategory()) {
                checkSelectedCategory = true;
                DonationPostTarget donationPostTarget = new DonationPostTarget();
                donationPostTarget.setCategoryId(tmpCategory.getId());
                donationPostTarget.setTarget(tmpCategory.getNumOfItem());

                for (int j = 0; j < targets.size(); j++) {
                    if (tmpCategory.getId() == targets.get(j).getCategoryId()) {
                        donationPostTarget.setDonationPostId(donationPost.getId());
                        donationPostTarget.setId(targets.get(j).getId());
                    }
                }
                donationPostTargetList.add(donationPostTarget);
            }
        }
    }

    private void notifyError(int addressLength, int contentLength) {
        if (addressLength == 0) {
            edtAddress.setHint(R.string.error_input_address);
            edtAddress.setHintTextColor(Color.RED);
        }
        if (contentLength == 0) {
            txtError.setText(R.string.error_input_content);
            txtError.setVisibility(View.VISIBLE);
        }
    }

    private void loadDonationPost() {
        edtTitle.setText(donationPost.getTitle());
        donationPostTitle = donationPost.getTitle();
        edtTitle.setEnabled(false);
        edtContent.setText(donationPost.getContent());
        edtAddress.setText(donationPost.getAddress());
        for (int i = 0; i < donationPost.getImages().size(); i++) {
            Image newImage = new Image();
            newImage.setUrl(donationPost.getImages().get(i).getUrl());
            newImage.setId(donationPost.getImages().get(i).getId());
            tmpImageList.add(newImage);
        }
        imageAdapter.setfilter(tmpImageList);
        rvSelectedImages.setVisibility(View.VISIBLE);
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

        if (requestCode == CATEGORY_REQUEST) {
            //selected items but not add to recycleview yet
            if (data != null) {
                Bundle bundle = data.getExtras();
                selectedCategoryList = (ArrayList<Category>) bundle.getSerializable("LISTCHOOSE");
                for (int i = 0; i < selectedCategoryList.size(); i++) {
                    Category tmpCategory = selectedCategoryList.get(i);
                    for (int j = 0; j < targets.size(); j++) {
                        if (tmpCategory.getId() == targets.get(j).getCategoryId() && !tmpCategory.isCheckSelectedCategory()) {
                            removedTargetIds.add(tmpCategory.getId());
                        }
                    }
                }
            }
        } else if (resultCode == RESULT_OK && data != null) {
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
                        if (tmpImage.getUri() == null) {
                            removedImageIds.add(tmpImage.getId());
                            firebaseImg.deleteImageOnFirebase(tmpImage.getUrl());
                        }
                        int position = tmpImageList.indexOf(tmpImage);
                        tmpImageList.set(position, newImage);
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
                        if (tmpImage.getUri() == null) {
                            removedImageIds.add(tmpImage.getId());
                            firebaseImg.deleteImageOnFirebase(tmpImage.getUrl());
                        }
                        int position = tmpImageList.indexOf(tmpImage);
                        tmpImageList.set(position, newImage);
                        imageAdapter.setfilter(tmpImageList);
                    }
                }
            }
            rvSelectedImages.setVisibility(View.VISIBLE);
        }
    }

    private Uri getUriFromCaptureImage(Intent data) {
        Bitmap captureImg = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        captureImg.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), captureImg, "CaptureIMG", null);
        return Uri.parse(path);
    }

    private void getComponents() {
        txtError = findViewById(R.id.txtError);
        edtContent = findViewById(R.id.edtContent);
        edtAddress = findViewById(R.id.edtAddress);
        rmaAPIService = RmaAPIUtils.getAPIService();
        toolbar = findViewById(R.id.tbToolbar);
        toolbar = findViewById(R.id.tbToolbar);
        rvSelectedImages = findViewById(R.id.rvSelectedImages);
        imageList = new ArrayList<>();
        tmpImageList = new ArrayList<>();
        removedImageIds = new ArrayList<>();
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        firebaseImg = new FirebaseImg();
        edtTitle = findViewById(R.id.edtTitle);
        btnAddImage = findViewById(R.id.btnAddImage);

        donationPost = (DonationPost) getIntent().getSerializableExtra("donationPost");
        selectedCategoryList = new ArrayList<>();
        targets = donationPost.getDonationPostTargets();
        txtCategory = findViewById(R.id.txtCategory);
        donationPostTargetList = new ArrayList<>();
        donationPostWrapper = new DonationPostWrapper();
        removedTargetIds = new ArrayList<>();
    }

    @Override
    public void onButtonClicked(int choice) {
        switch (choice) {
            case 0:
                getImageFromGallery();
                break;
            case 1:
                takePhoto();
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
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
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
        removedImageIds.add(tmpImage.getId());
        firebaseImg.deleteImageOnFirebase(tmpImage.getUrl());
        tmpImageList.remove(tmpImage);
        imageAdapter.setfilter(tmpImageList);
        if (imageList.size() == 0) {
            rvSelectedImages.setVisibility(View.GONE);
        }
    }

    private void getCategoryData() {
        rmaAPIService.getAllCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Category> tmpCategoryList;
                        tmpCategoryList = response.body();
                        for (int i = 0; i < tmpCategoryList.size(); i++) {
                            Category tmpCategory = tmpCategoryList.get(i);
                            for (int j = 0; j < targets.size(); j++) {
                                if (tmpCategory.getId() == targets.get(j).getCategoryId()) {
                                    tmpCategory.setCheckSelectedCategory(true);
                                    tmpCategory.setNumOfItem(targets.get(j).getTarget());
                                }
                            }
                            selectedCategoryList.add(tmpCategory);
                        }
                        progressDialog.dismiss();
                    } else {
                        Log.i("loadCategory", "category null");
                    }
                } else {
                    Log.i("loadCategory", "response unsucceed");
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.i("loadCategory", "failed on calling API");
            }
        });
    }

    private void loadProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(R.string.data_loading_noti);
        progressDialog.setMessage(String.valueOf(R.string.waiting_noti));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
}
