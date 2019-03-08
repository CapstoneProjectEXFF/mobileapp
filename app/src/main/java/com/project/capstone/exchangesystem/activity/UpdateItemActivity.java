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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.project.capstone.exchangesystem.fragment.ImageOptionDialog;
import com.project.capstone.exchangesystem.model.FirebaseImg;
import com.project.capstone.exchangesystem.model.PostAction;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.model.Category;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static com.project.capstone.exchangesystem.constants.AppStatus.*;

public class UpdateItemActivity extends AppCompatActivity implements ImageOptionDialog.ImageOptionListener {

    private final String PRIVACY_PUBLIC = "Công khai";
    private final String PRIVACY_FRIENDS = "Bạn bè";
    private final String TITLE = "Cập nhật thông tin";

    TextView txtTitle, btnUpdate, txtError, btnCancel;
    Spinner spCategory;
    RmaAPIService rmaAPIService;
    List<String> categoryList, privacyList, urlList;
    List<Integer> imageIdList, removedImages;
    List<ImageView> imageList;
    Button btnAddImage;
    ImageView tmpImage;
    EditText edtItemName, edtItemDes, edtItemAddress;
    Spinner spPrivacy;
    Context context;
    ArrayAdapter<String> dataPrivacyAdapter, dataCategoryAdapter;
    String authorization;
    int itemId, onClickFlag = -1, selectedPosition;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;

    List<Uri> selectedImages;
    GridLayout gridLayout;
    FirebaseImg firebaseImg;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);
        context = this;
        getComponents();
        setToolbar();

        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);

        imageList = new ArrayList<>();
        urlList = new ArrayList<>();
        imageIdList = new ArrayList<>();
        removedImages = new ArrayList<>();
        //list uri
        selectedImages = new ArrayList<>();
        firebaseImg = new FirebaseImg();
        Intent intent = getIntent();
        itemId = intent.getIntExtra("itemId", 0);

        getAllCategory();
        getAllPrivacy();

//        btnUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String itemName = edtItemName.getText().toString();
//                String itemAddress = edtItemAddress.getText().toString();
//                String itemDes = edtItemDes.getText().toString();
//                if (itemName.trim().length() == 0 || itemAddress.trim().length() == 0 || itemDes.trim().length() < 100) {
//                    notifyError(itemName.trim().length(), itemAddress.trim().length(), itemDes.trim().length());
//                } else {
//                    setItemData(itemName, itemAddress, itemDes);
//                }
//            }
//        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFlag = ADD_IMAGE_FLAG;
                ImageOptionDialog optionDialog = new ImageOptionDialog();
                optionDialog.show(getSupportFragmentManager(), "optionDialog");
            }
        });

//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, OwnInventory.class);
//                startActivity(intent);
//            }
//        });
    }

    private void setToolbar() {
        toolbar.setTitle(TITLE);
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
        String itemName = edtItemName.getText().toString();
        String itemAddress = edtItemAddress.getText().toString();
        String itemDes = edtItemDes.getText().toString();
        if (itemName.trim().length() == 0 || itemAddress.trim().length() == 0 || itemDes.trim().length() == 0) {
            notifyError(itemName.trim().length(), itemAddress.trim().length(), itemDes.trim().length());
        } else {
            setItemData(itemName, itemAddress, itemDes);
        }
        return true;
    }

    private void setItemData(String itemName, String itemAddress, String itemDes) {
        Item item = new Item();
        item.setId(itemId);
        item.setName(itemName);
        item.setAddress(itemAddress);
        item.setDescription(itemDes);
        item.setPrivacy("" + spPrivacy.getSelectedItemPosition());
        item.setCategory(new Category(spCategory.getSelectedItemPosition(), null, -1));
        item.setImageIds(removedImages);

        selectedImages.removeAll(Collections.singleton(null));
        if (selectedImages.size() != 0){
            firebaseImg.uploadImagesToFireBase(context, selectedImages, item, null, null, authorization, ITEM_UPDATE_ACTION, null);
        } else {
            new PostAction().manageItem(item, null, authorization, context, ITEM_UPDATE_ACTION);
        }
    }

    private void notifyError(int nameLength, int addressLength, int desLength) {
        if (nameLength == 0) {
            edtItemName.setHint("Bạn chưa điền tên đồ dùng");
            edtItemName.setHintTextColor(Color.RED);
        }
        if (addressLength == 0) {
            edtItemAddress.setHint("Bạn chưa điền địa chỉ");
            edtItemAddress.setHintTextColor(Color.RED);
        }
        if (desLength == 0) {
            txtError.setText("Bạn chưa thêm mô tả đồ dùng");
            txtError.setVisibility(View.VISIBLE);
        }
    }

    private void getAllPrivacy() {
        privacyList = new ArrayList<>();
        privacyList.add(PRIVACY_PUBLIC);
        privacyList.add(PRIVACY_FRIENDS);
        setDataForSpinner(spPrivacy, privacyList);
    }

    private void setDataForSpinner(Spinner spinner, List<String> dataArray) {
        ArrayAdapter dataAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, dataArray);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(dataAdapter);
    }

    private void getAllCategory() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Đang tải dữ liệu...");
        progressDialog.setMessage("Vui lòng chờ...");
        progressDialog.show();
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
                        setDataForSpinner(spCategory, categoryList);
                        if (categoryList.size() == result.size()){
                            loadItem();
                        }
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
        System.out.println("camera inside " + onClickFlag);
        if (resultCode == RESULT_OK && data != null){
            if (requestCode == GALLERY_REQUEST) {
                if (onClickFlag == ADD_IMAGE_FLAG) {
                    if (data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            selectedImages.add(data.getClipData().getItemAt(i).getUri());
                            createImageView();
                        }
                    } else {
                        selectedImages.add(data.getData());
                        createImageView();
                    }
                } else if (onClickFlag == CHANGE_IMAGE_FLAG) {
                    if (data.getData() != null) {
                        selectedImages.set(selectedPosition, data.getData());
//                        removedImages.add(imageIdList.get(selectedPosition));
//                        imageIdList.remove(selectedPosition);
                        if (selectedPosition < imageIdList.size() && imageIdList.get(selectedPosition) != -1){
                            removedImages.add(imageIdList.get(selectedPosition));
                            imageIdList.set(selectedPosition, -1);
                        }
                        try {
                            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImages.get(selectedPosition));
                            tmpImage.setImageBitmap(bmp);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (requestCode == CAMERA_REQUEST){
                if (onClickFlag == ADD_IMAGE_FLAG) {
                    if (data.getExtras() != null) {
                        Uri uri = getUriFromCaptureImage(data);
                        selectedImages.add(uri);
                        createImageView();
                    }
                } else if (onClickFlag == CHANGE_IMAGE_FLAG) {
                    if (data.getExtras() != null) {
                        Uri uri = getUriFromCaptureImage(data);
                        selectedImages.set(selectedPosition, uri);
//                        removedImages.add(imageIdList.get(selectedPosition));
//                        imageIdList.remove(selectedPosition);
                        if (selectedPosition < imageIdList.size() && imageIdList.get(selectedPosition) != -1){
                            removedImages.add(imageIdList.get(selectedPosition));
                            imageIdList.set(selectedPosition, -1);
                        }
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
    }

    private Uri getUriFromCaptureImage(Intent data) {
        Bitmap captureImg = (Bitmap)data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        captureImg.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), captureImg, "CaptureIMG", null);
        return Uri.parse(path);
    }

    private void loadItem() {
        if (authorization != null) {
            rmaAPIService.getItemById(authorization, itemId).enqueue(new Callback<Item>() {
                @Override
                public void onResponse(Call<Item> call, Response<Item> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            edtItemName.setText(response.body().getName());
                            edtItemDes.setText(response.body().getDescription());
                            edtItemAddress.setText(response.body().getAddress());
                            spPrivacy.setSelection(Integer.parseInt(response.body().getPrivacy()));
                            spCategory.setSelection((response.body().getCategory().getId() - 1));
                            for (int i = 0; i < response.body().getImages().size(); i++) {
                                urlList.add(response.body().getImages().get(i).getUrl());
                                selectedImages.add(null);
                                imageIdList.add(response.body().getImages().get(i).getId());
                                createImageView();
                            }
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "" + response.code(), Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Failed 1", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Item> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Cannot load", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void getComponents() {
//        txtTitle = findViewById(R.id.txtTitle);
//        txtTitle.setText("Cập nhật thông tin");
        txtError = findViewById(R.id.txtError);
//        btnUpdate = findViewById(R.id.btnConfirm);
//        btnUpdate.setText("Lưu");
        btnAddImage = findViewById(R.id.btnAddImage);
        edtItemName = findViewById(R.id.edtItemName);
        edtItemDes = findViewById(R.id.edtItemDes);
        edtItemAddress = findViewById(R.id.edtItemAddress);
        spPrivacy = findViewById(R.id.spPrivacy);
        spPrivacy.setPopupBackgroundResource(R.color.white);
        rmaAPIService = RmaAPIUtils.getAPIService();
        spCategory = findViewById(R.id.spCategory);
        spCategory.setPopupBackgroundResource(R.color.white);
        toolbar = findViewById(R.id.tbToolbar);
//        btnCancel = findViewById(R.id.btnCancel);
    }

    private void createImageView() {
        gridLayout = (GridLayout) findViewById(R.id.imageGrid);
        final ImageView imageView = new ImageView(this);

        //set image
        if (onClickFlag == -1) {
            Picasso.with(getApplicationContext()).load(urlList.get(urlList.size() - 1))
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(imageView);
        } else {
            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImages.get(selectedImages.size() - 1));
                imageView.setImageBitmap(bmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST);
            } else {
                setCameraPermission();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                System.out.println("camera after " + onClickFlag);
                startActivityForResult(intent, CAMERA_REQUEST);
                System.out.println("camera after " + onClickFlag);
            }
        } else {
            setCameraPermission();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            System.out.println("camera before 1: " + onClickFlag);
            startActivityForResult(intent, CAMERA_REQUEST);
            System.out.println("camera after 1: " + onClickFlag);
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
        if (requestCode == CAMERA_REQUEST){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        }
        if(requestCode == EXTERNAL_STORAGE_REQUEST){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setCameraPermission();
            }
        }
    }

    private void removeImage() {
        selectedImages.remove(selectedPosition);
        imageList.remove(selectedPosition);
        if (selectedPosition < imageIdList.size() && imageIdList.get(selectedPosition) != -1){
            removedImages.add(imageIdList.get(selectedPosition));
            imageIdList.set(selectedPosition, -1);
        }
        gridLayout.removeView(tmpImage);
    }
}
