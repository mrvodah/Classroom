package com.example.demo.database;

public class Lesson {

    public String name, link, content;

    public Lesson() {
    }

    public Lesson(String name, String link, String content) {
        this.name = name;
        this.link = link;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
