package com.example.ooduberu.chatapp.interfaces;

public interface Listener {
    void showProgressLoader();
    void hideProgressLoader();
    void sendNotification(String actionType,String user_id,String receiver_id);
}
