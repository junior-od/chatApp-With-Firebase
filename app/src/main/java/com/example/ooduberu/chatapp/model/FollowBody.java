package com.example.ooduberu.chatapp.model;

public class FollowBody {
    String request_type;
    String user_name;

    public FollowBody(){

    }

    public FollowBody(String request_type,String user_name) {
        this.request_type = request_type;
        this.user_name = user_name;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
