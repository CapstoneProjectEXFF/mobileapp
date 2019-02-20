package remote;

import com.google.gson.JsonObject;
import model.Item;
import model.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RmaAPIService {

    @GET("/user/{id}")
    Call<User> getUserById(@Path("id") Integer userId);

    @POST("/login")
//    @FormUrlEncoded
//    Call<Object> login(@Field("phoneNumber") String phone, @Field("password") String password);
//    Call<User> login(Req)
    Call<Object> login(@Body Map<String, String> body);

    @POST("/register")
    Call<Object> register(@Body Map<String, String> body);

    @GET("/phone")
    Call<Object> checkValidationLogin(@Query("phone") String phone);

    @GET("/item")
//    @Headers({"Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOjEsXCJwaG9uZU51bWJlclwiOlwiMDk3ODQzOTcxOFwiLFwiZnVsbE5hbWVcIjpudWxsLFwic3RhdHVzXCI6bnVsbCxcInJvbGVCeVJvbGVJZFwiOntcImlkXCI6MSxcIm5hbWVcIjpcInVzZXJcIn19IiwiZXhwIjoxNTUxNDE2NjA0fQ.akJMpxrPogLNMxb5zD1CRjnKOeFJ2bZkNVUoG_d8Vy3glhkWLQrqVGEVc9ocSuQsu0dr2dmHDVMovJeaxeuLWw"})
//    Call<Object> getAllItems(@Header(("Authorization")) String authorization);
    Call<List<Item>> getAllItems(@Header("Authorization") String authorization);

}

