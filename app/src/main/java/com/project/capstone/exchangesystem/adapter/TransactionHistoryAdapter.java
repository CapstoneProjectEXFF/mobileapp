package com.project.capstone.exchangesystem.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.constants.AppStatus;
import com.project.capstone.exchangesystem.model.Transaction;
import com.squareup.picasso.Picasso;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        SharedPreferences sharedPreferences = ((Activity) context).getSharedPreferences("localData", MODE_PRIVATE);
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
        Transaction transactions = (Transaction) getItem(position);
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
        return convertView;
    }
}
