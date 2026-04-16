package com.app.authenticationservice.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String COOKIE_NAME = "OAUTH2_AUTH_STATE";
    private static final int COOKIE_MAX_AGE_SECONDS = 180;
    private final Map<String, StoredAuthorizationRequest> authorizationRequests = new ConcurrentHashMap<>();

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        cleanupExpiredEntries();

        String state = request.getParameter("state");
        if (!StringUtils.hasText(state)) {
            Cookie cookie = findCookie(request, COOKIE_NAME);
            if (cookie != null && StringUtils.hasText(cookie.getValue())) {
                state = cookie.getValue();
            }
        }

        if (!StringUtils.hasText(state)) {
            return null;
        }

        StoredAuthorizationRequest stored = authorizationRequests.get(state);
        if (stored == null || stored.isExpired()) {
            authorizationRequests.remove(state);
            return null;
        }

        return stored.authorizationRequest();
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        if (authorizationRequest == null) {
            deleteCookie(response);
            return;
        }

        cleanupExpiredEntries();
        String state = authorizationRequest.getState();
        if (!StringUtils.hasText(state)) {
            deleteCookie(response);
            return;
        }

        authorizationRequests.put(state, new StoredAuthorizationRequest(
                authorizationRequest,
                Instant.now().plusSeconds(COOKIE_MAX_AGE_SECONDS)
        ));

        Cookie cookie = new Cookie(COOKIE_NAME, state);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
        response.addCookie(cookie);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        OAuth2AuthorizationRequest authRequest = loadAuthorizationRequest(request);
        String state = request.getParameter("state");
        if (!StringUtils.hasText(state)) {
            Cookie cookie = findCookie(request, COOKIE_NAME);
            if (cookie != null && StringUtils.hasText(cookie.getValue())) {
                state = cookie.getValue();
            }
        }

        if (StringUtils.hasText(state)) {
            authorizationRequests.remove(state);
        }

        deleteCookie(response);
        return authRequest;
    }

    private Cookie findCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }

        return null;
    }

    private void deleteCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private void cleanupExpiredEntries() {
        Iterator<Map.Entry<String, StoredAuthorizationRequest>> iterator = authorizationRequests.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, StoredAuthorizationRequest> entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
            }
        }
    }

    private record StoredAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            Instant expiresAt
    ) {
        private boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
