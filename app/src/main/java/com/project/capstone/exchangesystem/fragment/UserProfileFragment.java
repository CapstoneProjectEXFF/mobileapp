package com.project.capstone.exchangesystem.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.*;
import com.project.capstone.exchangesystem.adapter.ReviewerAdapter;
import com.project.capstone.exchangesystem.model.Rate;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class UserProfileFragment extends Fragment {
    String tempTransaction;
    String tempFriend;
    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;
    RecyclerView rvReviewers;
    ReviewerAdapter reviewerAdapter;
    ArrayList<Rate> ratingList;
    String authorization;
    int userId;
//    TextView txtNumberTransaction;


    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        rmaAPIService = RmaAPIUtils.getAPIService();
        sharedPreferences = getActivity().getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        userId = sharedPreferences.getInt("userId", 0);
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

        ImageButton btnQR = view.findViewById(R.id.btnQR);
        ImageView imageView = view.findViewById(R.id.imgUserProfile);
        TextView txtNameUserProfile = view.findViewById(R.id.txtNameUserProfile);
        TextView txtPhoneNumberProfile = view.findViewById(R.id.txtPhoneNumberProfile);
        final TextView txtNumberTransaction = view.findViewById(R.id.txtNumberTransaction);
        final TextView txtNumberFriend = view.findViewById(R.id.txtNumberFriends);
        Toolbar toolbar = view.findViewById(R.id.userProfileToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        SharedPreferences sharedPreferences = getContext().getSharedPreferences("localData", MODE_PRIVATE);
        String avatar = "dsa";
        if (sharedPreferences.contains("avatar")) {
            avatar = avatar + sharedPreferences.getString("avatar", "");
        }
        String phoneNumber = sharedPreferences.getString("phoneNumberSignIn", null);
        String userName = sharedPreferences.getString("username", null);
        String status = sharedPreferences.getString("status", null);
        int id = sharedPreferences.getInt("userId", 0);
        String authorization = sharedPreferences.getString("authorization", null);

        txtNameUserProfile.setText(userName);
        txtPhoneNumberProfile.setText(phoneNumber);
        Picasso.with(view.getContext()).load(avatar)
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image)
                .into(imageView);
        tempTransaction = "";
        tempFriend = "";

        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
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

        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), QRCodeActivity.class);
                startActivity(intent);
            }
        });

        //load reviewers
        showReviewers(view);

        return view;
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

    private void showReviewers(View view) {
        rvReviewers = view.findViewById(R.id.rvReviewers);
        ratingList = new ArrayList<>();
        reviewerAdapter = new ReviewerAdapter(getApplicationContext(), ratingList, new ReviewerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Rate rate) {
                //TODO VIEW USER'S PROFILE
                Toast.makeText(getApplicationContext(), "" + rate.getSender().getFullName(), Toast.LENGTH_LONG).show();
            }
        });
        rvReviewers.setHasFixedSize(true);
        rvReviewers.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        rvReviewers.setAdapter(reviewerAdapter);
        loadReviewers();
    }

    private void loadReviewers() {
        if (authorization != null) {
            rmaAPIService.getRating(userId).enqueue(new Callback<List<Rate>>() {
                @Override
                public void onResponse(Call<List<Rate>> call, Response<List<Rate>> response) {
                    if (response.body() != null) {
                        ArrayList<Rate> tmpRatingList = new ArrayList<>();
                        for (int i = 0; i < response.body().size(); i++) {
                            tmpRatingList.add(response.body().get(i));
                        }
                        ratingList.clear();
                        ratingList.addAll(tmpRatingList);
                        reviewerAdapter.notifyDataSetChanged();
//                        if (donators.size() > 0) {
//                            rvDonators.setVisibility(View.VISIBLE);
//                            txtNoDonators.setVisibility(View.GONE);
//                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Rate>> call, Throwable t) {

                }
            });
        }
    }
}
