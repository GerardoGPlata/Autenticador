package com.example.autenticador.api;

import com.example.autenticador.MainActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static Retrofit getInstance(){
        return new Retrofit.Builder()
                .baseUrl("http://10.0.1.1:55555/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
