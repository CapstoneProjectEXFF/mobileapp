package com.project.capstone.exchangesystem.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.constants.AppStatus;
import com.project.capstone.exchangesystem.model.*;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.*;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.project.capstone.exchangesystem.constants.AppStatus.TRADE_DONE_MESSAGE;
import static com.project.capstone.exchangesystem.constants.AppStatus.TRANSACTION_DONATED;
import static com.project.capstone.exchangesystem.constants.AppStatus.USER_ACCEPTED_TRADE_MESSAGE;
import static com.project.capstone.exchangesystem.constants.AppStatus.USER_ADDED_ITEM_MESSAGE;
import static com.project.capstone.exchangesystem.constants.AppStatus.USER_CANCELED_TRADE_CONFIRM_MESSAGE;
import static com.project.capstone.exchangesystem.constants.AppStatus.USER_REMOVED_ITEM_MESSAGE;
import static com.project.capstone.exchangesystem.constants.AppStatus.USER_RESET_TRADE_MESSAGE;

public class TransactionNotificationAdapter extends BaseAdapter {
    Context context;
    ArrayList<Object> notifications;
    int idMe;
    String authorization;
    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;


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

    @Override
    public int getItemViewType(int position) {
        if (notifications.get(position).getClass() == Relationship.class) {
            return 0;
        } else if (notifications.get(position).getClass() == Transaction.class) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        rmaAPIService = RmaAPIUtils.getAPIService();
        sharedPreferences = ((Activity) context).getSharedPreferences("localData", MODE_PRIVATE);
        idMe = sharedPreferences.getInt("userId", 0);
        authorization = sharedPreferences.getString("authorization", null);

        View view = convertView;
        int type = getItemViewType(position);
        if (view == null) {
            // Inflate the layout according to the view type
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (type == 0) {
                // Inflate the layout with image
                view = inflater.inflate(R.layout.notification_friend_request_item, parent, false);
            } else if (type == 1 || type == 2 || type == 3) {
                view = inflater.inflate(R.layout.transaction_notification, parent, false);
            }
        }
        //
        Object c = notifications.get(position);

        if (c.getClass() == Transaction.class) {
            Transaction transaction = (Transaction) c;
            String notification = "";

            if (transaction.getStatus().equals(AppStatus.TRANSACTION_DONE)) {
                if (transaction.getReceiverId() != idMe){
                    notification = "Cuộc trao đổi của bạn và " + transaction.getReceiver().getFullName() + " đã hoàn thành";
                    setNotiItem(notification, transaction.getSender().getAvatar(), view);
                } else {
                    notification = "Cuộc trao đổi của bạn và " + transaction.getSender().getFullName() + " đã hoàn thành";
                    setNotiItem(notification, transaction.getSender().getAvatar(), view);
                }
            } else if (transaction.getStatus().equals(TRANSACTION_DONATED)) {
                notification = transaction.getSender().getFullName() + " vừa quyên góp cho bài viết của bạn";
                setNotiItem(notification, transaction.getSender().getAvatar(), view);
            }
        } else if (c.getClass() == Relationship.class) {
            final Relationship relationship = (Relationship) c;
            ImageView imgProfileUser = (ImageView) view.findViewById(R.id.imgProfileUser);
            TextView txtNameUser = (TextView) view.findViewById(R.id.txtNameUser);
            TextView txtAddressUser = (TextView) view.findViewById(R.id.txtAddressUser);
            Button btnAcceptFriend = (Button) view.findViewById(R.id.btnAcceptFriend);
            Button btnDeclineFriend = (Button) view.findViewById(R.id.btnDeclineFriend);
            btnAcceptFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> bodyAccept = new HashMap<String, String>();
                    bodyAccept.put("id", String.valueOf(relationship.getId()));
                    rmaAPIService.acceptFriend(authorization, bodyAccept).enqueue(new Callback<ExffMessage>() {
                        @Override
                        public void onResponse(Call<ExffMessage> call, Response<ExffMessage> response) {
                            if (response.isSuccessful()) {
                                ExffMessage message = response.body();
                                notifications.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), message.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ExffMessage> call, Throwable t) {

                        }
                    });
                }
            });

            btnDeclineFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rmaAPIService.cancelFriendRequest(authorization, relationship.getId()).enqueue(new Callback<ExffMessage>() {
                        @Override
                        public void onResponse(Call<ExffMessage> call, Response<ExffMessage> response) {
                            if (response.isSuccessful()) {
                                ExffMessage message = response.body();
                                notifications.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), message.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ExffMessage> call, Throwable t) {

                            Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });

            txtNameUser.setText(relationship.getSender().getFullName());
            txtAddressUser.setText("");
            Picasso.with(context).load(relationship.getSender().getAvatar())
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(imgProfileUser);
        } else if (c.getClass() == NotiTransaction.class) {
            NotiTransaction notiItem = (NotiTransaction) c;

            final ImageView imgSender = (ImageView) view.findViewById(R.id.imgSender);
            final TextView txtNotification = (TextView) view.findViewById(R.id.txtNotification);

            Picasso.with(context).load(notiItem.getUsers().get(0).getAvatar())
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(imgSender);

            switch (notiItem.getNotification().getNotiType()) {
                case USER_ACCEPTED_TRADE_MESSAGE:
                    txtNotification.setText(notiItem.getUsers().get(0).getFullName() + " " + context.getString(R.string.user_accepted_trade));
                    break;
                case USER_CANCELED_TRADE_CONFIRM_MESSAGE:
                    txtNotification.setText(notiItem.getUsers().get(0).getFullName() + " " + context.getString(R.string.user_canceled_confirm_trade));
                    break;
                case USER_RESET_TRADE_MESSAGE:
                    txtNotification.setText("Phòng của bạn và " + notiItem.getUsers().get(0).getFullName() + " " + context.getString(R.string.user_reseted_trade));
                    break;
                case TRADE_DONE_MESSAGE:
                    txtNotification.setText(context.getString(R.string.trade_done) + "giữa bạn và " + notiItem.getUsers().get(0).getFullName());
                    break;
                case USER_ADDED_ITEM_MESSAGE:
                    setNotiByUserIdAndItemId(notiItem.getNotification().getMsg(), context.getString(R.string.user_added_item), txtNotification, notiItem.getUsers().get(0).getFullName());
                    break;
                case USER_REMOVED_ITEM_MESSAGE:
                    setNotiByUserIdAndItemId(notiItem.getNotification().getMsg(), context.getString(R.string.user_removed_item), txtNotification, notiItem.getUsers().get(0).getFullName());
                    break;
            }
        }
        return view;
    }

    private void setNotiItem(String notification, String avatar, View view) {
        ImageView imgSender = (ImageView) view.findViewById(R.id.imgSender);
        TextView txtNotification = (TextView) view.findViewById(R.id.txtNotification);
        txtNotification.setText(notification);
        Picasso.with(context).load(avatar)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(imgSender);
    }

    private void setNotiByUserIdAndItemId(String itemId, final String content, final TextView txtNotification, final String yourName) {
        final int tmpItemId = Integer.parseInt(itemId);

        SharedPreferences sharedPreferences = context.getSharedPreferences("localData", MODE_PRIVATE);
        String authorization = sharedPreferences.getString("authorization", null);
        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();

        if (authorization != null) {
            rmaAPIService.getItemById(authorization, tmpItemId).enqueue(new Callback<Item>() {
                @Override
                public void onResponse(Call<Item> call, Response<Item> response) {
                    if (response.body() != null) {
                        String noti = response.body().getName() + " " + content + " trong phòng của bạn và " + yourName;
                        txtNotification.setText(noti);
                        Log.i("getItem", response.body().toString());
                    } else {
                        Log.i("getItem", "null");
                    }
                }

                @Override
                public void onFailure(Call<Item> call, Throwable t) {
                    Log.i("getItem", "cannot connect");
                }
            });
        }
    }
}