package com.project.capstone.exchangesystem.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.TradeRealtimeActivity;
import com.project.capstone.exchangesystem.adapter.MessageAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Message;
import com.project.capstone.exchangesystem.model.Room;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.sockets.SocketServer;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.project.capstone.exchangesystem.constants.AppApi.WEBSERVER;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessengerTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessengerTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessengerTabFragment extends Fragment {

    public ListView lvMessageList;
    EditText edtMessage;
    ImageButton btnSend;

    RmaAPIService rmaRealtimeService;
    SharedPreferences sharedPreferences;

    SocketServer socketServer;

    TradeRealtimeActivity tradeRealtimeActivity;

    int myUserId, yourUserId;
    String authorization, roomName;
    public List<Message> messages, tmpMessage;
    public MessageAdapter messageAdapter;

    Room room;
    Item item;

    InputMethodManager inputMethodManager;

    private OnFragmentInteractionListener mListener;

    public MessengerTabFragment() {
        // Required empty public constructor
    }

    public static MessengerTabFragment newInstance() {
        MessengerTabFragment fragment = new MessengerTabFragment();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        messages = new ArrayList<>();
        tmpMessage = new ArrayList<>();

        tradeRealtimeActivity = (TradeRealtimeActivity) getActivity();
        sharedPreferences = getActivity().getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        item = tradeRealtimeActivity.getItem();
        room = tradeRealtimeActivity.getRoom();
        roomName = tradeRealtimeActivity.getRoomName();
        myUserId = tradeRealtimeActivity.getMyUserId();
        yourUserId = tradeRealtimeActivity.getYourUserId();

        rmaRealtimeService = RmaAPIUtils.getRealtimeService();

        socketServer = tradeRealtimeActivity.getSocketServer();

        messageAdapter = new MessageAdapter(getActivity().getApplicationContext(), messages, myUserId);
        socketServer.mSocket.on("send-msg", sendMsg);


//        if (item != null){
//            yourUserId = item.getUser().getId();
//            roomName = "" + myUserId + "-" + yourUserId;
//
//        } else {
//            room = (Room)getActivity().getIntent().getSerializableExtra("room");
//            roomName = room.getRoom();
//            for (int i = 0; i < room.getUsers().size(); i++){
//                if (room.getUsers().get(i).getUserId() != myUserId){
//                    yourUserId = room.getUsers().get(i).getUserId();
//                    break;
//                }
//            }
//        }
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("room", roomName);
//            jsonObject.put("userA", myUserId);
//            jsonObject.put("userB", yourUserId);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        socketServer.emitRoom(jsonObject);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messenger_tab, container, false);
        getComponents(view);
        socketServer.setMessengerTabFragment(this);


        lvMessageList.setAdapter(messageAdapter);

        loadRoomData();

        return view;
    }

    private void getComponents(final View view) {
        lvMessageList = view.findViewById(R.id.lvMessageList);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSend = view.findViewById(R.id.btnSend);

        loadRoomData();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMsg = edtMessage.getText().toString();
                if (newMsg.trim().length() > 0){
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("room", room.getRoom());
                        jsonObject.put("sender", "" + myUserId);
                        jsonObject.put("msg", newMsg);

                        socketServer.emitMsg(jsonObject);
                        edtMessage.setText("");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getActivity().getApplicationContext(), "sent", Toast.LENGTH_SHORT).show();
                }
//                sendTradeConfirmNoti();
            }
        });

        edtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    hideKeyboard(v);
                }
            }
        });
    }

    private void loadRoomData() {

        if (room == null){
            if (authorization != null){
                rmaRealtimeService.loadRoom(roomName).enqueue(new Callback<Room>() {
                    @Override
                    public void onResponse(Call<Room> call, Response<Room> response) {
                        if (response.body() != null){
                            room = response.body();
                        } else {
                            Log.i("loadRoomMsg", "null");
                        }
                    }

                    @Override
                    public void onFailure(Call<Room> call, Throwable t) {
                        Log.i("loadRoomMsg", t.getMessage());
                    }
                });
            }
        }

        tmpMessage = room.getMessages();
        messages.clear();
        messages.addAll(tmpMessage);
        messageAdapter.notifyDataSetChanged();


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

    private void hideKeyboard(View view) {
        inputMethodManager =(InputMethodManager) tradeRealtimeActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private Emitter.Listener sendMsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i("sendMsg", args[0].toString());
            JSONObject jsonObject = (JSONObject) args[0];
            try {
                String newMsg = jsonObject.getString("msg");
                String sender = jsonObject.getString("sender");
                Message newMessage = new Message(sender, newMsg);

                messages.add(newMessage);

                getActivity().runOnUiThread(new Runnable() {
//                tradeRealtimeActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.notifyDataSetChanged();
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
