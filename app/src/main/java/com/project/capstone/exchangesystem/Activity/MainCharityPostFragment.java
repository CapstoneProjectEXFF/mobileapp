package com.project.capstone.exchangesystem.Activity;

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

import java.util.ArrayList;


public class MainCharityPostFragment extends Fragment {
    Toolbar toolbar;
    ListView listView;
    MainCharityPostAdapter mainCharityPostAdapter;
    ArrayList<CharityPostItem> charityPostItemArrayList;
    View footerView;
    boolean isLoading = false;
    boolean limitData = false;
//    mHandler mHandler;


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
        charityPostItemArrayList = new ArrayList<>();
        mainCharityPostAdapter = new MainCharityPostAdapter(view.getContext(), charityPostItemArrayList);
        listView.setAdapter(mainCharityPostAdapter);
        GetBrandNewCharityPost();
        return view;
    }

    private void GetBrandNewCharityPost() {
        for (int i = 0; i < 3; i++) {
            charityPostItemArrayList.add(new CharityPostItem(0, "dsadsadsa", "Three devastating wildfires--the Camp, Woolsey, and Hill fires--are burning out of control in California. Firefighters are working around the clock to control the blazes and keep them from spreading into even more communities. More than 250,000 across the state have been forced to flee their homes. Governor Jerry Brown has declared the fires a major disaster, and is requesting federal emergency funds to aid families and communities affected by the fires.\n" +
                    "\n" +
                    "The Camp Fire, which is burning north of of Sacramento, California, destroyed the small city of Paradise and is continuing to spread. The fire has claimed the lives of 77 individuals and hundreds of people are still missing. It has also burned more than 6,700 homes and businesses in Paradise and its surrounding areas. The fire is now tied for the deadliest on record in California state history.", "d", "https://econsultancy.imgix.net/content/uploads/2018/01/05151122/ROW-50-charity.png"));
            mainCharityPostAdapter.notifyDataSetChanged();
        }
    }

//    private void ActionToolbar() {
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//    }


//    public class mHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    listView.addFooterView(footerView);
//                    break;
//                case 1:
//                    GetData(++page);
//                    isLoading = false;
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    }
//
//    public class ThreadData extends Thread {
//        @Override
//        public void run() {
//            mHandler.sendEmptyMessage(0);
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            Message message = mHandler.obtainMessage(1);
//            mHandler.sendMessage(message);
//            super.run();
//        }
//    }


}
