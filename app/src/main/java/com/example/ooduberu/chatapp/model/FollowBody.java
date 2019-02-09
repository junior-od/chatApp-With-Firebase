package com.example.ooduberu.chatapp.model;

public class FollowBody {
    String request_type;
    long time_followed;
    String activity_id;

    public FollowBody(){

    }

    public FollowBody(String request_type,long time_followed,String activity_id) {
        this.request_type = request_type;
        this.time_followed = time_followed;
        this.activity_id = activity_id;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public long getTime_followed() {
        return time_followed;
    }

    public void setTime_followed(long time_followed) {
        this.time_followed = time_followed;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }
}
