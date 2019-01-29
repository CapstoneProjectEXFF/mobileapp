package remote;

import model.User;
import retrofit2.Call;
import retrofit2.http.*;

public interface RmaAPIService {

    @GET("/user/{id}")
    Call<User> getUserById(@Path("id") Integer userId);

    @POST("/login")
    @FormUrlEncoded
    Call<User> login(@Field("phoneNumber") String phone, @Field("password") String password);
//    Call<User> login(Req)

}

