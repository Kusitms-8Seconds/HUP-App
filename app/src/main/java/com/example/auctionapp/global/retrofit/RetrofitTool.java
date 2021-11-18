package com.example.auctionapp.global.retrofit;

import com.google.gson.GsonBuilder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitTool {
    private static final String BASE_URL = "http://192.168.0.5:8080/"; // 내부IP(안드로이드휴대폰사용, cmd -> ipconfig IPv4주소)
    //private static final String BASE_URL = "http://10.0.2.2:8080/"; // 안드로이드에뮬레이터IP
    public static RestAPI getAPI(){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build()
                .create(RestAPI.class);
    }
    public static RestAPI getAPIWithNullConverter(){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(new MainNullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build()
                .create(RestAPI.class);
    }
    public static RestAPI getAPIWithAuthorizationToken(String token){
        Interceptor interceptor = chain -> {
            Request newRequest  = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        };
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create( new GsonBuilder().create()))
                .build()
                .create(RestAPI.class);
    }
}