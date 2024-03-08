package com.example.project1.activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class ChatActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginwithfirebase);

        mAuth = FirebaseAuth.getInstance();

        // Check if the user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, navigate to the next page
            navigateToNextPage(currentUser);
        } else {
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("54173507291-pps28h7skic4saa4ht5f8qg1fiks0fl3.apps.googleusercontent.com")  // Replace with your Web Client ID
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            findViewById(R.id.buttonGoogle).setOnClickListener(view -> signInWithGoogle());
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            handleGoogleSignInResult(data);
        }
    }

    private void handleGoogleSignInResult(Intent data) {
        try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account);
                Log.d("", "handleGoogleSignInResult: "+"userfound");

            } else {
                // Handle the case where the GoogleSignInAccount is null
                Log.d("", "handleGoogleSignInResult: "+"userfound");
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        String idToken = account.getIdToken();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Do something with the user data
                        Log.d("", "firebaseAuthWithGoogle: "+"done");
                        // After successful sign-in, navigate to the next page

                        navigateToNextPage(user);
                    } else {
                        Log.d("", "firebaseAuthWithGoogle: "+"not");
                        // If sign in fails, display a message to the user.
                        // Handle the error
                    }
                });
    }

    private void navigateToNextPage(FirebaseUser user) {
//        dialog.dismiss();
        // Replace NextActivity.class with the actual class you want to navigate to
        Intent intent = new Intent(ChatActivity.this, ChatRooms.class);
        startActivity(intent);
        finish(); // Optional: finish the current activity if needed
    }
}
