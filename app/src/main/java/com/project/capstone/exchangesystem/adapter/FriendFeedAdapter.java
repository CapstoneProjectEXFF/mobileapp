package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendFeedAdapter extends BaseAdapter {
    Context context;
    ArrayList<User> userList;

    public class ViewHolder {
        public ImageView imgProfileUser;
        public TextView txtNameUser, txtAddressUser;
        public Button btnAddFriend;
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

        FriendFeedAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new FriendFeedAdapter.ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.friend_feed_item, null);
            viewHolder.imgProfileUser = (ImageView) convertView.findViewById(R.id.imgProfileUser);
            viewHolder.txtNameUser = (TextView) convertView.findViewById(R.id.txtNameUser);
            viewHolder.txtAddressUser = (TextView) convertView.findViewById(R.id.txtAddressUser);
            viewHolder.btnAddFriend = (Button) convertView.findViewById(R.id.btnAddFriend);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (FriendFeedAdapter.ViewHolder) convertView.getTag();

        }
        User user = (User) getItem(position);

        viewHolder.txtNameUser.setText(user.getFullName());
        viewHolder.txtAddressUser.setText("Vietnam");
        Picasso.with(context).load(user.getAvatar())
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image)
                .into(viewHolder.imgProfileUser);

        return convertView;
    }


}
