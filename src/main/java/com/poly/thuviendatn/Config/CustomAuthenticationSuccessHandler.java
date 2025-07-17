package com.poly.thuviendatn.Config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER"); // Default to ROLE_USER if no role is found
        String redirectUrl = switch (role) {
            case "ROLE_ADMIN" -> "/admin/dashboard";
            case "ROLE_STAFF" -> "/staff/dashboard";
            case "ROLE_USER" -> "/user/dashboard";
            default -> "/home";
        };
        response.sendRedirect(redirectUrl);
    }
}