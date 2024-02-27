package com.example.project1.model;

public class LoginResponse {
    private String message;
    private int uid;

    @Override
    public String toString() {
        return "LoginResponse{" +
                "message='" + message + '\'' +
                ", uid=" + uid +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
