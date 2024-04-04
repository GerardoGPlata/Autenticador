package com.example.autenticador.model;

import com.google.gson.annotations.SerializedName;

public class AuthModels {
    public static class LoginRequest {
        @SerializedName("email")
        private String email;

        @SerializedName("password")
        private String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public class LoginResponse {
        @SerializedName("token")
        private String token;

        @SerializedName("message")
        private String message;

        public String getToken() {
            return token;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class VerifyCodeRequest {
        @SerializedName("three_factor_code")
        private Integer code;

        public VerifyCodeRequest(Integer code) {
            this.code = code;
        }
    }

    public class VerifyCodeResponse {
        @SerializedName("two_factor_code")
        private String two_factor_code;

        @SerializedName("message")
        private String message;

        public String getTwo_factor_code() {
            return two_factor_code;
        }

        public String getMessage() {
            return message;
        }
    }
}
