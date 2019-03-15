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
import com.project.capstone.exchangesystem.model.PostAction;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.Utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.model.Category;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;

import static com.project.capstone.exchangesystem.constants.AppStatus.ITEM_UPDATE_ACTION;

public class UpdateItemActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 2;
    private final String PRIVACY_PUBLIC = "Công khai";
    private final String PRIVACY_FRIENDS = "Bạn bè";
    private final int IMAGE_SIZE = 160;
    private final int IMAGE_MARGIN_TOP_RIGHT = 10;
    private final int ADD_IMAGE_FLAG = 1;
    private final int CHANGE_IMAGE_FLAG = 0;
    TextView txtTitle, btnUpdate, txtError;
    Spinner spCategory;
    RmaAPIService rmaAPIService;
    List<String> categoryList, privacyList, urlList;
    List<ImageView> imageList;
    Button btnAddImage;
    ImageView tmpImage;
    EditText edtItemName, edtItemDes, edtItemAddress;
    Spinner spPrivacy;
    Context context;
    ArrayAdapter<String> dataAdapter;
    String authorization;
    int itemId, onClickFlag = -1, selectedPosition;
    SharedPreferences sharedPreferences;

    List<Uri> selectedImages;
    GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);
        context = this;
        getComponents();
        getAllCategory();
        getAllPrivacy();

        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);

        imageList = new ArrayList<>();
        urlList = new ArrayList<>();
        //list uri
        selectedImages = new ArrayList<>();

        Intent intent = getIntent();
        itemId = intent.getIntExtra("itemId", 0);
        loadItem();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = edtItemName.getText().toString();
                String itemAddress = edtItemAddress.getText().toString();
                String itemDes = edtItemDes.getText().toString();
                if (itemName.trim().length() == 0 || itemAddress.trim().length() == 0 || itemDes.trim().length() < 100){
                    notifyError(itemName.trim().length(), itemAddress.trim().length(), itemDes.trim().length());
                } else {
                        setItemData(itemName, itemAddress, itemDes);
                }
            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFlag = ADD_IMAGE_FLAG;
                getImageFromGallery();
            }
        });
    }

    private void setItemData(String itemName, String itemAddress, String itemDes) {
        Item item = new Item();
        item.setId(itemId);
        item.setName(itemName);
        item.setAddress(itemAddress);
        item.setDescription(itemDes);
        item.setPrivacy("" + spCategory.getSelectedItemPosition());
        item.setCategory(new Category(spCategory.getSelectedItemPosition(), null, -1));
        new PostAction().manageItem(item, null, authorization, context, ITEM_UPDATE_ACTION);
    }

    private void notifyError(int nameLength, int addressLength, int desLength) {
        if (nameLength == 0){
            edtItemName.setHint("Bạn chưa điền tên đồ dùng");
            edtItemName.setHintTextColor(Color.RED);
        }
        if (addressLength == 0){
            edtItemAddress.setHint("Bạn chưa điền địa chỉ");
            edtItemAddress.setHintTextColor(Color.RED);
        }
        if (desLength < 100){
            txtError.setText("Mô tả còn thiếu " + (100 - desLength) + " ký tự");
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
        dataAdapter = new ArrayAdapter<>(context, R.layout.spinner_category_item, dataArray);
        dataAdapter.setDropDownViewResource(R.layout.spinner_category_item);
        spinner.setAdapter(dataAdapter);
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
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, R.layout.spinner_category_item, categoryList);
                        dataAdapter.setDropDownViewResource(R.layout.spinner_category_item);
                        spCategory.setAdapter(dataAdapter);
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
                                createImageView();
                            }
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
        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText("Cập nhật thông tin");
        txtError = findViewById(R.id.txtError);
        btnUpdate = findViewById(R.id.btnConfirm);
        btnUpdate.setText("Lưu");
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
        if (onClickFlag == -1) {
            Picasso.with(getApplicationContext()).load(urlList.get(urlList.size() - 1))
                    .placeholder(R.drawable.no)
                    .error(R.drawable.loadingimage)
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
}
