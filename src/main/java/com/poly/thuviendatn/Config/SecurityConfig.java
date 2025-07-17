package com.poly.thuviendatn.Config;

import com.poly.thuviendatn.Service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler successHandler, UserDetailsService userDetailsService) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/auth/login",
                    "/auth/register",
                    "/auth/register/**",
                    "/Css/**",
                    "/Image/**",
                    "/Image/sach/**",
                    "/Image/sach/chuong1/**",
                    "/Image/sach/chuong2/**",
                    "/Image/sach/chuong3/**",
                    "/js/**",
                    "/aboutus",
                    "/sach/gioithieu",
                    "/sach/chitiet/{id}",
                    "/sach/chitiet/{id}/danhgia",
                    "/sach/sachchitiet/{id}",
                    "/books",
                    "/books/**",
                    "/quenmatkhau",
                    "/auth/reset-password"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/staff/**").hasRole("STAFF")
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("maTaiKhoan") // ✅
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureUrl("/auth/login?error=true")
                .permitAll()
)
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("uniqueAndSecretKey")
                .tokenValiditySeconds(86400) // 1 day
            )
            .userDetailsService(userDetailsService)
            .csrf(csrf -> csrf.disable()); // Re-enable CSRF in production

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            switch (role) {
                case "ROLE_ADMIN":
                    response.sendRedirect("/");
                    break;
                case "ROLE_STAFF":
                    response.sendRedirect("/");
                    break;
                case "ROLE_USER":
                    response.sendRedirect("/");
                    break;
                default:
                    response.sendRedirect("/");
            }
        };
    }
}