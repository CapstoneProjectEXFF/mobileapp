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
    ArrayList<Item> chooseList;
    ArrayList<String> idItemList;
    String id;

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
        tradeAdapter = new TradeAdapter(this, chooseList);
        grideviewChoose.setAdapter(tradeAdapter);


        Intent intent = this.getIntent();
        int id = (int) intent.getIntExtra("id", 0);


        if (intent.hasExtra("itemMeIdList")) {
            ArrayList<String> listItem = intent.getStringArrayListExtra("itemMeIdList");
        } else {
            ArrayList<String> listItem = intent.getStringArrayListExtra("itemYouIdList");
        }

        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        String authorization = sharedPreferences.getString("authorization", null);


        if (authorization != null) {
            RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
            rmaAPIService.getItemsByUserIdWithPrivacy(authorization, id).enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {

                    List<Item> result = response.body();
                    for (int i = 0; i < result.size(); i++) {
                        if (result.get(i).getStatus().equals(ITEM_ENABLE)){
                            chooseList.add(result.get(i));
                            tradeAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Item>> call, Throwable t) {
                    System.out.println(t.getMessage());
                }
            });
        } else {
            System.out.println("Fail Test Authorization");
        }

    }

    public void addItem(View view) {
        Intent intent1 = this.getIntent();
        int id = (int) intent1.getIntExtra("id", 0);
        int countMe = grideviewChoose.getAdapter().getCount();
        ArrayList<Item> idItemList = new ArrayList<>();
        for (int i = 0; i < countMe; i++) {
            try {
                LinearLayout itemLayout = (LinearLayout) grideviewChoose.getChildAt(i);
                CheckBox checkBox = (CheckBox) itemLayout.findViewById(R.id.checkBoxTrade);
                if (checkBox.isChecked()) {
                    TextView tempView = (TextView) itemLayout.findViewById(R.id.txtTradeIDItem);
                    idItemList.add(chooseList.get(i));

                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println(ex.getCause());
                System.out.println(ex.getLocalizedMessage());
            }
        }

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("LISTCHOOSE", idItemList);
        intent.putExtras(bundle);
        intent.putExtra("tempID", id);
        setResult(1, intent);
        finish();
    }
}
