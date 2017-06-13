package com.a3sj.vts.retrofitpostexample;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;


public interface RegisterAPI {
    @FormUrlEncoded
    @POST("/RetrofitExample/insert.php")
    public void insertUser(
            @Field("route") String route,
            @Field("bus_number") String bus_number,
            @Field("driver_email") String driver_email,
            @Field("lattitude") String lattitude,
            @Field("longitude") String longitude,
            @Field("date") String date,
            @Field("departuretime") String departuretime,
            @Field("reachedtime") String reachedtime,
            Callback<Response> callback);
}
