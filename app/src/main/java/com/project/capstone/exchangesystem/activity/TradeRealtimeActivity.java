package com.project.capstone.exchangesystem.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import android.content.ClipData;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.adapter.TradePagerAdapter;
import com.project.capstone.exchangesystem.fragment.ImageOptionDialog;
import com.project.capstone.exchangesystem.fragment.TradeTabFragment;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Room;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.sockets.SocketServer;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.project.capstone.exchangesystem.constants.AppStatus.DELETE_IMAGE_OPTION;
import static com.project.capstone.exchangesystem.constants.AppStatus.DONATE_ACTIVITY_IMAGE_FLAG;

public class TradeRealtimeActivity extends AppCompatActivity implements ImageOptionDialog.ImageOptionListener {

    private static final String MY_ITEM_TAG = "itemMeIdList";
    private static final String YOUR_ITEM_TAG = "itemYouIdList";

    public SharedPreferences sharedPreferences;
    public RmaAPIService rmaRealtimeService;
    public SocketServer socketServer;

    public Item item, tmpItem;
    public Room room;

    public int myUserId, yourUserId;
    public String roomName = "", authorization = "", removedTag = "";

    public ItemAdapter myItemAdapter, yourItemAdapter;
    public ArrayList<Item> myItems, yourItems;

    TradeTabFragment tradeTabFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_realtime);

        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        myUserId = sharedPreferences.getInt("userId", 0);
        item = (Item) getIntent().getSerializableExtra("descriptionItem");

        rmaRealtimeService = RmaAPIUtils.getRealtimeService();

        socketServer = new SocketServer();
        socketServer.connect();
        socketServer.setActivity(this);
        socketServer.onRoom();

        myItems = new ArrayList<>();
        yourItems = new ArrayList<>();

        myItemAdapter = new ItemAdapter(getApplicationContext(), myItems, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                tmpItem = item;
                removedTag = MY_ITEM_TAG;
                ImageOptionDialog optionDialog = new ImageOptionDialog();
                optionDialog.setActivityFlag(DONATE_ACTIVITY_IMAGE_FLAG);
                optionDialog.show(getSupportFragmentManager(), "optionDialog");
            }
        });

        yourItemAdapter = new ItemAdapter(getApplicationContext(), yourItems, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                tmpItem = item;
                removedTag = YOUR_ITEM_TAG;
                ImageOptionDialog optionDialog = new ImageOptionDialog();
                optionDialog.setActivityFlag(DONATE_ACTIVITY_IMAGE_FLAG);
                optionDialog.show(getSupportFragmentManager(), "optionDialog");
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

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room", roomName);
            jsonObject.put("userA", "" + myUserId);
            jsonObject.put("userB", "" + yourUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socketServer.emitRoom(jsonObject);

//        if (room != null){
//            ViewPager viewPager = findViewById(R.id.vpPager);
//            TradePagerAdapter tradePagerAdapter = new TradePagerAdapter(getSupportFragmentManager());
//            viewPager.setAdapter(tradePagerAdapter);
//
//            TabLayout tabLayout = findViewById(R.id.tabBar);
//            tabLayout.setupWithViewPager(viewPager);
//            tabLayout.getTabAt(0).setIcon(R.drawable.round_cached_black_36);
//            tabLayout.getTabAt(1).setIcon(R.drawable.round_forum_black_36);
//        }
    }

    @Override
    public void onButtonClicked(int choice) {
        if (choice == DELETE_IMAGE_OPTION) {

            JSONObject tradeInfo = new JSONObject();
            try {
                tradeInfo.put("userId", "" + tmpItem.getUser().getId());
                tradeInfo.put("itemId", "" + tmpItem.getId());
                tradeInfo.put("room", roomName);

                socketServer.emitRemoveItem(tradeInfo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
