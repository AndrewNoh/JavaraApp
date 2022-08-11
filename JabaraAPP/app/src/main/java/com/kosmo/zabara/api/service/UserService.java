package com.kosmo.zabara.api.service;

import com.kosmo.zabara.api.dto.UserDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {

   @POST("users/login")
   Call<Map<String,Boolean>> isLogin(@Body UserDTO loginDTO);

   @FormUrlEncoded
   @POST("users/userinfo")
   Call<Map> callUserInfo(@Field("email") String email);

   @POST("users/userAddr")
   Call<Map<String,Boolean>> userAddr(@Body Map map);

   @POST("users/deposit")
   Call<Map> deposit(@Body Map map);

   @POST("users/remittance")
   Call<Map> remittance(@Body Map map);

   @POST("users/getbalance")
   Call<Map> getbalance(@Body Map map);
}
