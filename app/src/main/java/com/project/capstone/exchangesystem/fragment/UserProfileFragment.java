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
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.utils.UserSession;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class UserProfileFragment extends Fragment {
    String tempTransaction;
    String tempFriend, tempInventory, tempDonation;
    //    TextView txtNumberTransaction;
    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;
    String authorization;
    TextView txtNumberTransaction, txtNumberFriend, txtNumberInventory, txtNumberDonation, txtNameUserProfile, txtPhoneNumberProfile, txtAddressProfile;
    ImageView imageView;
    ImageButton btnQR, iconPhone;
    UserSession userSession;
    RecyclerView rvReviewers;
    ReviewerAdapter reviewerAdapter;
    ArrayList<Rate> ratingList;
    List<Transaction> transactions;
    int userId;

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
        authorization = sharedPreferences.getString("authorization", "");
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
        txtNumberInventory = view.findViewById(R.id.txtNumberInventory);
        txtNumberDonation = view.findViewById(R.id.txtNumberDonation);
        txtAddressProfile = view.findViewById(R.id.txtAddressUserProfile);
        txtNumberFriend = view.findViewById(R.id.txtNumberFriends);
        rvReviewers = view.findViewById(R.id.rvReviewers);
        iconPhone = view.findViewById(R.id.iconPhone);
        Toolbar toolbar = view.findViewById(R.id.userProfileToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        userSession = new UserSession(getApplicationContext());
        if (userSession.isUserLoggedIn()) {


            sharedPreferences = getContext().getSharedPreferences("localData", MODE_PRIVATE);
            String avatar = sharedPreferences.getString("avatar", "https://cdn1.iconfinder.com/data/icons/user-pictures/100/unknown-512.png");
            String phoneNumber = sharedPreferences.getString("phoneNumberSignIn", null);
            String userName = sharedPreferences.getString("username", "");
            String status = sharedPreferences.getString("status", "");
            int id = sharedPreferences.getInt("userId", 0);
            String address = sharedPreferences.getString("address", "");
            authorization = sharedPreferences.getString("authorization", "");
            userId = sharedPreferences.getInt("userId", 0);
            txtNameUserProfile.setText(userName);
            txtPhoneNumberProfile.setText(phoneNumber);
            Picasso.with(view.getContext()).load(avatar)
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(imageView);

            tempTransaction = "";
            tempFriend = "";
            tempInventory = "";
            tempDonation = "";
            rmaAPIService = RmaAPIUtils.getAPIService();
            txtAddressProfile.setText(address);

            getTransactionNumber();
            getFriendNumber();
            getDonationPostNumber();
            getInventoryNumber();

            btnQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), QRCodeActivity.class);
                    ArrayList<Integer> transactionIds = new ArrayList<>();
                    for (int i = 0; i < transactions.size(); i++) {
                        transactionIds.add(transactions.get(i).getId());
                    }
                    intent.putExtra("transactionIds", transactionIds);
                    startActivity(intent);
                }
            });

            //load reviewers
            showReviewers(view);
            getTransactionData();
        } else {
            view.findViewById(R.id.iconEdit).setVisibility(View.GONE);
            view.findViewById(R.id.linlay1).setVisibility(View.GONE);
            view.findViewById(R.id.linlay2).setVisibility(View.GONE);
            view.findViewById(R.id.linlay3).setVisibility(View.GONE);
            view.findViewById(R.id.btnQR).setVisibility(View.GONE);
            view.findViewById(R.id.testLayout).setVisibility(View.GONE);
            txtNameUserProfile.setVisibility(View.GONE);
            txtAddressProfile.setVisibility(View.GONE);
            txtPhoneNumberProfile.setVisibility(View.GONE);
            iconPhone.setVisibility(View.GONE);
            view.findViewById(R.id.linlay4).setVisibility(View.VISIBLE);
            setHasOptionsMenu(false);
            getActivity().setTitle(" ");


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

    private void getInventoryNumber() {
        rmaAPIService.countAllItemByUserId(authorization, userId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    tempInventory = response.body().toString();
                    txtNumberInventory.setText(tempInventory);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

        txtNumberInventory.setText(tempInventory);
    }

    private void getDonationPostNumber() {
        rmaAPIService.countDonationPostByUserId(userId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    tempDonation = response.body().toString();
                    txtNumberDonation.setText(tempDonation);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

        txtNumberDonation.setText(tempDonation);
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
                    }
                }

                @Override
                public void onFailure(Call<List<Rate>> call, Throwable t) {

                }
            });
        }
    }

    public void toLoginReminder(View view) {
        Intent signInActivity = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(signInActivity);

    }

    private void getTransactionData() {
        transactions = new ArrayList<>();
        if (authorization != null) {
            rmaAPIService.getAllTransactionByUserID(authorization).enqueue(new Callback<List<Transaction>>() {
                @Override
                public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                    if (response.isSuccessful()) {
                        transactions = response.body();
                    }
                }

                @Override
                public void onFailure(Call<List<Transaction>> call, Throwable t) {
                    System.out.println("Fail rồi");
                    Toast.makeText(getApplicationContext(), "Error Server", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}