package com.example.ooduberu.chatapp.interfaces;

public interface Listener {
    void showProgressLoader();
    void hideProgressLoader();
    void sendNotification(String actionType,String user_id,String receiver_id,String activity_id);
    void fetchFollowersAndFollowingData(String type,String user_id);
}
