package com.example.project1.model;

public class User {

    private int userId;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String password;

    public User() {
        // Empty constructor required by Retrofit
    }

    public User(String Firstname, String LastName, String Email, String PhoneNumber, String Password) {
        this.firstname = Firstname;
        this.lastname = LastName;
        this.email = Email;
        this.phone = PhoneNumber;
        this.password = Password;
    }
    public User(String Firstname) {
        this.firstname = Firstname;

    }

    public User(String firstName, String lastName) {
        this.firstname =firstName;
        this.lastname =lastName;

    }

    // Getter and Setter methods for each field

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String Firstname) {
        this.firstname = Firstname;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastName(String LastName) {
        this.lastname = LastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String Email) {
        this.email = Email;
    }

    public String getPhoneNumber() {
        return phone;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.phone = PhoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String Password) {
        this.password = Password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
