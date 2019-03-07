package com.project.capstone.exchangesystem.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.support.v7.widget.Toolbar;
import com.project.capstone.exchangesystem.R;
import com.squareup.picasso.Picasso;
import com.project.capstone.exchangesystem.model.Item;

import java.util.List;

public class DescriptionItem extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbarDescriptionItem;
    ImageView imgDescriptionItem;
    TextView txtNameDescriptionItem, txtNameUserDescriotionItem, txtViewDescriptionItem;
    Button btnTrade;

    String txtName;
    String txtDescription;
    String image;
    String nameUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_item);
        direct();
        ActionToolbar();
        GetInformation();
//        EventButton();
    }

    private void GetInformation() {


        Item item = (Item) getIntent().getSerializableExtra("descriptionItem");
        txtName = item.getName();
        txtDescription = item.getDescription();
//        image = item.getImage().get(0).getUrl();


        txtNameDescriptionItem.setText(txtName);
        txtViewDescriptionItem.setText(txtDescription);
        Picasso.with(getApplicationContext()).load("https://cdn.tgdd.vn/Products/Images/42/192001/samsung-galaxy-j6-plus-1-400x460.png")
                .placeholder(R.drawable.no)
                .error(R.drawable.loadingimage)
                .into(imgDescriptionItem);


    }

    private void ActionToolbar() {
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
        toolbarDescriptionItem = (Toolbar) findViewById(R.id.toolbarDescriptionItem);
        imgDescriptionItem = (ImageView) findViewById(R.id.imgDescriptionItem);
        txtNameDescriptionItem = (TextView) findViewById(R.id.txtNameDescriptionItem);
        txtNameUserDescriotionItem = (TextView) findViewById(R.id.txtNameUserDescriotionItem);
        txtViewDescriptionItem = (TextView) findViewById(R.id.txtViewDescriptionItem);
        btnTrade = (Button) findViewById(R.id.btnTrade);
    }

    public void toTradeActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
        Item item = (Item) getIntent().getSerializableExtra("descriptionItem");
        intent.putExtra("descriptionItem", item);
        startActivity(intent);

    }
}
