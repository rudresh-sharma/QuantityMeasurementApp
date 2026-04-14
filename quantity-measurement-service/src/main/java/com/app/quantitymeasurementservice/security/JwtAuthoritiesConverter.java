package com.app.quantitymeasurementservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Component
public class JwtAuthoritiesConverter implements Converter<String, Collection<? extends GrantedAuthority>> {

    @Override
    public Collection<? extends GrantedAuthority> convert(String roleClaim) {
        if (!StringUtils.hasText(roleClaim)) {
            return List.of();
        }

        String normalizedRole = roleClaim.trim().toUpperCase(Locale.ROOT);
        if (!normalizedRole.startsWith("ROLE_")) {
            normalizedRole = "ROLE_" + normalizedRole;
        }

        return List.of(new SimpleGrantedAuthority(normalizedRole));
    }
}
