package com.example.project1.activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.R;
import com.example.project1.sessionmanagement.UserSharedPreference;

public class IPv4Activity extends AppCompatActivity {

    private EditText editTextIpAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip4_adress_activity);

        editTextIpAddress = findViewById(R.id.editTextIpAddress);
        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the entered IPv4 address
                String ipAddress = editTextIpAddress.getText().toString().trim();
                if (ipAddress.equals("")) {
                    Toast.makeText(IPv4Activity.this, "IP Addrress is required!", Toast.LENGTH_SHORT).show();
                }else{


                // Store the IPv4 address in SharedPreferences
                    UserSharedPreference sh = new UserSharedPreference(IPv4Activity.this);
                    sh.addIP(ipAddress);
                    Log.d("IP", "onClick: "+sh.getIP());


//                     Navigate to the login page (replace LoginActivity.class with your actual login activity)
                Intent intent = new Intent(IPv4Activity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }}
        });
    }
}
