package com.example.project1.activites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.R;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText[] otpFields;
    private Button buttonVerifyOtp;
    private ProgressBar progressBar;

    private String verificationId;

    private SmsBroadcastReceiver smsBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        otpFields = new EditText[]{
                findViewById(R.id.editTextOtp1),
                findViewById(R.id.editTextOtp2),
                findViewById(R.id.editTextOtp3),
                findViewById(R.id.editTextOtp4),
                findViewById(R.id.editTextOtp5),
                findViewById(R.id.editTextOtp6)
        };

        buttonVerifyOtp = findViewById(R.id.buttonVerifyOtp);
        progressBar = findViewById(R.id.progressBar);

        verificationId = getIntent().getStringExtra("verificationId");

        // Start listening for incoming SMS using SMS Retriever API
        startSmsListener();

        for (int i = 0; i < otpFields.length; i++) {
            final int currentIndex = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Do nothing
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Do nothing
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 0 && currentIndex < otpFields.length - 1) {
                        // Move focus to the next OTP field
                        otpFields[currentIndex + 1].requestFocus();
                    } else if (editable.length() == 0 && currentIndex > 0) {
                        // Move focus to the previous OTP field
                        otpFields[currentIndex - 1].requestFocus();
                    }
                }
            });
        }

        buttonVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = getOtpFromFields();
                if (!otp.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    verifyOtp(otp);
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startSmsListener() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();

        // Start listening for incoming SMS
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    private class SmsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                    if (status != null) {
                        switch (status.getStatusCode()) {
                            case CommonStatusCodes.SUCCESS:
                                // SMS retrieved successfully
                                String otp = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE);
                                if (otp != null && otp.length() == 6) {
                                    // Auto-fill OTP fields
                                    fillOtpFields(otp);
                                }
                                break;
                            case CommonStatusCodes.TIMEOUT:
                                // Timeout occurred
                                break;
                        }
                    }
                }
            }
        }
    }

    private void fillOtpFields(String otp) {
        for (int i = 0; i < otpFields.length; i++) {
            otpFields[i].setText(String.valueOf(otp.charAt(i)));
        }
    }

    private String getOtpFromFields() {
        StringBuilder otpBuilder = new StringBuilder();
        for (EditText editText : otpFields) {
            otpBuilder.append(editText.getText().toString());
        }
        return otpBuilder.toString();
    }

    private void verifyOtp(String otp) {
        progressBar.setVisibility(View.VISIBLE);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Toast.makeText(OtpVerificationActivity.this, "OTP verification successful", Toast.LENGTH_SHORT).show();
                            // Handle the next steps, e.g., navigate to the main activity
                            navigateToResetPasswordActivity();
                        } else {
                            Toast.makeText(OtpVerificationActivity.this, "Failed to verify OTP. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the SMS broadcast receiver
        if (smsBroadcastReceiver != null) {
            unregisterReceiver(smsBroadcastReceiver);
        }
    }
    
}
