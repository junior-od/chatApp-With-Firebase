package com.example.ooduberu.chatapp.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean validateName(String fullName){
        boolean status;
        String full_name_pattern = "^[a-zA-Z\\s]+$";

        Pattern pattern = Pattern.compile(full_name_pattern);
        Matcher matcher = pattern.matcher(fullName);
        if (matcher.matches()){
            status = true;
        }
        else{
            status = false;
        }

        return  status;
    }

    public static boolean validateUsername(String username){
        boolean status;
        String username_pattern = "^[a-zA-Z0-9._-]{2,25}$";

        Pattern  pattern = Pattern.compile(username_pattern);
        Matcher matcher = pattern.matcher(username);
        if(matcher.matches()){
            status = true;
        }
        else{
            status = false;

        }

        return status;
    }

    public static boolean complexPassword(String username){
        boolean status;
        String username_pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";

        Pattern pattern = Pattern.compile(username_pattern);
        Matcher matcher = pattern.matcher(username);
        if(matcher.matches()){
            status = true;
        }
        else{
            status = false;

        }

        return status;
    }

}
