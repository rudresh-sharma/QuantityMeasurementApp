package com.app.quantitymeasurementapp.config;

import com.app.quantitymeasurementapp.security.AppUserDetailsService;
import com.app.quantitymeasurementapp.security.GoogleOAuth2SuccessHandler;
import com.app.quantitymeasurementapp.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final boolean googleOauthEnabled;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          AppUserDetailsService userDetailsService,
                          @Value("${spring.security.oauth2.client.registration.google.client-id:}") String googleClientId,
                          @Value("${spring.security.oauth2.client.registration.google.client-secret:}") String googleClientSecret) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.googleOauthEnabled = StringUtils.hasText(googleClientId) && StringUtils.hasText(googleClientSecret);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   ObjectProvider<GoogleOAuth2SuccessHandler> googleHandlerProvider,
                                                   AuthenticationProvider authenticationProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/oauth-success").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/v1/quantities/**").authenticated()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        if (googleOauthEnabled) {
            GoogleOAuth2SuccessHandler handler = googleHandlerProvider.getIfAvailable();
            if (handler != null) {
                http.oauth2Login(oauth2 -> oauth2.successHandler(handler));
            }
        }

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(AppUserDetailsService userDetailsService,
                                                         PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
