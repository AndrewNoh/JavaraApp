package com.kosmo.zabara.api.service;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ChatService {
    @POST("chat/chattingroom")
    Call<Map> selectList(@Body Map map);

    @POST("chat/joinchat")
    Call<Map> joinChat(@Body Map map);

    @POST("chat/insertchat")
    Call<Map> insertchat(@Body Map map);

    @Multipart
    @POST("chat/insertimgfromapp.do")
    Call<Map> insertimgfromapp(@Part MultipartBody.Part chatimg, @Part("roomno") RequestBody roomno, @Part("senduserno") RequestBody senduserno, @Part("unread_count") RequestBody unread_count, @Part("chatcontent") RequestBody chatcontent);

    @POST("chat/updateCall")
    Call<Map> updateCall(@Body Map map);

}
