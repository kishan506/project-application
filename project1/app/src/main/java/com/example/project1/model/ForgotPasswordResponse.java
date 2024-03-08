package com.example.project1.model;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordResponse {



    @SerializedName("message")
    private String message;

    // Additional fields based on your API response

    public ForgotPasswordResponse( String message) {

        this.message = message;
    }


    public String getMessage() {
        return message;
    }
}
