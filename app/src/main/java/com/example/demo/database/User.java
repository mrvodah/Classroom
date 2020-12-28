package com.example.demo.database;

public class User {
    public String fullName, email, password;
    public Boolean teacher;

    public User() {
    }

    public User(String fullName, String email, String password, Boolean teacher) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.teacher = teacher;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getTeacher() {
        return teacher;
    }

    public void setTeacher(Boolean teacher) {
        this.teacher = teacher;
    }
}
