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
import com.project.capstone.exchangesystem.model.Relationship;
import com.project.capstone.exchangesystem.model.Transaction;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class TransactionNotificationAdapter extends BaseAdapter {
    Context context;
    //    ArrayList<Transaction> transactions;
    ArrayList<Object> notifications;


    public TransactionNotificationAdapter(Context context, ArrayList<Object> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
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
    public int getItemViewType(int position) {
        return (notifications.get(position).getClass() == Relationship.class) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//
//        SharedPreferences sharedPreferences = ((Activity) context).getSharedPreferences("localData", MODE_PRIVATE);
//        final int idMe = sharedPreferences.getInt("userId", 0);
//
//        TransactionNotificationAdapter.ViewHolder viewHolder = null;
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.transaction_notification, null);
//            viewHolder.txtNotification = (TextView) convertView.findViewById(R.id.txtNotification);
//            viewHolder.txtDateNoti = (TextView) convertView.findViewById(R.id.txtDateNoti);
//            viewHolder.imgSender = (ImageView) convertView.findViewById(R.id.imgSender);
//            convertView.setTag(viewHolder);
//
//        } else {
//            viewHolder = (TransactionNotificationAdapter.ViewHolder) convertView.getTag();
//        }
//        Transaction transactions = (Transaction) getItem(position);
//        String notification = "";
//        if (transactions.getSenderId() == idMe && transactions.getStatus().equals(AppStatus.TRANSACTION_DONE)) {
//            notification = transactions.getReceiver().getFullName() + "đã đồng ý yêu cầu của bạn";
//        } else if (transactions.getReceiverId() == idMe && transactions.getStatus().equals(AppStatus.TRANSACTION_SEND)) {
//            notification = transactions.getSender().getFullName() + " vừa gửi yêu cầu";
//        } else if (transactions.getStatus().equals(AppStatus.TRANSACTION_RESEND)) {
//            if (transactions.getReceiverId() == idMe) {
//                notification = transactions.getSender().getFullName() + " vừa cập nhật yêu cầu";
//            } else {
//                notification = transactions.getReceiver().getFullName() + " vừa cập nhật yêu cầu";
//            }
//        }
//
//        viewHolder.txtNotification.setText(notification);
//
//        Date date = new Date();
//        date.setTime(transactions.getCreateTime().getTime());
//        String formattedDate = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(date);
//        viewHolder.txtDateNoti.setText(formattedDate);
//
//        Picasso.with(context).load(transactions.getSender().getAvatar())
//                .placeholder(R.drawable.ic_no_image)
//                .error(R.drawable.ic_no_image)
//                .into(viewHolder.imgSender);
//        return convertView;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SharedPreferences sharedPreferences = ((Activity) context).getSharedPreferences("localData", MODE_PRIVATE);
        final int idMe = sharedPreferences.getInt("userId", 0);
        View view = convertView;
        int type = getItemViewType(position);
        if (view == null) {
            // Inflate the layout according to the view type
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (type == 0) {
                // Inflate the layout with image
                view = inflater.inflate(R.layout.notification_friend_request_item, parent, false);
            } else {
                view = inflater.inflate(R.layout.transaction_notification, parent, false);
            }
        }
        //
        Object c = notifications.get(position);

        if (c.getClass() == Transaction.class) {
            Transaction transaction = (Transaction) c;
            ImageView imgSender = (ImageView) view.findViewById(R.id.imgSender);
            TextView txtNotification = (TextView) view.findViewById(R.id.txtNotification);
            TextView txtDateNoti = (TextView) view.findViewById(R.id.txtDateNoti);

            String notification = "";
            if (transaction.getSenderId() == idMe && transaction.getStatus().equals(AppStatus.TRANSACTION_DONE)) {
                notification = transaction.getReceiver().getFullName() + "đã đồng ý yêu cầu của bạn";
            } else if (transaction.getReceiverId() == idMe && transaction.getStatus().equals(AppStatus.TRANSACTION_SEND)) {
                notification = transaction.getSender().getFullName() + " vừa gửi yêu cầu";
            } else if (transaction.getStatus().equals(AppStatus.TRANSACTION_RESEND)) {
                if (transaction.getReceiverId() == idMe) {
                    notification = transaction.getSender().getFullName() + " vừa cập nhật yêu cầu";
                } else {
                    notification = transaction.getReceiver().getFullName() + " vừa cập nhật yêu cầu";
                }
            }

            txtNotification.setText(notification);

            Date date = new Date();
            date.setTime(transaction.getCreateTime().getTime());
            String formattedDate = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(date);
            txtDateNoti.setText(formattedDate);
            Picasso.with(context).load(transaction.getSender().getAvatar())
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(imgSender);

        } else if (c.getClass() == Relationship.class) {
            Relationship relationship = (Relationship) c;
            ImageView imgProfileUser = (ImageView) view.findViewById(R.id.imgProfileUser);
            TextView txtNameUser = (TextView) view.findViewById(R.id.txtNameUser);
            TextView txtAddressUser = (TextView) view.findViewById(R.id.txtAddressUser);
            Button btnAcceptFriend = (Button) view.findViewById(R.id.btnAcceptFriend);
            Button btnDeclineFriend = (Button) view.findViewById(R.id.btnDeclineFriend);


            txtNameUser.setText(relationship.getSender().getFullName());
            txtAddressUser.setText("");
            Picasso.with(context).load(relationship.getSender().getAvatar())
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(imgProfileUser);
        }
        return view;
    }

}
