package com.project.capstone.exchangesystem.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.CreateDonationPostActivity;
import com.project.capstone.exchangesystem.activity.DescriptionDonationPostActivity;
import com.project.capstone.exchangesystem.adapter.MainCharityPostAdapter;
import com.project.capstone.exchangesystem.dialog.LoginOptionDialog;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.utils.UserSession;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class MainCharityPostFragment extends Fragment {
    public int idMe;
    Toolbar toolbar;
    ListView listView;
    TextView btnAdd;
    MainCharityPostAdapter mainCharityPostAdapter;
    ArrayList<DonationPost> donationPosts;
    View footerView;
    boolean isLoading;
    boolean limitData;
    int page;
    mHandler mHandler;
    private static final int UPDATE_CODE = 1;
    private static final int ADD_CODE = 2;
    private boolean reloadNeed;
    UserSession userSession;


    public MainCharityPostFragment() {
        // Required empty public constructor
    }


    public static MainCharityPostFragment newInstance() {
        MainCharityPostFragment fragment = new MainCharityPostFragment();

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.reloadNeed) {
            mainCharityPostAdapter.clearFilter();
            page = 0;
            isLoading = false;
            limitData = false;
            getData(page);
            loadMoreData();
        }
        this.reloadNeed = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_CODE) { // Ah! We are back from EditActivity, did we make any changes?
            if (resultCode == Activity.RESULT_OK) {
                // Yes we did! Let's allow onResume() to reload the data
                this.reloadNeed = true;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("localData", MODE_PRIVATE);
        idMe = sharedPreferences.getInt("userId", 0);
        donationPosts = new ArrayList<>();
        page = 0;
        isLoading = false;
        limitData = false;
        userSession = new UserSession(getApplicationContext());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_charity_post, container, false);
        btnAdd = view.findViewById(R.id.btnAddCharityPost);
//        if (!userSession.isUserLoggedIn()) {
//            btnAdd.setVisibility(View.GONE);
//        }
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userSession.isUserLoggedIn()) {
                    Intent intent = new Intent(getActivity(), CreateDonationPostActivity.class);
                    startActivity(intent);
                } else {
                    LoginOptionDialog optionDialog = new LoginOptionDialog();
                    optionDialog.show(getActivity().getSupportFragmentManager(), "optionDialog");
                }
            }
        });
        toolbar = view.findViewById(R.id.ownDonationToolbar);
        listView = view.findViewById(R.id.charityPostListView);
        donationPosts = new ArrayList<>();
        mainCharityPostAdapter = new MainCharityPostAdapter(view.getContext(), donationPosts);
        listView.setAdapter(mainCharityPostAdapter);
        toolbar.setTitle("Từ Thiện");
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        footerView = layoutInflater.inflate(R.layout.progressbar, null);
        mHandler = new mHandler();
        getData(page);
        loadMoreData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DescriptionDonationPostActivity.class);
                intent.putExtra("descriptionDonationPost", donationPosts.get(position));
//                startActivity(intent);
                startActivityForResult(intent, UPDATE_CODE);
            }
        });
        return view;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//    }


    private void loadMoreData() {
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DescriptionDonationPostActivity.class);
                intent.putExtra("descriptionDonationPost", donationPosts.get(position));
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && isLoading == false && limitData == false) {
                    isLoading = true;
                    ThreadData threadData = new ThreadData();
                    threadData.start();
                }
            }
        });

    }


    public class mHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    listView.addFooterView(footerView);
                    break;
                case 1:
                    getData(++page);
                    isLoading = false;
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private void getData(int page) {

        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        rmaAPIService.getDonationPost(page, 3).enqueue(new Callback<List<DonationPost>>() {
            @Override
            public void onResponse(Call<List<DonationPost>> call, Response<List<DonationPost>> response) {
                if (response.isSuccessful()) {
                    List<DonationPost> donationPostList = response.body();
                    if (!donationPostList.isEmpty()) {
//                        mainCharityPostAdapter.setfilter(donationPosts);
                        donationPosts.addAll(donationPostList);
                        mainCharityPostAdapter.notifyDataSetChanged();
                    } else {
                        limitData = true;
                    }
                } else {
                    System.out.println("không gọi được");
                }
            }

            @Override
            public void onFailure(Call<List<DonationPost>> call, Throwable t) {
                System.out.println("vào hàm Failure");
            }
        });

    }

    public class ThreadData extends Thread {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(0);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message message = mHandler.obtainMessage(1);
            mHandler.sendMessage(message);
            super.run();
        }
    }
}