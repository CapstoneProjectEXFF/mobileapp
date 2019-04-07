package com.project.capstone.exchangesystem.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionDetail;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UpdateTransactionActivity extends AppCompatActivity {
    RecyclerView recyclerviewMe, recyclerviewYou;
    ArrayList<Item> choosedMe, choosedYou;
    ItemAdapter itemMeAdapter, itemYouAdapter;
    Button addMe, addYou;
    int idMe, idYou;
    ArrayList<String> itemIdsMe, itemIdsYou;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        direct();
    }

    private void direct() {
        recyclerviewMe = findViewById(R.id.recyclerviewMe);
        recyclerviewYou = findViewById(R.id.recyclerviewYou);

        choosedMe = new ArrayList<>();
        choosedYou = new ArrayList<>();

        itemIdsMe = new ArrayList<>();
        itemIdsYou = new ArrayList<>();

        choosedMe = (ArrayList<Item>) getIntent().getSerializableExtra("itemsMeUpdate");
        for (int i = 0; i < choosedMe.size(); i++) {
            itemIdsMe.add(String.valueOf(choosedMe.get(i).getId()));
        }


        choosedYou = (ArrayList<Item>) getIntent().getSerializableExtra("itemsYouUpdate");
        for (int i = 0; i < choosedYou.size(); i++) {
            itemIdsYou.add(String.valueOf(choosedYou.get(i).getId()));
        }

        idMe = getIntent().getIntExtra("idMeUpdate", 0);
        idYou = getIntent().getIntExtra("idYouUpdate", 0);

        itemMeAdapter = new ItemAdapter(getApplicationContext(), choosedMe, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

            }
        });

        recyclerviewMe.setHasFixedSize(true);
        recyclerviewMe.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        recyclerviewMe.setAdapter(itemMeAdapter);
        itemMeAdapter.notifyDataSetChanged();

        itemYouAdapter = new ItemAdapter(getApplicationContext(), choosedYou, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

            }
        });

        recyclerviewYou.setHasFixedSize(true);
        recyclerviewYou.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        recyclerviewYou.setAdapter(itemYouAdapter);
        itemYouAdapter.notifyDataSetChanged();


        addMe = findViewById(R.id.addMe);
        addYou = findViewById(R.id.addYou);

        addMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<String> itemList = getArrayList("itemMeIdList");
                Intent intent = new Intent(UpdateTransactionActivity.this, ChooseItemActivity.class);
                intent.putExtra("id", idMe);
                intent.putStringArrayListExtra("itemMeIdList", itemIdsMe);
                startActivityForResult(intent, 3);
            }
        });

        addYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateTransactionActivity.this, ChooseItemActivity.class);
                intent.putExtra("id", idYou);
                intent.putStringArrayListExtra("itemYouIdList", itemIdsYou);
                startActivityForResult(intent, 3);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        idMe = getIntent().getIntExtra("idMeUpdate", 0);
        idYou = getIntent().getIntExtra("idYouUpdate", 0);
        if (requestCode == 3) {
            try {

                Bundle bundle = data.getExtras();
                ArrayList<Item> idChoose = (ArrayList<Item>) bundle.getSerializable("LISTCHOOSE");
                int id = data.getIntExtra("tempID", 0);
                if (id == idMe) {
                    itemMeAdapter.setfilter(idChoose);
                    itemIdsMe.clear();
                    for (int i = 0; i < idChoose.size(); i++) {
                        itemIdsMe.add(String.valueOf(idChoose.get(i).getId()));
                    }
                    saveArrayList(itemIdsMe, "itemMeIdList");
                } else if (id == idYou) {
                    itemYouAdapter.setfilter(idChoose);
                    itemIdsYou.clear();
                    for (int i = 0; i < idChoose.size(); i++) {
                        itemIdsYou.add(String.valueOf(idChoose.get(i).getId()));
                    }
                    saveArrayList(itemIdsYou, "itemYouIdList");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

    public ArrayList<String> getArrayList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> strings = gson.fromJson(json, type);
        return strings;
    }

    public void saveArrayList(ArrayList<String> list, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public void sendTradeRequest(View view) {
        idMe = getIntent().getIntExtra("idMeUpdate", 0);
        idYou = getIntent().getIntExtra("idYouUpdate", 0);
        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        String authorization = sharedPreferences.getString("authorization", null);

        Set<Integer> itemsIDAfterUpdate = new HashSet<Integer>();
        Set<Integer> itemsIDBeforeUpdate = new HashSet<Integer>();
        Intent intent = this.getIntent();
        final TransactionRequestWrapper transactionRequestWrapper = (TransactionRequestWrapper) intent.getSerializableExtra("transactionDetail");
        if (transactionRequestWrapper == null) {
            Toast.makeText(getApplicationContext(), "Transaction bị rỗng", Toast.LENGTH_LONG).show();
        }
        List<TransactionDetail> transactionDetailBefore = transactionRequestWrapper.getDetails();
        for (int i = 0; i < transactionDetailBefore.size(); i++) {
            itemsIDBeforeUpdate.add(transactionDetailBefore.get(i).getItemId());

        }
        ArrayList<Item> itemsMeAfterUpdate = itemMeAdapter.getfilter();
        ArrayList<Item> itemsYouAfterUpdate = itemYouAdapter.getfilter();

        if (itemsMeAfterUpdate.size() == 0 || itemsYouAfterUpdate.size() == 0) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();

        } else {
            for (int i = 0; i < itemsMeAfterUpdate.size(); i++) {
                itemsIDAfterUpdate.add(itemsMeAfterUpdate.get(i).getId());
            }
            for (int i = 0; i < itemsYouAfterUpdate.size(); i++) {
                itemsIDAfterUpdate.add(itemsYouAfterUpdate.get(i).getId());
            }
            Set<Integer> itemsKeep = new HashSet<Integer>(itemsIDAfterUpdate);
            itemsKeep.retainAll(itemsIDBeforeUpdate);
            List<Integer> itemsKeepList = new ArrayList<Integer>(itemsKeep);

            Set<Integer> itemsEliminate = new HashSet<Integer>(itemsIDBeforeUpdate);
            itemsEliminate.removeAll(itemsIDAfterUpdate);
            List<Integer> itemsEliminateList = new ArrayList<Integer>(itemsEliminate);

            Set<Integer> itemsAdd = new HashSet<Integer>(itemsIDAfterUpdate);
            itemsAdd.removeAll(itemsIDBeforeUpdate);
            List<Integer> itemsAddList = new ArrayList<Integer>(itemsAdd);

            List<TransactionDetail> transactionDetailsAfterUpdate = new ArrayList<>();
//        for (int i = 0; i < itemsKeepList.size(); i++) {
//            TransactionDetail temp = new TransactionDetail();
//            temp.setItemId(itemsKeepList.get(i));
//
//            for (int j = 0; j < transactionDetailBefore.size(); j++) {
//                if (transactionDetailBefore.get(j).getItemId() == itemsKeepList.get(i)) {
//                    temp.setId(transactionDetailBefore.get(j).getId());
//                    System.out.println("Test keep item" +transactionDetailBefore.get(j).getId());
//                }
//            }
//            temp.setTransactionId(transactionRequestWrapper.getTransaction().getId());
////            for (int k = 0; k < itemsMeAfterUpdate.size(); k++) {
////                if (itemsMeAfterUpdate.get(k).getId() == itemsKeepList.get(i)) {
////                    temp.setUserId(idMe);
////                }
////            }
////            for (int k = 0; k < itemsYouAfterUpdate.size(); k++) {
////                if (itemsYouAfterUpdate.get(k).getId() == itemsKeepList.get(i)) {
////                    temp.setUserId(idYou);
////                }
////            }
//            transactionDetailsAfterUpdate.add(temp);
////            System.out.println("Test user id transaction detail " + temp.getUserId());
//        }
            for (int i = 0; i < itemsEliminateList.size(); i++) {
                TransactionDetail temp = new TransactionDetail();
                temp.setItemId(itemsEliminateList.get(i));

                for (int j = 0; j < transactionDetailBefore.size(); j++) {
                    if (transactionDetailBefore.get(j).getItemId() == itemsEliminateList.get(i)) {
                        temp.setId(transactionDetailBefore.get(j).getId());
                    }
                }
                for (int k = 0; k < itemsMeAfterUpdate.size(); k++) {
                    if (itemsMeAfterUpdate.get(k).getId() == itemsEliminateList.get(i)) {
                        temp.setUserId(idMe);
                    }
                }
                for (int k = 0; k < itemsYouAfterUpdate.size(); k++) {
                    if (itemsYouAfterUpdate.get(k).getId() == itemsEliminateList.get(i)) {
                        temp.setUserId(idYou);
                    }
                }
                transactionDetailsAfterUpdate.add(temp);
            }
            for (int i = 0; i < itemsAddList.size(); i++) {
                TransactionDetail temp = new TransactionDetail();
                temp.setItemId(itemsAddList.get(i));
                temp.setTransactionId(transactionRequestWrapper.getTransaction().getId());


                for (int k = 0; k < itemsMeAfterUpdate.size(); k++) {
                    if (itemsMeAfterUpdate.get(k).getId() == itemsAddList.get(i)) {
                        temp.setUserId(idMe);
                    }
                }
                for (int k = 0; k < itemsYouAfterUpdate.size(); k++) {
                    if (itemsYouAfterUpdate.get(k).getId() == itemsAddList.get(i)) {
                        temp.setUserId(idYou);
                    }
                }
                transactionDetailsAfterUpdate.add(temp);

            }
            System.out.println("Test itemsEliminate size " + itemsEliminate.size());
            System.out.println("Test itemsAdd size " + itemsAdd.size());

            while (itemsEliminate.size() == 0 && itemsAdd.size() == 0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog));

                alertDialogBuilder.setTitle("Draft");
                alertDialogBuilder.setMessage("Discard draft?");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialogBuilder.setPositiveButton("DISCARD", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(UpdateTransactionActivity.this, "Discard", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(UpdateTransactionActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialogBuilder.show();
            }

            Transaction transactionAfterUpdate = new Transaction();
            transactionAfterUpdate.setId(transactionRequestWrapper.getTransaction().getId());
            transactionAfterUpdate.setSenderId(transactionRequestWrapper.getTransaction().getSenderId());
            transactionAfterUpdate.setReceiverId(transactionRequestWrapper.getTransaction().getReceiverId());

            TransactionRequestWrapper transactionRequestWrapperAfterUpdate = new TransactionRequestWrapper(transactionAfterUpdate, transactionDetailsAfterUpdate);

            RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
            rmaAPIService.updateTransaction(authorization, transactionRequestWrapperAfterUpdate).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Update Thành Công", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Fail rồi nhé", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Sao Thế này", Toast.LENGTH_LONG).show();

                }
            });

        }
    }
}
