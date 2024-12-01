package com.scholl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.scholl.entity.LoginRequest;
import com.scholl.entity.TokenResponse;
import com.scholl.service.ApiService;
import com.scholl.util.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 获取界面中的控件
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.btnLogin);

        // 设置登录按钮的点击事件
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取用户输入的用户名和密码
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    // 如果用户名或密码为空，提示用户
                    Toast.makeText(LoginActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    // 如果用户名和密码不为空，进行登录
                    login(username, password);
                }
            }
        });
    }

    private void login(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.login(loginRequest).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    String accessToken = response.body().getAccess();
                    String refreshToken = response.body().getRefresh();

                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("access_token", accessToken);
                    editor.putString("refresh_token", refreshToken);
                    editor.apply();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    Toast.makeText(LoginActivity.this, "login success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "api error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
