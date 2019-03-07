package com.project.capstone.exchangesystem.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.project.capstone.exchangesystem.Activity.OwnInventory;
import com.project.capstone.exchangesystem.R;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;


public class UserProfileFragment extends Fragment {


    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        ImageView imageView = view.findViewById(R.id.imgUserProfile);
        TextView txtNameUserProfile = view.findViewById(R.id.txtNameUserProfile);
        TextView txtPhoneNumberProfile = view.findViewById(R.id.txtPhoneNumberProfile);


        SharedPreferences sharedPreferences = getContext().getSharedPreferences("localData", MODE_PRIVATE);
        String avatar = sharedPreferences.getString("avatar", null);
        String phoneNumber = sharedPreferences.getString("phoneNumberSignIn", null);
        String userName = sharedPreferences.getString("username", null);
        String status = sharedPreferences.getString("status", null);
        int id = sharedPreferences.getInt("userId", 0);

        txtNameUserProfile.setText(userName);
        txtPhoneNumberProfile.setText(phoneNumber);
        Picasso.with(view.getContext()).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSds7xM5V2GKMhmwIdQNAWProLwB1-cIZwnS7nYtnyMkcosV1b3IQ")
                .placeholder(R.drawable.no)
                .error(R.drawable.loadingimage)
                .into(imageView);

        return view;
    }
    public void toOwnInventory(View view) {
        Intent iOwnInventory = new Intent(getContext(), OwnInventory.class);
        startActivity(iOwnInventory);
    }
}
