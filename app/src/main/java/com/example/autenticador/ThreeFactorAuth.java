package com.example.autenticador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.autenticador.api.ApiClient;
import com.example.autenticador.api.ApiService;
import com.example.autenticador.model.AuthModels;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ThreeFactorAuth extends AppCompatActivity {

    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_three_factor_auth);

        EditText etCode = findViewById(R.id.etCode);
        Button btnVerify = findViewById(R.id.btnVerify);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            showLogoutDialog();
        });

        btnVerify.setOnClickListener(v -> {
            //Validar que el campo no esté vacío
            if (etCode.getText().toString().isEmpty()) {
                Toast.makeText(ThreeFactorAuth.this, "Por favor, ingresa el código", Toast.LENGTH_SHORT).show();
                return;
            }
            int verifyCode = Integer.parseInt(etCode.getText().toString());
            verify(verifyCode);
        });
    }

    private void logout() {
        Retrofit retrofit = ApiClient.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String authToken = "Bearer " + sharedPreferences.getString("token", "");

        apiService.logout(authToken).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("token");
                    editor.apply();
                    Intent intent = new Intent(ThreeFactorAuth.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ThreeFactorAuth.this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ThreeFactorAuth.this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verify(int code){

        //Bloquear boton de verificar
        Button btnVerify = findViewById(R.id.btnVerify);
        btnVerify.setEnabled(false);
        Retrofit retrofit = ApiClient.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String authToken = "Bearer " + sharedPreferences.getString("token", "");

        AuthModels.VerifyCodeRequest verifyCodeRequest = new AuthModels.VerifyCodeRequest(code);

        apiService.verifyCode(authToken, verifyCodeRequest).enqueue(new Callback<AuthModels.VerifyCodeResponse>() {
            @Override
            public void onResponse(Call<AuthModels.VerifyCodeResponse> call, Response<AuthModels.VerifyCodeResponse> response) {
                if (response.isSuccessful()) {
                    AuthModels.VerifyCodeResponse verifyCodeResponse = response.body();
                    showVerifyDialog(verifyCodeResponse.getTwo_factor_code());
                } else {
                    Toast.makeText(ThreeFactorAuth.this, "Código incorrecto", Toast.LENGTH_SHORT).show();
                    //Desbloquear boton de verificar
                    btnVerify.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<AuthModels.VerifyCodeResponse> call, Throwable t) {
                Toast.makeText(ThreeFactorAuth.this, "Error al verificar código", Toast.LENGTH_SHORT).show();
                //Desbloquear boton de verificar
                btnVerify.setEnabled(true);
            }
        });
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_logout, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);

        dialogView.findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showVerifyDialog(String verifyCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_verify, null);
        builder.setView(dialogView);

        TextView etVerifyCode = dialogView.findViewById(R.id.etVerifyCode);
        TextView tvTimer = dialogView.findViewById(R.id.tvTimer);
        Button btnVerify = dialogView.findViewById(R.id.btnAccept);

        //Iniciar temporizador
        startTimer(tvTimer);

        etVerifyCode.setText(verifyCode);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes agregar la lógica para verificar el código
                countDownTimer.cancel();
                alertDialog.dismiss();
                //Desbloquear boton de verificar
                Button btnVerify = findViewById(R.id.btnVerify);
                btnVerify.setEnabled(true);
            }
        });



        alertDialog.show();
    }

    private void startTimer(TextView tvTimer) {
        countDownTimer = new CountDownTimer(600000, 1000) { // 600000 ms = 10 minutos
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                tvTimer.setText("Tiempo restante: " + minutes + ":" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("Tiempo restante: 0:00");
                // Aquí puedes agregar la lógica cuando el temporizador finalice
            }
        }.start();
    }
}