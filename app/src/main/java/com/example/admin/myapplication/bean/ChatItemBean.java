package com.example.admin.myapplication.bean;

/**
 * Created by Admin on 2016/8/4.
 */
public class ChatItemBean {

    private int icon;
    private String name;
    private String tools;

    public ChatItemBean(){}

    public ChatItemBean(int icon, String name, String tools) {
        this.icon = icon;
        this.name = name;
        this.tools = tools;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTools() {
        return tools;
    }

    public void setTools(String tools) {
        this.tools = tools;
    }
}
