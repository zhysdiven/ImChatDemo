package com.example.admin.myapplication.bean;

/**
 * Created by Admin on 2016/8/4.
 */
public class FaceBean {

    private int icon;
    private String content;

    public FaceBean(int icon) {
        this.icon = icon;
    }

    public FaceBean(int icon, String content) {
        this.icon = icon;
        this.content = content;
    }

    public FaceBean(){}

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
