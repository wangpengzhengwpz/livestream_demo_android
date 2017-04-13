package cn.ucai.live.data.restapi;

import cn.ucai.live.I;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by w on 2017/4/13.
 */

public interface LiveService {
    @GET("live/getAllGifts")
    Call<String> getAllGifts();

    @GET("findUserByUserName")
    Call<String> loadUserInfo(@Query(I.User.USER_NAME) String username);
}
