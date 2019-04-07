package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.Message;

import java.util.ArrayList;
import java.util.List;

import static com.project.capstone.exchangesystem.constants.AppStatus.RECEIVE_MSG;
import static com.project.capstone.exchangesystem.constants.AppStatus.SEND_MSG;

public class MessageAdapter extends BaseAdapter {

    Context context;
    List<Message> messages;
    int myUserId;

    public MessageAdapter (Context context, List<Message> messages, int myUserId){
        this.context = context;
        this.messages = messages;
        this.myUserId = myUserId;
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

    public class ViewHolder{
        TextView txtName, txtMessage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageAdapter.ViewHolder viewHolder = null;
        Message message = messages.get(position);

//        if (convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (myUserId == Integer.parseInt(message.getSender())){
                convertView = layoutInflater.inflate(R.layout.send_message_layout, null);
            } else {
                convertView = layoutInflater.inflate(R.layout.receive_message_layout, null);
                viewHolder.txtName = convertView.findViewById(R.id.txtName);
            }

            viewHolder.txtMessage = convertView.findViewById(R.id.txtMessage);

//            convertView.setTag(viewHolder);

//        } else {
//            viewHolder = (MessageAdapter.ViewHolder) convertView.getTag();
//        }

        viewHolder.txtMessage.setText(message.getMsg());

        if (myUserId != Integer.parseInt(message.getSender())){
            viewHolder.txtName.setText(message.getSender());
        }

        return convertView;
    }
}
