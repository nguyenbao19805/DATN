package com.poly.thuviendatn.Util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        // Create a BCryptPasswordEncoder instance
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Password to encode
        String password = "123";
        
        // Generate BCrypt hash
        String encodedPassword = encoder.encode(password);
        
        // Print the hash
        System.out.println("Encoded password for '" + password + "': " + encodedPassword);
    }
}