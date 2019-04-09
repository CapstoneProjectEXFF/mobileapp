package com.project.capstone.exchangesystem.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.ChooseItemActivity;
import com.project.capstone.exchangesystem.activity.TradeRealtimeActivity;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.adapter.SelectedItemAdapter;
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

import static android.content.Context.MODE_PRIVATE;

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
    LinearLayout linearTradeList, linearFinalList, linearButton;
    ImageView ivQRCode;

    public SelectedItemAdapter myItemAdapter, yourItemAdapter, myFinalItemAdapter, yourFinalItemAdapter;

    public int myUserId, yourUserId, selectedUserId;
    private ArrayList<Item> tmpMySelectedItems, tmpYourSelectedItems, myAvailableItems, yourAvailableItems;
    ArrayList<String> myItemIds, yourItemIds;

    public String authorization;
    String roomName;
    View view;
    Room room;
    Boolean checkTradeConfirm = false;
    Item item;

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

        sharedPreferences = getActivity().getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);

        tradeRealtimeActivity = (TradeRealtimeActivity) getActivity();
        item = tradeRealtimeActivity.getItem();
        roomName = tradeRealtimeActivity.getRoomName();
        room = tradeRealtimeActivity.getRoom();
        myUserId = tradeRealtimeActivity.getMyUserId();
        yourUserId = tradeRealtimeActivity.getYourUserId();

        rmaRealtimeService = RmaAPIUtils.getRealtimeService();
        rmaAPIService = RmaAPIUtils.getAPIService();

        myItemIds = new ArrayList<>();
        yourItemIds = new ArrayList<>();
        tmpMySelectedItems = new ArrayList<>();
        tmpYourSelectedItems = new ArrayList<>();
        myAvailableItems = tradeRealtimeActivity.getMyAvailableItems();
        yourAvailableItems = tradeRealtimeActivity.getYourAvailableItems();

        myItemAdapter = tradeRealtimeActivity.getMyItemAdapter();
        yourItemAdapter = tradeRealtimeActivity.getYourItemAdapter();

        socketServer = tradeRealtimeActivity.getSocketServer();
        socketServer.mSocket.on("item-added", addedItemInfo);
        socketServer.mSocket.on("item-removed", removedItemInfo);
        socketServer.mSocket.on("trade-done", tradeDoneData);
        socketServer.mSocket.on("user-accepted-trade", acceptedUser);
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
        getComponents();

        setItemAdapter(MY_ITEM_TAG, 2);
        setItemAdapter(YOUR_ITEM_TAG, 2);

        loadRoomData();

        return view;
    }

    public void loadRoomData() {

        for (int i = 0; i < room.getUsers().size(); i++) {
            List<String> tmpItemIds = room.getUsers().get(i).getItem();
            int tmpUserId = room.getUsers().get(i).getUserId();

            if (tmpItemIds.size() > 0) {
                for (int j = 0; j < room.getUsers().get(i).getItem().size(); j++) {
                    int tmpItemId = Integer.parseInt(room.getUsers().get(i).getItem().get(j));

                    if (tmpUserId == myUserId) {
                        int pos = 0;
                        while (pos < myAvailableItems.size()) {
                            if (myAvailableItems.get(pos).getId() == tmpItemId) {
                                Item tmpItem = myAvailableItems.get(pos);
                                tmpMySelectedItems.add(tmpItem);
                                myAvailableItems.remove(tmpItem);
                                myItemAdapter.setfilter(tmpMySelectedItems);
                            } else {
                                pos++;
                            }
                        }
                    } else {
                        int pos = 0;
                        boolean checkExistedItem = false;
                        while (pos < yourAvailableItems.size()) {
                            if (yourAvailableItems.get(pos).getId() == tmpItemId) {
                                checkExistedItem = true;
                                Item tmpItem = yourAvailableItems.get(pos);
                                tmpYourSelectedItems.add(tmpItem);
                                yourAvailableItems.remove(tmpItem);
                                yourItemAdapter.setfilter(tmpYourSelectedItems);
                            } else {
                                pos++;
                            }
                        }
                        if (!checkExistedItem) {
                            getItemById(tmpItemId);
                        }
                    }
                }
            }
        }

        //when join room by clicking on trade button
        if (item != null) {
            JSONObject tradeInfo = new JSONObject();
            try {
                tradeInfo.put("userId", "" + item.getUser().getId());
                tradeInfo.put("itemId", "" + item.getId());
                tradeInfo.put("room", roomName);
                socketServer.emitAddItem(tradeInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getItemById(int tmpItemId) {
        if (authorization != null) {
            rmaAPIService.getItemById(authorization, tmpItemId).enqueue(new Callback<Item>() {
                @Override
                public void onResponse(Call<Item> call, Response<Item> response) {
                    if (response.body() != null) {
                        Item tmpItem = response.body();
//                        if (!checkInSelectedItems(tmpYourSelectedItems, tmpItem)){
                            tmpItem.setCheckPrivacy(true);
                            tmpYourSelectedItems.add(tmpItem);
                            yourItemAdapter.setfilter(tmpYourSelectedItems);
//                        }
                    }
                }

                @Override
                public void onFailure(Call<Item> call, Throwable t) {
                    Log.i("getItemById", "" + t.getMessage());
                }
            });
        }
    }

    private void setItemAdapter(String itemTag, int spanCount) {
        if (itemTag.equals(MY_ITEM_TAG)) {
            rvMyItems.setHasFixedSize(true);
            rvMyItems.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
            rvMyItems.setAdapter(myItemAdapter);
        } else {
            rvYourItems.setHasFixedSize(true);
            rvYourItems.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
            rvYourItems.setAdapter(yourItemAdapter);
        }
    }

    private void getComponents() {
        rvMyItems = view.findViewById(R.id.rvMyItems);
        rvYourItems = view.findViewById(R.id.rvYourItems);
        btnChooseMyItems = view.findViewById(R.id.btnChooseMyItems);
        btnChooseYourItems = view.findViewById(R.id.btnChooseYourItems);
        btnSendRequest = view.findViewById(R.id.btnSendRequest);
        btnCancel = view.findViewById(R.id.btnCancel);
        linearTradeList = view.findViewById(R.id.linearTradeList);
        linearFinalList = view.findViewById(R.id.linearFinalList);
        linearButton = view.findViewById(R.id.linearButton);
        ivQRCode = view.findViewById(R.id.ivQRCode);

        btnChooseMyItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseItems(tmpMySelectedItems, myUserId, myAvailableItems);
            }
        });

        btnChooseYourItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseItems(tmpYourSelectedItems, yourUserId, yourAvailableItems);
            }
        });

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTradeConfirm = true;
                JSONObject data = new JSONObject();
                try {
                    data.put("room", roomName);
                    data.put("userId", myUserId);
                    data.put("token", authorization);
                    socketServer.emitTradeConfirm(data);

                    linearTradeList.setVisibility(View.GONE);

                    rvYourItems = view.findViewById(R.id.rvYourFinalItems);
                    rvMyItems = view.findViewById(R.id.rvMyFinalItems);

                    myItemAdapter = tradeRealtimeActivity.getMyFinalItemAdapter();
                    yourItemAdapter = tradeRealtimeActivity.getYourFinalItemAdapter();

                    setItemAdapter(MY_ITEM_TAG, 1);
                    setItemAdapter(YOUR_ITEM_TAG, 1);

                    linearFinalList.setVisibility(View.VISIBLE);
                    btnSendRequest.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTradeConfirm) {
                    //TODO remove all selected item
                    Toast.makeText(getActivity().getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
                } else {
                    checkTradeConfirm = false;

                    //TODO cancel trade confirm
                    Toast.makeText(getActivity().getApplicationContext(), "bye", Toast.LENGTH_SHORT).show();

                    linearFinalList.setVisibility(View.GONE);

                    rvYourItems = view.findViewById(R.id.rvYourItems);
                    rvMyItems = view.findViewById(R.id.rvMyItems);

                    myItemAdapter = tradeRealtimeActivity.getMyItemAdapter();
                    yourItemAdapter = tradeRealtimeActivity.getYourItemAdapter();

                    setItemAdapter(MY_ITEM_TAG, 2);
                    setItemAdapter(YOUR_ITEM_TAG, 2);

                    linearTradeList.setVisibility(View.VISIBLE);
                    btnSendRequest.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void chooseItems(ArrayList<Item> itemList, int userId, ArrayList<Item> availableItems) {
        selectedUserId = userId;
        Intent intent = new Intent(getContext(), ChooseItemActivity.class);
        intent.putExtra("availableItems", availableItems);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getComponents();
        if (requestCode == 2) {
            //selected items but not add to recycleview yet
            if (data != null) {
                Bundle bundle = data.getExtras();
                ArrayList<Item> tmpSelectedItems = (ArrayList<Item>) bundle.getSerializable("LISTCHOOSE");

                if (tmpSelectedItems.size() > 0) {
                    for (int i = 0; i < tmpSelectedItems.size(); i++) {
                        JSONObject tradeInfo = new JSONObject();
                        try {
                            if (selectedUserId == myUserId) {
                                tradeInfo.put("userId", myUserId);
                            } else {
                                tradeInfo.put("userId", yourUserId);
                            }
                            tradeInfo.put("itemId", tmpSelectedItems.get(i).getId());
                            tradeInfo.put("room", roomName);
                            socketServer.emitAddItem(tradeInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
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

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean("hasConnection", hasConnection);
//    }

    public ArrayList<Item> getMyAvailableItems() {
        return myAvailableItems;
    }

    public ArrayList<Item> getYourAvailableItems() {
        return yourAvailableItems;
    }

    public ArrayList<Item> getTmpMySelectedItems() {
        return tmpMySelectedItems;
    }

    public void setTmpMySelectedItems(ArrayList<Item> tmpMySelectedItems) {
        this.tmpMySelectedItems = tmpMySelectedItems;
    }

    public ArrayList<Item> getTmpYourSelectedItems() {
        return tmpYourSelectedItems;
    }

    public void setTmpYourSelectedItems(ArrayList<Item> tmpYourSelectedItems) {
        this.tmpYourSelectedItems = tmpYourSelectedItems;
    }

    public SelectedItemAdapter getMyItemAdapter() {
        return myItemAdapter;
    }

    public SelectedItemAdapter getYourItemAdapter() {
        return yourItemAdapter;
    }

    public int getYourUserId() {
        return yourUserId;
    }

    public void createQRCode(String qrCode){
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCode, BarcodeFormat.QR_CODE, 500, 500);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            linearTradeList.setVisibility(View.GONE);
            linearFinalList.setVisibility(View.GONE);
            linearButton.setVisibility(View.GONE);
            ivQRCode.setVisibility(View.VISIBLE);
            ivQRCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener acceptedUser = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String userId = args[0].toString();
            Log.i("acceptedUser", userId);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = new JSONObject();
                    try {
//                String message = "" + userId + getActivity().getString(R.string.trade_done);
                        String message = userId + " đã chốt.";
                        jsonObject.put("room", room.getRoom());
                        jsonObject.put("sender", userId);
                        jsonObject.put("msg", message);
                        Toast.makeText(getActivity().getApplicationContext(), "aaaaaa", Toast.LENGTH_SHORT).show();
                        socketServer.emitMsg(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    };

    private Emitter.Listener tradeDoneData = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("tradeDoneData", "alo1234");
            final String roomName = args[0].toString();
            Log.i("tradeDoneData", roomName);

//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (authorization != null) {
//                        rmaRealtimeService.loadRoom(roomName).enqueue(new Callback<Room>() {
//                            @Override
//                            public void onResponse(Call<Room> call, Response<Room> response) {
//                                if (response.body() != null) {
////                                    String qrCode = response.body().getQrCode();
////                                    Log.i("tradeDone QRCode", qrCode);
////                                    createQRCode(qrCode);
//                                } else {
//                                    Log.i("tradeDoneData", "null");
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<Room> call, Throwable t) {
//                                Log.i("tradeDoneData", t.getMessage());
//                            }
//                        });
//
//
//                    }
//                }
//            });
        }
    };

    private Emitter.Listener removedItemInfo = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("removedItemInfo", args[0].toString());
            JSONObject itemInfo = (JSONObject) args[0];
            try {
                final int itemId = Integer.parseInt(itemInfo.getString("itemId"));
                final int userId = Integer.parseInt(itemInfo.getString("userId"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("removedItemInfoaaa", "dooooooo");

                        if (userId == myUserId) {
                            for (int i = 0; i < tmpMySelectedItems.size(); i++) {
                                if (itemId == tmpMySelectedItems.get(i).getId()) {
                                    Item tmpItem = tmpMySelectedItems.get(i);
                                    tmpMySelectedItems.remove(tmpItem);
                                    Log.i("removedMyItem", "" + tmpItem.getId());
                                    getMyItemAdapter().setfilter(tmpMySelectedItems);
                                    myAvailableItems.add(tmpItem);
                                }
                            }
                        } else {
                            for (int i = 0; i < tmpYourSelectedItems.size(); i++) {
                                if (itemId == tmpYourSelectedItems.get(i).getId()) {
                                    Item tmpItem = tmpYourSelectedItems.get(i);
                                    tmpYourSelectedItems.remove(tmpItem);
                                    getYourItemAdapter().setfilter(tmpYourSelectedItems);
                                    if (!tmpItem.isCheckPrivacy()) {
                                        yourAvailableItems.add(tmpItem);
                                    }
                                }
                            }
                        }

                        Log.i("removedItemInfoaaa", "raaaaaaaa");
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener addedItemInfo = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i("addedItemInfo", args[0].toString());
            JSONObject itemInfo = (JSONObject) args[0];
            try {
                final int itemId = Integer.parseInt(itemInfo.getString("itemId"));
                final int userId = Integer.parseInt(itemInfo.getString("userId"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (userId == myUserId) {
                            for (int i = 0; i < myAvailableItems.size(); i++) {
                                Item tmpItem = myAvailableItems.get(i);

                                if (itemId == tmpItem.getId()) {
                                    //check selected item existed in recyleview or not
                                    if (!checkInSelectedItems(tmpMySelectedItems, tmpItem)){
                                        tmpMySelectedItems.add(myAvailableItems.get(i));
                                        getMyItemAdapter().setfilter(tmpMySelectedItems);
                                        myAvailableItems.remove(tmpItem);
                                    }
                                }
                            }
                        } else {
                            boolean checkExistedItem = false;
                            for (int i = 0; i < yourAvailableItems.size(); i++) {
                                Item tmpItem = yourAvailableItems.get(i);
                                if (itemId == tmpItem.getId()) {
                                    //check selected item existed in recyleview or not
                                    if (!checkInSelectedItems(tmpYourSelectedItems, tmpItem)) {
                                        checkExistedItem = true;
                                        tmpYourSelectedItems.add(yourAvailableItems.get(i));
                                        getYourItemAdapter().setfilter(tmpYourSelectedItems);
                                        yourAvailableItems.remove(tmpItem);
                                    }
                                }
                            }
                            //when user chooses owned private item to exchange with whom is not friend
                            if (!checkExistedItem) {
                                Log.i("addedItemInfo private", "" + itemId);
                                getItemById(itemId);
                            }
                        }
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private boolean checkInSelectedItems(ArrayList<Item> tmpSelectedItems, Item tmpItem) {
        for (int j = 0; j < tmpSelectedItems.size(); j++){
            if (tmpSelectedItems.get(j).getId() == tmpItem.getId()){
                return true;
            }
        }
        return false;
    }
}