package com.app.authenticationservice.config;

import com.app.authenticationservice.security.AppUserDetailsService;
import com.app.authenticationservice.security.CookieOAuth2AuthorizationRequestRepository;
import com.app.authenticationservice.security.GoogleOAuth2FailureHandler;
import com.app.authenticationservice.security.GoogleOAuth2SuccessHandler;
import com.app.authenticationservice.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final boolean googleOauthEnabled;
    private final List<String> frontendOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          AppUserDetailsService userDetailsService,
                          @Value("${app.cors.allowed-origin:http://localhost:4200}") String frontendOrigins,
                          @Value("${spring.security.oauth2.client.registration.google.client-id:}") String googleClientId,
                          @Value("${spring.security.oauth2.client.registration.google.client-secret:}") String googleClientSecret) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.frontendOrigins = Arrays.stream(frontendOrigins.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
        this.googleOauthEnabled = StringUtils.hasText(googleClientId) && StringUtils.hasText(googleClientSecret);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   ObjectProvider<ClientRegistrationRepository> clientRegistrationRepositoryProvider,
                                                   CookieOAuth2AuthorizationRequestRepository authorizationRequestRepository,
                                                   ObjectProvider<GoogleOAuth2SuccessHandler> googleHandlerProvider,
                                                   ObjectProvider<GoogleOAuth2FailureHandler> googleFailureHandlerProvider,
                                                   AuthenticationProvider authenticationProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(
                        googleOauthEnabled ? SessionCreationPolicy.IF_REQUIRED : SessionCreationPolicy.STATELESS
                ))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/oauth-success").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        if (googleOauthEnabled) {
            ClientRegistrationRepository clientRegistrationRepository = clientRegistrationRepositoryProvider.getIfAvailable();
            GoogleOAuth2SuccessHandler handler = googleHandlerProvider.getIfAvailable();
            GoogleOAuth2FailureHandler failureHandler = googleFailureHandlerProvider.getIfAvailable();
            if (handler != null && failureHandler != null && clientRegistrationRepository != null) {
                http.oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint ->
                                endpoint.authorizationRequestRepository(authorizationRequestRepository)
                                        .authorizationRequestResolver(
                                                googleAuthorizationRequestResolver(clientRegistrationRepository)
                                        ))
                        .successHandler(handler)
                        .failureHandler(failureHandler));
            }
        }

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(frontendOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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

    private OAuth2AuthorizationRequestResolver googleAuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization");

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            @Nullable
            public OAuth2AuthorizationRequest resolve(jakarta.servlet.http.HttpServletRequest request) {
                return customizeAuthorizationRequest(defaultResolver.resolve(request));
            }

            @Override
            @Nullable
            public OAuth2AuthorizationRequest resolve(jakarta.servlet.http.HttpServletRequest request,
                                                      String clientRegistrationId) {
                return customizeAuthorizationRequest(defaultResolver.resolve(request, clientRegistrationId));
            }
        };
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest request) {
        if (request == null) {
            return null;
        }

        Map<String, Object> additionalParameters = new HashMap<>(request.getAdditionalParameters());
        additionalParameters.put("prompt", "select_account");

        return OAuth2AuthorizationRequest.from(request)
                .additionalParameters(additionalParameters)
                .build();
    }
}
