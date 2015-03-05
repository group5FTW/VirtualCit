package com.example.liz.virtualcit.Model;

public class MenuObject {
    private String name;
    private String url;

    public MenuObject(String a, String b) {
        setName(a);
        setUrl(b);
    }

    public MenuObject() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
