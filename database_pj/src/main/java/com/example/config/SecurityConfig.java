package com.example.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/register", "/api/login", "/api/**", "/api/merchants/**", "/images/**", "/api/check-login", "/api/coupon/**", "/api/orders/**", "/api/orders", "/api/packages/**", "/api/**").permitAll() // 允许匿名访问
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 会话管理// .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 会话管理
                )
                .csrf(AbstractHttpConfigurer::disable) // 关闭 CSRF（如果前端不使用 CSRF 令牌）
                .cors(Customizer.withDefaults()); // 启用 CORS
        return http.build();
    }


    // ✅ 配置 CORS 规则
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // 允许前端访问
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 允许的 HTTP 方法
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // 允许的请求头
        configuration.setAllowCredentials(true); // 允许携带 Cookie 认证

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}