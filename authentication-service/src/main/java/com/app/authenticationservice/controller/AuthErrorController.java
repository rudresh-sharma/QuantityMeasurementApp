package com.app.authenticationservice.controller;

import com.app.authenticationservice.exception.ErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Controller
public class AuthErrorController {

    private final String oauthRedirectUri;

    public AuthErrorController(
            @Value("${auth.oauth2.redirect-uri:http://localhost:4200/oauth-success}") String oauthRedirectUri) {
        this.oauthRedirectUri = oauthRedirectUri;
    }

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request) {
        String originalPath = stringAttribute(request, RequestDispatcher.ERROR_REQUEST_URI);
        String message = resolveErrorMessage(request);
        int statusCode = intAttribute(request, RequestDispatcher.ERROR_STATUS_CODE, 500);
        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        if (isOauthPath(originalPath)) {
            String safeMessage = StringUtils.hasText(message)
                    ? message
                    : "Google sign-in could not be completed right now. Please try again.";
            return "redirect:" + UriComponentsBuilder.fromUriString(oauthRedirectUri)
                    .queryParam("error", safeMessage)
                    .build()
                    .toUriString();
        }

        return ResponseEntity.status(status).body(new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                StringUtils.hasText(message) ? message : "Unexpected server error",
                StringUtils.hasText(originalPath) ? originalPath : request.getRequestURI()
        ));
    }

    private boolean isOauthPath(String path) {
        return StringUtils.hasText(path)
                && (path.startsWith("/login/oauth2/") || path.startsWith("/oauth2/"));
    }

    private String resolveErrorMessage(HttpServletRequest request) {
        Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (throwable != null && StringUtils.hasText(throwable.getMessage())) {
            return throwable.getMessage();
        }

        return stringAttribute(request, RequestDispatcher.ERROR_MESSAGE);
    }

    private String stringAttribute(HttpServletRequest request, String attributeName) {
        Object value = request.getAttribute(attributeName);
        return value instanceof String text ? text : null;
    }

    private int intAttribute(HttpServletRequest request, String attributeName, int defaultValue) {
        Object value = request.getAttribute(attributeName);
        return value instanceof Integer number ? number : defaultValue;
    }
}
