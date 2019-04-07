package com.project.capstone.exchangesystem.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.ChooseItemActivity;
import com.project.capstone.exchangesystem.activity.TradeRealtimeActivity;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Room;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TradeTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TradeTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TradeTabFragment extends Fragment {

    private static final String MY_ITEM_TAG = "itemMeIdList";
    private static final String YOUR_ITEM_TAG = "itemYouIdList";

    SharedPreferences sharedPreferences;
    public RmaAPIService rmaAPIService, rmaRealtimeService;

    RecyclerView rvMyItems, rvYourItems;
    ImageButton btnChooseMyItems, btnChooseYourItems;
    Button btnSendRequest, btnCancel;

    public ItemAdapter myItemAdapter, yourItemAdapter;

    public int myUserId, yourUserId;
    public ArrayList<Item> myItems, yourItems, tmpMyItems, tmpYourItems;
    ArrayList<String> myItemIds, yourItemIds;


    public String authorization;
    String roomName, removedTag;
    View view;
    Room room;
    Boolean hasConnection = false;
    Item tmpItem, item;

    SocketServer socketServer;

    TradeRealtimeActivity tradeRealtimeActivity;

    private OnFragmentInteractionListener mListener;

    public TradeTabFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TradeTabFragment newInstance(String param1, String param2) {
        TradeTabFragment fragment = new TradeTabFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tradeRealtimeActivity = (TradeRealtimeActivity) getActivity();
        sharedPreferences = tradeRealtimeActivity.sharedPreferences;
        authorization = tradeRealtimeActivity.authorization;
        item = tradeRealtimeActivity.item;
        roomName = tradeRealtimeActivity.roomName;
        room = tradeRealtimeActivity.room;
        myUserId = tradeRealtimeActivity.myUserId;
        yourUserId = tradeRealtimeActivity.yourUserId;

        rmaRealtimeService = RmaAPIUtils.getRealtimeService();
        rmaAPIService = RmaAPIUtils.getAPIService();

        myItems = tradeRealtimeActivity.myItems;
        yourItems = tradeRealtimeActivity.yourItems;

        myItemIds = new ArrayList<>();
        yourItemIds = new ArrayList<>();
        tmpMyItems = new ArrayList<>();
        tmpYourItems = new ArrayList<>();

        myItemAdapter = tradeRealtimeActivity.myItemAdapter;
        yourItemAdapter = tradeRealtimeActivity.yourItemAdapter;

        socketServer = tradeRealtimeActivity.socketServer;
        socketServer.onAddItem();
        socketServer.onRemoveItem();


//        item = (Item) getActivity().getIntent().getSerializableExtra("descriptionItem");



    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_trade_tab, container, false);
        socketServer.setTradeTabFragment(this);
        getComponents(view);

//        socketServer.onRemoveItem();

//        if (!savedInstanceState.getBoolean("hasConnection")) {

//        }

//        hasConnection = true;

        setItemAdapter(view, MY_ITEM_TAG);
        setItemAdapter(view, YOUR_ITEM_TAG);

        loadRoomData();

        return view;
    }

    private void getItemById(int tmpItemId) {
        if (authorization != null) {
            rmaAPIService.getItemById(authorization, tmpItemId).enqueue(new Callback<Item>() {
                @Override
                public void onResponse(Call<Item> call, Response<Item> response) {
                    if (response.body() != null) {
                        tmpItem = response.body();
                        if (tmpItem.getUser().getId() == myUserId){
                            tmpMyItems.add(tmpItem);
                            myItemAdapter.setfilter(tmpMyItems);
                        } else {
                            tmpYourItems.add(tmpItem);
                            yourItemAdapter.setfilter(tmpYourItems);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Item> call, Throwable t) {
                    Log.i("getItemById", t.getMessage());
                }
            });
        }
    }

    private void loadRoomData() {
        if (item != null){
            JSONObject tradeInfo = new JSONObject();
            try {
                tradeInfo.put("userId", "" + item.getUser().getId());
                tradeInfo.put("itemId", "" + item.getId());
                tradeInfo.put("room", roomName);
                socketServer.emitAddItem(tradeInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            for (int i = 0; i < room.getUsers().size(); i++) {
                List<String> tmpItemIds = room.getUsers().get(i).getItem();
                if (tmpItemIds.size() > 0) {
                    for (int j = 0; j < room.getUsers().get(i).getItem().size(); j++) {
                        int tmpItemId = Integer.parseInt(room.getUsers().get(i).getItem().get(j));
                        getItemById(tmpItemId);
                    }
                }
            }
        }

//        if (room == null){
//            if (authorization != null){
//                rmaRealtimeService.loadRoom(roomName).enqueue(new Callback<Room>() {
//                    @Override
//                    public void onResponse(Call<Room> call, Response<Room> response) {
//                        if (response.body() != null){
//                            room = response.body();
//                        } else {
//                            Log.i("loadRoomTrade", "null");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<Room> call, Throwable t) {
//                        Log.i("loadRoomTrade", t.getMessage());
//                    }
//                });
//            }
//        }
//
//        final boolean[] checkItem = {false};
//        for (int i = 0; i < room.getUsers().size(); i++) {
//            List<String> tmpItemIds = room.getUsers().get(i).getItem();
//            if (tmpItemIds.size() > 0) {
//                checkItem[0] = true;
//                for (int j = 0; j < room.getUsers().get(i).getItem().size(); j++) {
//                    int tmpItemId = Integer.parseInt(room.getUsers().get(i).getItem().get(j));
//                    getItemById(tmpItemId);
//                }
//            }
//        }
//
//        if (!checkItem[0] && item != null){
//
//            JSONObject tradeInfo = new JSONObject();
//            try {
//                tradeInfo.put("userId", "" + item.getUser().getId());
//                tradeInfo.put("itemId", "" + item.getId());
//                tradeInfo.put("room", roomName);
//                socketServer.emitAddItem(tradeInfo);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private void setItemAdapter(View view, String itemTag) {
        if (itemTag.equals(MY_ITEM_TAG)) {

            rvMyItems.setHasFixedSize(true);
            rvMyItems.setLayoutManager(new GridLayoutManager(getContext(), 2));
            rvMyItems.setAdapter(myItemAdapter);
        } else {

            rvYourItems.setHasFixedSize(true);
            rvYourItems.setLayoutManager(new GridLayoutManager(getContext(), 2));
            rvYourItems.setAdapter(yourItemAdapter);
        }
    }

    private void getComponents(View view) {
        rvMyItems = view.findViewById(R.id.rvMyItems);
        rvYourItems = view.findViewById(R.id.rvYourItems);
        btnChooseMyItems = view.findViewById(R.id.btnChooseMyItems);
        btnChooseYourItems = view.findViewById(R.id.btnChooseYourItems);
        btnSendRequest = view.findViewById(R.id.btnSendRequest);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnChooseMyItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseItems(myItems, myUserId, MY_ITEM_TAG);
            }
        });

        btnChooseYourItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseItems(yourItems, yourUserId, YOUR_ITEM_TAG);
            }
        });

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "len m", Toast.LENGTH_SHORT).show();
                JSONObject data = new JSONObject();
                try {
                    data.put("room", roomName);
                    data.put("userId", myUserId);
                    data.put("token", authorization);
                    socketServer.emitTradeConfirm(data);

                    //TODO check finshed or not
                    btnChooseMyItems.setEnabled(false);
                    btnChooseYourItems.setEnabled(false);
                    rvMyItems.setEnabled(false);
                    rvYourItems.setEnabled(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getActivity().getApplicationContext(), "traded", Toast.LENGTH_SHORT).show();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void chooseItems(ArrayList<Item> itemList, int userId, String itemIdTag) {
        ArrayList<Integer> selectedItemIdsStr = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            selectedItemIdsStr.add(itemList.get(i).getId());
        }
        Intent intent = new Intent(getContext(), ChooseItemActivity.class);
        intent.putExtra("id", userId);
        intent.putExtra(itemIdTag, selectedItemIdsStr);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getComponents(view);
        if (requestCode == 2) {
            Bundle bundle = data.getExtras();
            int tmpId = (int) bundle.getSerializable("tempID");
            if (tmpId == myUserId) {
                myItems = (ArrayList<Item>) bundle.getSerializable("LISTCHOOSE");

                if (myItems.size() > 0) {
                    for (int i = 0; i < myItems.size(); i++){
                        JSONObject tradeInfo = new JSONObject();
                        try {
                            tradeInfo.put("userId", myItems.get(i).getUser().getId());
                            tradeInfo.put("itemId", myItems.get(i).getId());
                            tradeInfo.put("room", roomName);
                            socketServer.emitAddItem(tradeInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else if (tmpId == yourUserId) {
                yourItems = (ArrayList<Item>) bundle.getSerializable("LISTCHOOSE");

                if (yourItems.size() > 0) {
                    for (int i = 0; i < yourItems.size(); i++){
                        JSONObject tradeInfo = new JSONObject();
                        try {
                            tradeInfo.put("userId", yourItems.get(i).getUser().getId());
                            tradeInfo.put("itemId", yourItems.get(i).getId());
                            tradeInfo.put("room", roomName);
                            socketServer.emitAddItem(tradeInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
//                yourItemAdapter.setfilter(yourItems);
            }
        }
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hasConnection", hasConnection);
    }

}
