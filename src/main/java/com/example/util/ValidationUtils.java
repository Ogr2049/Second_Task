package com.example.util;

import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[a-zA-Zа-яА-ЯёЁ\\s-]{2,50}$");
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches() && name.trim().length() >= 2;
    }
    
    public static boolean isValidAge(Integer age) {
        return age != null && age >= 1 && age <= 120;
    }
}