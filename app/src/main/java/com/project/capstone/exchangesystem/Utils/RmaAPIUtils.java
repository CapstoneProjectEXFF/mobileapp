package com.project.capstone.exchangesystem.Utils;

import com.project.capstone.exchangesystem.remote.RetrofitClient;
import com.project.capstone.exchangesystem.remote.RmaAPIService;

public class RmaAPIUtils {
//        public static final String LOCAL_IP = "http://10.82.137.166";
    public static final String LOCAL_IP = "http://172.16.2.0";



    public static final String PORT = "8080";
    public static final String BASE_URL = LOCAL_IP + ":" + PORT;

    public static RmaAPIService getAPIService() {
        RmaAPIService rmaAPIService = RetrofitClient.getClient(BASE_URL).create(RmaAPIService.class);
        return rmaAPIService;
    }
}