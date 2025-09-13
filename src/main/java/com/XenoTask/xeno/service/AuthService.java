package com.xenotask.xeno.service;

import com.xenotask.xeno.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    public String login(String email) {
        userService.updateLastLogin(email);
        return jwtTokenProvider.generateToken(email, Map.of("sub", email));
    }

    public void register(String email, String password) {
        userService.register(email, password);
    }
}

