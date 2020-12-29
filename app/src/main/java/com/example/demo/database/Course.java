package com.example.demo.database;

import java.io.Serializable;

public class Course implements Serializable {

    public String name;
    public int price;
    public String type;
    public String email;
    public String content;
    public String image;

    public Course() {
    }

    public Course(String name, int price, String type, String email, String content) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.email = email;
        this.content = content;
    }

    public Course(String name, int price, String type, String email, String content, String image) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.email = email;
        this.content = content;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
