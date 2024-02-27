package com.example.project1.model;

public class UserDTO {

    private String email;
    private String password;

    public UserDTO() {
        // Empty constructor required by Retrofit
    }

    public UserDTO( String Email,  String Password) {

        this.email = Email;

        this.password = Password;
    }

    // Getter and Setter methods for each field

    public String getEmail() {
        return email;
    }

    public void setEmail(String Email) {
        this.email = Email;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String Password) {
        this.password = Password;
    }
}
