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

    @GET("live/createChatRoom")
    Call<String> createLiveRoom(
            @Query("auth") String auth,
            @Query("name") String name,
            @Query("description") String description,
            @Query("owner") String owner,
            @Query("maxusers") int maxusers,
            @Query("members") String members
    );

    @GET("live/deleteChatRoom")
    Call<String> deleteLiveRoom(
            @Query("auth") String auth,
            @Query("chatRoomId") String chatRoomId);
}
