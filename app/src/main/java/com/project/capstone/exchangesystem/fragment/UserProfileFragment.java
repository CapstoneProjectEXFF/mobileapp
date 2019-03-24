package com.project.capstone.exchangesystem.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.project.capstone.exchangesystem.activity.EditUserProfileActivity;
import com.project.capstone.exchangesystem.activity.OwnInventory;
import com.project.capstone.exchangesystem.activity.ChangePassword;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.SignInActivity;
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
        SharedPreferences settings = getContext().getSharedPreferences("localData", Context.MODE_PRIVATE);
        settings.edit().clear().commit();
        Intent intent = new Intent(getContext(), SignInActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        ImageView imageView = view.findViewById(R.id.imgUserProfile);
        TextView txtNameUserProfile = view.findViewById(R.id.txtNameUserProfile);
        TextView txtPhoneNumberProfile = view.findViewById(R.id.txtPhoneNumberProfile);

        Toolbar toolbar = view.findViewById(R.id.userProfileToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        SharedPreferences sharedPreferences = getContext().getSharedPreferences("localData", MODE_PRIVATE);
        String avatar = "";
        if (sharedPreferences.contains("avatar")) {
            avatar = avatar + sharedPreferences.getString("avatar", null);
        }
        String phoneNumber = sharedPreferences.getString("phoneNumberSignIn", null);
        String userName = sharedPreferences.getString("username", null);
        String status = sharedPreferences.getString("status", null);
        int id = sharedPreferences.getInt("userId", 0);

        txtNameUserProfile.setText(userName);
        txtPhoneNumberProfile.setText(phoneNumber);
        Picasso.with(view.getContext()).load(avatar)
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image)
                .into(imageView);

        return view;
    }

    public void toOwnInventory(View view) {
        Intent iOwnInventory = new Intent(getContext(), OwnInventory.class);
        startActivity(iOwnInventory);
    }


}
