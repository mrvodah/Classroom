package com.example.demo.common;

import com.example.demo.database.User;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Common {

    public static User currentUser;
    public static String[] types = new String[]{"English", "Math", "Physical"};

    public static String getEmail(String email) {
        return email.replace('.', '1');
    }

    public static String getMoney(int money) {
        try {
            NumberFormat formatter = new DecimalFormat("#,###");
            return formatter.format(money) + " VND";
        } catch (Exception ex) {
            return money + " VND";
        }
    }

}
