package com.example.admin.myapplication.bean;

/**
 * Created by Admin on 2016/8/4.
 */
public class ChatMsgBean {

    private String id;
    private String send;
    private String reciver;
    private int type;
    private String msg;
    private String time;
    private String state;
    private int audioLength;
    private String videoPath;

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public int getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(int audioLength) {
        this.audioLength = audioLength;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ChatMsgBean{" +
                ", type=" + type +
                ", msg='" + msg + '\'' +
                ", time='" + time + '\'' +
                ", audioLength=" + audioLength +
                ", videoPath='" + videoPath + '\'' +
                '}';
    }
}
