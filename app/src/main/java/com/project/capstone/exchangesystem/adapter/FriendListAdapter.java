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
import com.project.capstone.exchangesystem.activity.UserProfileActivity;
import com.project.capstone.exchangesystem.model.ExffMessage;
import com.project.capstone.exchangesystem.model.Relationship;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class FriendListAdapter extends BaseAdapter {
    Context context;
    ArrayList<Relationship> userArrayList;

    public FriendListAdapter(Context context, ArrayList<Relationship> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    public class ViewHolder {
        public ImageView imgUser;
        public TextView txtNameFriend, txtPhoneNumber;
        public Button btnUnfriend;
    }

    @Override
    public int getCount() {
        return userArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return userArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SharedPreferences sharedPreferences = ((Activity) context).getSharedPreferences("localData", MODE_PRIVATE);
        final String authorization = sharedPreferences.getString("authorization", null);
        final int userID = sharedPreferences.getInt("userId", 0);
        final RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        FriendListAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new FriendListAdapter.ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.friend_list_feed_item, null);
            viewHolder.txtNameFriend = (TextView) convertView.findViewById(R.id.txtNameFriend);
            viewHolder.txtPhoneNumber = (TextView) convertView.findViewById(R.id.txtPhoneNumber);
            viewHolder.btnUnfriend = (Button) convertView.findViewById(R.id.btnUnfriend);
            viewHolder.imgUser = (ImageView) convertView.findViewById(R.id.imgUser);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (FriendListAdapter.ViewHolder) convertView.getTag();
        }
        final Relationship relationshipItem = (Relationship) getItem(position);
        User userItem = new User();
        if (relationshipItem.getSenderId() == userID) {
            userItem = relationshipItem.getReceiver();
        } else {
            userItem = relationshipItem.getSender();
        }


        viewHolder.txtNameFriend.setText(userItem.getFullName());
        viewHolder.txtPhoneNumber.setText(userItem.getPhone());
        viewHolder.btnUnfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> bodyUnfriend = new HashMap<String, String>();
                bodyUnfriend.put("id", String.valueOf(relationshipItem.getId()));
                System.out.println("test " + relationshipItem.getId());

                rmaAPIService.unfriend(authorization, bodyUnfriend).enqueue(new Callback<ExffMessage>() {
                    @Override
                    public void onResponse(Call<ExffMessage> call, Response<ExffMessage> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            userArrayList.remove(position);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<ExffMessage> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        Picasso.with(context).load(userItem.getAvatar())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(viewHolder.imgUser);
        final User finalUserItem = userItem;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("friendDetail", finalUserItem);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
