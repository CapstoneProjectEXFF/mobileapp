package remote;

import com.google.gson.JsonObject;
import model.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.HashMap;
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

}

