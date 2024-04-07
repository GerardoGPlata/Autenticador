package com.example.autenticador;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autenticador.api.ApiClient;
import com.example.autenticador.api.ApiService;
import com.example.autenticador.model.AuthModels;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnLogin = findViewById(R.id.btnLogin);
        EditText etCorreo = findViewById(R.id.etCorreo);
        EditText etPassword = findViewById(R.id.etPassword);

        //Validar si ya hay un token guardado en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        //Si ya hay un token guardado, redirigir a la siguiente pantalla
        if (!token.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, ThreeFactorAuth.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(v -> {

            login(etCorreo.getText().toString(), etPassword.getText().toString());
        });
    }

    private void login(String email, String password)
    {
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setEnabled(false);

        //Validar que los campos no estén vacíos
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
            return;
        }

        //Validar que el email sea válido
        if (!isValidEmail(email)) {
            Toast.makeText(MainActivity.this, "Por favor, introduce un email válido", Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
            return;
        }


        AuthModels.LoginRequest loginRequest = new AuthModels.LoginRequest(email, password);

        Retrofit retrofit = ApiClient.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.login(loginRequest).enqueue(new Callback<AuthModels.LoginResponse>() {
            @Override
            public void onResponse(Call<AuthModels.LoginResponse> call, retrofit2.Response<AuthModels.LoginResponse> response) {
                if (response.isSuccessful()) {
                    AuthModels.LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();
                    String message = loginResponse.getMessage();
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                    if(message.equals("Usuario no autorizado")) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                        return;
                    }
                    if (token.isEmpty()) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                        return;
                    }
                    //guardar el token en SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("token", token);
                    myEdit.apply();


                    //redirigir a la siguiente pantalla
                    Intent intent = new Intent(MainActivity.this, ThreeFactorAuth.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(MainActivity.this, "Verifica tus credenciales", Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                }
            }
            @Override
            public void onFailure(Call<AuthModels.LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println(t.getMessage());
                btnLogin.setEnabled(true);
            }
        });
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}