package com.project.capstone.exchangesystem.sockets;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ShareActionProvider;

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
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
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

    private String authorization;
    private RmaAPIService rmaRealtimeService = RmaAPIUtils.getRealtimeService();
    private RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();

    private Fragment fragment;
    private TradeTabFragment tradeTabFragment;
    private MessengerTabFragment messengerTabFragment;
    private Activity activity;

    public Room room;

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

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

    public void emitTradeReset(JSONObject data){
        mSocket.emit("reset-trade", data);
    }

    public void emitTradeUnconfirm(JSONObject data){
        mSocket.emit("unconfirm-trade", data);
    }

    public void emitQRCode(JSONObject data){
        mSocket.emit("qr-scan", data);
    }

}
