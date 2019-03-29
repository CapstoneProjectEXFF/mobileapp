package com.project.capstone.exchangesystem.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.constants.AppStatus;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.squareup.picasso.Picasso;
import com.project.capstone.exchangesystem.model.CharityPostItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class TransactionNotificationAdapter extends BaseAdapter {
    Context context;
    ArrayList<Transaction> transactions;

    public TransactionNotificationAdapter(Context context, ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
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

    public class ViewHolder {
        public TextView txtNotification, txtDateNoti;
        public ImageView imgSender;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SharedPreferences sharedPreferences = ((Activity) context).getSharedPreferences("localData", MODE_PRIVATE);
        final int idMe = sharedPreferences.getInt("userId", 0);

        TransactionNotificationAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.transaction_notification, null);
            viewHolder.txtNotification = (TextView) convertView.findViewById(R.id.txtNotification);
            viewHolder.txtDateNoti = (TextView) convertView.findViewById(R.id.txtDateNoti);
            viewHolder.imgSender = (ImageView) convertView.findViewById(R.id.imgSender);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (TransactionNotificationAdapter.ViewHolder) convertView.getTag();
        }
        Transaction transactions = (Transaction) getItem(position);
        String notification = "";
        if (transactions.getSenderId() == idMe && transactions.getStatus().equals(AppStatus.TRANSACTION_DONE)) {
            notification = transactions.getReceiver().getFullName() + "đã đồng ý yêu cầu của bạn";
        } else if (transactions.getReceiverId() == idMe && transactions.getStatus().equals(AppStatus.TRANSACTION_SEND)) {
            notification = transactions.getSender().getFullName() + " vừa gửi yêu cầu";
        } else if (transactions.getStatus().equals(AppStatus.TRANSACTION_RESEND)) {
            if (transactions.getReceiverId() == idMe) {
                notification = transactions.getSender().getFullName() + " vừa cập nhật yêu cầu";
            } else {
                notification = transactions.getReceiver().getFullName() + " vừa cập nhật yêu cầu";
            }
        }

        viewHolder.txtNotification.setText(notification);

        Date date = new Date();
        date.setTime(transactions.getCreateTime().getTime());
        String formattedDate = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(date);
        viewHolder.txtDateNoti.setText(formattedDate);

        Picasso.with(context).load(transactions.getSender().getAvatar())
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image)
                .into(viewHolder.imgSender);
        return convertView;
    }
}
