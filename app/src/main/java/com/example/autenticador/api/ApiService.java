package com.example.autenticador.api;

import com.example.autenticador.model.AuthModels;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<AuthModels.LoginResponse> login(@Body AuthModels.LoginRequest loginRequest);

    @POST("logout")
    Call<Void> logout(@Header("Authorization") String authToken);

    @POST("userThreeFactorCode")
    Call<AuthModels.VerifyCodeResponse> verifyCode(@Header("Authorization") String authToken, @Body AuthModels.VerifyCodeRequest verifyCodeRequest);
}
