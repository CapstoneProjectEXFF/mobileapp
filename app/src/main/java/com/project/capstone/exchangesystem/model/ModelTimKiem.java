//package com.project.capstone.exchangesystem.model;
//
//import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
//import com.project.capstone.exchangesystem.remote.RmaAPIService;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ModelTimKiem {
//    ArrayList<Item> itemsResult = new ArrayList<>();
//
//    public ArrayList<Item> TimKiemSanPhamTheoTen(String tensp, String tenmang, String tenham, int limit) {
////        ArrayList<Item> itemsResult = new ArrayList<>();
////
////        List<HashMap<String, String>> attrs = new ArrayList<>();
////
////        HashMap<String, String> hsHam = new HashMap<>();
////        hsHam.put("ham", tenham);
////
////        HashMap<String, String> hsTenSP = new HashMap<>();
////        hsTenSP.put("tensp", String.valueOf(tensp));
////
////        HashMap<String, String> hsLimit = new HashMap<>();
////        hsLimit.put("limit", String.valueOf(limit));
////
////        attrs.add(hsHam);
////        attrs.add(hsTenSP);
////        attrs.add(hsLimit);
//
//        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
//        rmaAPIService.findItems(tensp).enqueue(new Callback<List<Item>>() {
//            @Override
//            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
//                List<Item> result = response.body();
//                for (int i = 0; i < result.size(); i++) {
//                    itemsResult.add(result.get(i));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Item>> call, Throwable t) {
//                System.out.println("Fail Search Item");
//            }
//        });
//
//
//        return itemsResult;
//    }
//}
