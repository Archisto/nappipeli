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

    @POST("winners/new")
    Call<Winner> createWinner(@Body Winner winner);

    @GET("counters")
    Call<List<Counter>> counters();

    @POST("counters/new")
    Call<Counter> createCounter(@Body Counter counter);

    @POST("counters/update")
    Call<Counter> updateCounter(@Body Counter counter);
}
