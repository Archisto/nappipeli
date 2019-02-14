package com.laurikosonen.nappipeli;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GameService {
    @GET("winners")
    Call<List<Winner>> winners();

    @GET("counter")
    Call<Counter> getCounter();

    @POST("counter/raise")
    Call<Counter> modifyCounter(@Body int value);
}
