package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.Room;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    Context context;
    ArrayList<Room> rooms;
    private final OnItemClickListener listener;

    public RoomAdapter(Context context, ArrayList<Room> rooms, OnItemClickListener listener) {
        this.context = context;
        this.rooms = rooms;
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
        public TextView txtRoomName;

        public ViewHolder(@NonNull View view) {
            super(view);
            txtRoomName = view.findViewById(R.id.txtRoomName);
        }

        public void bind(final Room room, final OnItemClickListener listener) {
            txtRoomName.setText(room.getRoom());
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
