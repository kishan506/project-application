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
import com.example.project1.model.User;
import com.example.project1.sessionmanagement.UserSharedPreference;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextPhoneNumber;
    private EditText editTextPassword;
    private EditText editTextCPassword;
    private Button buttonRegister;

    private static String BASE_URL = "http://";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextCPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    sendRegistrationData();
                }
            }
        });
    }

    private void sendRegistrationData() {
        // Add logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.d("OkHttp", message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor);
        UserSharedPreference sh = new UserSharedPreference(this);
        BASE_URL=BASE_URL+sh.getIP()+"/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FastApiService fastApiService = retrofit.create(FastApiService.class);

        User user = new User(
                editTextFirstName.getText().toString(),
                editTextLastName.getText().toString(),
                editTextEmail.getText().toString(),
                editTextPhoneNumber.getText().toString(),
                editTextPassword.getText().toString()
        );

        Call<Response<ResponseBody>> call = fastApiService.registerUser(user);

        call.enqueue(new Callback<Response<ResponseBody>>() {
            @Override
            public void onResponse(Call<Response<ResponseBody>> call, Response<Response<ResponseBody>> response) {
                if (response.isSuccessful()) {
                    showToast("Registration successful");
                    navigateToLoginActivity();
                } else {
                    Log.d("response", "onResponse: "+response.body().toString());
                    showToast("Registration failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Response<ResponseBody>> call, Throwable t) {
                showToast("Network error. Please try again.");
                Log.e("NetworkError", "Failed to make the network request", t);
            }
        });
    }

    private boolean validateFields() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
//        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            editTextFirstName.setError("First name is required");
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError("Last name is required");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email address");
            return false;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            editTextPhoneNumber.setError("Phone number is required");
            return false;
        } else if (phoneNumber.length() != 10) {
            editTextPhoneNumber.setError("Enter a valid 10-digit phone number");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return false;
        }


        if (password.equals(editTextCPassword)) {
            editTextPassword.setError("Passwords do not match");
            editTextCPassword.setError("Passwords do not match");
            return false;
        }

        // Add more validation rules as needed

        return true; // All validations passed
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
