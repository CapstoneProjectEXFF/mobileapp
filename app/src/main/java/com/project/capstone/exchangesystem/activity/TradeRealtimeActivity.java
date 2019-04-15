package com.project.capstone.exchangesystem.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.github.nkzawa.emitter.Emitter;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.SelectedItemAdapter;
import com.project.capstone.exchangesystem.adapter.TradePagerAdapter;
import com.project.capstone.exchangesystem.fragment.ImageOptionDialog;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Room;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.sockets.SocketServer;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.project.capstone.exchangesystem.constants.AppStatus.*;

public class TradeRealtimeActivity extends AppCompatActivity implements ImageOptionDialog.ImageOptionListener {

    private SharedPreferences sharedPreferences;
    private RmaAPIService rmaAPIService, rmaRealtimeService;
    private SocketServer socketServer;

    private Item item, tmpItem;
    private Room room;
    private User friendAccount;

    private int myUserId, yourUserId;
    private String roomName = "", authorization = "";

    private SelectedItemAdapter myItemAdapter, yourItemAdapter, myFinalItemAdapter, yourFinalItemAdapter;
    private ArrayList<Item> mySelectedItems, yourSelectedItems, myAvailableItems, yourAvailableItems;

    boolean checkRoom = false;

    Toolbar tbToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_realtime);

        getComponents();
        loadFriendInf(yourUserId);
        loadAvailableItems(myUserId);
        loadAvailableItems(yourUserId);

    }

    private void setToolbar() {
        tbToolbar.setTitle(friendAccount.getFullName());
        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tbToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getComponents() {
        tbToolbar = findViewById(R.id.tbToolbar);

        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        myUserId = sharedPreferences.getInt("userId", 0);
        rmaAPIService = RmaAPIUtils.getAPIService();
        rmaRealtimeService = RmaAPIUtils.getRealtimeService();

        item = (Item) getIntent().getSerializableExtra("descriptionItem");
        mySelectedItems = new ArrayList<>();
        yourSelectedItems = new ArrayList<>();

        myAvailableItems = new ArrayList<>();
        yourAvailableItems = new ArrayList<>();

        socketServer = new SocketServer();
        socketServer.connect();
        socketServer.setAuthorization(authorization);
        socketServer.setActivity(this);
        socketServer.mSocket.on("room-ready", onRoomReady);

        myItemAdapter = new SelectedItemAdapter(getApplicationContext(), mySelectedItems, new SelectedItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                tmpItem = item;
                ImageOptionDialog optionDialog = new ImageOptionDialog();
                optionDialog.setActivityFlag(DONATE_ACTIVITY_IMAGE_FLAG);
                optionDialog.show(getSupportFragmentManager(), "optionDialog");
            }
        });

        yourItemAdapter = new SelectedItemAdapter(getApplicationContext(), yourSelectedItems, new SelectedItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                tmpItem = item;
                ImageOptionDialog optionDialog = new ImageOptionDialog();
                optionDialog.setActivityFlag(DONATE_ACTIVITY_IMAGE_FLAG);
                optionDialog.show(getSupportFragmentManager(), "optionDialog");
            }
        });

        myFinalItemAdapter = new SelectedItemAdapter(getApplicationContext(), mySelectedItems, new SelectedItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

            }
        });

        yourFinalItemAdapter = new SelectedItemAdapter(getApplicationContext(), yourSelectedItems, new SelectedItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

            }
        });

        if (item != null) {
            yourUserId = item.getUser().getId();
            roomName = "" + myUserId + "-" + yourUserId;
        } else {
            room = (Room) getIntent().getSerializableExtra("room");
            roomName = room.getRoom();

            for (int i = 0; i < room.getUsers().size(); i++) {
                if (room.getUsers().get(i).getUserId() != myUserId) {
                    yourUserId = room.getUsers().get(i).getUserId();
                    break;
                }
            }
        }
    }

    private void loadFriendInf(int yourUserId) {
        if (authorization != null) {
            rmaAPIService.getUserById(yourUserId).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.body() != null){
                        friendAccount = response.body();
                        setToolbar();
                    } else {
                        Log.i("loadFriendInf", "friendAccount - null");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.i("loadFriendInf", "cannot load friendAccount");
                }
            });
        }
//        socketServer.emitRoom(jsonObject);
    }

    private void loadAvailableItems(final int userId) {

        if (authorization != null) {
            rmaAPIService.getItemsByUserIdWithPrivacy(authorization, userId).enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    Log.i("loadAvailableItems", "" + response.body().size());
                    List<Item> result = new ArrayList<>();
                    result = response.body();
                    if (result.size() > 0) {
                        List<Item> tmpAvailableItems = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++) {
                            if (result.get(i).getStatus().equals(ITEM_ENABLE)) {
                                tmpAvailableItems.add(result.get(i));
                            }
                        }
                        if (tmpAvailableItems.size() > 0) {
                            if (userId == myUserId) {
                                myAvailableItems.addAll(tmpAvailableItems);
                            } else {
                                yourAvailableItems.addAll(tmpAvailableItems);
                            }
                        }
                    }
                    if (userId == yourUserId) {
                        //emit room when finish loading your available items
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("room", roomName);
                            jsonObject.put("userA", "" + myUserId);
                            jsonObject.put("userB", "" + yourUserId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        socketServer.emitRoom(jsonObject);
                    }
                }

                @Override
                public void onFailure(Call<List<Item>> call, Throwable t) {
                    Log.i("loadAvailableItems", "" + t.getMessage());
                }
            });
        } else {
            Log.i("loadAvailableItems", "load failed");
        }
    }

    @Override
    public void onButtonClicked(int choice) {
        if (choice == DELETE_IMAGE_OPTION) {

            JSONObject tradeInfo = new JSONObject();
            try {
                int tmpUserId = tmpItem.getUser().getId();
                tradeInfo.put("userId", "" + tmpUserId);
                tradeInfo.put("itemId", "" + tmpItem.getId());
                tradeInfo.put("room", roomName);
                socketServer.emitRemoveItem(tradeInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public SocketServer getSocketServer() {
        return socketServer;
    }

    public Item getItem() {
        return item;
    }

    public Room getRoom() {
        return room;
    }

    public int getMyUserId() {
        return myUserId;
    }

    public int getYourUserId() {
        return yourUserId;
    }

    public String getRoomName() {
        return roomName;
    }

    public SelectedItemAdapter getMyItemAdapter() {
        return myItemAdapter;
    }

    public SelectedItemAdapter getYourItemAdapter() {
        return yourItemAdapter;
    }

    public ArrayList<Item> getMyAvailableItems() {
        return myAvailableItems;
    }

    public ArrayList<Item> getYourAvailableItems() {
        return yourAvailableItems;
    }

    public SelectedItemAdapter getMyFinalItemAdapter() {
        return myFinalItemAdapter;
    }

    public SelectedItemAdapter getYourFinalItemAdapter() {
        return yourFinalItemAdapter;
    }

    private Emitter.Listener onRoomReady = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("loadRoom", args[0].toString());
            final String tmpRoomName = args[0].toString();

            if (!checkRoom && tmpRoomName.equals(roomName)){
                checkRoom = true;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (authorization != null) {
                            rmaRealtimeService.loadRoom(roomName).enqueue(new Callback<Room>() {
                                @Override
                                public void onResponse(Call<Room> call, Response<Room> response) {
                                    if (response.body() != null) {
                                        room = response.body();

                                        ViewPager viewPager = findViewById(R.id.vpPager);
                                        TradePagerAdapter tradePagerAdapter = new TradePagerAdapter(getSupportFragmentManager());
                                        viewPager.setAdapter(tradePagerAdapter);

                                        TabLayout tabLayout = findViewById(R.id.tabBar);
                                        tabLayout.setupWithViewPager(viewPager);
                                        tabLayout.getTabAt(0).setIcon(R.drawable.round_cached_black_36);
                                        tabLayout.getTabAt(1).setIcon(R.drawable.round_forum_black_36);
                                    } else {
                                        Log.i("loadRoom", "null");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Room> call, Throwable t) {
                                    Log.i("loadRoom error", t.getMessage());
                                }
                            });
                        }
                    }
                });
            }
        }
    };
}
