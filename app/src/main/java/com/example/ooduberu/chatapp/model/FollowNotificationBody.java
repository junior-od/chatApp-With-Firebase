package com.example.ooduberu.chatapp.model;

public class FollowNotificationBody {
    String from;
    String message;

    public FollowNotificationBody(){

    }

    public FollowNotificationBody(String from, String message) {
        this.from = from;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
