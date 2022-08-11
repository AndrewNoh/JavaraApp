package com.kosmo.zabara.api.service;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BoardService {

    @POST("board/selectList")
    Call<Map> selectList(@Body Map map);

    @POST("board/viewSelect")
    Call<Map> viewSelect(@Body Map map);

    @POST("board/likeOne")
    Call<Map> likeOne(@Body Map map);

    @POST("board/updatePrice")
    Call<Map> updatePrice(@Body Map map);

    @POST("board/changeStatus")
    Call<Map> changeStatus(@Body Map map);

    @POST("board/choseMenu")
    Call<Map> choseMenu(@Body Map map);
}
