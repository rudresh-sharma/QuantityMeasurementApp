package com.app.authenticationservice.security;

import com.app.authenticationservice.client.UserServiceClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserServiceClient userServiceClient;

    public AppUserDetailsService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            var user = userServiceClient.getUserByEmail(username);
            return new User(
                    user.getEmail(),
                    user.getPasswordHash() == null ? "" : user.getPasswordHash(),
                    user.isEnabled(),
                    true,
                    true,
                    true,
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        } catch (RuntimeException ex) {
            throw new UsernameNotFoundException("User not found: " + username, ex);
        }
    }
}
