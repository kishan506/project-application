package com.example.project1.activites;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.R;
import com.example.project1.api.FastApiService;
import com.example.project1.model.LoginResponse;
import com.example.project1.model.UserDTO;
import com.example.project1.sessionmanagement.UserSharedPreference;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private static  String BASE_URL = "http://";


    private boolean validateFields() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email address");
            return false;
        }


        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return false;
        }

        return true; // All validations passed
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserSharedPreference sh = new UserSharedPreference(this);
        if(sh.getUserDetails()!=0)
        {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    // Retrieve data from EditText fields
                    String email = editTextEmail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();


                    // Add logging interceptor
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.d("OkHttp", message));
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                            .addInterceptor(loggingInterceptor);
//                    getting ip from sharedprfrences

                    BASE_URL=BASE_URL+sh.getIP()+"/";
                    Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(BASE_URL)
                            .client(clientBuilder.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    FastApiService fastApiService = retrofit.create(FastApiService.class);

                    UserDTO userDTO = new UserDTO(email,password);
                    Call<LoginResponse> call = fastApiService.loginUser(userDTO);

                    call.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            if (response.isSuccessful()) {
                                showToast("Login successful");
                                Log.d("login response", "onResponse: "+response.body().toString());
                                UserSharedPreference sh = new UserSharedPreference(LoginActivity.this);
                                sh.addUserDetails(response.body().getUid());
                                // Navigate to the activity_main screen
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                showToast("Login failed. Please try again.");
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            showToast("Network error. Please try again.");
                            Log.e("NetworkError", "Failed to make the network request", t);                        
                        }
                    });
                    
                }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use an Intent to start the RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

}
