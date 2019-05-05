package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.TradeRealtimeActivity;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Message;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.project.capstone.exchangesystem.constants.AppStatus.RECEIVE_MSG;
import static com.project.capstone.exchangesystem.constants.AppStatus.SEND_MSG;
import static com.project.capstone.exchangesystem.constants.AppStatus.TRADE_DONE_MESSAGE;
import static com.project.capstone.exchangesystem.constants.AppStatus.USER_ACCEPTED_TRADE_MESSAGE;
import static com.project.capstone.exchangesystem.constants.AppStatus.USER_ADDED_ITEM_MESSAGE;
import static com.project.capstone.exchangesystem.constants.AppStatus.USER_CANCELED_TRADE_CONFIRM_MESSAGE;
import static com.project.capstone.exchangesystem.constants.AppStatus.USER_REMOVED_ITEM_MESSAGE;
import static com.project.capstone.exchangesystem.constants.AppStatus.USER_RESET_TRADE_MESSAGE;

public class MessageAdapter extends BaseAdapter {

    Context context;
    List<Message> messages;
    int myUserId, senderId;
    User friendAccount;

    public MessageAdapter(Context context, List<Message> messages, int myUserId, User friendAccount) {
        this.context = context;
        this.messages = messages;
        this.myUserId = myUserId;
        this.friendAccount = friendAccount;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder {
        TextView txtMessage;
        ImageView ivAvatar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageAdapter.ViewHolder viewHolder = null;
        Message message = messages.get(position);

//        if (convertView == null){
        viewHolder = new ViewHolder();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        senderId = Integer.parseInt(message.getSender());

        if (senderId < 0) {
            convertView = layoutInflater.inflate(R.layout.noti_message_layout, null);
        } else if (senderId == myUserId) {
            convertView = layoutInflater.inflate(R.layout.send_message_layout, null);
        } else {
            convertView = layoutInflater.inflate(R.layout.receive_message_layout, null);
            viewHolder.ivAvatar = convertView.findViewById(R.id.ivAvatar);
        }

        viewHolder.txtMessage = convertView.findViewById(R.id.txtMessage);

        if (senderId < 0) {
            String noti = "";

            switch (senderId) {
                case USER_ACCEPTED_TRADE_MESSAGE:
                    setNoti(message, context.getString(R.string.user_accepted_trade), viewHolder);
                    break;
                case USER_CANCELED_TRADE_CONFIRM_MESSAGE:
                    setNoti(message, context.getString(R.string.user_canceled_confirm_trade), viewHolder);
                    break;
//                case USER_RESET_TRADE_MESSAGE:
//                    setNoti(message, context.getString(R.string.user_reseted_trade), viewHolder);
//                    break;
                case TRADE_DONE_MESSAGE:
                    viewHolder.txtMessage.setText(context.getString(R.string.trade_done));
                    break;
                case USER_ADDED_ITEM_MESSAGE:
                    setNotiByUserIdAndItemId(message, context.getString(R.string.user_added_item), viewHolder);
                    break;
                case USER_REMOVED_ITEM_MESSAGE:
                    setNotiByUserIdAndItemId(message, context.getString(R.string.user_removed_item), viewHolder);
                    break;
            }

        } else {
            viewHolder.txtMessage.setText(message.getMsg());

            if (myUserId != senderId) {
                Picasso.with(context).load(friendAccount.getAvatar())
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)
                        .into(viewHolder.ivAvatar);
            }
        }

        return convertView;
    }

    private void setNotiByUserIdAndItemId(Message message, final String content, final MessageAdapter.ViewHolder viewHolder) {
        final int tmpItemId = Integer.parseInt(message.getMsg());

        SharedPreferences sharedPreferences = context.getSharedPreferences("localData", MODE_PRIVATE);
        String authorization = sharedPreferences.getString("authorization", null);
        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();

        if (authorization != null){
            rmaAPIService.getItemById(authorization, tmpItemId).enqueue(new Callback<Item>() {
                @Override
                public void onResponse(Call<Item> call, Response<Item> response) {
                    if (response.body() != null){
                        String noti = response.body().getName() + " " + content;
                        viewHolder.txtMessage.setText(noti);
                    }
                }

                @Override
                public void onFailure(Call<Item> call, Throwable t) {

                }
            });
        }
    }

    private void setNoti(Message message, String content, MessageAdapter.ViewHolder viewHolder) {
        int tmpUserId = Integer.parseInt(message.getMsg());
        String noti;

        if (tmpUserId == myUserId) {
            noti = context.getString(R.string.me_confirmed) + " " + content;
        } else {
            noti = friendAccount.getFullName() + " " + content;
        }

        viewHolder.txtMessage.setText(noti);
    }
}