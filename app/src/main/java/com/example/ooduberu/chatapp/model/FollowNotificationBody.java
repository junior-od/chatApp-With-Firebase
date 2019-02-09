package com.example.ooduberu.chatapp.model;

public class FollowNotificationBody {
    String from;
    String message;
    String activity_id;

    public FollowNotificationBody(){

    }

    public FollowNotificationBody(String from, String message,String activity_id) {
        this.from = from;
        this.message = message;
        this.activity_id = activity_id;
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

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }
}
