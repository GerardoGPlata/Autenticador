package com.example.autenticador.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static Retrofit getInstance(){
        return new Retrofit.Builder()
                .baseUrl("http://192.168.100.2:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
