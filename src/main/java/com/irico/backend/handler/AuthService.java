package com.irico.backend.handler;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.irico.backend.user.dto.AuthenticationResponse;
import com.irico.backend.user.dto.LoginRequest;
import com.irico.backend.user.dto.SignUpRequest;
import com.irico.backend.user.UserModel;
import com.irico.backend.user.UserRepository;
import com.irico.backend.security.JwtUtil;
import com.irico.backend.security.UserPrincipal;

@Service
public class AuthService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JwtUtil jwtTokenProvider;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthenticationResponse singUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        UserModel user = new UserModel();
        user.setUserName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(request.getRole()));

        userRepository.save(user);
        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtTokenProvider.generateToken(principal);
        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(token);
        response.setRole(request.getRole());
        response.setId(user.getUserId());
        response.setName(user.getUserName());
        response.setEmail(user.getEmail());
        return response;
    }

    public AuthenticationResponse logIn(LoginRequest request) {
        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        UserPrincipal principal = new UserPrincipal(user);

        String token = jwtTokenProvider.generateToken(principal);
        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(token);
        response.setRole(user.getRoles().iterator().next());
        response.setId(user.getUserId());
        response.setName(user.getUserName());
        response.setEmail(user.getEmail());
        return response;
    }
}
