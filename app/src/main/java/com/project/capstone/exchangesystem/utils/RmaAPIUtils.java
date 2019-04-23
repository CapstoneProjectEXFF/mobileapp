package com.project.capstone.exchangesystem.utils;

import com.project.capstone.exchangesystem.constants.AppApi;
import com.project.capstone.exchangesystem.remote.RetrofitClient;
import com.project.capstone.exchangesystem.remote.RmaAPIService;

import static com.project.capstone.exchangesystem.constants.AppApi.VERIFY_URL;
import static com.project.capstone.exchangesystem.constants.AppApi.WEBSERVER;

public class RmaAPIUtils {
    //        public static final String LOCAL_IP = "http://10.82.137.166";
    public static final String LOCAL_IP = AppApi.API_URL;
    public static final String PORT = AppApi.API_PORT;
    public static final String BASE_URL = LOCAL_IP + ":" + PORT;

    public static RmaAPIService getAPIService() {
        RmaAPIService rmaAPIService = RetrofitClient.getClient(BASE_URL).create(RmaAPIService.class);
        return rmaAPIService;
    }

    public static RmaAPIService getRealtimeService() {
        RmaAPIService rmaAPIService = RetrofitClient.getClient(WEBSERVER).create(RmaAPIService.class);
        return rmaAPIService;
    }

    public static RmaAPIService getThirdPartyService() {
        RmaAPIService rmaAPIService = RetrofitClient.getClient(VERIFY_URL).create(RmaAPIService.class);
        return rmaAPIService;
    }
}