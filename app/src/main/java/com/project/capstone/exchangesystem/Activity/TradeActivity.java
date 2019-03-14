package com.project.capstone.exchangesystem.Activity;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.project.capstone.exchangesystem.Utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.fragment.MyInventoryFragment;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.fragment.YourInventoryFragment;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionDetail;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class TradeActivity extends AppCompatActivity {

    Fragment selectedFragment;
    Fragment leftFragment, rightFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        leftFragment = YourInventoryFragment.newInstance();
        rightFragment = MyInventoryFragment.newInstance();


        tx.replace(R.id.fragment_switch1, leftFragment);
        tx.replace(R.id.fragment_switch2, rightFragment);
        tx.commit();
    }


    public void toYourInventoryFragment(View view) {
//        Fragment selectedFragment = null;
//        selectedFragment = new YourInventoryFragment();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_switch, selectedFragment).addToBackStack(null);
//        transaction.commit();

    }

    public void toMyInventoryFragment(View view) {
//        Fragment selectedFragment = null;
//        selectedFragment = new MyInventoryFragment();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_switch, selectedFragment).addToBackStack(null);
//        transaction.commit();
    }

    public void sendTradeRequest(View view) {
        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();

        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        int idMe = sharedPreferences.getInt("userId", 0);
        String authorization = sharedPreferences.getString("authorization", null);


        Item item = (Item) getIntent().getSerializableExtra("descriptionItem");
        int idYou = item.getUser().getId();


        GridView gridViewMyInventory = (GridView) findViewById(R.id.gridViewMyInventory);

        List<TransactionDetail> transactionDetailList = new ArrayList<>();
        int countMe = gridViewMyInventory.getAdapter().getCount();
        for (int i = 0; i < countMe; i++) {
            LinearLayout itemLayout = (LinearLayout) gridViewMyInventory.getChildAt(i);
            CheckBox checkBox = (CheckBox) itemLayout.findViewById(R.id.checkBoxTrade);

            if (checkBox.isChecked()) {
                TextView tempView = (TextView) itemLayout.findViewById(R.id.txtTradeIDItem);
                TransactionDetail temp = new TransactionDetail();
                temp.setItemId(Integer.parseInt(String.valueOf(tempView.getText())));
                temp.setUserId(idMe);
                transactionDetailList.add(temp);
            }
        }


        GridView gridViewYourInventory = (GridView) findViewById(R.id.gridViewYourInventory);
        int countYou = gridViewYourInventory.getAdapter().getCount();
        for (int i = 0; i < countYou; i++) {
            LinearLayout itemLayout = (LinearLayout) gridViewYourInventory.getChildAt(i);
            CheckBox checkBox = (CheckBox) itemLayout.findViewById(R.id.checkBoxTrade);

            if (checkBox.isChecked()) {
                TextView tempView = (TextView) itemLayout.findViewById(R.id.txtTradeIDItem);
                TransactionDetail temp = new TransactionDetail();
                temp.setItemId(Integer.parseInt(String.valueOf(tempView.getText())));
                temp.setUserId(idYou);
                transactionDetailList.add(temp);
            }
        }

        Transaction transaction = new Transaction();

        transaction.setReceiverId(idYou);
        transaction.setStatus("0");
        transaction.setDonationPostId(-1);

        TransactionRequestWrapper transactionRequestWrapper = new TransactionRequestWrapper(transaction, transactionDetailList);

        rmaAPIService.sendTradeRequest(authorization, transactionRequestWrapper).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                System.out.println("test response " + response.isSuccessful());
                System.out.println(response.body());

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
    }


}
