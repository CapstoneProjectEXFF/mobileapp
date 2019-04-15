package com.project.capstone.exchangesystem.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.*;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.utils.UserSession;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class UserProfileFragment extends Fragment {
    String tempTransaction;
    String tempFriend;
    //    TextView txtNumberTransaction;
    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;
    String authorization;
    TextView txtNumberTransaction, txtNumberFriend, txtNameUserProfile, txtPhoneNumberProfile, txtAddressProfile;
    ImageView imageView;
    ImageButton btnQR;
    UserSession userSession;

    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_userprofile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changepassword:
                changePassword();
                return true;
            case R.id.edituserprofile:
                editUserProfile();
                return true;
            case R.id.logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changePassword() {
        Intent intent = new Intent(getContext(), ChangePassword.class);
        startActivity(intent);
    }

    private void editUserProfile() {
        Intent intent = new Intent(getContext(), EditUserProfileActivity.class);
        startActivity(intent);
    }


    private void logout() {
        getActivity().finish();
        SharedPreferences settings = getContext().getSharedPreferences("localData", Context.MODE_PRIVATE);
        settings.edit().clear().commit();
        Intent intent = new Intent(getContext(), SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        view.findViewById(R.id.linlay4).setVisibility(View.GONE);
        btnQR = view.findViewById(R.id.btnQR);
        imageView = view.findViewById(R.id.imgUserProfile);
        txtNameUserProfile = view.findViewById(R.id.txtNameUserProfile);
        txtPhoneNumberProfile = view.findViewById(R.id.txtPhoneNumberProfile);
        txtNumberTransaction = view.findViewById(R.id.txtNumberTransaction);
        txtAddressProfile = view.findViewById(R.id.txtAddressUserProfile);
        txtNumberFriend = view.findViewById(R.id.txtNumberFriends);
        Toolbar toolbar = view.findViewById(R.id.userProfileToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        userSession = new UserSession(getApplicationContext());
        if (userSession.isUserLoggedIn()) {


            sharedPreferences = getContext().getSharedPreferences("localData", MODE_PRIVATE);
            String avatar = "dsa";
            if (sharedPreferences.contains("avatar")) {
                avatar = avatar + sharedPreferences.getString("avatar", "");
            }
            String phoneNumber = sharedPreferences.getString("phoneNumberSignIn", "");
            String userName = sharedPreferences.getString("username", "");
            String status = sharedPreferences.getString("status", "");
            int id = sharedPreferences.getInt("userId", 0);
            String address = sharedPreferences.getString("address", "");
            authorization = sharedPreferences.getString("authorization", "");

            txtNameUserProfile.setText(userName);
            txtPhoneNumberProfile.setText(phoneNumber);
            Picasso.with(view.getContext()).load(avatar)
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(imageView);
            tempTransaction = "";
            tempFriend = "";
            rmaAPIService = RmaAPIUtils.getAPIService();
            txtAddressProfile.setText(address);

            getTransactionNumber();
            getFriendNumber();
            btnQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), QRCodeActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            view.findViewById(R.id.iconEdit).setVisibility(View.GONE);
            view.findViewById(R.id.linlay1).setVisibility(View.GONE);
            view.findViewById(R.id.linlay2).setVisibility(View.GONE);
            view.findViewById(R.id.linlay3).setVisibility(View.GONE);
            view.findViewById(R.id.btnQR).setVisibility(View.GONE);
            view.findViewById(R.id.testLayout).setVisibility(View.GONE);
            txtNameUserProfile.setVisibility(View.GONE);
            txtAddressProfile.setVisibility(View.GONE);
            view.findViewById(R.id.linlay4).setVisibility(View.VISIBLE);

        }

        return view;
    }

    private void getFriendNumber() {
        rmaAPIService.countFriendByUserId(authorization).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    tempFriend = response.body().toString();
                    txtNumberFriend.setText(tempFriend);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
        txtNumberFriend.setText(tempFriend);
    }

    private void getTransactionNumber() {
        rmaAPIService.countAllTransactionByUserId(authorization).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    tempTransaction = response.body().toString();
                    txtNumberTransaction.setText(tempTransaction);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
        txtNumberTransaction.setText(tempTransaction);
    }

    public void toOwnInventory(View view) {
        Intent iOwnInventory = new Intent(getContext(), OwnInventory.class);
        startActivity(iOwnInventory);
    }

    public void toOwnTransaction(View view) {
        Intent iOwnTransaction = new Intent(getContext(), OwnTransaction.class);
        startActivity(iOwnTransaction);
    }

    public void toOwnFriendList(View view) {
        Intent iOwnFriendList = new Intent(getContext(), OwnTransaction.class);
        startActivity(iOwnFriendList);
    }

    public void toOwnDonationPost(View view) {
        Intent iOwnFriendList = new Intent(getContext(), OwnTransaction.class);
        startActivity(iOwnFriendList);
    }

    public void toLoginReminder(View view) {
        Intent signInActivity = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(signInActivity);
    }
}
