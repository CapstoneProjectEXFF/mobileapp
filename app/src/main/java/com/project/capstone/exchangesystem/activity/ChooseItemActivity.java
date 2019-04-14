package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.adapter.TradeAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.remote.RmaAPIService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.project.capstone.exchangesystem.constants.AppStatus.ITEM_ENABLE;

public class ChooseItemActivity extends AppCompatActivity {
    GridView grideviewChoose;
    TradeAdapter tradeAdapter;
    ArrayList<Item> chooseList, availableItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_item);

        direct();
        getData();
    }

    private void getData() {
    }

    private void direct() {
        grideviewChoose = findViewById(R.id.grideviewChoose);
        chooseList = new ArrayList<>();
        availableItems = new ArrayList<>();
        Intent intent = this.getIntent();
        availableItems = (ArrayList<Item>) intent.getSerializableExtra("availableItems");
        tradeAdapter = new TradeAdapter(this, availableItems);
        grideviewChoose.setAdapter(tradeAdapter);
    }

    public void addItem(View view) {
        ArrayList<Item> selectedItems = new ArrayList<>();
        int countMe = grideviewChoose.getAdapter().getCount();
        for (int i = 0; i < countMe; i++) {
            try {
                LinearLayout itemLayout = (LinearLayout) grideviewChoose.getChildAt(i);
                CheckBox checkBox = itemLayout.findViewById(R.id.checkBoxTrade);
                if (checkBox.isChecked()) {
                    selectedItems.add(availableItems.get(i));
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println(ex.getCause());
                System.out.println(ex.getLocalizedMessage());
            }
        }

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("LISTCHOOSE", selectedItems);
        intent.putExtras(bundle);
        setResult(1, intent);
        finish();
    }
}
