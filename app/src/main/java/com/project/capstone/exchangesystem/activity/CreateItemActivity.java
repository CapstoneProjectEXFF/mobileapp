package com.project.capstone.exchangesystem.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ImageAdapter;
import com.project.capstone.exchangesystem.fragment.ImageOptionDialog;
import com.project.capstone.exchangesystem.model.Category;
import com.project.capstone.exchangesystem.model.FirebaseImg;
import com.project.capstone.exchangesystem.model.Image;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.project.capstone.exchangesystem.constants.AppStatus.*;

public class CreateItemActivity extends AppCompatActivity implements ImageOptionDialog.ImageOptionListener {

    TextView txtError, txtCategory;
    LinearLayout btnAddImage;
    EditText edtItemName, edtItemDes, edtPreferItems;
    Context context;
    String authorization;
    SharedPreferences sharedPreferences;
    int onClickFlag;
    FirebaseImg firebaseImg;
    RmaAPIService rmaAPIService;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    ImageAdapter imageAdapter;
    ArrayList<Image> imageList, tmpImageList;
    Image tmpImage;
    RecyclerView rvSelectedImages;
    ScrollView scrollView;
    RadioGroup spPrivacy;
    InputMethodManager inputMethodManager;
    ArrayList<Category> selectedCategoryList;
    Category selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        context = this;

        loadProgressDialog();
        getComponents();
        setToolbar();
        setImageAdapter();
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

        spPrivacy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                hideKeyboard(group);
            }
        });

        txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChooseCategoryItemActivity.class);
                intent.putExtra("selectedCategory", selectedCategoryList);
                intent.putExtra("selectedCategoryPos", selectedCategory);
                startActivityForResult(intent, CATEGORY_REQUEST);
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

    private void getCategoryData() {
        rmaAPIService.getAllCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Category> tmpCategoryList;
                        tmpCategoryList = response.body();
                        for (int i = 0; i < tmpCategoryList.size(); i++) {
                            selectedCategoryList.add(tmpCategoryList.get(i));
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
        rvSelectedImages.setLayoutManager(new GridLayoutManager(this, 5));
        rvSelectedImages.setAdapter(imageAdapter);
    }

    private void setToolbar() {
        toolbar.setTitle(R.string.title_create_item);
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
        String itemDes = edtItemDes.getText().toString();

        if (itemName.trim().length() == 0 || itemDes.trim().length() == 0 || imageList.size() == 0 || selectedCategory == null) {
            notifyError(itemName.trim().length(), itemDes.trim().length());
        } else {
            if (firebaseImg.checkLoginFirebase()) {
                setItemData(itemName, itemDes);
            }
        }
        return true;
    }

    private void setItemData(String itemName, String itemDes) {
        Item item = new Item();
        item.setName(itemName);
        item.setDescription(itemDes);

        View checkedPrivacyButton = spPrivacy.findViewById(spPrivacy.getCheckedRadioButtonId());
        item.setPrivacy("" + spPrivacy.indexOfChild(checkedPrivacyButton));
        item.setCategory(selectedCategory);
        item.setPreferItems(edtPreferItems.getText().toString());
        List<Uri> listUri = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            listUri.add(imageList.get(i).getUri());
        }
        firebaseImg.uploadImagesToFireBase(context, listUri, item, null, null, authorization, ITEM_CREATE_ACTION, null);
    }

    private void notifyError(int nameLength, int desLength) {
        if (nameLength == 0) {
            edtItemName.setHint(R.string.error_input_itemName);
            edtItemName.setHintTextColor(Color.RED);
        }
        if (desLength == 0) {
            txtError.setText(R.string.error_input_itemDesciption);
            txtError.setVisibility(View.VISIBLE);
        }
        if (imageList.size() == 0) {
            Toast.makeText(getApplicationContext(), R.string.error_input_image, Toast.LENGTH_LONG).show();
        }
        if (selectedCategory == null){
            Toast.makeText(getApplicationContext(), R.string.error_category, Toast.LENGTH_LONG).show();
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

        if (requestCode == CATEGORY_REQUEST) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                selectedCategoryList = (ArrayList<Category>) bundle.getSerializable("LISTCHOOSE");
                selectedCategory = (Category) bundle.getSerializable("selectedCategory");
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
        Bitmap captureImg = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        captureImg.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), captureImg, "CaptureIMG", null);
        return Uri.parse(path);
    }

    private void getComponents() {
        txtError = findViewById(R.id.txtError);
        btnAddImage = findViewById(R.id.btnAddImage);
        edtItemName = findViewById(R.id.edtItemName);
        edtItemDes = findViewById(R.id.edtItemDes);
        spPrivacy = findViewById(R.id.spPrivacy);
        rmaAPIService = RmaAPIUtils.getAPIService();
        toolbar = findViewById(R.id.tbToolbar);
        rvSelectedImages = findViewById(R.id.rvSelectedImages);
        imageList = new ArrayList<>();
        tmpImageList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        firebaseImg = new FirebaseImg();
        scrollView = findViewById(R.id.scrollView);
        edtPreferItems = findViewById(R.id.edtPreferItems);
        txtCategory = findViewById(R.id.txtCategory);
        selectedCategoryList = new ArrayList<>();
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
        tmpImageList.remove(tmpImage);
        imageAdapter.setfilter(tmpImageList);
        if (imageList.size() == 0) {
            rvSelectedImages.setVisibility(View.GONE);
        }
    }

    private void hideKeyboard(View view) {
        inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}