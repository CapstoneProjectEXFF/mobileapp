package com.project.capstone.exchangesystem.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.TradeRealtimeActivity;
import com.project.capstone.exchangesystem.adapter.MessageAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Message;
import com.project.capstone.exchangesystem.model.Room;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.sockets.SocketServer;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

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
    String authorization, roomName, yourName;
    public List<Message> messages, tmpMessage;
    public MessageAdapter messageAdapter;
    User friendAccount;

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
        yourName = tradeRealtimeActivity.getYourName();
        friendAccount = tradeRealtimeActivity.getFriendAccount();

        rmaRealtimeService = RmaAPIUtils.getRealtimeService();

        socketServer = tradeRealtimeActivity.getSocketServer();

        messageAdapter = new MessageAdapter(getActivity().getApplicationContext(), messages, myUserId, friendAccount);
        socketServer.mSocket.on("send-msg", sendMsg);
        socketServer.mSocket.on("trade-change", tradeChange);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messenger_tab, container, false);
        getComponents(view);

        lvMessageList.setAdapter(messageAdapter);

        loadRoomData();

        return view;
    }

    private void getComponents(final View view) {
        lvMessageList = view.findViewById(R.id.lvMessageList);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSend = view.findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMsg = edtMessage.getText().toString();
                if (newMsg.trim().length() > 0) {
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
                }
            }
        });

        edtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    private void loadRoomData() {

        if (room == null) {
            if (authorization != null) {
                rmaRealtimeService.loadRoom(roomName).enqueue(new Callback<Room>() {
                    @Override
                    public void onResponse(Call<Room> call, Response<Room> response) {
                        if (response.body() != null) {
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
        inputMethodManager = (InputMethodManager) tradeRealtimeActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
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
                String tmpRoomName = jsonObject.getString("room");

                if (tmpRoomName.equals(roomName)) {
                    Message newMessage = new Message(sender, newMsg);

                    messages.add(newMessage);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Emitter.Listener tradeChange = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("tradeChange", args[0].toString());
//            JSONObject data = (JSONObject) args[0];
//            try {
//                String tmpRoomName = data.getString("room");
//                String notiType = "" + data.getInt("notiType");
//                String msg = data.getString("msg");
//
//                if (tmpRoomName.equals(roomName)) {
//                    Message newMessage = new Message(notiType, msg);
//
//                    messages.add(newMessage);
//
//                    if (getActivity() == null) {
//                        return;
//                    } else {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                messageAdapter.notifyDataSetChanged();
//                            }
//                        });
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    };
}