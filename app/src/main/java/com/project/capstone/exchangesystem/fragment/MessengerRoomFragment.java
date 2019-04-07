package com.project.capstone.exchangesystem.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.TradeRealtimeActivity;
import com.project.capstone.exchangesystem.adapter.RoomAdapter;
import com.project.capstone.exchangesystem.model.Room;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.project.capstone.exchangesystem.constants.AppApi.WEBSERVER;

public class MessengerRoomFragment extends Fragment {

    RmaAPIService rmaRealtimeService;
    SharedPreferences sharedPreferences;
    int userId;
    String authorization;
    List<String> nameRoomList;
    RecyclerView rvRoomList;
    RoomAdapter roomAdapter;
    ArrayList<Room> rooms;
    View view;

    private OnFragmentInteractionListener mListener;

    public MessengerRoomFragment() {
        // Required empty public constructor
    }

    public static MessengerRoomFragment newInstance() {
        MessengerRoomFragment fragment = new MessengerRoomFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nameRoomList = new ArrayList<>();
        sharedPreferences = getContext().getSharedPreferences("localData", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        authorization = sharedPreferences.getString("authorization", null);

        rmaRealtimeService = RmaAPIUtils.getRealtimeService();
        rooms = new ArrayList<>();

        if (authorization != null){
            rmaRealtimeService.loadRoomByUserId(userId).enqueue(new Callback<List<Room>>() {
                @Override
                public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                    if (response.body() != null){
                        Log.i("room size: ", "" + response.body().size());
                        List<Room> tmpRooms = response.body();
                        rooms.clear();
                        rooms.addAll(tmpRooms);
                        roomAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Room>> call, Throwable t) {
                    Log.i("messageTab", "failed");

                }
            });
//            mSocket.connect();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_messenger_room, container, false);
        rvRoomList = view.findViewById(R.id.rvRoomList);

        roomAdapter = new RoomAdapter(view.getContext(), rooms, new RoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Room room) {
                Intent intent = new Intent(view.getContext(), TradeRealtimeActivity.class);
                intent.putExtra("room", room);
                startActivity(intent);
            }
        });
        rvRoomList.setHasFixedSize(true);
        rvRoomList.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 1));
        rvRoomList.setAdapter(roomAdapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
