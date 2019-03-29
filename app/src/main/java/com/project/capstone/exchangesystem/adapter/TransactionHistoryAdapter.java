package com.project.capstone.exchangesystem.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.OwnTransaction;
import com.project.capstone.exchangesystem.activity.TransactionDetailActivity;
import com.project.capstone.exchangesystem.constants.AppStatus;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionDetail;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class TransactionHistoryAdapter extends BaseAdapter {
    Context context;
    ArrayList<Transaction> transactions;

    public TransactionHistoryAdapter(Context context, ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    public class ViewHolder {
        public Button btnSender, btnReceiver;
        public TextView txtStatusTrans, txtDateTrans;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();

        SharedPreferences sharedPreferences = ((Activity) context).getSharedPreferences("localData", MODE_PRIVATE);
        final String authorization = sharedPreferences.getString("authorization", null);
        final int idMe = sharedPreferences.getInt("userId", 0);
        TransactionHistoryAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new TransactionHistoryAdapter.ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.transaction_item, null);
            viewHolder.btnSender = (Button) convertView.findViewById(R.id.btnSender);
            viewHolder.btnReceiver = (Button) convertView.findViewById(R.id.btnReceiver);
            viewHolder.txtDateTrans = (TextView) convertView.findViewById(R.id.txtDateTrans);
            viewHolder.txtStatusTrans = (TextView) convertView.findViewById(R.id.txtStatusTrans);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (TransactionHistoryAdapter.ViewHolder) convertView.getTag();
        }
        final Transaction transactions = (Transaction) getItem(position);
        String status = "";
        if (transactions.getStatus().equals(AppStatus.TRANSACTION_DONE)) {
            status = status + "Giao dịch thành công";
        } else if (transactions.getStatus().equals(AppStatus.TRANSACTION_RESEND) || transactions.getStatus().equals(AppStatus.TRANSACTION_SEND)) {
            status = status + "Đang chờ xử lý";
        }
        viewHolder.txtStatusTrans.setText(status);
        viewHolder.btnSender.setText(transactions.getSender().getFullName());
        viewHolder.btnReceiver.setText(transactions.getReceiver().getFullName());
        Date date = new Date();
        date.setTime(transactions.getCreateTime().getTime());
        String formattedDate = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(date);
        viewHolder.txtDateTrans.setText(formattedDate);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rmaAPIService.getTransactionByTransID(authorization, transactions.getId()).enqueue(new Callback<TransactionRequestWrapper>() {
                    @Override
                    public void onResponse(Call<TransactionRequestWrapper> call, Response<TransactionRequestWrapper> response) {
                        System.out.println("vào được");
                        if (response.isSuccessful()) {
                            TransactionRequestWrapper temp = response.body();
                            Intent intent = new Intent(context, TransactionDetailActivity.class);
                            intent.putExtra("transactionDetail", temp);
                            context.startActivity(intent);
                        } else {
                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionRequestWrapper> call, Throwable t) {
                        System.out.println("fail in daa");
                    }
                });

            }
        });
        return convertView;
    }
}
