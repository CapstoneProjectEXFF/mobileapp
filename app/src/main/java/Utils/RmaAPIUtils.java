package Utils;

import remote.RetrofitClient;
import remote.RmaAPIService;

public class RmaAPIUtils {
    public static final String LOCAL_IP = "http://172.16.1.117";
    public static final String PORT = "8080";
    public static final String BASE_URL = LOCAL_IP + ":" + PORT;

    public static RmaAPIService getAPIService() {
        RmaAPIService rmaAPIService = RetrofitClient.getClient(BASE_URL).create(RmaAPIService.class);
        return rmaAPIService;
    }

}

