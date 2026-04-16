package com.app.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class GatewayCorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer(
            @Value("${app.cors.allowed-origin:http://localhost:4200}") String frontendOrigins) {
        List<String> allowedOrigins = Arrays.stream(frontendOrigins.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins.toArray(String[]::new))
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization")
                        .allowCredentials(true);
            }
        };
    }
}
