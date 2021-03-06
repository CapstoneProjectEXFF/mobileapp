package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.project.capstone.exchangesystem.R;
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

public class FriendFeedAdapter extends BaseAdapter {
    Context context;
    ArrayList<User> userList;
    boolean isSent;


    public class ViewHolder {
        public ImageView imgProfileUser;
        public TextView txtNameUser, txtAddressUser;
        public Button btnAddFriend, btnDeclineRequest;
    }

    public FriendFeedAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("localData", MODE_PRIVATE);
        final String authorization = sharedPreferences.getString("authorization", null);
        FriendFeedAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new FriendFeedAdapter.ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.friend_feed_item, null);
            viewHolder.imgProfileUser = (ImageView) convertView.findViewById(R.id.imgProfileUser);
            viewHolder.txtNameUser = (TextView) convertView.findViewById(R.id.txtNameUser);
            viewHolder.txtAddressUser = (TextView) convertView.findViewById(R.id.txtAddressUser);
            viewHolder.btnAddFriend = (Button) convertView.findViewById(R.id.btnAddFriend);
            viewHolder.btnDeclineRequest = convertView.findViewById(R.id.btnDeclineRequest);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (FriendFeedAdapter.ViewHolder) convertView.getTag();

        }
        final User user = (User) getItem(position);

        viewHolder.txtNameUser.setText(user.getFullName());
        viewHolder.txtAddressUser.setText("Vietnam");
        Picasso.with(context).load(user.getAvatar())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(viewHolder.imgProfileUser);

        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> friendRequestBody = new HashMap<String, String>();
                friendRequestBody.put("receiverId", String.valueOf(user.getId()));
                RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
                rmaAPIService.addFriend(authorization, friendRequestBody).enqueue(new Callback<Relationship>() {
                    @Override
                    public void onResponse(Call<Relationship> call, Response<Relationship> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Send Request Successfully", Toast.LENGTH_LONG).show();
                            finalViewHolder.btnAddFriend.setVisibility(View.GONE);
                            finalViewHolder.btnDeclineRequest.setVisibility(View.VISIBLE);
                        } else {

                            System.out.println(context.getString(R.string.add_friend_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<Relationship> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), context.getString(R.string.error_request), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        viewHolder.btnDeclineRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> declineRequest = new HashMap<String, String>();
                declineRequest.put("userId", String.valueOf(user.getId()));
                RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
                rmaAPIService.unfriend(authorization, declineRequest).enqueue(new Callback<ExffMessage>() {
                    @Override
                    public void onResponse(Call<ExffMessage> call, Response<ExffMessage> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Decline Request Successfully", Toast.LENGTH_LONG).show();
                            finalViewHolder.btnDeclineRequest.setVisibility(View.GONE);
                            finalViewHolder.btnAddFriend.setVisibility(View.VISIBLE);
                        } else {
                            System.out.println("Fail Decline Request");
                        }
                    }

                    @Override
                    public void onFailure(Call<ExffMessage> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error Server", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        // TODO show information
        return convertView;
    }
}
