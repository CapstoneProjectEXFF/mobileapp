package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.Room;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    Context context;
    ArrayList<Room> rooms;
    int userId;
    private final OnItemClickListener listener;

    public RoomAdapter(Context context, ArrayList<Room> rooms, int userId, OnItemClickListener listener) {
        this.context = context;
        this.rooms = rooms;
        this.userId = userId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.room_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Room room = rooms.get(position);
        viewHolder.bind(room, listener);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtRoomName, txtLastMsg;
        public ImageView imgUser;

        public ViewHolder(@NonNull View view) {
            super(view);
            txtRoomName = view.findViewById(R.id.txtRoomName);
            txtLastMsg = view.findViewById(R.id.txtLastMsg);
            imgUser = view.findViewById(R.id.imgUser);
        }

        public void bind(final Room room, final OnItemClickListener listener) {

            for (int i = 0; i < room.getUsers().size(); i++){
                if (room.getUsers().get(i).getUserId() != userId){
                    txtRoomName.setText(room.getUsers().get(i).getFullName());
                    Picasso.with(context).load(room.getUsers().get(i).getAvatar())
                            .placeholder(R.drawable.user)
                            .error(R.drawable.user)
                            .into(imgUser);
                    break;
                }
            }

            if (room.getMessages().size() > 0){
                txtLastMsg.setText(room.getMessages().get(room.getMessages().size() - 1).getMsg());
            } else {
                txtLastMsg.setText("");
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(room);
                }
            });
        }


    }

    public interface OnItemClickListener {
        void onItemClick(Room room);
    }
}
