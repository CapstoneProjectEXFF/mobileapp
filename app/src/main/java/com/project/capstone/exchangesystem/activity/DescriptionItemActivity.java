package com.project.capstone.exchangesystem.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.dialog.LoginOptionDialog;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.utils.UserSession;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static com.project.capstone.exchangesystem.constants.AppStatus.CANCEL_IMAGE_OPTION;
import static com.project.capstone.exchangesystem.constants.AppStatus.LOGIN_REMINDER;

public class DescriptionItemActivity extends AppCompatActivity implements LoginOptionDialog.LoginOptionListener {
    android.support.v7.widget.Toolbar toolbarDescriptionItem;
    ImageView imgDescriptionItem, imgAvatar;
    TextView txtDateDescriptionItem, txtNameUserDescriotionItem, txtViewDescriptionItem;
    Button btnTrade;
    ImageButton btnShare;
    UserSession userSession;


    //share facebook
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;
    String authorization;
    int idMe;

    Item item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_description_item);
        direct();

//        EventButton();

        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();

        if (appLinkData == null) { //check if app was opened from link or not
            GetInformation(-1);
        } else {
            int uriItemId = Integer.parseInt(appLinkData.toString().replace("https://exff-104b8.firebaseapp.com/item.html?id=", ""));
            GetInformation(uriItemId);
        }
        actionToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userSession.isUserLoggedIn() && idMe == item.getUser().getId()) {
            getMenuInflater().inflate(R.menu.menu_edit_post_option, menu);
            menu.getItem(0).setTitle(R.string.update_item);
            menu.getItem(1).setTitle(R.string.delete_item);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.editpost) {
            Intent intent = new Intent(getApplicationContext(), UpdateItemActivity.class);
            intent.putExtra("itemId", item.getId());
            startActivity(intent);
        } else if (menuItem.getItemId() == R.id.deletepost) {
            // TODO dialog choose options
            deleteItem();
        }
        return true;
    }

    private void deleteItem() {
        System.out.println("test authorization " + authorization);
        System.out.println("test id " + item.getId());
        rmaAPIService.deleteItemWithId(authorization, item.getId()).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.delete_item_noti, Toast.LENGTH_LONG).show();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error Request", Toast.LENGTH_LONG).show();
                    System.out.println(response.body());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error Server", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void GetInformation(int uriItemId) {
        if (uriItemId == -1) {
            item = (Item) getIntent().getSerializableExtra("descriptionItem");
            setItemInf(item);
        } else {
            sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
            authorization = sharedPreferences.getString("authorization", null);
            idMe = sharedPreferences.getInt("userId", 0);
            if (authorization != null) {
                txtDateDescriptionItem.setText("");
                rmaAPIService.getItemById(authorization, uriItemId).enqueue(new Callback<Item>() {
                    @Override
                    public void onResponse(Call<Item> call, Response<Item> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                item = response.body();
                                setItemInf(item);
                            } else {
                                Log.i("Item", "null");
                                Toast.makeText(getApplicationContext(), "exe", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.i("Item", "cannot load item");
                        }
                    }

                    @Override
                    public void onFailure(Call<Item> call, Throwable t) {
                        Log.i("Item", "failed");
                    }
                });
            }
        }
    }

    private void setItemInf(Item item) {
        toolbarDescriptionItem.setTitle(item.getName());
        txtNameUserDescriotionItem.setText(item.getUser().getFullName());
        txtDateDescriptionItem.setText(convertDatetime(item.getCreateTime()));
        txtViewDescriptionItem.setText(item.getDescription());

        String url = "";
        if (item.getImage().size() > 0) {
            url = item.getImage().get(0).getUrl();
            Picasso.with(getApplicationContext()).load(url)
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(imgDescriptionItem);
        } else {
            imgDescriptionItem.setImageResource(R.drawable.ic_no_image);
        }
        if (item.getUser() != null && item.getUser().getAvatar() != null) {
            url = item.getUser().getAvatar();
            Picasso.with(getApplicationContext()).load(url)
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(imgAvatar);
        } else {
            imgDescriptionItem.setImageResource(R.drawable.ic_no_image);
        }
    }

    private void actionToolbar() {
        setSupportActionBar(toolbarDescriptionItem);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarDescriptionItem.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void direct() {
        userSession = new UserSession(getApplicationContext());
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        idMe = sharedPreferences.getInt("userId", 0);
        authorization = sharedPreferences.getString("authorization", null);
        toolbarDescriptionItem = findViewById(R.id.toolbarDescriptionItem);
        imgDescriptionItem = findViewById(R.id.imgDescriptionItem);
        imgAvatar = findViewById(R.id.imgUserAvatar);
        txtDateDescriptionItem = findViewById(R.id.txtDateDescriptionItem);
        txtNameUserDescriotionItem = findViewById(R.id.txtNameUserDescriotionItem);
        txtViewDescriptionItem = findViewById(R.id.txtViewDescriptionItem);
        btnTrade = findViewById(R.id.btnTrade);
        btnShare = findViewById(R.id.btnShare);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setQuote("Test").setContentUrl(Uri.parse("https://exff-104b8.firebaseapp.com/item.html?id=" + item.getId())).build();
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    shareDialog.show(shareLinkContent);
                }
            }
        });

        rmaAPIService = RmaAPIUtils.getAPIService();
    }

    public void toTradeActivity(View view) {
        if (userSession.isUserLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), TradeRealtimeActivity.class);
            Item item = (Item) getIntent().getSerializableExtra("descriptionItem");
            intent.putExtra("descriptionItem", item);
            startActivity(intent);
        } else {
            // TODO dialog ask user
            LoginOptionDialog optionDialog = new LoginOptionDialog();
            optionDialog.show(getSupportFragmentManager(), "optionDialog");
        }
    }

    private String convertDatetime(Timestamp timestamp) {
        String date = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        try {
            date = simpleDateFormat.format(timestamp);
        } catch (Exception e) {
        }
        return date;
    }

    @Override
    public void onButtonClicked(int choice) {
        switch (choice) {
            case LOGIN_REMINDER:
                login();
                break;
            case CANCEL_IMAGE_OPTION:
                break;
            default:
                break;
        }
    }

    private void login() {
        Intent signInActivity = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(signInActivity);
    }
}
