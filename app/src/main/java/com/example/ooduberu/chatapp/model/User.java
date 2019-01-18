package com.example.ooduberu.chatapp.model;

public class User {
    String first_name,last_name,user_name,email,image,chat_background_image,status,device_token,header_image,account_type;
    String user_id;

    public User(){

    }

    public User(String first_name,String last_name,String user_name,String image,String user_id){
        this.first_name = first_name;
        this.last_name = last_name;
        this.user_name = user_name;
        this.image = image;
        this.user_id = user_id;
    }

    public User(String first_name, String last_name, String user_name, String email, String image, String chat_background_image, String status, String device_token,String header_image,String account_type) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.user_name = user_name;
        this.email = email;
        this.image = image;
        this.chat_background_image = chat_background_image;
        this.status = status;
        this.device_token = device_token;
        this.header_image = header_image;
        this.account_type = account_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChat_background_image() {
        return chat_background_image;
    }

    public void setChat_background_image(String chat_background_image) {
        this.chat_background_image = chat_background_image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getHeader_image() {
        return header_image;
    }

    public void setHeader_image(String header_image) {
        this.header_image = header_image;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }
}
