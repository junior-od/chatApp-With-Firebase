package com.example.ooduberu.chatapp.model;

public class FollowBody {
    String request_type;

    public FollowBody(){

    }

    public FollowBody(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
