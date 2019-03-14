package com.project.capstone.exchangesystem.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.AbsListView;
import android.widget.AdapterView;
import com.project.capstone.exchangesystem.Activity.DescriptionItem;
import com.project.capstone.exchangesystem.Utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.adapter.MainCharityPostAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.CharityPostItem;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class MainCharityPostFragment extends Fragment {
    Toolbar toolbar;
    ListView listView;
    MainCharityPostAdapter mainCharityPostAdapter;
    ArrayList<DonationPost> donationPosts;
    View footerView;
    boolean isLoading = false;
    boolean limitData = false;
    int page = 0;
    mHandler mHandler;


    public MainCharityPostFragment() {
        // Required empty public constructor
    }


    public static MainCharityPostFragment newInstance() {
        MainCharityPostFragment fragment = new MainCharityPostFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_charity_post, container, false);
        listView = (ListView) view.findViewById(R.id.charityPostListView);
        donationPosts = new ArrayList<>();
        mainCharityPostAdapter = new MainCharityPostAdapter(view.getContext(), donationPosts);
        listView.setAdapter(mainCharityPostAdapter);
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        footerView = layoutInflater.inflate(R.layout.progressbar, null);
        mHandler = new mHandler();
        GetData(page);
        LoadMoreData();
        return view;
    }

    private void LoadMoreData() {
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext().getApplicationContext(), DescriptionItem.class);
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

    private void GetBrandNewCharityPost() {
//        for (int i = 0; i < 3; i++) {
//            donationPosts.add(new Don(0, "dsadsadsa", "Three devastating wildfires--the Camp, Woolsey, and Hill fires--are burning out of control in California. Firefighters are working around the clock to control the blazes and keep them from spreading into even more communities. More than 250,000 across the state have been forced to flee their homes. Governor Jerry Brown has declared the fires a major disaster, and is requesting federal emergency funds to aid families and communities affected by the fires.\n" +
//                    "\n" +
//                    "The Camp Fire, which is burning north of of Sacramento, California, destroyed the small city of Paradise and is continuing to spread. The fire has claimed the lives of 77 individuals and hundreds of people are still missing. It has also burned more than 6,700 homes and businesses in Paradise and its surrounding areas. The fire is now tied for the deadliest on record in California state history.", "d", "https://econsultancy.imgix.net/content/uploads/2018/01/05151122/ROW-50-charity.png"));
//            mainCharityPostAdapter.notifyDataSetChanged();
//        }

        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
    }


    public class mHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    listView.addFooterView(footerView);
                    break;
                case 1:
                    GetData(++page);
                    isLoading = false;
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private void GetData(int page) {

        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        rmaAPIService.getDonationPost(page, 3).enqueue(new Callback<List<DonationPost>>() {
            @Override
            public void onResponse(Call<List<DonationPost>> call, Response<List<DonationPost>> response) {
                if (response.isSuccessful()) {

                    List<DonationPost> donationPostList = response.body();
                    if (!donationPostList.isEmpty()) {
                        donationPosts.addAll(donationPostList);
                        mainCharityPostAdapter.notifyDataSetChanged();
                        System.out.println("đã vào hàm response");
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
