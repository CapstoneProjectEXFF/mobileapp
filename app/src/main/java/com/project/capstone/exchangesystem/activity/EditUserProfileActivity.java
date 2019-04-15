package com.project.capstone.exchangesystem.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.FirebaseImg;
import com.project.capstone.exchangesystem.model.PostAction;
import com.project.capstone.exchangesystem.model.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.project.capstone.exchangesystem.constants.AppStatus.USER_UPDATE_ACTION;

public class EditUserProfileActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 2;
    private final int IMAGE_SIZE = 330;

    Toolbar toolbar;
    EditText txtPhoneNumber, txtName, txtAddress;
    //    boolean flag1 = true, flag2 = true, flag3 = true, flag4 = true, flag5 = true;
    boolean avaCheck = false;
    String toastText;
    String userPhoneNumber, authorization, name, address, avatar, status;
    int userID;
    ImageView ivAvatar;
    Uri avatarUri;
    Button btnSubmit;
    FirebaseImg firebaseImg;
    Context context;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        direct();
        ActionToolbar();
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void direct() {
        firebaseImg = new FirebaseImg();
        context = this;
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        userID = sharedPreferences.getInt("userId", 0);
        userPhoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
        authorization = sharedPreferences.getString("authorization", null);
        name = sharedPreferences.getString("username", null);
        if (sharedPreferences.contains("avatar")) {
            avatar = sharedPreferences.getString("avatar", null);
        }
        status = sharedPreferences.getString("status", null);
        address = sharedPreferences.getString("address", null);


        toolbar = findViewById(R.id.edituserprofileToolbar);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumberEditProfile);
        txtName = findViewById(R.id.txtNameEditUserProfile);
        txtAddress = findViewById(R.id.txtAdressEditUserProfile);
        ivAvatar = findViewById(R.id.ivAvatar);
        btnSubmit = findViewById(R.id.btnSubmit);

        txtPhoneNumber.setText(userPhoneNumber);
        txtName.setText(name);
        txtAddress.setText(address);
        Picasso.with(getApplicationContext()).load(avatar)
                .placeholder(R.drawable.no)
                .error(R.drawable.loadingimage)
                .into(ivAvatar);
        resizeImage();

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = txtName.getText().toString();
                if (fullName.trim().length() < 6) {
                    Toast.makeText(getApplicationContext(), "Your Name is not long enough\n", Toast.LENGTH_LONG).show();
                } else {
                    setUserData(fullName);
                }
            }
        });
    }

    private void resizeImage() {
        ViewGroup.LayoutParams layoutParams = ivAvatar.getLayoutParams();
        layoutParams.height = IMAGE_SIZE;
        layoutParams.width = IMAGE_SIZE;
        ivAvatar.setLayoutParams(layoutParams);
    }

    private void setUserData(String fullName) {
        User user = new User();
        user.setFullName(fullName);
        List<Uri> uri = new ArrayList<>();
        SharedPreferences.Editor editor = getSharedPreferences("localData", MODE_PRIVATE).edit();
        if (avaCheck) {
            uri.add(avatarUri);
            firebaseImg.uploadImagesToFireBase(context, uri, null, null, user, authorization, USER_UPDATE_ACTION, editor);
        } else {
            new PostAction().manageUser(user, avatar, authorization, context, USER_UPDATE_ACTION, editor);
        }

    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] imageTypes = {"image/jpeg", "image/png", "image/jpg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, imageTypes);

        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                avatarUri = data.getData();
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), avatarUri);
                    ivAvatar.setImageBitmap(bmp);
                    resizeImage();
                    avaCheck = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
