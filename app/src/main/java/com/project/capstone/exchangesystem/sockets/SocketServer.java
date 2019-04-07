package com.project.capstone.exchangesystem.sockets;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.TradeRealtimeActivity;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.adapter.TradePagerAdapter;
import com.project.capstone.exchangesystem.fragment.MessengerTabFragment;
import com.project.capstone.exchangesystem.fragment.TradeTabFragment;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Message;
import com.project.capstone.exchangesystem.model.Room;
import com.project.capstone.exchangesystem.remote.RmaAPIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.project.capstone.exchangesystem.constants.AppApi.WEBSERVER;

public class SocketServer {

    private static final int ADD_ITEM = 0;
    private static final int REMOVE_ITEM = 1;

    public Socket mSocket;
    private ListView listView;


    private Fragment fragment;
    private TradeTabFragment tradeTabFragment;
    private MessengerTabFragment messengerTabFragment;
    private Activity activity;


    private TradeRealtimeActivity realtimeTradingActivity;
    public Room room;

    public TradeTabFragment getTradeTabFragment() {
        return tradeTabFragment;
    }

    public void setTradeTabFragment(TradeTabFragment tradeTabFragment) {
        this.tradeTabFragment = tradeTabFragment;
    }

    public MessengerTabFragment getMessengerTabFragment() {
        return messengerTabFragment;
    }

    public void setMessengerTabFragment(MessengerTabFragment messengerTabFragment) {
        this.messengerTabFragment = messengerTabFragment;
    }

    public SocketServer() {
        try {
            mSocket = IO.socket(WEBSERVER);
        } catch (URISyntaxException e) {
        }
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void connect() {
        mSocket.connect();
        Log.i("connect", "connected");
    }

    public void emitRoom(JSONObject roomInfo) {
        mSocket.emit("get-room", roomInfo);
    }

    public void emitMsg(JSONObject msg) {
        mSocket.emit("send-msg", msg);
    }

    public void emitAddItem(JSONObject tradeInfo) {
        mSocket.emit("add-item", tradeInfo);
    }

    public void emitRemoveItem(JSONObject tradeInfo) {
        mSocket.emit("remove-item", tradeInfo);
    }

    public void emitTradeConfirm(JSONObject data) {
        mSocket.emit("confirm-trade", data);
    }

    public void onRoom() {
        mSocket.on("room-ready", onRoomReady);
    }

    public void onMsg() {
        mSocket.on("send-msg", sendMsg);
    }

    public void onAddItem() {
        mSocket.on("item-added", addedItemInfo);
    }

    public void onRemoveItem() {
        mSocket.on("item-removed", removedItemInfo);
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

//                final MessengerTabFragment messengerTabFragment = (MessengerTabFragment) fragment;
                messengerTabFragment.messages.add(newMessage);

                messengerTabFragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messengerTabFragment.messageAdapter.notifyDataSetChanged();
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private Emitter.Listener onRoomReady = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final TradeRealtimeActivity tradeRealtimeActivity = (TradeRealtimeActivity) activity;
            Log.i("loadRoom", args[0].toString());
            final String roomName = args[0].toString();

            tradeRealtimeActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (tradeRealtimeActivity.authorization != null) {
                        tradeRealtimeActivity.rmaRealtimeService.loadRoom(roomName).enqueue(new Callback<Room>() {
                            @Override
                            public void onResponse(Call<Room> call, Response<Room> response) {
                                if (response.body() != null) {
                                    tradeRealtimeActivity.room = response.body();

                                    ViewPager viewPager = tradeRealtimeActivity.findViewById(R.id.vpPager);
                                    TradePagerAdapter tradePagerAdapter = new TradePagerAdapter(tradeRealtimeActivity.getSupportFragmentManager());
                                    viewPager.setAdapter(tradePagerAdapter);

                                    TabLayout tabLayout =  tradeRealtimeActivity.findViewById(R.id.tabBar);
                                    tabLayout.setupWithViewPager(viewPager);
                                    tabLayout.getTabAt(0).setIcon(R.drawable.round_cached_black_36);
                                    tabLayout.getTabAt(1).setIcon(R.drawable.round_forum_black_36);
                                } else {
                                    Log.i("loadRoom", "dm =.=");
                                }
                            }

                            @Override
                            public void onFailure(Call<Room> call, Throwable t) {
                                Log.i("loadRoom", t.getMessage());
                            }
                        });


                    }
                }
            });
        }
    };

    private Emitter.Listener addedItemInfo = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i("addedItemInfo", args[0].toString());
            JSONObject itemInfo = (JSONObject) args[0];
            try {
                final int itemId = Integer.parseInt(itemInfo.getString("itemId"));

//                final TradeTabFragment tradeTabFragment = (TradeTabFragment) fragment;

                tradeTabFragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getItemById(itemId, tradeTabFragment);
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
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

//                final TradeTabFragment tradeTabFragment = (TradeTabFragment) fragment;

                tradeTabFragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("removedItemInfoaaa", "dooooooo");

                        if (userId == tradeTabFragment.myUserId) {
                            for (int i = 0; i < tradeTabFragment.myItems.size(); i++) {
                                Item tmpItem = tradeTabFragment.myItems.get(i);
                                if (tmpItem.getId() == itemId) {
                                    tradeTabFragment.tmpMyItems.remove(tmpItem);
                                    tradeTabFragment.myItemAdapter.setfilter(tradeTabFragment.tmpMyItems);
                                    break;
                                }
                            }
                        } else {
                            for (int i = 0; i < tradeTabFragment.yourItems.size(); i++) {
                                Item tmpItem = tradeTabFragment.yourItems.get(i);
                                if (tmpItem.getId() == itemId) {
                                    tradeTabFragment.tmpYourItems.remove(tmpItem);
                                    tradeTabFragment.yourItemAdapter.setfilter(tradeTabFragment.tmpYourItems);
                                    break;
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

    private void getItemById(final int tmpItemId, TradeTabFragment tradeTabFragment) {
        String authorization = tradeTabFragment.authorization;
        RmaAPIService rmaAPIService = tradeTabFragment.rmaAPIService;
        final ArrayList<Item> tmpMyItems = tradeTabFragment.tmpMyItems;
        final ArrayList<Item> tmpYourItems = tradeTabFragment.tmpYourItems;
        final int myUserId = tradeTabFragment.myUserId;
        final ItemAdapter myItemAdapter = tradeTabFragment.myItemAdapter;
        final ItemAdapter yourItemAdapter = tradeTabFragment.yourItemAdapter;

        if (authorization != null) {
            rmaAPIService.getItemById(authorization, tmpItemId).enqueue(new Callback<Item>() {
                @Override
                public void onResponse(Call<Item> call, Response<Item> response) {
                    if (response.body() != null) {
                        Item tmpItem = response.body();
                        if (tmpItem.getUser().getId() == myUserId) {
                            boolean check = false;
                            for (int i = 0; i < tmpMyItems.size(); i++) {
                                if (tmpMyItems.get(i).getId() == tmpItem.getId()) {
                                    check = true;
                                    break;
                                }
                            }
                            if (!check) {
                                Log.i("added item", "aaaaaaaaaaaa");
                                tmpMyItems.add(tmpItem);
                                myItemAdapter.setfilter(tmpMyItems);
                            }

                        } else {

                            boolean check = false;
                            for (int i = 0; i < tmpYourItems.size(); i++) {
                                if (tmpYourItems.get(i).getId() == tmpItem.getId()) {
                                    check = true;
                                    break;
                                }
                            }

                            if (!check) {
                                Log.i("added item", "aaaaaaaaaaaa");
                                tmpYourItems.add(tmpItem);
                                yourItemAdapter.setfilter(tmpYourItems);
                            }
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

}
